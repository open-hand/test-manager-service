package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.api.vo.asgard.QuartzTask;
import io.choerodon.test.manager.api.vo.asgard.ScheduleMethodDTO;
import io.choerodon.test.manager.api.vo.asgard.ScheduleTaskDTO;

/**
 * Created by zongw.lee@gmail.com on 23/11/2018
 */
public interface ScheduleService {

    QuartzTask create(long projectId, ScheduleTaskDTO dto);

    List<ScheduleMethodDTO> getMethodByService(long projectId, String service);
}
