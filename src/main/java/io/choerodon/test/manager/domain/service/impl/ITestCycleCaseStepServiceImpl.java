package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCaseStepEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import io.choerodon.test.manager.domain.service.ITestCaseStepService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@Component
public class ITestCycleCaseStepServiceImpl implements ITestCycleCaseStepService {
	@Autowired
	ITestCaseStepService iTestCaseStepService;

	@Autowired
	ITestCycleCaseDefectRelService iTestCycleCaseDefectRelServicel;


	@Override
	public void deleteByTestCycleCase(TestCycleCaseE testCycleCaseE) {
		TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
		testCycleCaseStepE.setExecuteId(testCycleCaseE.getExecuteId());
		testCycleCaseStepE.deleteSelf();
	}

	@Override
	public void deleteStep(TestCycleCaseStepE testCycleCaseStepE) {
		testCycleCaseStepE.deleteSelf();
	}


	@Override
	public List<TestCycleCaseStepE> update(List<TestCycleCaseStepE> testCycleCaseStepE) {
		List<TestCycleCaseStepE> list = new ArrayList<>();
		testCycleCaseStepE.forEach(v -> list.add(v.updateSelf()));
		return list;
	}


	@Override
	public List<TestCycleCaseStepE> querySubStep(TestCycleCaseE testCycleCaseE) {
		TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
		testCycleCaseStepE.setExecuteId(testCycleCaseE.getExecuteId());
		List<TestCycleCaseStepE> testCycleCaseEs = testCycleCaseStepE.querySelf();
		testCycleCaseEs.forEach(v -> {
			v.setDefects(iTestCycleCaseDefectRelServicel.query(v.getExecuteStepId(), "CYCLE_STEP"));
		});
//		testCycleCaseEs.forEach(v->{
//			Optional.ofNullable(iTestCycleCaseDefectRelServicel.query(v.getExecuteStepId(),"CYCLE_STEP"))
//					.ifPresent(u->{
//						v.setDefectId(u.getId());
//						v.setDefectIssueId(u.getIssueId());
//						v.setDefectName(u.getDefectName());
//					});
//		});
		return testCycleCaseEs;
	}


	/**
	 * 启动测试例分步任务
	 *
	 * @param testCycleCaseE
	 */
	@Override
	public void createTestCycleCaseStep(TestCycleCaseE testCycleCaseE) {
		TestCaseStepE testCaseStepE = TestCaseStepEFactory.create();
		testCaseStepE.setIssueId(testCycleCaseE.getIssueId());
		List<TestCaseStepE> testCaseStepES = iTestCaseStepService.query(testCaseStepE);

		TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
		testCaseStepES.forEach(v -> {
			testCycleCaseStepE.setStepId(v.getStepId());
			testCycleCaseStepE.setExecuteId(testCycleCaseE.getExecuteId());
			testCycleCaseStepE.addSelf();
		});
	}
}
