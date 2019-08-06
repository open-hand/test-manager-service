//package io.choerodon.test.manager.domain.service.impl;
//
//import com.github.pagehelper.PageInfo;
//import io.choerodon.base.domain.PageRequest;
//import io.choerodon.test.manager.domain.service.ITestCycleCaseHistoryService;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE;
//import org.springframework.stereotype.Component;
//
///**
// * Created by 842767365@qq.com on 6/11/18.
// */
//@Component
//public class ITestCycleCaseHistoryServiceImpl implements ITestCycleCaseHistoryService {
//
//    @Override
//    public TestCycleCaseHistoryE insert(TestCycleCaseHistoryE testCycleCaseHistoryE) {
//        return testCycleCaseHistoryE.addSelf();
//    }
//
//
//    @Override
//    public PageInfo<TestCycleCaseHistoryE> query(TestCycleCaseHistoryE testCycleCaseHistoryE, PageRequest pageRequest) {
//        return testCycleCaseHistoryE.querySelf(pageRequest);
//    }
//}
