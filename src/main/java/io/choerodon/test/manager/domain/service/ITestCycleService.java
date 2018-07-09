package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface ITestCycleService {
    TestCycleE insert(TestCycleE testCycleE);

    void delete(TestCycleE testCycleE);

    List<TestCycleE> update(List<TestCycleE> testCycleE);

    Page<TestCycleE> query(TestCycleE testCycleE, PageRequest pageRequest);

    List<TestCycleE> querySubCycle(TestCycleE testCycleE);

	TestCycleE cloneFolder(TestCycleE protoTestCycleE, TestCycleE newTestCycleE, Long projectId);

	TestCycleE cloneCycle(TestCycleE protoTestCycleE, TestCycleE newTestCycleE, Long projectId);

//	List<TestCycleE> sort(List<TestCycle	E> testCycleES);

    List<TestCycleE> queryCycleWithBar(Long versionId);

	List<TestCycleE> filterCycleWithBar(String filter, Long[] versionIds);
	/**
	 * 查找Cycle存放默认路径
	 *
	 * @return
	 */
	Long findDefaultCycle(Long projectId);
}
