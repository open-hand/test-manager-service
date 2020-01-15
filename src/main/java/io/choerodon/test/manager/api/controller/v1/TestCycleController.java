package io.choerodon.test.manager.api.controller.v1;

import java.util.Optional;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.api.vo.TestCycleVO;
import io.choerodon.test.manager.api.vo.TestTreeIssueFolderVO;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.app.service.TestPlanServcie;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by 842767365@qq.com on 6/12/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/cycle")
public class TestCycleController {
    @Autowired
    TestCycleService testCycleService;

    @Autowired
    TestPlanServcie testPlanServcie;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("增加计划文件夹")
    @PostMapping
    public ResponseEntity<TestCycleVO> insert(@PathVariable(name = "project_id") Long projectId,
                                              @RequestBody TestCycleVO testCycleVO) {
        return Optional.ofNullable(testCycleService.insert(projectId, testCycleVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycle.insert"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除计划文件夹")
    @DeleteMapping("/delete/{cycleId}")
    public ResponseEntity delete(@PathVariable(name = "project_id") Long projectId,
                                 @PathVariable(name = "cycleId") Long cycleId) {

        testCycleService.delete(cycleId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改计划文件夹")
    @PutMapping
    public ResponseEntity<TestCycleVO> update(@PathVariable(name = "project_id") Long projectId,
                                              @RequestBody TestCycleVO testCycleVO) {
        return Optional.ofNullable(testCycleService.update(projectId, testCycleVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.error.testCycle.update"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询树")
    @GetMapping(value = "/tree")
    public ResponseEntity<TestTreeIssueFolderVO> queryTree(@PathVariable(name = "project_id") Long projectId,
                                                           @RequestParam("plan_id") Long planId){
        return new ResponseEntity<>(testCycleService.queryTreeByPlanId(planId,projectId),HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("移动文件夹")
    @PutMapping("/move")
    public ResponseEntity<String> moveFolder(@PathVariable(name = "project_id") Long projectId,
                                             @RequestParam(name = "target_cycle_id") Long targetCycleId,
                                             @RequestBody TestCycleVO testCycleVO) {
        return Optional.ofNullable(testCycleService.moveCycle(projectId, targetCycleId, testCycleVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.cycle.move"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("操作计划日历")
    @PostMapping("/operate_calendar")
    public ResponseEntity operatePlanCalendar(@PathVariable(name = "project_id") Long projectId,
                                                           @RequestBody TestCycleVO testCycleVO,
                                                           @RequestParam(defaultValue = "true") Boolean isCycle){
        testPlanServcie.operatePlanCalendar(projectId,testCycleVO,isCycle);
        return new ResponseEntity(HttpStatus.OK);
    }
}