package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import java.util.Optional;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestCycleVO;
import io.choerodon.test.manager.api.vo.TestTreeIssueFolderVO;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.app.service.TestPlanService;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
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
    TestPlanService testPlanService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("增加计划文件夹")
    @PostMapping
    public ResponseEntity<TestCycleVO> insert(@ApiParam(value = "项目id", required = true)
                                              @PathVariable(name = "project_id") Long projectId,
                                              @ApiParam(value = "文件夹新建vo", required = true)
                                              @RequestBody TestCycleVO testCycleVO) {
        return Optional.ofNullable(testCycleService.insert(projectId, testCycleVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycle.insert"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除计划文件夹")
    @DeleteMapping("/delete/{cycleId}")
    public ResponseEntity delete(@ApiParam(value = "项目id", required = true)
                                 @PathVariable(name = "project_id") Long projectId,
                                 @ApiParam(value = "计划文件夹id", required = true)
                                 @PathVariable(name = "cycleId")
                                 @Encrypt Long cycleId) {

        testCycleService.delete(cycleId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("修改计划文件夹")
    @PutMapping
    public ResponseEntity<TestCycleVO> update(@ApiParam(value = "项目id", required = true)
                                              @PathVariable(name = "project_id") Long projectId,
                                              @ApiParam(value = "计划文件夹更新vo", required = true)
                                              @RequestBody TestCycleVO testCycleVO) {
        return Optional.ofNullable(testCycleService.update(projectId, testCycleVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.error.testCycle.update"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询树")
    @GetMapping(value = "/tree")
    public ResponseEntity<TestTreeIssueFolderVO> queryTree(@ApiParam(value = "项目id", required = true)
                                                           @PathVariable(name = "project_id") Long projectId,
                                                           @ApiParam(value = "计划id", required = true)
                                                           @RequestParam("plan_id")
                                                           @Encrypt Long planId) {
        return new ResponseEntity<>(testCycleService.queryTreeByPlanId(planId, projectId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("移动文件夹")
    @PutMapping("/move")
    public ResponseEntity<List<TestCycleDTO>> moveFolder(@ApiParam(value = "项目id", required = true)
                                                         @PathVariable(name = "project_id") Long projectId,
                                                         @ApiParam(value = "目标计划文件夹id", required = true)
                                                         @RequestParam(name = "target_cycle_id", required = false)
                                                         @Encrypt Long targetCycleId,
                                                         @ApiParam(value = "移动文件夹vo", required = true)
                                                         @RequestBody TestCycleVO testCycleVO) {
        return Optional.ofNullable(testCycleService.batchMoveCycle(projectId, targetCycleId, testCycleVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.cycle.move"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("操作计划日历")
    @PostMapping("/operate_calendar")
    public ResponseEntity operatePlanCalendar(@ApiParam(value = "项目id", required = true)
                                              @PathVariable(name = "project_id") Long projectId,
                                              @ApiParam(value = "计划文件夹vo", required = true)
                                              @RequestBody TestCycleVO testCycleVO,
                                              @ApiParam(value = "是否是文件夹", required = true)
                                              @RequestParam(defaultValue = "true") Boolean isCycle) {
        testPlanService.operatePlanCalendar(projectId, testCycleVO, isCycle);
        return new ResponseEntity(HttpStatus.OK);
    }
}