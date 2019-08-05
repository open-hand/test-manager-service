//package io.choerodon.test.manager.domain.service.impl;
//
//import java.util.List;
//
//import org.springframework.stereotype.Service;
//
//import io.choerodon.test.manager.domain.service.ITestAutomationResultService;
//import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationResultE;
//
//@Service
//public class ITestAutomationResultServiceImpl implements ITestAutomationResultService {
//
//    @Override
//    public List<TestAutomationResultE> query(TestAutomationResultE testAutomationResultE) {
//        return testAutomationResultE.query();
//    }
//
//    @Override
//    public TestAutomationResultE add(TestAutomationResultE testAutomationResultE) {
//        return testAutomationResultE.addSelf();
//    }
//
//    @Override
//    public TestAutomationResultE update(TestAutomationResultE testAutomationResultE) {
//        return testAutomationResultE.updateSelf();
//    }
//
//    @Override
//    public void delete(TestAutomationResultE testAutomationResultE) {
//        testAutomationResultE.deleteSelf();
//    }
//}
