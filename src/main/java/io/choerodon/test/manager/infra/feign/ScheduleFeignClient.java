package io.choerodon.test.manager.infra.feign;


import io.choerodon.asgard.api.dto.QuartzTask;
import io.choerodon.asgard.api.dto.ScheduleMethodDTO;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.asgard.api.dto.ScheduleTaskDTO;
import io.choerodon.test.manager.infra.feign.callback.ScheduleFeignClientFallback;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zongw.lee@gmail.com
 * @since 2018/11/23
 */
@Component
@FeignClient(value = "asgard-service", fallback = ScheduleFeignClientFallback.class)
public interface ScheduleFeignClient {
    @ApiOperation(value = "项目层创建定时任务")
    @PostMapping(value = "/v1/schedules/projects/{project_id}/tasks")
    ResponseEntity<QuartzTask> create(@PathVariable("project_id") long projectId,
                                      @RequestBody @Valid ScheduleTaskDTO dto);

    @ApiOperation(value = "项目层根据服务名获取方法")
    @GetMapping("/v1/schedules/projects/{project_id}/methods/service")
    ResponseEntity<List<ScheduleMethodDTO>> getMethodByService(@PathVariable("project_id") long projectId,
                                                               @RequestParam(name = "service") String service);
    }

