package io.choerodon.test.manager.app.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.app.service.ExcelService;
import io.choerodon.test.manager.app.service.ExcelServiceHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zongw.lee@gmail.com on 08/11/2018
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ExcelServiceHandlerImpl implements ExcelServiceHandler {

    @Autowired
    private ExcelService excelService;

    public void setExcelService(ExcelService excelService) {
        this.excelService = excelService;
    }

    /**
     * 导出一个cycle下的测试详情
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
     * 导出项目下的所有用例
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
     * 导出版本下的所有用例
     *
     * @param versionId not null
     * @param request
     * @param response
     */
    @Override
    public void exportCaseByVersion(Long projectId, Long versionId, HttpServletRequest request, HttpServletResponse response, Long organizationId) {
        excelService.exportCaseVersionByTransaction(projectId, versionId, request, response, DetailsHelper.getUserDetails().getUserId(), organizationId);
    }

    /**
     * 导出文件夹下的所有用例
     *
     * @param projectId
     * @param folderId
     * @param request
     * @param response
     * @param organizationId
     */
    @Override
    public void exportCaseByFolder(Long projectId, Long folderId, HttpServletRequest request, HttpServletResponse response, Long organizationId) {
        excelService.exportCaseFolderByTransaction(projectId, folderId, request, response, DetailsHelper.getUserDetails().getUserId(),false,null);
    }

    /**
     * 导出失败重试
     *
     * @param projectId
     * @param fileHistoryId
     */
    @Override
    public void exportFailCase(Long projectId, Long fileHistoryId) {
        excelService.exportFailCaseByTransaction(projectId, fileHistoryId, DetailsHelper.getUserDetails().getUserId());
    }
}
