package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import java.util.Optional;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderRelDTO;
import io.choerodon.test.manager.app.service.TestIssueFolderRelService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/issueFolderRel")
public class TestIssueFolderRelController {

    @Autowired
    TestIssueFolderRelService testIssueFolderRelService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询issues")
    @PostMapping("/query")
    public ResponseEntity queryIssuesByParameter(@PathVariable(name = "project_id") Long projectId,
                                                 @RequestParam(name = "folder_id", required = false) Long folderId,
                                                 @RequestParam(name = "version_id", required = false) Long versionId,
                                                 @RequestBody
                                                         SearchDTO searchDTO,
                                                 @SortDefault(value = "issueId", direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Optional.ofNullable(testIssueFolderRelService.query(projectId, folderId, versionId, searchDTO, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testIssueFolderRel.query"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("通过issueIds查询issues")
    @PostMapping("/query/by/issueId")
    public ResponseEntity queryIssuesById(@PathVariable(name = "project_id") Long projectId,
                                          @RequestParam(name = "folder_id",required = false) Long folderId,
                                          @RequestParam(name = "version_id",required = false) Long versionId,
                                          @RequestBody Long[] issueIds) {
        return Optional.ofNullable(testIssueFolderRelService.queryIssuesById(projectId, versionId, folderId, issueIds))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.queryForm.toIssue.byId"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除文件夹下的issue")
    @PutMapping("/delete")
    public ResponseEntity delete(@PathVariable(name = "project_id") Long projectId,
                                 @RequestBody List<Long> issuesId) {
        testIssueFolderRelService.delete(projectId,issuesId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建测试并建立测试和文件夹的关系")
    @PostMapping("/testAndRelationship")
    public ResponseEntity<TestIssueFolderRelDTO> insertTestAndRelationship(@PathVariable(name = "project_id") Long projectId,
                                                                           @RequestParam(name = "folder_id", required = false) Long folderId,
                                                                           @RequestParam(name = "version_id") Long versionId,
                                                                           @RequestBody IssueCreateDTO issueCreateDTO) {
        return Optional.ofNullable(testIssueFolderRelService.insertTestAndRelationship(issueCreateDTO, projectId, folderId, versionId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testIssueFolderRel.insert"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建测试和文件夹的关系")
    @PostMapping
    public ResponseEntity<List<TestIssueFolderRelDTO>> insertRelationship(@PathVariable(name = "project_id") Long projectId,
                                                                          @RequestBody List<TestIssueFolderRelDTO> testIssueFolderRelDTOS) {
        return Optional.ofNullable(testIssueFolderRelService.insertRelationship(projectId, testIssueFolderRelDTOS))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testIssueFolderRel.insert"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("移动文件夹下issue")
    @PutMapping("/move")
    public ResponseEntity moveIssue(@PathVariable(name = "project_id") Long projectId,
                                    @RequestParam(name = "folder_id") Long folderId,
                                    @RequestParam(name = "version_id") Long versionId,
                                    @RequestBody List<IssueInfosDTO> issues) {
        testIssueFolderRelService.moveFolderIssue(projectId, versionId, folderId, issues);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("复制文件夹下issue")
    @PutMapping("/copy")
    public ResponseEntity copyIssue(@PathVariable(name = "project_id") Long projectId,
                                    @RequestParam(name = "folder_id") Long folderId,
                                    @RequestParam(name = "version_id") Long versionId,
                                    @RequestBody List<IssueInfosDTO> issues) {
        testIssueFolderRelService.copyIssue(projectId, versionId, folderId, issues);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
