//package io.choerodon.test.manager.domain.service;
//
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
//import com.github.pagehelper.PageInfo;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//
///**
// * Created by 842767365@qq.com on 6/11/18.
// */
//public interface ITestCycleCaseService {
//
//    /**
//     * 创建一个测试例
//     *
//     * @param testCycleCaseE
//     * @return
//     */
//	TestCycleCaseE runTestCycleCase(TestCycleCaseE testCycleCaseE, Long projectId);
//
//	void delete(TestCycleCaseE testCycleCaseE);
//
//
//    PageInfo<TestCycleCaseE> query(TestCycleCaseE testCycleCaseES, Pageable pageable);
//
//	PageInfo<TestCycleCaseE> queryByFatherCycle(List<TestCycleCaseE> testCycleCaseES, Pageable pageable);
//
//	TestCycleCaseE cloneCycleCase(TestCycleCaseE testCycleCaseE, Long projectId);
//
//    List<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE);
//
//    TestCycleCaseE queryOne(TestCycleCaseE testCycleCaseE);
//
//	TestCycleCaseE changeStep( Long projectId, TestCycleCaseE currentStepE);
//
//	List<Long> getActiveCase(Long range, Long projectId, String day);
//
//	List<TestCycleCaseE> queryByIssue(Long versionId);
//
//	List<TestCycleCaseE> queryInIssues(Long[] issuesIds);
//
//	List<TestCycleCaseE> queryCaseAllInfoInCyclesOrVersions(Long[] cycleIds, Long[] versionIds);
//
//	Long countCaseNotRun(Long projectId);
//
//	Long countCaseNotPlain(Long projectId);
//
//	Long countCaseSum(Long projectId);
//}
