package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.asgard.api.dto.QuartzTask;
import io.choerodon.asgard.api.dto.ScheduleTaskDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.devops.api.dto.ReplaceResult;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.dto.ApplicationDeployDTO;
import io.choerodon.test.manager.api.dto.TestAppInstanceDTO;
import io.choerodon.test.manager.app.service.TestAppInstanceService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by zongw.lee@gmail.com on 23/11/2018.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/app_instances")
public class TestAppInstanceController {

    @Autowired
    TestAppInstanceService instanceService;

    /**
     * 查询value
     *
     * @param projectId    项目id
     * @param appId        应用id
     * @param envId        环境id
     * @param appVersionId 版本id
     * @return ReplaceResult
     */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER,
            InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "查询value列表")
    @GetMapping("/value")
    public ResponseEntity<ReplaceResult> queryValues(
            @PathVariable(value = "project_id") Long projectId,
            @RequestParam(value = "appId") Long appId,
            @RequestParam(value = "envId") Long envId,
            @RequestParam(value = "appVersionId") Long appVersionId) {
        return Optional.ofNullable(instanceService.queryValues(projectId, appId, envId, appVersionId))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.values.query"));
    }

    /**
     * 立刻部署应用
     *
     * @param projectId            项目id
     * @param applicationDeployDTO 部署信息
     * @return ApplicationInstanceDTO
     */
    @Permission(level = ResourceLevel.PROJECT,
            roles = {InitRoleCode.PROJECT_OWNER,
                    InitRoleCode.PROJECT_MEMBER})
    @PostMapping
    public ResponseEntity<TestAppInstanceDTO> deploy(
            @PathVariable(value = "project_id") Long projectId,
            @RequestBody ApplicationDeployDTO applicationDeployDTO) {
        return Optional.ofNullable(instanceService.create(applicationDeployDTO, projectId, DetailsHelper.getUserDetails().getUserId()))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.application.deploy.immediate"));
    }

    /**
     * 定时部署应用
     *
     * @param projectId       项目id
     * @param scheduleTaskDTO 定时信息
     * @return QuartzTask
     */
    @Permission(level = ResourceLevel.PROJECT,
            roles = {InitRoleCode.PROJECT_OWNER,
                    InitRoleCode.PROJECT_MEMBER})
    @PostMapping("/schedule")
    public ResponseEntity<QuartzTask> deployBySchedule(
            @PathVariable(value = "project_id") Long projectId,
            @RequestBody ScheduleTaskDTO scheduleTaskDTO) {
        return Optional.ofNullable(instanceService.createTimedTaskForDeploy(scheduleTaskDTO, projectId))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.application.deploy.schedule"));
    }
}
