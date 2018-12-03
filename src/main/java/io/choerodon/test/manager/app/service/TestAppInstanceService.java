package io.choerodon.test.manager.app.service;

import io.choerodon.asgard.api.dto.QuartzTask;
import io.choerodon.asgard.api.dto.ScheduleTaskDTO;
import io.choerodon.devops.api.dto.ReplaceResult;
import io.choerodon.test.manager.api.dto.ApplicationDeployDTO;
import io.choerodon.test.manager.api.dto.TestAppInstanceDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceE;

import java.util.List;
import java.util.Map;

/**
 * Created by zongw.lee@gmail.com on 22/11/2018
 */
public interface TestAppInstanceService {

    List<TestAppInstanceDTO> query(TestAppInstanceE instanceE);

    TestAppInstanceDTO create(ApplicationDeployDTO deployDTO,Long projectId);

    void createBySchedule(Map<String,Object> data);

    void closeInstance(String releaseNames,Long status,String logFile);

    QuartzTask createTimedTaskForDeploy(ScheduleTaskDTO taskDTO, Long projectId);

    ReplaceResult queryValues(Long projectId,Long appId, Long envId, Long appVersionId);

    void updateInstance(String releaseNames,String podName,String conName);

    void shutdownInstance(Long instanceId,Long status);
}
