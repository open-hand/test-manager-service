package io.choerodon.test.manager.app.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 842767365@qq.com on 8/9/18.
 */
public interface ExcelService {

    void exportCaseTemplate(Long projectId, HttpServletRequest request, HttpServletResponse response);

    void exportCaseFolderByTransaction(Long projectId, Long folderId, HttpServletRequest request, HttpServletResponse response, Long userId, Long organizationId);

    void exportCaseProjectByTransaction(Long projectId, HttpServletRequest request, HttpServletResponse response, Long userId, Long organizationId);

    void exportCycleCaseInOneCycleByTransaction(Long cycleId, Long projectId, HttpServletRequest request, HttpServletResponse response, Long userId, Long organizationId);

    void exportCaseVersionByTransaction(Long projectId, Long versionId, HttpServletRequest request, HttpServletResponse response, Long userId, Long organizationId);

    void exportFailCaseByTransaction(Long projectId, Long fileHistoryId,Long userId);
}
