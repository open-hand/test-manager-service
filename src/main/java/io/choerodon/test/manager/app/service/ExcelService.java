package io.choerodon.test.manager.app.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 842767365@qq.com on 8/9/18.
 */
public interface ExcelService {
	void exportCycleCaseInOneCycle(Long cycleId, Long projectId, HttpServletRequest request,
								   HttpServletResponse response);

	void exportCaseByProject(Long projectId, HttpServletRequest request,
								   HttpServletResponse response);

	void exportCaseByVersion(Long projectId,Long versionId, HttpServletRequest request, HttpServletResponse response);

	void exportCaseByFolder(Long projectId, Long folderId, HttpServletRequest request, HttpServletResponse response);

	void exportCaseTemplate(Long projectId, HttpServletRequest request, HttpServletResponse response);
}
