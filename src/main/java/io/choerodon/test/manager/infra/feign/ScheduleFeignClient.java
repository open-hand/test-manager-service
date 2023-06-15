package io.choerodon.test.manager.infra.feign;


import javax.validation.Valid;
import java.util.List;

import io.choerodon.test.manager.api.vo.asgard.QuartzTask;
import io.choerodon.test.manager.api.vo.asgard.ScheduleMethodDTO;
import io.choerodon.test.manager.api.vo.asgard.ScheduleTaskDTO;
import io.choerodon.test.manager.infra.feign.callback.ScheduleFeignClientFallback;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @author zongw.lee@gmail.com
 * @since 2018/11/23
 */
@Component
@FeignClient(value = "zknow-asgard", fallbackFactory = ScheduleFeignClientFallback.class)
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

