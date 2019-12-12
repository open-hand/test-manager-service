package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.asgard.api.dto.QuartzTask;
import io.choerodon.asgard.api.dto.ScheduleMethodDTO;
import io.choerodon.asgard.api.dto.ScheduleTaskDTO;

/**
 * Created by zongw.lee@gmail.com on 23/11/2018
 */
public interface ScheduleService {

    QuartzTask create(long projectId, ScheduleTaskDTO dto);

    List<ScheduleMethodDTO> getMethodByService(long projectId, String service);
}
