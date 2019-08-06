//package io.choerodon.test.manager.domain.service.impl;
//
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
//import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
//import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
///**
// * Created by 842767365@qq.com on 6/11/18.
// */
//@Component
//public class ITestCycleCaseDefectRelServiceImpl implements ITestCycleCaseDefectRelService {
//
//    @Autowired
//    TestCaseFeignClient testCaseFeignClient;
//
//
//    @Override
//    public TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
//        return testCycleCaseDefectRelE.addSelf();
//    }
//
//    @Override
//    public void delete(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
//        testCycleCaseDefectRelE.deleteSelf();
//    }
//
//    @Override
//    public Boolean updateProjectIdByIssueId(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
//        return testCycleCaseDefectRelE.updateProjectIdByIssueId();
//
//    }
//
//}
