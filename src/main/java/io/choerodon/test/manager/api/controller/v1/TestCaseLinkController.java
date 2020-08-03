package io.choerodon.test.manager.api.controller.v1;

import java.util.List;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.choerodon.test.manager.api.vo.IssueLinkVO;
import io.choerodon.test.manager.app.service.TestCaseLinkService;
import io.choerodon.test.manager.infra.dto.TestCaseLinkDTO;
import io.swagger.annotations.ApiOperation;
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
    public ResponseEntity<List<TestCaseLinkDTO>> create(@PathVariable(name = "project_id") Long projectId,
                                                        @RequestParam("case_id") @Encrypt Long caseId,
                                                        @RequestBody @Encrypt List<Long> issueIds) {
        return new ResponseEntity<>(testCaseLinkService.create(projectId, caseId, issueIds), HttpStatus.NO_CONTENT);
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("解除关联issue")
    @DeleteMapping
    public ResponseEntity delete(@PathVariable(name = "project_id") Long projectId,
                                 @RequestParam
                                 @Encrypt Long linkId) {
        testCaseLinkService.delete(projectId, linkId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询测试用例关联的问题链接")
    @GetMapping("/list_issue_info")
    public ResponseEntity<List<IssueLinkVO>> queryLinkIssues(@PathVariable(name = "project_id") Long projectId,
                                                             @RequestParam(name = "case_id")
                                                             @Encrypt Long caseId) {
        return new ResponseEntity<>(testCaseLinkService.queryLinkIssues(projectId, caseId), HttpStatus.OK);
    }
}
