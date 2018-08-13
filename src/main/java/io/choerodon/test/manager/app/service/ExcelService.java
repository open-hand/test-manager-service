package io.choerodon.test.manager.app.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 842767365@qq.com on 8/9/18.
 */
public interface ExcelService {
	void exportCycleCaseInOneCycle(Long cycleId, Long projectId, HttpServletRequest request,
								   HttpServletResponse response);
}
