package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.choerodon.test.manager.api.vo.TestTreeIssueFolderVO;
import io.choerodon.test.manager.app.service.TestPlanServcie;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import com.github.pagehelper.PageInfo;

import io.choerodon.test.manager.api.vo.agile.ProductVersionPageDTO;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.api.vo.BatchCloneCycleVO;
import io.choerodon.test.manager.api.vo.TestCycleVO;
import io.choerodon.test.manager.api.vo.TestFileLoadHistoryVO;
import io.choerodon.test.manager.app.service.TestCycleService;

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
    @ApiOperation("查询测试循环")
    @GetMapping("/query/one/{cycleId}")
    public TestCycleVO queryOne(@PathVariable(name = "project_id") Long projectId,
                                @PathVariable(name = "cycleId") Long cycleId) {
        return testCycleService.getOneCycle(cycleId);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询project下的测试循环")
    @GetMapping("/query")
    public ResponseEntity getTestCycle(@PathVariable(name = "project_id") Long projectId,
                                       @RequestParam(required = false, name = "assignedTo") Long assignedTo) {
        return Optional.ofNullable(testCycleService.getTestCycle(projectId, assignedTo))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycle.query.getTestCycle"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询project下的测试循环")
    @GetMapping("/count/color/in/version/{versionId}")
    public ResponseEntity getTestCycleCaseCountInVersion(@PathVariable(name = "project_id") Long projectId,
                                                         @PathVariable(name = "versionId") Long versionId,
                                                         @RequestParam(required = false, name = "cycleId") Long cycleId) {
        return Optional.ofNullable(testCycleService.getTestCycleCaseCountInVersion(versionId, projectId, cycleId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycle.query.getTestCycleCaseCountInVersion"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询版本下的测试循环，批量克隆用")
    @GetMapping("/batch/clone/query/{versionId}")
    public ResponseEntity getTestCycleInVersionForBatchClone(@PathVariable(name = "project_id") Long projectId,
                                                             @PathVariable(name = "versionId") Long versionId) {
        return Optional.ofNullable(testCycleService.getTestCycleInVersionForBatchClone(versionId, projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycle.query.getTestCycleInVersionForBatchClone"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询项目下的计划")
    @PostMapping("/query/version")
    public ResponseEntity<PageInfo<ProductVersionPageDTO>> getTestCycleVersion(@PathVariable(name = "project_id") Long projectId, @RequestBody Map<String, Object> searchParamMap) {
        return testCycleService.getTestCycleVersion(projectId, searchParamMap);
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("克隆循环")
    @PostMapping("/clone/cycle/{cycleId}")
    public ResponseEntity cloneCycle(@PathVariable(name = "cycleId") Long cycleId,
                                     @RequestBody TestCycleVO testCycleVO,
                                     @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(testCycleService.cloneCycle(cycleId, testCycleVO.getVersionId(), testCycleVO.getCycleName(), projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testCycle.query.cloneCycle"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("克隆文件夹")
    @PostMapping("/clone/folder/{cycleId}")
    public ResponseEntity cloneFolder(
            @ApiParam(value = "循环id", required = true)
            @PathVariable(name = "cycleId") Long cycleId,
            @PathVariable(name = "project_id") Long projectId,
            @RequestBody TestCycleVO testCycleVO) {
        return Optional.ofNullable(testCycleService.cloneFolder(cycleId, testCycleVO, projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testCycle.query.cloneFolder"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("批量克隆循环及选定的文件夹")
    @PostMapping("/batch/clone/{versionId}")
    public ResponseEntity batchCloneCycles(
            @PathVariable(name = "project_id") Long projectId,
            @PathVariable(name = "versionId") Long versionId,
            @RequestBody List<BatchCloneCycleVO> list) {
        testCycleService.batchCloneCycles(projectId, versionId, list);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询最近一次批量克隆记录")
    @GetMapping("/batch/clone/latest")
    public ResponseEntity<TestFileLoadHistoryVO> queryLatestLoadHistory(@PathVariable("project_id") Long projectId) {
        return Optional.ofNullable(testCycleService.queryLatestBatchCloneHistory(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.OK));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("通过cycleId获取目录下所有的文件夹")
    @GetMapping("/query/folder/cycleId/{cycleId}")
    public ResponseEntity<List<TestCycleVO>> getFolderByCycleId(@PathVariable(name = "project_id") Long projectId,
                                                                @PathVariable(name = "cycleId") Long cycleId) {
        return Optional.ofNullable(testCycleService.getFolderByCycleId(cycleId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycle.query.getFolderByCycleId"));

    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("同步文件夹")
    @PostMapping("/synchro/folder/{folderId}/in/{cycleId}")
    public ResponseEntity synchroFolder(@PathVariable(name = "project_id") Long projectId,
                                        @PathVariable(name = "cycleId") Long cycleId,
                                        @PathVariable(name = "folderId") Long folderId
    ) {
        Assert.notNull(folderId, "error.folderId.not.be.null");
        Assert.notNull(cycleId, "error.cycleId.not.be.null");
        testCycleService.synchroFolder(cycleId, folderId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("同步cycleId下文件夹")
    @PostMapping("/synchro/folder/all/in/cycle/{cycleId}")
    public ResponseEntity synchroFolderInCycle(@PathVariable(name = "project_id") Long projectId,
                                               @PathVariable(name = "cycleId") Long cycleId
    ) {
        Assert.notNull(cycleId, "error.cycleId.not.be.null");
        testCycleService.synchroFolderInCycle(cycleId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("同步versionId下文件夹")
    @PostMapping("/synchro/folder/all/in/version/{versionId}")
    public ResponseEntity synchroFolderInVersion(@PathVariable(name = "project_id") Long projectId,
                                                 @PathVariable(name = "versionId") Long versionId
    ) {
        Assert.notNull(versionId, "error.versionId.not.be.null");
        testCycleService.synchroFolderInVersion(versionId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询version下所有cycle")
    @GetMapping("/get/cycles/all/in/version/{versionId}")
    public ResponseEntity getCyclesInVersion(@PathVariable(name = "project_id") Long projectId,
                                             @PathVariable(name = "versionId") Long versionId) {

        return Optional.ofNullable(testCycleService.getCyclesInVersion(versionId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycle.query.getCyclesInVersion"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("批量修改cycle下所有的case的指定人")
    @PutMapping("/batch/change/cycleCase/assignedTo/{userId}/in/cycle/{cycleId}")
    public ResponseEntity batchChangeAssignedInOneCycle(@PathVariable(name = "project_id") Long projectId,
                                                        @PathVariable(name = "userId") Long userId,
                                                        @PathVariable(name = "cycleId") Long cycleId) {
        testCycleService.batchChangeAssignedInOneCycle(projectId, userId, cycleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("测试循环、阶段重名校验")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@ApiParam(value = "项目id", required = true)
                                             @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "类型", required = true)
                                             @RequestParam String type,
                                             @ApiParam(value = "循环或阶段名字", required = true)
                                             @RequestParam String cycleName,
                                             @ApiParam(value = "版本id", required = true)
                                             @RequestParam Long versionId,
                                             @ApiParam(value = "父循环id", required = true)
                                             @RequestParam Long parentCycleId) {
        return Optional.ofNullable(testCycleService.checkName(projectId, type, cycleName, versionId,parentCycleId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.cycleName.check"));
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