package io.choerodon.test.manager.app.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.SearchDTO;
import io.choerodon.test.manager.api.vo.TestIssuesUploadHistoryVO;
import io.choerodon.test.manager.api.vo.TestFileLoadHistoryVO;
import io.choerodon.test.manager.infra.dto.TestFileLoadHistoryDTO;
import org.springframework.data.domain.Pageable;

public interface TestFileLoadHistoryService {

    TestIssuesUploadHistoryVO queryLatestImportIssueHistory(Long projectId);

    List<TestFileLoadHistoryVO> queryIssues(Long projectId);

    List<TestFileLoadHistoryVO> queryCycles(Long projectId);

    TestFileLoadHistoryDTO queryLatestHistory(TestFileLoadHistoryDTO testFileLoadHistoryDTO);

    PageInfo<TestFileLoadHistoryDTO> basePageFileHistoryByOptions(Long projectId, Long folderId, SearchDTO searchDTO, Pageable pageable);

    PageInfo<TestFileLoadHistoryVO> pageFileHistoryByoptions(Long projectId, Long folderId, SearchDTO searchDTO, Pageable pageable);
}
