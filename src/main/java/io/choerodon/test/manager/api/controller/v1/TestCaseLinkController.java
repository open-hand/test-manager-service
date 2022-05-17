package io.choerodon.test.manager.api.controller.v1;

import java.util.List;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.choerodon.test.manager.api.vo.IssueLinkVO;
import io.choerodon.test.manager.api.vo.TestCaseLinkVO;
import io.choerodon.test.manager.api.vo.TestCaseVO;
import io.choerodon.test.manager.app.service.TestCaseLinkService;
import io.choerodon.test.manager.infra.dto.TestCaseLinkDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaotianxin
 * @since 2019/11/19
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/case_link")
public class TestCaseLinkController {

    @Autowired
    private TestCaseLinkService testCaseLinkService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("创建问题链接")
    @PostMapping
    public ResponseEntity<List<TestCaseLinkDTO>> create(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId,
                                                        @ApiParam(value = "用例id", required = true)
                                                        @RequestParam("case_id") @Encrypt Long caseId,
                                                        @ApiParam(value = "关联的issue id集合", required = true)
                                                        @RequestBody @Encrypt List<Long> issueIds) {
        return new ResponseEntity<>(testCaseLinkService.create(projectId, caseId, issueIds), HttpStatus.NO_CONTENT);
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("解除关联issue")
    @DeleteMapping
    public ResponseEntity delete(@ApiParam(value = "项目id", required = true)
                                 @PathVariable(name = "project_id") Long projectId,
                                 @ApiParam(value = "关联id", required = true)
                                 @RequestParam
                                 @Encrypt Long linkId) {
        testCaseLinkService.delete(projectId, linkId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询测试用例关联的问题链接")
    @GetMapping("/list_issue_info")
    public ResponseEntity<List<IssueLinkVO>> queryLinkIssues(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "用例id", required = true)
                                                             @RequestParam(name = "case_id")
                                                             @Encrypt Long caseId) {
        return new ResponseEntity<>(testCaseLinkService.queryLinkIssues(projectId, caseId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("issue详情页创建用例并关联")
    @PostMapping("/create_and_link")
    public ResponseEntity<List<TestCaseLinkDTO>> createAndLink(@ApiParam(value = "项目id", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               @ApiParam(value = "关联的issue id", required = true)
                                                               @RequestParam("issue_id") @Encrypt Long issueId,
                                                               @ApiParam(value = "用例创建vo", required = true)
                                                               @RequestBody TestCaseVO testCaseVO) {
        return new ResponseEntity<>(testCaseLinkService.createAndLink(projectId, issueId, testCaseVO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("issue详情页关联用例")
    @PostMapping("/create_by_issue")
    public ResponseEntity creatByIssue(@ApiParam(value = "项目id", required = true)
                                       @PathVariable(name = "project_id") Long projectId,
                                       @ApiParam(value = "关联的issue id", required = true)
                                       @RequestParam("issue_id") @Encrypt Long issueId,
                                       @ApiParam(value = "关联的用例id集合", required = true)
                                       @RequestBody @Encrypt List<Long> caseIds) {
        testCaseLinkService.createByIssue(projectId, issueId, caseIds);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询问题关联的测试用例")
    @GetMapping("/list_link_case_info")
    public ResponseEntity<List<TestCaseLinkVO>> queryLinkCases(@ApiParam(value = "项目id", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               @ApiParam(value = "issueId", required = true)
                                                               @RequestParam(name = "issue_id")
                                                               @Encrypt Long issueId) {
        return new ResponseEntity<>(testCaseLinkService.queryLinkCases(projectId, issueId), HttpStatus.OK);
    }
}
