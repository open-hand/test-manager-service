package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface ITestCycleCaseHistoryService {
    TestCycleCaseHistoryE insert(TestCycleCaseHistoryE testCycleCaseHistoryE);

    void delete(List<TestCycleCaseHistoryE> testCycleCaseHistoryE);

    List<TestCycleCaseHistoryE> update(List<TestCycleCaseHistoryE> testCycleCaseHistoryE);

    Page<TestCycleCaseHistoryE> query(TestCycleCaseHistoryE testCycleCaseHistoryE, PageRequest pageRequest);
}
