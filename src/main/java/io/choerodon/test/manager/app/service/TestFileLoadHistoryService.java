package io.choerodon.test.manager.app.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import io.choerodon.test.manager.api.vo.agile.SearchDTO;
import io.choerodon.test.manager.api.vo.TestIssuesUploadHistoryVO;
import io.choerodon.test.manager.api.vo.TestFileLoadHistoryVO;
import io.choerodon.test.manager.infra.dto.TestFileLoadHistoryDTO;
import org.springframework.data.domain.Pageable;

public interface TestFileLoadHistoryService {

    TestIssuesUploadHistoryVO queryLatestImportIssueHistory(Long projectId);

    List<TestFileLoadHistoryVO> queryCycles(Long projectId);

    TestFileLoadHistoryDTO queryLatestHistory(TestFileLoadHistoryDTO testFileLoadHistoryDTO);

    PageInfo<TestFileLoadHistoryVO> pageFileHistoryByoptions(Long projectId, SearchDTO searchDTO, Pageable pageable);
}
