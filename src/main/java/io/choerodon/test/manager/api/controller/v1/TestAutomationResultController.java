package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.dto.TestAutomationResultDTO;
import io.choerodon.test.manager.app.service.TestAutomationResultService;

@RestController
@RequestMapping("/v1/projects/{project_id}/automation/result")
public class TestAutomationResultController {

    @Autowired
    private TestAutomationResultService testAutomationResultService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询")
    @GetMapping("/query/{id}")
    public ResponseEntity<List<TestAutomationResultDTO>> query(@PathVariable("project_id") Long projectId,
                                                               @PathVariable("id") Long id) {
        TestAutomationResultDTO testAutomationResultDTO = new TestAutomationResultDTO();
        testAutomationResultDTO.setId(id);
        return Optional.ofNullable(testAutomationResultService.query(testAutomationResultDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testAutomationResult.query.id"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("变动一个测试报告(增加|修改)")
    @PutMapping("/change")
    public ResponseEntity<TestAutomationResultDTO> changeOneAutomationResult(@PathVariable("project_id") Long projectId,
                                                                             @RequestBody TestAutomationResultDTO testAutomationResultDTO) {
        return Optional.ofNullable(testAutomationResultService.changeAutomationResult(testAutomationResultDTO, projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.automationResult.update"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除一个测试报告")
    @DeleteMapping("/remove")
    public ResponseEntity removeAutomationResult(@PathVariable("project_id") Long projectId,
                                                 @RequestBody TestAutomationResultDTO testAutomationResultDTO) {
        testAutomationResultService.removeAutomationResult(testAutomationResultDTO);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
