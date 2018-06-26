package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface ITestCycleService {
    TestCycleE insert(TestCycleE testCycleE);

    void delete(TestCycleE testCycleE);

    List<TestCycleE> update(List<TestCycleE> testCycleE);

    Page<TestCycleE> query(TestCycleE testCycleE, PageRequest pageRequest);

    List<TestCycleE> querySubCycle(TestCycleE testCycleE);

    List<TestCycleE> getTestCycle(Long versionId);

    List<TestCycleE> queryCycleWithBar(Long versionId);
}
