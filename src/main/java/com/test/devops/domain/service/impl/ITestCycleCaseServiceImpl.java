package com.test.devops.domain.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.test.devops.domain.entity.TestCaseStepE;
import com.test.devops.domain.entity.TestCycleCaseE;
import com.test.devops.domain.entity.TestCycleCaseStepE;
import com.test.devops.domain.entity.TestCycleE;
import com.test.devops.domain.factory.TestCaseStepEFactory;
import com.test.devops.domain.factory.TestCycleCaseEFactory;
import com.test.devops.domain.factory.TestCycleCaseStepEFactory;
import com.test.devops.domain.factory.TestCycleEFactory;
import com.test.devops.domain.service.ITestCaseStepService;
import com.test.devops.domain.service.ITestCycleCaseService;
import com.test.devops.domain.service.ITestCycleCaseStepService;
import com.test.devops.infra.feign.ProductionVersionClient;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@Component
public class ITestCycleCaseServiceImpl implements ITestCycleCaseService {
	@Autowired
	ITestCycleCaseStepService iTestCycleCaseStepService;

	@Autowired
	ProductionVersionClient productionVersionClient;

	private final String TEMP_CYCLE_NAME = "临时";
	private final String TEMP_CYCLE_TYPE = "CYCLE";


	@Override
	public void delete(TestCycleCaseE testCycleCaseE) {
		List<TestCycleCaseE> removeList = testCycleCaseE.querySelf();
		removeList.forEach(v -> deleteCaseWithSubStep(v));
	}

	private void deleteCaseWithSubStep(TestCycleCaseE testCycleCaseE) {
		iTestCycleCaseStepService.deleteByTestCycleCase(testCycleCaseE);
		testCycleCaseE.deleteSelf();
	}


	@Override
	public Page<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE, PageRequest pageRequest) {
		return testCycleCaseE.querySelf(pageRequest);
	}

	@Override
	public List<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE) {
		return testCycleCaseE.querySelf();
	}

	@Override
	public TestCycleCaseE queryOne(TestCycleCaseE testCycleCaseE) {
		testCycleCaseE = testCycleCaseE.queryOne();
		if (testCycleCaseE != null) {
			testCycleCaseE.setTestCycleCaseStepES(iTestCycleCaseStepService.querySubStep(testCycleCaseE));
		}
		return testCycleCaseE;
	}


	/**
	 * 启动测试循环
	 *
	 * @param testCycleCaseE
	 * @return
	 */
	@Override
	public TestCycleCaseE runTestCycleCase(TestCycleCaseE testCycleCaseE, Long projectId) {
		if (testCycleCaseE.getCycleId() == null) {
			testCycleCaseE.setCycleId(createTempCycle(projectId).getCycleId());
		}
		TestCycleCaseE testCycleCase = testCycleCaseE.addSelf();
		createTestCaseStep(testCycleCase);
		return testCycleCase;
	}

	/**
	 * 启动测试时如果位置顶cycle则默认放入未计划下的临时文件夹下。
	 *
	 * @param projectId
	 * @return
	 */
	private TestCycleE createTempCycle(Long projectId) {
		ResponseEntity<Page<ProductVersionPageDTO>> rs = productionVersionClient.listByOptions(projectId, Maps.asMap(Sets.newHashSet("statusCode"), v -> "version_planning"));
		List<ProductVersionPageDTO> lists = rs.getBody().getContent();
		switch (lists.size()) {
			case 1:
				TestCycleE cycle = TestCycleEFactory.create();
				cycle.setVersionId(lists.get(0).getVersionId());
				cycle.setCycleName(TEMP_CYCLE_NAME);
				return cycle.querySelf().stream().findFirst().orElseGet(() -> {
					//新建临时文件夹 添加相关属性
					cycle.setType(TEMP_CYCLE_TYPE);
					return cycle.addSelf();
				});
			default:
				throw new CommonException("error.folder.version_planning.notFound");
		}

	}

	/**
	 * 启动测试例分步任务
	 *
	 * @param testCycleCaseE
	 */
	private void createTestCaseStep(TestCycleCaseE testCycleCaseE) {
		iTestCycleCaseStepService.createTestCycleCaseStep(testCycleCaseE);
	}


	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCycleCaseE changeStep(TestCycleCaseE testCycleCaseE, Long projectId) {

		if (testCycleCaseE.getExecuteId() == null) {
			testCycleCaseE.setRank(RankUtil.Operation.INSERT.getRank(testCycleCaseE.getLastRank(), testCycleCaseE.getNextRank()));

			testCycleCaseE = runTestCycleCase(testCycleCaseE, projectId);
		} else {
			testCycleCaseE.setRank(RankUtil.Operation.UPDATE.getRank(testCycleCaseE.getLastRank(), testCycleCaseE.getNextRank()));
			testCycleCaseE = testCycleCaseE.updateSelf();
		}
		return testCycleCaseE;
	}

}
