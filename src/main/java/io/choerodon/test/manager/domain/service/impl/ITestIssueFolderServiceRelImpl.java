//package io.choerodon.test.manager.domain.service.impl;
//
//import io.choerodon.test.manager.domain.service.ITestIssueFolderRelService;
//import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderRelE;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * Created by zongw.lee@gmail.com on 08/31/2018
// */
//@Component
//public class ITestIssueFolderServiceRelImpl implements ITestIssueFolderRelService {
//
//    @Override
//    public List<TestIssueFolderRelE> query(TestIssueFolderRelE testIssueFolderRelE) {
//        return testIssueFolderRelE.queryAllUnderProject();
//    }
//
//    @Override
//    public TestIssueFolderRelE queryOne(TestIssueFolderRelE testIssueFolderRelE) {
//        return testIssueFolderRelE.queryOneIssueUnderProjectVersionFolder();
//    }
//
//    @Override
//    public TestIssueFolderRelE insert(TestIssueFolderRelE testIssueFolderRelE) {
//        return testIssueFolderRelE.addSelf();
//    }
//
//    @Override
//    public void delete(TestIssueFolderRelE testIssueFolderRelE) {
//        testIssueFolderRelE.deleteSelf();
//    }
//
//    @Override
//    public TestIssueFolderRelE updateFolderByIssue(TestIssueFolderRelE testIssueFolderRelE) {
//        return testIssueFolderRelE.updateFolderByIssue();
//    }
//
//    @Override
//    public TestIssueFolderRelE updateVersionByFolderWithNoLock(TestIssueFolderRelE testIssueFolderRelE) {
//        return testIssueFolderRelE.updateVersionByFolderWithNoLock();
//    }
//
//}
