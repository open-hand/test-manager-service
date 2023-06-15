package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.vo.ExecutionCaseStatusChangeSettingVO;
import io.choerodon.test.manager.app.service.ExecutionCaseStatusChangeSettingService;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author zhaotianxin
 * @date 2021-05-11 10:51
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/execution_status_change_setting")
public class ExecutionCaseStatusChangeSettingController {

    @Autowired
    private ExecutionCaseStatusChangeSettingService executionCaseStatusChangeSettingService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<ExecutionCaseStatusChangeSettingVO> save(@ApiParam(value = "项目id", required = true)
                                                                   @PathVariable(value = "project_id") Long projectId,
                                                                   @ApiParam(value = "组织id", required = true)
                                                                   @RequestParam Long organizationId,
                                                                   @ApiParam(value = "状态更新vo", required = true)
                                                                   @RequestBody ExecutionCaseStatusChangeSettingVO executionCaseStatusChangeSettingVO) {
        executionCaseStatusChangeSettingService.save(projectId, organizationId, executionCaseStatusChangeSettingVO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    public ResponseEntity<List<ExecutionCaseStatusChangeSettingVO>> list(@ApiParam(value = "项目id", required = true)
                                                                         @PathVariable(value = "project_id") Long projectId,
                                                                         @ApiParam(value = "组织id", required = true)
                                                                         @RequestParam Long organizationId,
                                                                         @ApiParam(value = "工作项类型id", required = true)
                                                                         @RequestParam Long issueTypeId,
                                                                         @ApiParam(value = "状态id集合", required = true)
                                                                         @RequestBody List<Long> statusIds) {
        return Optional.ofNullable(executionCaseStatusChangeSettingService.listByIssueStatusIds(projectId, organizationId, issueTypeId, statusIds))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.execution.status.change.setting.list"));
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/query_option")
    public ResponseEntity<ExecutionCaseStatusChangeSettingVO> queryByOption(@ApiParam(value = "项目id", required = true)
                                                                            @PathVariable(value = "project_id") Long projectId,
                                                                            @ApiParam(value = "组织id", required = true)
                                                                            @RequestParam Long organizationId,
                                                                            @ApiParam(value = "工作项类型id", required = true)
                                                                            @RequestParam @Encrypt Long issueTypeId,
                                                                            @ApiParam(value = "状态id", required = true)
                                                                            @RequestParam @Encrypt Long statusId) {
        return new ResponseEntity<>(executionCaseStatusChangeSettingService.queryByOption(projectId, organizationId, issueTypeId, statusId), HttpStatus.OK);
    }
}
