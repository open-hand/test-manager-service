package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface ITestCycleService {
    TestCycleE insert(TestCycleE testCycleE);

	void delete(TestCycleE testCycleE, Long projectId);

	TestCycleE update(TestCycleE testCycleE);

	List<TestCycleE> queryChildCycle(TestCycleE testCycleE);

	TestCycleE cloneFolder(TestCycleE protoTestCycleE, TestCycleE newTestCycleE, Long projectId);

	TestCycleE cloneCycle(TestCycleE protoTestCycleE, TestCycleE newTestCycleE, Long projectId);

	List<TestCycleE> queryCycleWithBar(Long[] versionId,Long assignedTo);

	List<Long> selectCyclesInVersions(Long[] versionIds);

}
