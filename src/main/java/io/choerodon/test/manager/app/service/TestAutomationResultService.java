package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.api.dto.TestAutomationResultDTO;

public interface TestAutomationResultService {
    List<TestAutomationResultDTO> query(TestAutomationResultDTO testAutomationResultDTO);

    TestAutomationResultDTO changeAutomationResult(TestAutomationResultDTO testAutomationResultDTO, Long projectId);

    void removeAutomationResult(TestAutomationResultDTO testAutomationResultDTO);
}
