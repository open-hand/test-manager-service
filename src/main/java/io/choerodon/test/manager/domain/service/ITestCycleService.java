//package io.choerodon.test.manager.domain.service;
//
//import io.choerodon.test.manager.api.vo.BatchCloneCycleVO;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
//
//import java.util.List;
//
///**
// * Created by 842767365@qq.com on 6/11/18.
// */
//public interface ITestCycleService {
//    TestCycleE insert(Long projectId, TestCycleE testCycleE);
//
//    void delete(TestCycleE testCycleE, Long projectId);
//
//    TestCycleE update(Long projectId, TestCycleE testCycleE);
//
//    List<TestCycleE> queryChildCycle(TestCycleE testCycleE);
//
//    TestCycleE cloneFolder(TestCycleE protoTestCycleE, TestCycleE newTestCycleE, Long projectId);
//
//    TestCycleE cloneCycle(TestCycleE protoTestCycleE, TestCycleE newTestCycleE, Long projectId);
//
//    List<TestCycleE> queryCycleWithBar(Long projectId, Long[] versionId, Long assignedTo);
//
//    List<TestCycleE> queryCycleWithBarOneCycle(Long cycleId);
//
//    List<Long> selectCyclesInVersions(Long[] versionIds);
//
//    List<String> queryUpdateRank(TestCycleE testCycleE);
//
//    void insertCaseToFolder(Long issueFolderId, Long cycleId);
//
//    Boolean checkSameNameCycleForBatchClone(Long versionId, List<BatchCloneCycleVO> list);
//
//    void batchCloneCycleAndFolders(Long projectId, Long versionId, List<BatchCloneCycleVO> list, Long userId);
//}
