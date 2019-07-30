//package io.choerodon.test.manager.domain.repository;
//
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
//
//import java.util.List;
//
///**
// * Created by 842767365@qq.com on 6/11/18.
// */
//public interface TestCycleCaseDefectRelRepository {
//    TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE);
//
//    void delete(TestCycleCaseDefectRelE testCycleCaseDefectRelE);
//
//    List<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE);
//
//    List<TestCycleCaseDefectRelE> queryInIssues(Long[] issues,Long projectId);
//
//    List<Long> queryAllIssueIds();
//
//    Boolean updateProjectIdByIssueId(TestCycleCaseDefectRelE testCycleCaseDefectRelE);
//
//    List<Long> queryIssueIdAndDefectId(Long projectId);
//}
