package io.choerodon.test.manager.app.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ExcelServiceHandler {
    void setExcelService(ExcelService excelService);

    void exportCycleCaseInOneCycle(Long cycleId, Long projectId, HttpServletRequest request, HttpServletResponse response, Long organizationId);

    void exportCaseByFolder(Long projectId, Long folderId, HttpServletRequest request, HttpServletResponse response, Long organizationId);

    void exportFailCase(Long projectId, Long fileHistoryId);
}
