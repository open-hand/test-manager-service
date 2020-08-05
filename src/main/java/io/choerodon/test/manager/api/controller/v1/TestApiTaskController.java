package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.test.manager.app.service.TestApiTaskService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author lihao
 */

@RestController
@RequestMapping("/v1/projects/{project_id}/api_test/tasks")
public class TestApiTaskController {

    @Autowired
    private TestApiTaskService testApiTaskService;

    /**
     * 执行指定任务
     *
     * @param projectId 项目id
     * @param taskId    任务id
     */
    @PostMapping
    public void executeTask(
            @ApiParam(value = "项目id")
            @PathVariable("project_id") Long projectId,
            @ApiParam(value = "任务id")
            @RequestParam("task_id") Long taskId) {
        testApiTaskService.executeTask(projectId, taskId);
    }
}
