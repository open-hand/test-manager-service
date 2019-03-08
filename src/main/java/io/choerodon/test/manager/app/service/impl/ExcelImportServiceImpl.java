package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.choerodon.test.manager.infra.common.utils.ExcelUtil;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.test.manager.app.service.ExcelImportService;
import io.choerodon.test.manager.app.service.ExcelService;
import io.choerodon.test.manager.domain.service.IExcelImportService;
import io.choerodon.test.manager.domain.service.impl.IExcelImportServiceImpl;
import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
import io.choerodon.test.manager.infra.feign.IssueFeignClient;

@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelImportServiceImpl.class);

    @Autowired
    private IExcelImportService iExcelImportService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private TestCaseServiceImpl testCaseService;

    public void setIssueFeignClient(IssueFeignClient issueFeignClient) {
        ((IExcelImportServiceImpl) iExcelImportService).setIssueFeignClient(issueFeignClient);
    }

    @Override
    public boolean cancelFileUpload(Long historyId) {
        return iExcelImportService.cancelFileUpload(historyId);
    }

    @Override
    public void downloadImportTemp(HttpServletRequest request, HttpServletResponse response) {
        ExcelUtil.setExcelHeaderByStream(request, response);
        excelService.downloadWorkBookByStream(iExcelImportService.buildImportTemp(), response);
    }

    @Async
    @Override
    public void importIssueByExcel(Long organizationId, Long projectId, Long versionId, Long userId, Workbook issuesWorkbook) {
        TestIssueFolderE folderE = iExcelImportService.getFolder(projectId, versionId, "导入");
        TestFileLoadHistoryE loadHistoryE = iExcelImportService.initLoadHistory(projectId, folderE.getFolderId(), userId);
        TestFileLoadHistoryE.Status status = TestFileLoadHistoryE.Status.SUCCESS;
        List<Long> issueIds = new ArrayList<>();

        Sheet testCasesSheet = issuesWorkbook.getSheet("测试用例");

        if (iExcelImportService.isEmptyTemp(testCasesSheet)) {
            logger.info("空模板");
            iExcelImportService.finishImport(loadHistoryE, userId, status);
            return;
        }

        Iterator<Row> rowIterator = iExcelImportService.rowIteratorSkipFirst(testCasesSheet);

        double nonBlankRowCount = (testCasesSheet.getPhysicalNumberOfRows() - 1) / 95.;
        double progress = 0.;
        long successfulCount = 0L;
        long failedCount = 0L;
        List<Integer> errorRowIndexes = new ArrayList<>();
        IssueDTO issueDTO = null;
        Row currentRow;
        logger.info("开始导入");
        while (rowIterator.hasNext()) {
            currentRow = rowIterator.next();

            if (iExcelImportService.isCanceled(loadHistoryE.getId())) {
                status = TestFileLoadHistoryE.Status.CANCEL;
                logger.info("已取消");
                iExcelImportService.removeRow(currentRow);
                if (!issueIds.isEmpty()) {
                    testCaseService.batchDeleteIssues(projectId, issueIds);
                }
                break;
            }

            if (iExcelImportService.isIssueHeaderRow(currentRow)) {
                issueDTO = iExcelImportService.processIssueHeaderRow(currentRow, organizationId, projectId, versionId, folderE.getFolderId());
                if (issueDTO == null) {
                    failedCount++;
                } else {
                    successfulCount++;
                    issueIds.add(issueDTO.getIssueId());
                }
            }
            iExcelImportService.processRow(issueDTO, currentRow, errorRowIndexes);
            iExcelImportService.updateProgress(loadHistoryE, userId, ++progress / nonBlankRowCount);
        }

        loadHistoryE.setSuccessfulCount(successfulCount);
        loadHistoryE.setFailedCount(failedCount);

        if (!errorRowIndexes.isEmpty() && status != TestFileLoadHistoryE.Status.CANCEL) {
            logger.info("导入数据有误，上传 error workbook");
            iExcelImportService.shiftErrorRowsToTop(testCasesSheet, errorRowIndexes);
            if (iExcelImportService.uploadErrorWorkbook(issuesWorkbook, loadHistoryE) == null) {
                status = TestFileLoadHistoryE.Status.FAILURE;
            }
        }

        iExcelImportService.finishImport(loadHistoryE, userId, status);
    }

}
