//package io.choerodon.test.manager.domain.repository;
//
//import com.github.pagehelper.PageInfo;
//import io.choerodon.base.domain.PageRequest;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
//
//import java.util.List;
//
///**
// * Created by 842767365@qq.com on 6/11/18.
// */
//public interface TestCycleRepository {
//    TestCycleE insert(TestCycleE testCycleE);
//
//    void delete(TestCycleE testCycleE);
//
//    TestCycleE update(TestCycleE testCycleE);
//
//    PageInfo<TestCycleE> query(TestCycleE testCycleE, PageRequest pageRequest);
//
//    List<TestCycleE> query(TestCycleE testCycleE);
//
//    TestCycleE queryOne(TestCycleE testCycleE);
//
//    List<TestCycleE> queryBar(Long projectId, Long[] versionId, Long assignedTo);
//
//    List<TestCycleE> queryBarOneCycle(Long cycleId);
//
//    List<Long> selectCyclesInVersions(Long[] versionIds);
//
//    void validateCycle(TestCycleE testCycleE);
//
//    List<TestCycleE> queryAll();
//
//    List<TestCycleE> queryChildCycle(TestCycleE testCycleE);
//
//    List<TestCycleE> queryCycleInVersion(TestCycleE testCycleE);
//
//    List<String> queryUpdateRank(TestCycleE testCycleE);
//
//    String getLastedRank(TestCycleE testCycleE);
//
//    Long getCount(TestCycleE testCycleE);
//
//    List<TestCycleE> queryChildFolderByRank(TestCycleE testCycleE);
//}
