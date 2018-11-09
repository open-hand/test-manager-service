package io.choerodon.test.manager.app.service.impl;

import io.choerodon.agile.api.dto.ProductVersionDTO;
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
import io.choerodon.test.manager.infra.common.utils.SpringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
        historyDTOS.stream().filter(v -> v.getSourceType() == 2L).forEach(v ->
                v.setName(Optional.ofNullable(testCaseService.getVersionInfo(v.getProjectId()).get(v.getLinkedId())).map(ProductVersionDTO::getName).orElse("版本已被删除")));
        historyDTOS.stream().filter(v -> v.getSourceType() == 3L).forEach(v -> {
            cycleE.setCycleId(v.getLinkedId());
            v.setName(Optional.ofNullable(cycleE.queryOne()).map(TestCycleE::getCycleName).orElse("循环已被删除"));
        });
        historyDTOS.stream().filter(v -> v.getSourceType() == 4L).forEach(v ->
                v.setName(Optional.ofNullable(folderE.queryByPrimaryKey(v.getLinkedId())).map(TestIssueFolderE::getName).orElse("文件夹已被删除")));

        return historyDTOS;
    }

    @Override
    public TestFileLoadHistoryDTO queryLatestImportIssueHistory() {
        TestFileLoadHistoryE testFileLoadHistoryE = SpringUtil.getApplicationContext().getBean(TestFileLoadHistoryE.class);
        testFileLoadHistoryE.setCreatedBy(DetailsHelper.getUserDetails().getUserId());
        testFileLoadHistoryE = iTestFileLoadHistoryService.queryLatestImportIssueHistory(testFileLoadHistoryE);
        if (testFileLoadHistoryE == null) {
            return null;
        }

        return ConvertHelper.convert(testFileLoadHistoryE, TestFileLoadHistoryDTO.class);
    }

}
