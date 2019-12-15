//package io.choerodon.test.manager.api.controller.v1;
//
//import java.util.List;
//import java.util.Optional;
//
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import io.choerodon.test.manager.api.vo.agile.IssueCreateDTO;
//import org.springframework.data.domain.Sort;
//import io.choerodon.core.enums.ResourceType;
//import io.choerodon.core.exception.CommonException;
//import io.choerodon.core.iam.InitRoleCode;
//import org.springframework.data.domain.Pageable;
//import io.choerodon.core.annotation.Permission;
//import org.springframework.data.web.SortDefault;
//import io.choerodon.test.manager.api.vo.IssueInfosVO;
//import io.choerodon.test.manager.api.vo.TestFolderRelQueryVO;
//import io.choerodon.test.manager.api.vo.TestIssueFolderRelVO;
//import io.choerodon.test.manager.app.service.TestIssueFolderRelService;
//
///**
// * Created by zongw.lee@gmail.com on 08/31/2018
// */
//@RestController
//@RequestMapping(value = "/v1/projects/{project_id}/issueFolderRel")
//public class TestIssueFolderRelController {
//
//    @Autowired
//    TestIssueFolderRelService testIssueFolderRelService;
//
//    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
//    @ApiOperation("查询issues")
//    @PostMapping("/query")
//    public ResponseEntity queryIssuesByParameter(@PathVariable(name = "project_id") Long projectId,
//                                                 @RequestParam(name = "folderId", required = false) Long folderId,
//                                                 @RequestBody
//                                                         TestFolderRelQueryVO testFolderRelQueryVO,
//                                                 @SortDefault(value = "issueId", direction = Sort.Direction.DESC) Pageable pageable,
//                                                 @RequestParam Long organizationId) {
//        return Optional.ofNullable(testIssueFolderRelService.query(projectId, folderId, testFolderRelQueryVO, pageable, organizationId))
//                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
//                .orElseThrow(() -> new CommonException("error.testIssueFolderRel.query"));
//    }
//
//    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
//    @ApiOperation("通过issueIds查询issues")
//    @PostMapping("/query/by/issueId")
//    public ResponseEntity queryIssuesById(@PathVariable(name = "project_id") Long projectId,
//                                          @RequestParam(name = "folderId", required = false) Long folderId,
//                                          @RequestParam(name = "versionId", required = false) Long versionId,
//                                          @RequestBody Long[] issueIds,
//                                          @RequestParam  Long organizationId) {
//        return Optional.ofNullable(testIssueFolderRelService.queryIssuesById(projectId, versionId, folderId, issueIds, organizationId))
//                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
//                .orElseThrow(() -> new CommonException("error.Issue.queryForm.toIssue.byId"));
//    }
//
//    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
//    @ApiOperation("创建测试并建立测试和文件夹的关系")
//    @PostMapping("/testAndRelationship")
//    public ResponseEntity<TestIssueFolderRelVO> insertTestAndRelationship(@PathVariable(name = "project_id") Long projectId,
//                                                                          @RequestParam(name = "folderId", required = false) Long folderId,
//                                                                          @RequestParam(name = "versionId") Long versionId,
//                                                                          @RequestParam(value = "applyType") String applyType,
//                                                                          @RequestBody IssueCreateDTO issueCreateDTO) {
//        return Optional.ofNullable(testIssueFolderRelService.insertTestAndRelationship(issueCreateDTO, projectId, folderId, versionId, applyType))
//                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
//                .orElseThrow(() -> new CommonException("error.testIssueFolderRel.insert"));
//    }
//
//    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
//    @ApiOperation("创建测试和文件夹的关系")
//    @PostMapping
//    public ResponseEntity<List<TestIssueFolderRelVO>> insertRelationship(@PathVariable(name = "project_id") Long projectId,
//                                                                         @RequestBody List<TestIssueFolderRelVO> testIssueFolderRelVOS) {
//        return Optional.ofNullable(testIssueFolderRelService.insertBatchRelationship(projectId, testIssueFolderRelVOS))
//                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
//                .orElseThrow(() -> new CommonException("error.testIssueFolderRel.insert"));
//    }
//
//    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
//    @ApiOperation("移动文件夹下issue")
//    @PutMapping("/move")
//    public ResponseEntity moveIssue(@PathVariable(name = "project_id") Long projectId,
//                                    @RequestParam(name = "folderId") Long folderId,
//                                    @RequestParam(name = "versionId") Long versionId,
//                                    @RequestBody List<IssueInfosVO> issues) {
//        testIssueFolderRelService.moveFolderIssue(projectId, versionId, folderId, issues);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
//    @ApiOperation("复制文件夹下issue")
//    @PutMapping("/copy")
//    public ResponseEntity copyIssue(@PathVariable(name = "project_id") Long projectId,
//                                    @RequestParam(name = "folderId") Long folderId,
//                                    @RequestParam(name = "versionId") Long versionId,
//                                    @RequestBody List<IssueInfosVO> issues) {
//        testIssueFolderRelService.copyIssue(projectId, versionId, folderId, issues);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
//    @ApiOperation("克隆文件夹下的一个issue")
//    @PutMapping("/copy/issue/{issueId}")
//    public ResponseEntity<TestIssueFolderRelVO> cloneOneIssue(@PathVariable(name = "project_id") Long projectId,
//                                                              @PathVariable(name = "issueId") Long issueId) {
//        return Optional.ofNullable(testIssueFolderRelService.cloneOneIssue(projectId, issueId))
//                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
//                .orElseThrow(() -> new CommonException("error.testIssueFolderRel.clone"));
//    }
//}
