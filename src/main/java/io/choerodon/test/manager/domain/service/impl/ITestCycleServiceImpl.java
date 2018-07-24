package io.choerodon.test.manager.domain.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.domain.repository.TestCycleRepository;
import io.choerodon.test.manager.domain.service.ITestStatusService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.domain.service.ITestCycleCaseService;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.domain.test.manager.factory.TestStatusEFactory;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.test.manager.infra.mapper.TestCycleMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class ITestCycleServiceImpl implements ITestCycleService {

	@Autowired
	ProductionVersionClient productionVersionClient;

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
		iTestCycleCaseService.delete(testCycleCaseE, projectId);
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


//    @Override
//    public List<TestCycleE> getTestCycle(Long versionId) {
//        TestCycleE testCycleE = TestCycleEFactory.create();
//        testCycleE.setVersionId(versionId);
//        return querySubCycle(testCycleE);
//    }

	@Override
	public List<TestCycleE> queryCycleWithBar(Long[] versionId) {
		TestCycleE testCycleE = TestCycleEFactory.create();
		//testCycleE.setVersionId(versionId);
		return countStatus(testCycleE.querySelfWithBar(versionId));
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
		switch (lists.size()) {
			case 1:
				TestCycleE cycle = TestCycleEFactory.create();
				cycle.setVersionId(lists.get(0).getVersionId());
				cycle.setCycleName(TestCycleE.TEMP_CYCLE_NAME);
				return cycle.querySelf().stream().findFirst()
						.orElseThrow(() -> new CommonException("error.folder.version_planning.notFound")).getCycleId();
			default:
				throw new CommonException("error.folder.version_planning.notFound");
		}
	}

	public TestCycleE cloneFolder(TestCycleE protoTestCycleE, TestCycleE newTestCycleE, Long projectId) {
		TestCycleE newCycleE = newTestCycleE.cloneCycle(protoTestCycleE);

		TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();
		testCycleCaseE.setCycleId(protoTestCycleE.getCycleId());
		Long defaultStatus = iTestStatusService.getDefaultStatusId(TestStatusE.STATUS_TYPE_CASE);
		final String[] lastRank = new String[1];
		lastRank[0] = testCycleCaseE.getLastedRank(testCycleCaseE.getCycleId());

		iTestCycleCaseService.query(testCycleCaseE).forEach(v -> {
			v.setExecuteId(null);
			v.setRank(RankUtil.Operation.INSERT.getRank(lastRank[0], null));
			v.setCycleId(newCycleE.getCycleId());
			v.setExecutionStatus(defaultStatus);
			v.setObjectVersionNumber(null);
			lastRank[0] = iTestCycleCaseService.cloneCycleCase(v, projectId).getRank();
		});
		return newCycleE;
	}

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


	@Override
	public List<Long> selectCyclesInVersions(Long[] versionIds) {
		return testCycleRepository.selectCyclesInVersions(versionIds);
	}

}
