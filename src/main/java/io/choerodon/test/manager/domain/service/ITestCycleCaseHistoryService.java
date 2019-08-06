package io.choerodon.test.manager.domain.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface ITestCycleCaseHistoryService {
    TestCycleCaseHistoryE insert(TestCycleCaseHistoryE testCycleCaseHistoryE);

    PageInfo<TestCycleCaseHistoryE> query(TestCycleCaseHistoryE testCycleCaseHistoryE, PageRequest pageRequest);
}
