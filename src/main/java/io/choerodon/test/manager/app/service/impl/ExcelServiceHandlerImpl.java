package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.app.service.ExcelService;
import io.choerodon.test.manager.app.service.ExcelServiceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zongw.lee@gmail.com on 08/11/2018
 */
@Component
public class ExcelServiceHandlerImpl implements ExcelServiceHandler {

    @Autowired
    private ExcelService excelService;

    public void setExcelService(ExcelService excelService){
        this.excelService = excelService;
    }

    /**
     * 导出一个cycle下的测试详情，默认HSSFWorkBook
     *
     * @param cycleId
     * @param projectId
     */
    @Override
    public void exportCycleCaseInOneCycle(Long cycleId, Long projectId, HttpServletRequest request,
                                          HttpServletResponse response, Long organizationId) {
        excelService.exportCycleCaseInOneCycleByTransaction(cycleId, projectId, request, response, DetailsHelper.getUserDetails().getUserId(), organizationId);
    }

    /**
     * 导出项目下的所有用例，默认XSSF WorkBook
     *
     * @param projectId not null
     * @param request
     * @param response
     */
    @Override
    public void exportCaseByProject(Long projectId, HttpServletRequest request, HttpServletResponse response, Long organizationId) {
        excelService.exportCaseProjectByTransaction(projectId, request, response, DetailsHelper.getUserDetails().getUserId(), organizationId);
    }

    /**
     * 导出项目下的所有用例，默认XSSF WorkBook
     *
     * @param versionId not null
     * @param request
     * @param response
     */
    @Override
    public void exportCaseByVersion(Long projectId, Long versionId, HttpServletRequest request, HttpServletResponse response, Long organizationId) {
        excelService.exportCaseVersionByTransaction(projectId, versionId, request, response, DetailsHelper.getUserDetails().getUserId(), organizationId);
    }

    @Override
    public void exportCaseByFolder(Long projectId, Long folderId, HttpServletRequest request, HttpServletResponse response, Long organizationId) {
        excelService.exportCaseFolderByTransaction(projectId, folderId, request, response, DetailsHelper.getUserDetails().getUserId(), organizationId);
    }

    @Override
    public void exportFailCase(Long projectId, Long fileHistoryId) {
        excelService.exportFailCaseByTransaction(projectId, fileHistoryId, DetailsHelper.getUserDetails().getUserId());
    }
}
