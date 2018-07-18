package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.domain.test.manager.factory.*;
import io.choerodon.test.manager.domain.service.ITestCaseStepService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseStepService;
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

    @Autowired
    TestCycleCaseAttachmentRelService attachmentRelService;

    @Override
    public void deleteByTestCycleCase(TestCycleCaseE testCycleCaseE) {
        TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
        testCycleCaseStepE.setExecuteId(testCycleCaseE.getExecuteId());
        deleteStep(testCycleCaseStepE);
    }

    @Override
    public void deleteStep(TestCycleCaseStepE testCycleCaseStepE) {
        testCycleCaseStepE.querySelf().forEach(v -> {
            deleteLinkedAttachment(v.getExecuteStepId());
            deleteLinkedDefect(v.getExecuteStepId());
        });
        testCycleCaseStepE.deleteSelf();
    }

    private void deleteLinkedAttachment(Long stepId) {
        TestCycleCaseAttachmentRelE attachmentRelE = TestCycleCaseAttachmentRelEFactory.create();
        attachmentRelE.setAttachmentLinkId(stepId);
        attachmentRelE.setAttachmentType(TestCycleCaseAttachmentRelE.ATTACHMENT_CYCLE_STEP);
        attachmentRelE.querySelf().forEach(v -> attachmentRelService.delete(TestCycleCaseAttachmentRelE.ATTACHMENT_BUCKET, v.getId()));
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
