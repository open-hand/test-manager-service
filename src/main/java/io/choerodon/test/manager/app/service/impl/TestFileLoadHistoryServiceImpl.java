package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.dto.TestFileLoadHistoryDTO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestFileLoadHistoryService;
import io.choerodon.test.manager.domain.service.ITestFileLoadHistoryService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestIssueFolderEFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestFileLoadHistoryServiceImpl implements TestFileLoadHistoryService {

    @Autowired
    ITestFileLoadHistoryService iTestFileLoadHistoryService;

    @Autowired
    TestCaseService testCaseService;

    @Override
    public List<TestFileLoadHistoryDTO> query(Long projectId) {
        TestCycleE cycleE = TestCycleEFactory.create();
        TestIssueFolderE folderE = TestIssueFolderEFactory.create();

        TestFileLoadHistoryDTO testFileLoadHistoryDTO = new TestFileLoadHistoryDTO();
        testFileLoadHistoryDTO.setCreatedBy(DetailsHelper.getUserDetails().getUserId());
        testFileLoadHistoryDTO.setProjectId(projectId);

        List<TestFileLoadHistoryDTO> historyDTOS = ConvertHelper.convertList(iTestFileLoadHistoryService.queryDownloadFile(ConvertHelper.convert(testFileLoadHistoryDTO, TestFileLoadHistoryE.class)), TestFileLoadHistoryDTO.class);

        historyDTOS.stream().filter(v -> v.getSourceType() == 1L).forEach(v -> v.setName(testCaseService.getProjectInfo(v.getLinkedId()).getName()));
        historyDTOS.stream().filter(v -> v.getSourceType() == 2L).forEach(v -> v.setName(testCaseService.getVersionInfo(v.getProjectId()).get(v.getLinkedId()).getName()));
        historyDTOS.stream().filter(v -> v.getSourceType() == 3L).forEach(v -> {
            cycleE.setCycleId(v.getLinkedId());
            v.setName(cycleE.queryOne().getCycleName());
        });
        historyDTOS.stream().filter(v -> v.getSourceType() == 4L).forEach(v -> v.setName(folderE.queryByPrimaryKey(v.getLinkedId()).getName()));

        return historyDTOS;
    }

}
