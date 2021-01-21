package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.vo.agile.ProjectInfoVO;
import io.choerodon.test.manager.api.vo.event.ProjectEvent;

/**
 * @author: 25499
 * @date: 2019/11/22 9:32
 * @description:
 */
public interface TestProjectInfoService {

    void initializationProjectInfo(ProjectEvent projectEvent);

    ProjectInfoVO updateProjectInfo(Long projectId, ProjectInfoVO projectInfoVO);

    ProjectInfoVO queryProjectInfo(Long projectId);
}
