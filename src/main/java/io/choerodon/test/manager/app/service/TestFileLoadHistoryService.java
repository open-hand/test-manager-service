package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.vo.agile.SearchDTO;
import io.choerodon.test.manager.api.vo.TestIssuesUploadHistoryVO;
import io.choerodon.test.manager.api.vo.TestFileLoadHistoryVO;
import io.choerodon.test.manager.infra.dto.TestFileLoadHistoryDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

public interface TestFileLoadHistoryService {

    TestIssuesUploadHistoryVO queryLatestImportIssueHistory(Long projectId);

    List<TestFileLoadHistoryVO> queryCycles(Long projectId);

    TestFileLoadHistoryDTO queryLatestHistory(TestFileLoadHistoryDTO testFileLoadHistoryDTO);

    Page<TestFileLoadHistoryVO> pageFileHistoryByoptions(Long projectId, SearchDTO searchDTO, PageRequest pageRequest);
}
