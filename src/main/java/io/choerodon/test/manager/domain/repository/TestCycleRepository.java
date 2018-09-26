package io.choerodon.test.manager.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleRepository {
    TestCycleE insert(TestCycleE testCycleE);

    void delete(TestCycleE testCycleE);

    TestCycleE update(TestCycleE testCycleE);

    Page<TestCycleE> query(TestCycleE testCycleE, PageRequest pageRequest);

    List<TestCycleE> query(TestCycleE testCycleE);

	TestCycleE queryOne(TestCycleE testCycleE);

	List<TestCycleE> queryBar(Long[] versionId,Long assignedTo);

	List<Long> selectCyclesInVersions(Long[] versionIds);

	void validateCycle(TestCycleE testCycleE);

	List<TestCycleE> queryAll();

	List<TestCycleE> queryChildCycle(TestCycleE testCycleE);
}
