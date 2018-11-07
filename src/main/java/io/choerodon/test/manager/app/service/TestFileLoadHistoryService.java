package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.dto.TestFileLoadHistoryDTO;

import java.util.List;

public interface TestFileLoadHistoryService {

   List<TestFileLoadHistoryDTO> query(Long projectId);
}
