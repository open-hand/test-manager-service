package io.choerodon.test.manager.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestAutomationHistoryDTO;

import java.util.Map;

public interface TestAutomationHistoryService {

    PageInfo<TestAutomationHistoryDTO> queryWithInstance(Map map, PageRequest pageRequest, Long projectId);

    String queryFrameworkByResultId(Long projectId, Long resultId);
}
