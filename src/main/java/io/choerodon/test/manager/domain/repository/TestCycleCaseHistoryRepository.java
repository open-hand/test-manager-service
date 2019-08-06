package io.choerodon.test.manager.domain.repository;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseHistoryRepository {
    TestCycleCaseHistoryE insert(TestCycleCaseHistoryE testCycleCaseHistoryE);

    PageInfo<TestCycleCaseHistoryE> query(TestCycleCaseHistoryE testCycleCaseHistoryE, PageRequest pageRequest);

}
