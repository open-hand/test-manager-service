package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Optional;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */

@RestController
@RequestMapping(value = "/v1/project/{project_id}/test/case")
public class TestCaseController {
    @Autowired
    TestCaseService testCaseService;

    @Permission(permissionPublic = true)
    @ApiOperation("增加测试")
    @PostMapping
    public ResponseEntity<IssueDTO> create(@ApiParam(value = "项目id", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @ApiParam(value = "创建issue对象", required = true)
                                           @RequestBody IssueCreateDTO issueCreateDTO) {
        return Optional.ofNullable(testCaseService.insert(projectId, issueCreateDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.Issue.createIssue"));
    }

    @Permission(permissionPublic = true)
    @ApiOperation("删除测试")
    @DeleteMapping("/{issueId}")
    public ResponseEntity<Boolean> delete(@ApiParam(value = "项目id", required = true)
                                          @PathVariable(name = "project_id") Long projectId,
                                          @ApiParam(value = "issueId", required = true)
                                          @PathVariable Long issueId) {
        testCaseService.delete(projectId, issueId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Permission(permissionPublic = true)
    @ApiOperation("修改测试")
    @PutMapping
    public ResponseEntity<IssueDTO> update(@ApiParam(value = "项目id", required = true) @PathVariable(name = "project_id") Long projectId,
                                           @ApiParam(value = "更新issue对象", required = true)
                                           @RequestBody JSONObject issueUpdate) {
        return testCaseService.update(projectId, issueUpdate);
    }

    @Permission(permissionPublic = true)
    @ApiOperation("查询一个测试")
    @GetMapping("/query/{issueId}")
    public ResponseEntity<IssueDTO> queryOne(@ApiParam(value = "项目id", required = true)
                                             @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "issueId", required = true)
                                             @PathVariable Long issueId) {
        return testCaseService.query(projectId, issueId);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("分页过滤查询issue列表(不包含子任务)")
    @CustomPageRequest
    @GetMapping(value = "/query/no_sub")
    public ResponseEntity<Page<IssueCommonDTO>> listIssueWithoutSub(@ApiIgnore
                                                                    @ApiParam(value = "分页信息", required = true)
                                                                    @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
                                                                            PageRequest pageRequest,
                                                                    @ApiParam(value = "项目id", required = true)
                                                                    @PathVariable(name = "project_id") Long projectId) {
        return testCaseService.listIssueWithoutSub(projectId, "", pageRequest);
    }

}
