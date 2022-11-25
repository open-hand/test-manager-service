package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.vo.asgard.QuartzTask;
import io.choerodon.test.manager.api.vo.asgard.ScheduleTaskDTO;
import io.choerodon.test.manager.api.vo.devops.InstanceValueVO;

/**
 * Created by zongw.lee@gmail.com on 22/11/2018
 */
public interface TestAppInstanceService {

    QuartzTask createTimedTaskForDeploy(ScheduleTaskDTO taskDTO, Long projectId);

    InstanceValueVO queryValues(Long projectId, Long appId, Long envId, Long appVersionId);

    void updateInstance(String releaseNames, String podName, String conName);

    void updateLog(String releaseNames, String logFile);

    void updateStatus(Long instanceId, Long status);
}
