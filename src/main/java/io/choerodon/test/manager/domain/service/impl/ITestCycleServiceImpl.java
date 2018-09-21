package io.choerodon.test.manager.domain.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.domain.repository.TestCycleRepository;
import io.choerodon.test.manager.domain.service.ITestStatusService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.domain.service.ITestCycleCaseService;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class ITestCycleServiceImpl implements ITestCycleService {

	@Autowired
	ProductionVersionClient productionVersionClient;

	@Autowired
	TestCycleCaseService testCycleCaseService;

	@Autowired
	ITestCycleCaseService iTestCycleCaseService;

	@Autowired
	TestCycleRepository testCycleRepository;

	@Autowired
	ITestStatusService iTestStatusService;

	@Override
	public TestCycleE insert(TestCycleE testCycleE) {
		return testCycleE.addSelf();
	}

	@Override
	public void delete(TestCycleE testCycleE, Long projectId) {

		List<TestCycleE> testCycleES = testCycleE.querySelf();
		testCycleES.forEach(v -> {
			if (v.getType().equals(TestCycleE.CYCLE)) {
				TestCycleE testCycle = TestCycleEFactory.create();
				testCycle.setParentCycleId(v.getCycleId());
				delete(testCycle, projectId);
			}
			deleteCycleWithCase(v, projectId);
		});
	}

	private void deleteCycleWithCase(TestCycleE testCycleE, Long projectId) {
		TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();
		testCycleCaseE.setCycleId(testCycleE.getCycleId());
		testCycleCaseE.querySelf().forEach(v -> testCycleCaseService.delete(v.getExecuteId(), projectId));
		testCycleE.deleteSelf();
	}

	@Override
	public TestCycleE update(TestCycleE testCycleE) {
		return testCycleE.updateSelf();
	}

	@Override
	public Page<TestCycleE> query(TestCycleE testCycleE, PageRequest pageRequest) {
		return testCycleE.querySelf(pageRequest);
	}

	@Override
	public List<TestCycleE> querySubCycle(TestCycleE testCycleE) {
		return testCycleE.querySelf();
	}

	@Override
	public List<TestCycleE> queryChildCycle(TestCycleE testCycleE) {
		return testCycleE.queryChildCycle();
	}


	@Override
	public List<TestCycleE> queryCycleWithBar(Long[] versionId,Long assignedTo) {
		TestCycleE testCycleE = TestCycleEFactory.create();
		return countStatus(testCycleE.querySelfWithBar(versionId,assignedTo));
	}

	@Override
	public List<TestCycleE> filterCycleWithBar(String filter, Long[] versionIds) {
		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("parameter", filter);
		filterMap.put("versionIds", versionIds);
		TestCycleE testCycleE = TestCycleEFactory.create();
		return countStatus(testCycleE.filterWithBar(filterMap));
	}

	private List<TestCycleE> countStatus(List<TestCycleE> testCycleES) {
		testCycleES.stream().filter(v -> StringUtils.equals(v.getType(), TestCycleE.CYCLE))
				.forEach(u -> u.countChildStatus(u.getChildFolder(testCycleES)));
		return testCycleES;
	}


	@Override
	public Long findDefaultCycle(Long projectId) {
		ResponseEntity<Page<ProductVersionPageDTO>> rs = productionVersionClient.listByOptions(projectId, Maps.asMap(Sets.newHashSet("statusCode"), v -> "version_planning"));
		List<ProductVersionPageDTO> lists = rs.getBody().getContent();
		int i = lists.size();
		if (i == 1) {
			TestCycleE cycle = TestCycleEFactory.create();
			cycle.setVersionId(lists.get(0).getVersionId());
			cycle.setCycleName(TestCycleE.TEMP_CYCLE_NAME);
			return cycle.querySelf().stream().findFirst()
					.orElseThrow(() -> new CommonException("error.folder.version_planning.notFound")).getCycleId();
		} else {
			throw new CommonException("error.folder.version_planning.notFound");
		}
	}

	/**克隆一个cycle和其拥有的cycleCase
	 * @param protoTestCycleE
	 * @param newTestCycleE
	 * @param projectId
	 * @return
	 */
	@Override
	public TestCycleE cloneFolder(TestCycleE protoTestCycleE, TestCycleE newTestCycleE, Long projectId) {
		TestCycleE newCycleE = newTestCycleE.cloneCycle(protoTestCycleE);
		cloneSubCycleCase(protoTestCycleE.getCycleId(),newCycleE.getCycleId(),projectId);

		return newCycleE;
	}

	/** 克隆一个循环和起子文件夹
	 * @param protoTestCycleE
	 * @param newTestCycleE
	 * @param projectId
	 * @return
	 */
	@Override
	public TestCycleE cloneCycle(TestCycleE protoTestCycleE, TestCycleE newTestCycleE, Long projectId) {
		TestCycleE parentCycle = cloneFolder(protoTestCycleE, newTestCycleE, projectId);
		protoTestCycleE.getChildFolder().forEach(v -> {
			TestCycleE testCycleE = TestCycleEFactory.create();
			testCycleE.setParentCycleId(parentCycle.getCycleId());
			cloneFolder(v, testCycleE, projectId);
		});
		return parentCycle;
	}

	/** 克隆循环下的cycleCase
	 * @param protoTestCycleId
	 * @param newCycleId
	 * @param projectId
	 */
	private void cloneSubCycleCase(Long protoTestCycleId, Long newCycleId, Long projectId) {
		Assert.notNull(protoTestCycleId,"error.clone.cycle.protoCycleId.not.be.null");
		Assert.notNull(newCycleId,"error.clone.cycle.newCycleId.not.be.null");

		TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();
		testCycleCaseE.setCycleId(protoTestCycleId);
		Long defaultStatus = iTestStatusService.getDefaultStatusId(TestStatusE.STATUS_TYPE_CASE);
		final String[] lastRank = new String[1];
		lastRank[0] = testCycleCaseE.getLastedRank(protoTestCycleId);
		//查询出cycle下所有case将其创建到新的cycle下并执行
		iTestCycleCaseService.query(testCycleCaseE).forEach(v ->
				lastRank[0] = iTestCycleCaseService.cloneCycleCase(
						v.getCloneCase(RankUtil.Operation.INSERT.getRank(lastRank[0], null), newCycleId, defaultStatus)
						, projectId).getRank()
		);
	}



	@Override
	public List<Long> selectCyclesInVersions(Long[] versionIds) {
		return testCycleRepository.selectCyclesInVersions(versionIds);
	}

}
