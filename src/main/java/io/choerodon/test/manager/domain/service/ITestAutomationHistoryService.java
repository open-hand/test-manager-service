package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationHistoryE;

import java.util.List;

public interface ITestAutomationHistoryService {

    List<TestAutomationHistoryE> query(TestAutomationHistoryE testAutomationHistory);

    TestAutomationHistoryE queryByPrimaryKey(Long historyId);

    TestAutomationHistoryE update(TestAutomationHistoryE testAutomationHistory);

    void delete(TestAutomationHistoryE testAutomationHistory);

    TestAutomationHistoryE insert(TestAutomationHistoryE testAutomationHistory);

    void shutdownInstance(Long instanceId,Long status);
}
