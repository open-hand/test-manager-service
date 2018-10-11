package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.infra.mapper.TestCycleMapper;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/12/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/cycle")
public class TestCycleController {
    @Autowired
    TestCycleService testCycleService;

    @Autowired
    TestCycleMapper testCycleMapper;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("增加测试循环")
    @PostMapping
    public ResponseEntity<TestCycleDTO> insert(@PathVariable(name = "project_id") Long projectId,
                                               @RequestBody TestCycleDTO testCycleDTO) {
        return Optional.ofNullable(testCycleService.insert(testCycleDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycle.insert"));

    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除测试循环")
    @DeleteMapping("/delete/{cycleId}")
    ResponseEntity delete(@PathVariable(name = "project_id") Long projectId,
                          @PathVariable(name = "cycleId") Long cycleId) {
        TestCycleDTO cycleDTO = new TestCycleDTO();
        cycleDTO.setCycleId(cycleId);
        testCycleService.delete(cycleDTO, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改测试循环")
    @PutMapping
    ResponseEntity<TestCycleDTO> update(@PathVariable(name = "project_id") Long projectId,
                                        @RequestBody TestCycleDTO testCycleDTO) {
        return Optional.ofNullable(testCycleService.update(testCycleDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.error.testCycle.update"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询测试循环")
    @GetMapping("/query/one/{cycleId}")
    TestCycleDTO queryOne(@PathVariable(name = "project_id") Long projectId,
                          @PathVariable(name = "cycleId") Long cycleId) {
        return testCycleService.getOneCycle(cycleId);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询project下的测试循环")
    @GetMapping("/query")
    ResponseEntity getTestCycle(@PathVariable(name = "project_id") Long projectId,
                                @RequestParam(required = false, name = "assignedTo") Long assignedTo) {
        return Optional.ofNullable(testCycleService.getTestCycle(projectId, assignedTo))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycle.query.getTestCycle"));
    }


    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询项目下的计划")
    @PostMapping("/query/version")
    ResponseEntity<Page<ProductVersionPageDTO>> getTestCycleVersion(@PathVariable(name = "project_id") Long projectId, @RequestBody Map<String, Object> searchParamMap) {
        return testCycleService.getTestCycleVersion(projectId, searchParamMap);
    }


    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("克隆循环")
    @PostMapping("/clone/cycle/{cycleId}")
    ResponseEntity cloneCycle(@PathVariable(name = "cycleId") Long cycleId,
                              @RequestBody TestCycleDTO testCycleDTO,
                              @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(testCycleService.cloneCycle(cycleId, testCycleDTO.getVersionId(), testCycleDTO.getCycleName(), projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testCycle.query.cloneCycle"));

    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("克隆文件夹")
    @PostMapping("/clone/folder/{cycleId}")
    ResponseEntity cloneFolder(
            @ApiParam(value = "循环id", required = true)
            @PathVariable(name = "cycleId") Long cycleId,
            @PathVariable(name = "project_id") Long projectId,
            @RequestBody TestCycleDTO testCycleDTO) {
        return Optional.ofNullable(testCycleService.cloneFolder(cycleId, testCycleDTO, projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testCycle.query.cloneFolder"));

    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("通过cycleId获取目录下所有的文件夹")
    @PostMapping("/query/folder/cycleId/{cycleId}")
    ResponseEntity getFolderByCycleId(@PathVariable(name = "project_id") Long projectId,
                                      @PathVariable(name = "cycleId") Long cycleId) {
        return Optional.ofNullable(testCycleService.getFolderByCycleId(cycleId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycle.query.getFolderByCycleId"));

    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("同步文件夹")
    @PostMapping("/synchro/folder/{folderId}/in/{cycleId}")
    ResponseEntity synchroFolder(@PathVariable(name = "project_id") Long projectId,
                                 @PathVariable(name = "cycleId") Long cycleId,
                                 @PathVariable(name = "folderId") Long folderId
    ) {
        Assert.notNull(folderId, "error.folderId.not.be.null");
        Assert.notNull(cycleId, "error.cycleId.not.be.null");
        testCycleService.synchroFolder(cycleId, folderId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("同步cycleId下文件夹")
    @PostMapping("/synchro/folder/all/in/cycle/{cycleId}")
    ResponseEntity synchroFolderInCycle(@PathVariable(name = "project_id") Long projectId,
                                 @PathVariable(name = "cycleId") Long cycleId
    ) {
        Assert.notNull(cycleId, "error.cycleId.not.be.null");
        testCycleService.synchroFolderInCycle(cycleId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("同步versionId下文件夹")
    @PostMapping("/synchro/folder/all/in/version/{versionId}")
    ResponseEntity synchroFolderInVersion(@PathVariable(name = "project_id") Long projectId,
                                          @PathVariable(name = "versionId") Long versionId
    ) {
        Assert.notNull(versionId, "error.versionId.not.be.null");
        testCycleService.synchroFolderInVersion(versionId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修复cycle及其关联的数据")
    @GetMapping("/fix")
    public ResponseEntity fixCycleData(@PathVariable(name = "project_id") Long projectId) {
        testCycleService.fixCycleData(projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
