package io.choerodon.test.manager.app.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.context.request.RequestAttributes;

public interface ExcelImportService {

    boolean cancelFileUpload(Long historyId);

    void downloadImportTemp(HttpServletRequest request, HttpServletResponse response, Long organizationId, Long projectId);

    void importIssueByExcel(Long projectId, Long folderId, Long userId, Workbook workbook, RequestAttributes requestAttributes);

    Workbook buildImportTemp(Long organizationId, Long projectId);
}
