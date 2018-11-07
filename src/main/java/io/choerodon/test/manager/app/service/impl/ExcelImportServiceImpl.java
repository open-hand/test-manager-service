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

        Sheet testCasesSheet = issuesWorkbook.getSheet("测试用例");

        Iterator<Row> rowIterator = testCasesSheet.rowIterator();
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }
        if (!rowIterator.hasNext()) {
            logger.info("空模板");
            iExcelImportService.finishImport(loadHistoryE, userId, true);
            return;
        }

        double nonBlankRowCount = (testCasesSheet.getPhysicalNumberOfRows() - 1) / 95.;
        double progress = 0.;
        long successfulCount = 0L;
        long failedCount = 0L;
        List<Integer> errorRowIndexes = new ArrayList<>();
        IssueDTO issueDTO = null;
        Row currentRow;
        logger.info("开始导入");
        do {
            currentRow = rowIterator.next();
            if (iExcelImportService.isIssueHeaderRow(currentRow)) {
                issueDTO = iExcelImportService.processIssueHeaderRow(currentRow, projectId, versionId, folderE.getFolderId());
                if (issueDTO == null) {
                    failedCount++;
                } else {
                    successfulCount++;
                }
            }
            if (issueDTO == null) {
                errorRowIndexes.add(currentRow.getRowNum());
            } else {
                iExcelImportService.processRow(issueDTO, currentRow);
            }

            iExcelImportService.updateProgress(loadHistoryE, userId, ++progress / nonBlankRowCount);

        } while (rowIterator.hasNext());

        loadHistoryE.setSuccessfulCount(successfulCount);
        loadHistoryE.setFailedCount(failedCount);

        boolean isSuccessful = true;
        if (!errorRowIndexes.isEmpty()) {
            logger.info("导入数据有误，上传 error workbook");
            iExcelImportService.shiftErrorRowsToTop(testCasesSheet, errorRowIndexes);
            isSuccessful = iExcelImportService.uploadErrorWorkbook(issuesWorkbook, loadHistoryE) != null;
        }

        iExcelImportService.finishImport(loadHistoryE, userId, isSuccessful);
    }

}
