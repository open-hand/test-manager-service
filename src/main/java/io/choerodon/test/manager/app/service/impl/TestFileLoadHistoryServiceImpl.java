package io.choerodon.test.manager.app.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.test.manager.api.vo.agile.SearchDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.app.service.TestIssueFolderService;
import io.choerodon.test.manager.infra.util.ConvertUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.vo.TestIssuesUploadHistoryVO;
import io.choerodon.test.manager.api.vo.TestFileLoadHistoryVO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestFileLoadHistoryService;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;
import io.choerodon.test.manager.infra.dto.TestFileLoadHistoryDTO;
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.infra.enums.TestFileLoadHistoryEnums;
import io.choerodon.test.manager.infra.mapper.TestCycleMapper;
import io.choerodon.test.manager.infra.mapper.TestFileLoadHistoryMapper;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class TestFileLoadHistoryServiceImpl implements TestFileLoadHistoryService {

    private TestCaseService testCaseService;
    private TestFileLoadHistoryMapper testFileLoadHistoryMapper;
    private TestIssueFolderMapper testIssueFolderMapper;
    private TestCycleMapper cycleMapper;
    private ModelMapper modelMapper;

    public TestFileLoadHistoryServiceImpl(TestCaseService testCaseService,
                                          TestFileLoadHistoryMapper testFileLoadHistoryMapper,
                                          TestIssueFolderMapper testIssueFolderMapper,
                                          TestCycleMapper cycleMapper,
                                          ModelMapper modelMapper) {
        this.testCaseService = testCaseService;
        this.testFileLoadHistoryMapper = testFileLoadHistoryMapper;
        this.testIssueFolderMapper = testIssueFolderMapper;
        this.cycleMapper = cycleMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TestFileLoadHistoryVO> queryCycles(Long projectId) {
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        TestFileLoadHistoryVO testFileLoadHistoryVO = new TestFileLoadHistoryVO();
        testFileLoadHistoryVO.setCreatedBy(DetailsHelper.getUserDetails().getUserId());
        testFileLoadHistoryVO.setProjectId(projectId);
        testFileLoadHistoryVO.setSourceType(3L);

        List<TestFileLoadHistoryVO> historyDTOS = modelMapper.map(queryDownloadFileByParameter(modelMapper
                .map(testFileLoadHistoryVO, TestFileLoadHistoryDTO.class)), new TypeToken<List<TestFileLoadHistoryVO>>() {
        }.getType());

        historyDTOS.stream().forEach(v -> {
            testCycleDTO.setCycleId(v.getLinkedId());
            v.setName(Optional.ofNullable(cycleMapper.selectOne(testCycleDTO)).map(TestCycleDTO::getCycleName).orElse("循环已被删除"));
        });
        return historyDTOS;
    }

    @Override
    public TestIssuesUploadHistoryVO queryLatestImportIssueHistory(Long projectId) {
        TestFileLoadHistoryDTO testFileLoadHistoryDTO = new TestFileLoadHistoryDTO();

        testFileLoadHistoryDTO.setProjectId(projectId);
        testFileLoadHistoryDTO.setCreatedBy(DetailsHelper.getUserDetails().getUserId());
        testFileLoadHistoryDTO.setActionType(TestFileLoadHistoryEnums.Action.UPLOAD_CASE.getTypeValue());
        testFileLoadHistoryDTO.setStatus(TestFileLoadHistoryEnums.Status.SUCCESS.getTypeValue());
        testFileLoadHistoryDTO = queryLatestHistory(testFileLoadHistoryDTO);
        if (testFileLoadHistoryDTO == null) {
            return null;
        }

        TestIssuesUploadHistoryVO testIssuesUploadHistoryVO = modelMapper.map(testFileLoadHistoryDTO, TestIssuesUploadHistoryVO.class);

        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        testIssueFolderDTO.setFolderId(testFileLoadHistoryDTO.getLinkedId());
        testIssueFolderDTO = testIssueFolderMapper.selectByPrimaryKey(testFileLoadHistoryDTO.getLinkedId());

        if (!ObjectUtils.isEmpty(testIssueFolderDTO)) {
            testIssuesUploadHistoryVO.setVersionName(testCaseService.getVersionInfo(projectId)
                    .get(testIssueFolderDTO.getVersionId()).getName());
        }

        return testIssuesUploadHistoryVO;
    }

    private List<TestFileLoadHistoryDTO> queryDownloadFileByParameter(TestFileLoadHistoryDTO testFileLoadHistoryE) {
        List<TestFileLoadHistoryDTO> res = testFileLoadHistoryMapper.select(testFileLoadHistoryE);
        Collections.sort(res, Comparator.comparing(TestFileLoadHistoryDTO::getCreationDate));
        return res;
    }

    @Override
    public TestFileLoadHistoryDTO queryLatestHistory(TestFileLoadHistoryDTO testFileLoadHistoryDTO) {
        List<TestFileLoadHistoryDTO> testFileLoadHistoryDTOS = testFileLoadHistoryMapper.queryLatestHistory(testFileLoadHistoryDTO);
        if (testFileLoadHistoryDTOS == null || testFileLoadHistoryDTOS.isEmpty()) {
            return null;
        }
        return modelMapper.map(testFileLoadHistoryDTOS.get(0), TestFileLoadHistoryDTO.class);
    }

    @Override
    public PageInfo<TestFileLoadHistoryVO> pageFileHistoryByoptions(Long projectId, SearchDTO searchDTO, Pageable pageable) {
        return ConvertUtils.convertPage(listExportHistory(projectId,TestFileLoadHistoryEnums.Action.DOWNLOAD_CASE.getTypeValue(),searchDTO,pageable), TestFileLoadHistoryVO.class);
    }

    private PageInfo<TestFileLoadHistoryDTO> listExportHistory(Long projectId, Long actionType,SearchDTO searchDTO, Pageable pageable) {
           return  PageHelper.startPage(pageable.getPageNumber(), pageable.getPageSize())
                   .doSelectPageInfo(() -> testFileLoadHistoryMapper.listExportHistory(projectId,actionType, searchDTO.getAdvancedSearchArgs()));
    }
}
