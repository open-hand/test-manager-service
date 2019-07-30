package io.choerodon.test.manager.app.service;

import java.util.Map;

import io.choerodon.asgard.api.dto.QuartzTask;
import io.choerodon.asgard.api.dto.ScheduleTaskDTO;
import io.choerodon.devops.api.dto.ReplaceResult;
import io.choerodon.test.manager.api.vo.ApplicationDeployVO;
import io.choerodon.test.manager.api.vo.TestAppInstanceVO;

/**
 * Created by zongw.lee@gmail.com on 22/11/2018
 */
public interface TestAppInstanceService {

    TestAppInstanceVO create(ApplicationDeployVO deployDTO, Long projectId, Long userId);

    void createBySchedule(Map<String, Object> data);

    QuartzTask createTimedTaskForDeploy(ScheduleTaskDTO taskDTO, Long projectId);

    ReplaceResult queryValues(Long projectId, Long appId, Long envId, Long appVersionId);

    void updateInstance(String releaseNames, String podName, String conName);

    void updateLog(String releaseNames, String logFile);

    void updateStatus(Long instanceId, Long status);
}
