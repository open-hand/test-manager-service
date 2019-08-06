package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.api.vo.TestAutomationResultVO;

public interface TestAutomationResultService {
    List<TestAutomationResultVO> query(TestAutomationResultVO testAutomationResultVO);

    TestAutomationResultVO changeAutomationResult(TestAutomationResultVO testAutomationResultVO, Long projectId);

    void removeAutomationResult(TestAutomationResultVO testAutomationResultVO);
}
