package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.dto.TestIssuesUploadHistoryDTO;
import io.choerodon.test.manager.api.dto.TestFileLoadHistoryDTO;

import java.util.List;

public interface TestFileLoadHistoryService {

    TestIssuesUploadHistoryDTO queryLatestImportIssueHistory(Long projectId);

    List<TestFileLoadHistoryDTO> queryIssues(Long projectId);

    List<TestFileLoadHistoryDTO> queryCycles(Long projectId);
}
