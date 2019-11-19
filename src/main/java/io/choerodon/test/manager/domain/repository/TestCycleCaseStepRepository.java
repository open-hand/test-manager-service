//package io.choerodon.test.manager.domain.repository;
//
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
//import com.github.pagehelper.PageInfo;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//
///**
// * Created by 842767365@qq.com on 6/11/18.
// */
//public interface TestCycleCaseStepRepository {
//    TestCycleCaseStepE insert(TestCycleCaseStepE testCycleCaseStepE);
//
//    void delete(TestCycleCaseStepE testCycleCaseStepE);
//
//    TestCycleCaseStepE update(TestCycleCaseStepE testCycleCaseStepE);
//
//    PageInfo<TestCycleCaseStepE> query(TestCycleCaseStepE testCycleCaseStepE, Pageable pageable);
//
//	List<TestCycleCaseStepE> query(TestCycleCaseStepE testCycleCaseStepE);
//    List<TestCycleCaseStepE> queryCycleCaseForReporter(Long[] ids);
//
//    List<TestCycleCaseStepE> batchInsert(List<TestCycleCaseStepE> testCycleCaseSteps);
//}
