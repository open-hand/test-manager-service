package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.domain.test.manager.factory.TestCaseStepEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import io.choerodon.test.manager.domain.service.ITestCaseStepService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseStepService;
import io.choerodon.test.manager.domain.test.manager.factory.TestStatusEFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
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
    public Page<TestCycleCaseStepE> querySubStep(TestCycleCaseE testCycleCaseE, PageRequest pageRequest, Long projectId) {
        TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
        testCycleCaseStepE.setExecuteId(testCycleCaseE.getExecuteId());
        Page<TestCycleCaseStepE> testCycleCaseEs = testCycleCaseStepE.querySelf(pageRequest);
        testCycleCaseEs.forEach(v -> {
            v.setDefects(iTestCycleCaseDefectRelServicel.query(v.getExecuteStepId(), TestCycleCaseDefectRelE.CASE_STEP, projectId));
        });
        return testCycleCaseEs;
    }


    /**
     * 启动测试例分步任务
     *
     * @param testCycleCaseE
     */
    @Override
	public void createTestCycleCaseStep(TestCycleCaseE testCycleCaseE, Long projectId) {
        TestCaseStepE testCaseStepE = TestCaseStepEFactory.create();
        testCaseStepE.setIssueId(testCycleCaseE.getIssueId());
        List<TestCaseStepE> testCaseStepES = iTestCaseStepService.query(testCaseStepE);
        TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
        TestStatusE e = TestStatusEFactory.create();
		testCycleCaseStepE.setStepStatus(e.getDefaultStatusId(projectId, TestStatusE.STATUS_TYPE_CASE_STEP));
        testCaseStepES.forEach(v -> {
            testCycleCaseStepE.setStepId(v.getStepId());
            testCycleCaseStepE.setExecuteId(testCycleCaseE.getExecuteId());
            testCycleCaseStepE.addSelf();
        });
    }
}
