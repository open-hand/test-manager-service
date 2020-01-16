package io.choerodon.test.manager.app.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
