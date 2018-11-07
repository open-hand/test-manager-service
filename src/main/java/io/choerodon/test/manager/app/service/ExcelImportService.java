package io.choerodon.test.manager.app.service;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;

public interface ExcelImportService {

    void downloadImportTemp(HttpServletResponse response);

    void importIssueByExcel(Long projectId, Long versionId, Long userId, Workbook workbook);
}
