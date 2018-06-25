package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.domain.service.ITestCycleCaseService;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@Component
public class ITestCycleServiceImpl implements ITestCycleService {

	@Autowired
	ProductionVersionClient productionVersionClient;

	@Autowired
	ITestCycleCaseService iTestCycleCaseService;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCycleE insert(TestCycleE testCycleE) {
		return testCycleE.addSelf();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void delete(TestCycleE testCycleE) {
		TestCycleE cycle = TestCycleEFactory.create();
		cycle.setVersionId(testCycleE.getVersionId());
		cycle.setParentCycleId(testCycleE.getCycleId());
		cycle.querySelf().forEach(this::deleteCycleWithCase);
	}

	private void deleteCycleWithCase(TestCycleE testCycleE) {
		TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();
		testCycleCaseE.setCycleId(testCycleE.getCycleId());
		iTestCycleCaseService.delete(testCycleCaseE);
		testCycleE.deleteSelf();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public List<TestCycleE> update(List<TestCycleE> testCycleE) {
		List<TestCycleE> testCycleES = new ArrayList<>();
		testCycleE.forEach(v -> testCycleES.add(v.updateSelf()));
		return testCycleES;
	}

	@Override
	public Page<TestCycleE> query(TestCycleE testCycleE, PageRequest pageRequest) {
		return testCycleE.querySelf(pageRequest);
	}

	@Override
	public List<TestCycleE> querySubCycle(TestCycleE testCycleE) {
		return testCycleE.querySelf();
	}

	public List<TestCycleE> sort(List<TestCycleE> testCycleES) {
		List<TestCycleE> testCaseStepES = new ArrayList<>();
		doSort(testCycleES, null, testCaseStepES);
		return testCaseStepES;
	}

	private void doSort(List<TestCycleE> testCycleES, Long parentId, List<TestCycleE> result) {
		Long nextParentId = parentId;
		for (int i = 0; i < testCycleES.size(); i++) {
			TestCycleE e = testCycleES.get(i);
			if (e.getParentCycleId() == parentId) {
				nextParentId = e.getCycleId();
				result.add(e);
				testCycleES.remove(e);
				break;
			}
		}
		if (testCycleES.isEmpty()) {
			return;
		}
		if (nextParentId == parentId) {
			throw new CommonException("error.test.case.step.sort");
		}
		doSort(testCycleES, nextParentId, result);
	}

	@Override
	public List<TestCycleE> getTestCycle(Long versionId) {
		TestCycleE testCycleE = TestCycleEFactory.create();
		testCycleE.setVersionId(versionId);
		return querySubCycle(testCycleE);
	}

	@Override
	public List<TestCycleE> queryCycleWithBar(Long versionId) {
		TestCycleE testCycleE = TestCycleEFactory.create();
		testCycleE.setVersionId(versionId);
		return testCycleE.querySelfWithBar();
	}
}
