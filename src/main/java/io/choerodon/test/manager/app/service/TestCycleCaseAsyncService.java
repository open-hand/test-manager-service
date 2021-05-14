package io.choerodon.test.manager.app.service;

import java.util.List;

/**
 * @author chihao.ran@hand-china.com
 * 2021/05/14 17:14
 */
public interface TestCycleCaseAsyncService {
    /**
     * 异步删除
     *
     * @param cycleCaseIds 要删除的用例id
     * @param projectId    项目id
     */
    void asyncBatchDelete(List<Long> cycleCaseIds, Long projectId);
}
