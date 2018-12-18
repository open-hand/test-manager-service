package io.choerodon.test.manager.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestAutomationHistoryDTO;

import java.util.Map;

public interface TestAutomationHistoryService {

    Page<TestAutomationHistoryDTO> queryWithInstance(Map map, PageRequest pageRequest,Long projectId);
}
