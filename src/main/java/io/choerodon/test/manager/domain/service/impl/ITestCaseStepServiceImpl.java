//package io.choerodon.test.manager.domain.service.impl;
//
//import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
//import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
//import io.choerodon.test.manager.domain.service.ITestCaseStepService;
//import io.choerodon.test.manager.domain.service.ITestCycleCaseStepService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.Assert;
//
//import java.util.List;
//import java.util.Optional;
//
//
///**
// * Created by 842767365@qq.com on 6/11/18.
// */
//
//@Service
//public class ITestCaseStepServiceImpl implements ITestCaseStepService {
//
//    @Autowired
//    ITestCycleCaseStepService testCycleCaseStepService;
//
//    @Autowired
//    TestCycleCaseAttachmentRelService attachmentRelService;
//
//    @Override
//    public List<TestCaseStepE> query(TestCaseStepE testCaseStepE) {
//        return testCaseStepE.querySelf();
//    }
//
//
//    /**
//     * 删除测试步骤，并删除相关执行步骤和步骤下附件
//     *
//     * @param testCaseStepE
//     */
//    @Override
//    public void removeStep(TestCaseStepE testCaseStepE) {
//        Assert.notNull(testCaseStepE, "error.case.step.remove.param.not.null");
//        Optional.ofNullable(testCaseStepE.querySelf()).ifPresent(m ->
//                m.forEach(v -> {
//                    deleteCycleCaseStep(v);
//                    attachmentRelService.delete(v.getStepId(), TestCycleCaseAttachmentRelE.ATTACHMENT_CASE_STEP);
//                })
//        );
//
//        testCaseStepE.deleteSelf();
//    }
//
//    private void deleteCycleCaseStep(TestCaseStepE testCaseStepE) {
//        TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
//        testCycleCaseStepE.setStepId(testCaseStepE.getStepId());
//        testCycleCaseStepService.deleteStep(testCycleCaseStepE);
//    }
//
//
//}
