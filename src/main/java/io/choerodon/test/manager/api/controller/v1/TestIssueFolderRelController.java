package io.choerodon.test.manager.api.controller.v1;

import java.util.Optional;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
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

//    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
//    @ApiOperation("查询状态")
//    @GetMapping("/query")
//    public ResponseEntity query(@PathVariable(name = "project_id") Long projectId) {
//        return Optional.ofNullable(testIssueFolderRelService.query(projectId))
//                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
//                .orElseThrow(() -> new CommonException("error.testIssueFolder.query"));
//
//    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除状态")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable(name = "project_id") Long projectId,
                                 @PathVariable(name = "id") Long id) {
        TestIssueFolderRelDTO dto = new TestIssueFolderRelDTO();
        dto.setId(id);
        testIssueFolderRelService.delete(dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("插入状态")
    @PostMapping
    public ResponseEntity<TestIssueFolderRelDTO> insert(@PathVariable(name = "project_id") Long projectId,
                                                        @RequestParam(name = "folder_id")Long folderId,
                                                        @RequestParam(name = "version_id")Long versionId,
                                                        @RequestBody IssueCreateDTO issueCreateDTO) {
        return Optional.ofNullable(testIssueFolderRelService.insert(issueCreateDTO,projectId,folderId,versionId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testIssueFolder.insert"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新状态")
    @PutMapping("/update")
    public ResponseEntity<TestIssueFolderRelDTO> update(@PathVariable(name = "project_id") Long projectId,
                                                     @RequestBody TestIssueFolderRelDTO testIssueFolderRelDTO) {
        return Optional.ofNullable(testIssueFolderRelService.update(testIssueFolderRelDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testIssueFolder.update"));
    }
}
