package io.choerodon.test.manager.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.vo.TestAutomationHistoryVO;

import java.util.Map;

public interface TestAutomationHistoryService {

    Page<TestAutomationHistoryVO> queryWithInstance(Map map, PageRequest pageRequest, Long projectId);

    String queryFrameworkByResultId(Long projectId, Long resultId);

    void shutdownInstance(Long instanceId,Long status);
}
