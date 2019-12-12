package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
import io.choerodon.test.manager.app.service.TestIssueFolderService;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/issueFolder")
public class TestIssueFolderController {

    @Autowired
    private TestIssueFolderService testIssueFolderService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询文件夹，返回树结构")
    @GetMapping("/query")
    public ResponseEntity query(@PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(testIssueFolderService.queryTreeFolder(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testIssueFolder.query"));
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除文件夹")
    @DeleteMapping("/{folderId}")
    public ResponseEntity delete(@PathVariable(name = "project_id") Long projectId,
                                 @PathVariable(name = "folderId") Long folderId) {
        testIssueFolderService.delete(projectId, folderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建文件夹")
    @PostMapping
    public ResponseEntity<TestIssueFolderVO> create(@PathVariable(name = "project_id") Long projectId,
                                                    @RequestBody TestIssueFolderVO testIssueFolderVO) {
        return Optional.ofNullable(testIssueFolderService.create(projectId,testIssueFolderVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testIssueFolder.insert"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新文件夹")
    @PutMapping("/update")
    public ResponseEntity<TestIssueFolderVO> update(@PathVariable(name = "project_id") Long projectId,
                                                    @RequestBody TestIssueFolderVO testIssueFolderVO) {
        return Optional.ofNullable(testIssueFolderService.update(testIssueFolderVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testIssueFolder.update"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("复制文件夹")
    @PutMapping("/copy")
    public ResponseEntity copyFolder(@PathVariable(name = "project_id") Long projectId,
                                     @RequestParam(name = "targetFolderId") Long targetFolderId,
                                     @RequestBody Long[] folderIds) {
        testIssueFolderService.copyFolder(projectId, targetFolderId, folderIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("移动文件夹")
    @PutMapping("/move")
    public ResponseEntity moveFolder(@PathVariable(name = "project_id") Long projectId,
                                     @RequestParam(name = "targetFolderId") Long targetFolderId,
                                     @RequestBody TestIssueFolderVO issueFolderVO) {
        testIssueFolderService.moveFolder(projectId,targetFolderId, issueFolderVO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
