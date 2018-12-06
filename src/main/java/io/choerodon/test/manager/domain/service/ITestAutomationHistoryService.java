package io.choerodon.test.manager.domain.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestAutomationHistoryDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationHistoryE;

import java.util.List;
import java.util.Map;

public interface ITestAutomationHistoryService {

    List<TestAutomationHistoryE> query(TestAutomationHistoryE testAutomationHistory);

    TestAutomationHistoryE queryByPrimaryKey(Long historyId);

    TestAutomationHistoryE update(TestAutomationHistoryE testAutomationHistory);

    void delete(TestAutomationHistoryE testAutomationHistory);

    TestAutomationHistoryE insert(TestAutomationHistoryE testAutomationHistory);

    void shutdownInstance(Long instanceId,Long status);

    Page<TestAutomationHistoryDTO> queryWithInstance(Map map, PageRequest pageRequest);
}
