package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

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
import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;

@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelImportServiceImpl.class);

    @Autowired
    private IExcelImportService iExcelImportService;

    @Autowired
    private ExcelService excelService;

    @Override
    public boolean cancelFileUpload(Long historyId) {
        return iExcelImportService.cancelFileUpload(historyId);
    }

    @Override
    public void downloadImportTemp(HttpServletResponse response) {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        excelService.downloadWorkBookByStream(iExcelImportService.buildImportTemp(), response);
    }

    @Async
    @Override
    public void importIssueByExcel(Long projectId, Long versionId, Long userId, Workbook issuesWorkbook) {
        TestIssueFolderE folderE = iExcelImportService.getFolder(projectId, versionId);
        TestFileLoadHistoryE loadHistoryE = iExcelImportService.initLoadHistory(projectId, folderE.getFolderId(), userId);
        TestFileLoadHistoryE.Status status = TestFileLoadHistoryE.Status.SUCCESS;

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

            if (status == TestFileLoadHistoryE.Status.CANCEL || iExcelImportService.isCanceled(loadHistoryE.getId())) {
                status = TestFileLoadHistoryE.Status.CANCEL;
                logger.info("已取消");
                iExcelImportService.removeRow(currentRow);
                continue;
            }

            if (iExcelImportService.isIssueHeaderRow(currentRow)) {
                issueDTO = iExcelImportService.processIssueHeaderRow(currentRow, projectId, versionId, folderE.getFolderId());
                if (issueDTO == null) {
                    failedCount++;
                } else {
                    successfulCount++;
                }
            }
            iExcelImportService.processRow(issueDTO, currentRow, errorRowIndexes);
            iExcelImportService.updateProgress(loadHistoryE, userId, ++progress / nonBlankRowCount);
        }

        loadHistoryE.setSuccessfulCount(successfulCount);
        loadHistoryE.setFailedCount(failedCount);

        if (!errorRowIndexes.isEmpty()) {
            logger.info("导入数据有误，上传 error workbook");
            iExcelImportService.shiftErrorRowsToTop(testCasesSheet, errorRowIndexes);
            if (iExcelImportService.uploadErrorWorkbook(issuesWorkbook, loadHistoryE) == null) {
                status = TestFileLoadHistoryE.Status.FAILURE;
            }
        }

        iExcelImportService.finishImport(loadHistoryE, userId, status);
    }

}
