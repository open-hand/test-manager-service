package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.api.vo.TestIssuesUploadHistoryVO;
import io.choerodon.test.manager.api.vo.TestFileLoadHistoryVO;
import io.choerodon.test.manager.infra.dto.TestFileLoadHistoryDTO;

public interface TestFileLoadHistoryService {

    TestIssuesUploadHistoryVO queryLatestImportIssueHistory(Long projectId);

    List<TestFileLoadHistoryVO> queryIssues(Long projectId);

    List<TestFileLoadHistoryVO> queryCycles(Long projectId);

    TestFileLoadHistoryDTO queryLatestHistory(TestFileLoadHistoryDTO testFileLoadHistoryDTO);
}
