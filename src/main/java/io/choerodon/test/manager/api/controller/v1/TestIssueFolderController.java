package io.choerodon.test.manager.api.controller.v1;

import java.util.Optional;
import java.util.regex.Pattern;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.test.manager.api.vo.TestTreeIssueFolderVO;
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.swagger.annotation.Permission;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
import io.choerodon.test.manager.app.service.TestIssueFolderService;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/issueFolder")
public class TestIssueFolderController {

    public static final String TEST_ISSUE_FOLDER_NAME_REG = "^[^/%]{1,50}$";

    @Autowired
    private TestIssueFolderService testIssueFolderService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询文件夹，返回树结构")
    @GetMapping("/query")
    public ResponseEntity<TestTreeIssueFolderVO> query(@PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(testIssueFolderService.queryTreeFolder(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testIssueFolder.query"));
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除文件夹")
    @DeleteMapping("/{folderId}")
    public ResponseEntity delete(@PathVariable(name = "project_id") Long projectId,
                                 @PathVariable(name = "folderId")
                                 @Encrypt Long folderId) {
        testIssueFolderService.delete(projectId, folderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("创建文件夹")
    @PostMapping
    public ResponseEntity<TestIssueFolderVO> create(@PathVariable(name = "project_id") Long projectId,
                                                    @RequestBody TestIssueFolderVO testIssueFolderVO) {
        if (!Pattern.matches(TEST_ISSUE_FOLDER_NAME_REG, testIssueFolderVO.getName())) {
            throw new CommonException("error.testIssueFolder.name.invalid");
        }
        return Optional.ofNullable(testIssueFolderService.create(projectId, testIssueFolderVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testIssueFolder.insert"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("更新文件夹")
    @PutMapping("/update")
    public ResponseEntity<TestIssueFolderVO> update(@PathVariable(name = "project_id") Long projectId,
                                                    @RequestBody TestIssueFolderVO testIssueFolderVO) {
        if (!Pattern.matches(TEST_ISSUE_FOLDER_NAME_REG, testIssueFolderVO.getName())) {
            throw new CommonException("error.testIssueFolder.name.invalid");
        }
        return Optional.ofNullable(testIssueFolderService.update(testIssueFolderVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testIssueFolder.update"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("移动文件夹")
    @PutMapping("/move")
    public ResponseEntity<String> moveFolder(@PathVariable(name = "project_id") Long projectId,
                                             @RequestParam(name = "targetFolderId", required = false)
                                             @Encrypt Long targetFolderId,
                                             @RequestBody TestIssueFolderVO issueFolderVO) {
        return Optional.ofNullable(testIssueFolderService.moveFolder(projectId, targetFolderId, issueFolderVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testIssueFolder.move"));
    }

    /**
     * 复制计划文件夹
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("复制计划文件夹")
    @PostMapping("/folder/{folderId}/clone")
    public ResponseEntity<TestIssueFolderDTO> cloneTestCaseFolder(@PathVariable(name = "project_id") Long projectId,
                                                                  @PathVariable(name = "folderId")
                                                                  @Encrypt Long folderId) {
        return Results.success(testIssueFolderService.cloneFolder(projectId, folderId));
    }

    /**
     * 复制计划文件夹
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("检查复制文件夹名称合规")
    @GetMapping("/folder/{folderId}/clone/check_name")
    public ResponseEntity<Boolean> checkCopyFolderName(@PathVariable(name = "project_id") Long projectId,
                                                       @PathVariable(name = "folderId")
                                                       @Encrypt Long folderId) {
        return Results.success(testIssueFolderService.checkCopyFolderName(projectId, folderId));
    }
}
