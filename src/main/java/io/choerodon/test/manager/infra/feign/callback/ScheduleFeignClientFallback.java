package io.choerodon.test.manager.infra.feign.callback;

import io.choerodon.asgard.api.dto.QuartzTask;
import io.choerodon.asgard.api.dto.ScheduleMethodDTO;
import io.choerodon.asgard.api.dto.ScheduleTaskDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.feign.ScheduleFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zongw.lee@gmail.com on 23/11/2018
 */
@Component
public class ScheduleFeignClientFallback implements ScheduleFeignClient {

    private static final String CREATE_ERROR = "error.ScheduleFeign.create";
    private static final String QUERY_ERROR = "error.ScheduleFeign.query";

    @Override
    public ResponseEntity<QuartzTask> create(long projectId, ScheduleTaskDTO dto) {
        throw new CommonException(CREATE_ERROR);
    }

    @Override
    public ResponseEntity<List<ScheduleMethodDTO>> getMethodByService(long projectId, String service) {
        throw new CommonException(QUERY_ERROR);
    }
}
