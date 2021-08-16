package io.choerodon.test.manager.app.service;

import java.util.List;

/**
 * @author huaxin.deng@hand-china.com 2021-08-16 13:39:05
 */

public interface TestCaseAsyncService {

    void asyncBatchDeleteCase(Long projectId, List<Long> caseIds);

}
