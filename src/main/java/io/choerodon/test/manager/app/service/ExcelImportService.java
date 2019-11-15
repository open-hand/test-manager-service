package io.choerodon.test.manager.app.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;

public interface ExcelImportService {

    boolean cancelFileUpload(Long historyId);

    void downloadImportTemp(HttpServletRequest request, HttpServletResponse response, Long organizationId, Long projectId);

    void importIssueByExcel(Long organizationId, Long projectId, Long folderId, Long versionId, Long userId, Workbook workbook);

    Workbook buildImportTemp(Long organizationId, Long projectId);
}
