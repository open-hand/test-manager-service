package io.choerodon.test.manager.app.service;

import io.choerodon.asgard.api.dto.QuartzTask;
import io.choerodon.asgard.api.dto.ScheduleTaskDTO;
import io.choerodon.devops.api.dto.ReplaceResult;
import io.choerodon.test.manager.api.dto.ApplicationDeployDTO;
import io.choerodon.test.manager.api.dto.TestAppInstanceDTO;

import java.util.Map;

import org.springframework.security.core.Authentication;

/**
 * Created by zongw.lee@gmail.com on 22/11/2018
 */
public interface TestAppInstanceService {

    TestAppInstanceDTO create(ApplicationDeployDTO deployDTO, Long projectId, Long userId);

    void createBySchedule(Map<String, Object> data);

    QuartzTask createTimedTaskForDeploy(ScheduleTaskDTO taskDTO, Long projectId);

    ReplaceResult queryValues(Long projectId, Long appId, Long envId, Long appVersionId);

    void updateInstance(String releaseNames, String podName, String conName);

    void updateLog(String releaseNames, String logFile);

    void updateStatus(Long instanceId, Long status);
}
