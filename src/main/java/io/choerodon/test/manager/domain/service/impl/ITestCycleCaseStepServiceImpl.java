package io.choerodon.test.manager.domain.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.domain.service.ITestStatusService;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.domain.test.manager.factory.*;
import io.choerodon.test.manager.domain.service.ITestCaseStepService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseStepService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class ITestCycleCaseStepServiceImpl implements ITestCycleCaseStepService {
    @Autowired
    ITestCaseStepService iTestCaseStepService;

    @Autowired
    ITestCycleCaseDefectRelService iTestCycleCaseDefectRelServicel;

    @Autowired
    TestCycleCaseAttachmentRelService attachmentRelService;
    @Autowired
    ITestStatusService iTestStatusService;


    @Override
    public void deleteByTestCycleCase(TestCycleCaseE testCycleCaseE) {
        TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
        testCycleCaseStepE.setExecuteId(testCycleCaseE.getExecuteId());
        deleteStep(testCycleCaseStepE);
    }

    @Override
    public void deleteStep(TestCycleCaseStepE testCycleCaseStepE) {
        Optional.ofNullable(testCycleCaseStepE.querySelf()).ifPresent(
                m -> m.forEach(v -> {
                    attachmentRelService.delete(v.getExecuteStepId(), TestCycleCaseAttachmentRelE.ATTACHMENT_CYCLE_STEP);
                    deleteLinkedDefect(v.getExecuteStepId());
                })
        );
        testCycleCaseStepE.deleteSelf();
    }

    private void deleteLinkedDefect(Long stepId) {
        TestCycleCaseDefectRelE caseDefectRelE = TestCycleCaseDefectRelEFactory.create();
        caseDefectRelE.setDefectLinkId(stepId);
        caseDefectRelE.setDefectType(TestCycleCaseDefectRelE.CASE_STEP);
        caseDefectRelE.querySelf().forEach(v -> iTestCycleCaseDefectRelServicel.delete(v));
    }


    @Override
    public List<TestCycleCaseStepE> update(List<TestCycleCaseStepE> testCycleCaseStepE) {
        List<TestCycleCaseStepE> list = new ArrayList<>();
        testCycleCaseStepE.forEach(v -> list.add(v.updateSelf()));
        return list;
    }


    @Override
    public PageInfo<TestCycleCaseStepE> querySubStep(TestCycleCaseE testCycleCaseE, PageRequest pageRequest, Long projectId) {
        TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
        testCycleCaseStepE.setExecuteId(testCycleCaseE.getExecuteId());
        return testCycleCaseStepE.querySelf(pageRequest);
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
        testCycleCaseStepE.setStepStatus(iTestStatusService.getDefaultStatusId(TestStatusE.STATUS_TYPE_CASE_STEP));
        testCaseStepES.forEach(v -> {
            testCycleCaseStepE.setStepId(v.getStepId());
            testCycleCaseStepE.setExecuteId(testCycleCaseE.getExecuteId());
            testCycleCaseStepE.addSelf();
        });
    }
}
