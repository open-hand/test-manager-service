package io.choerodon.test.manager.api.controller.v1;

import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.vo.asgard.QuartzTask;
import io.choerodon.test.manager.api.vo.asgard.ScheduleTaskDTO;
import io.choerodon.test.manager.api.vo.devops.InstanceValueVO;
import io.choerodon.test.manager.app.service.TestAppInstanceService;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Created by zongw.lee@gmail.com on 23/11/2018.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/app_service_instances")
public class TestAppInstanceController {

    @Autowired
    TestAppInstanceService instanceService;

    /**
     * 查询value
     *
     * @param projectId 项目id
     * @param appId     应用id
     * @param envId     环境id
     * @param versionId 版本id
     * @return ReplaceResult
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询value列表")
    @GetMapping("/value")
    public ResponseEntity<InstanceValueVO> queryValues(@ApiParam(value = "项目id", required = true)
                                                       @PathVariable(value = "project_id") Long projectId,
                                                       @ApiParam(value = "应用id", required = true)
                                                       @RequestParam(value = "appId") @Encrypt Long appId,
                                                       @ApiParam(value = "环境id", required = true)
                                                       @RequestParam(value = "envId") @Encrypt Long envId,
                                                       @ApiParam(value = "版本id", required = true)
                                                       @RequestParam(value = "versionId") @Encrypt Long versionId) {
        return Optional.ofNullable(instanceService.queryValues(projectId, appId, envId, versionId))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.values.query"));
    }

    /**
     * 定时部署应用
     * hh
     *
     * @param projectId       项目id
     * @param scheduleTaskDTO 定时信息
     * @return QuartzTask
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/schedule")
    public ResponseEntity<QuartzTask> deployBySchedule(@ApiParam(value = "项目id", required = true)
                                                       @PathVariable(value = "project_id") Long projectId,
                                                       @ApiParam(value = "定时信息", required = true)
                                                       @RequestBody ScheduleTaskDTO scheduleTaskDTO) {
        return Optional.ofNullable(instanceService.createTimedTaskForDeploy(scheduleTaskDTO, projectId))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.application.deploy.schedule"));
    }
}
