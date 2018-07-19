package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.app.service.ReporterFormService;
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

import java.util.List;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */

@RestController
@RequestMapping(value = "/v1/projects/{project_id}/case")
public class TestCaseController {
    @Autowired
    ReporterFormService reporterFormService;


    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成报表从issue到缺陷")
    @PostMapping("/get/reporter/from/issue")
	public ResponseEntity createFormsFromIssueToDefect(@PathVariable(name = "project_id") Long projectId,
													   @RequestBody
															   SearchDTO searchDTO,
													   @ApiIgnore
													   @ApiParam(value = "分页信息", required = true)
													   @SortDefault(value = "issueId", direction = Sort.Direction.DESC) PageRequest pageRequest) {

        return Optional.ofNullable(reporterFormService.createFromIssueToDefect(projectId, searchDTO, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.Issue.createForm.toDefect"));
    }

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("通过IssueId生成issue到缺陷的报表")
	@PostMapping("/get/reporter/from/issue/by/issueId")
	public ResponseEntity createFormsFromIssueToDefectByIssueId(@PathVariable(name = "project_id") Long projectId,
																@RequestBody Long[] issueIds) {

		return Optional.ofNullable(reporterFormService.createFromIssueToDefect(projectId, issueIds))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.Issue.createForm.toDefect.byId"));
	}

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("生成报表从缺陷到issue")
	@PostMapping("/get/reporter/from/defect")
	public ResponseEntity createFormDefectFromIssue(@PathVariable(name = "project_id") Long projectId,
													@ApiIgnore
													@ApiParam(value = "分页信息", required = true)
													@SortDefault(value = "issueId", direction = Sort.Direction.DESC) PageRequest pageRequest) {


		return Optional.ofNullable(reporterFormService.createFormDefectFromIssue(projectId, pageRequest))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.Issue.queryForm.toIssue"));
	}

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("通过缺陷Id生成报表从缺陷到issue")
	@PostMapping("/get/reporter/from/defect/by/issueId")
	public ResponseEntity createFormDefectFromIssueById(@PathVariable(name = "project_id") Long projectId,
														@RequestBody Long[] issueIds) {

		return Optional.ofNullable(reporterFormService.createFormDefectFromIssue(projectId, issueIds))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.Issue.queryForm.toIssue.byId"));
	}


//    @Permission(level = ResourceLevel.PROJECT)
//    @ApiOperation("增加测试")
//    @PostMapping
//    public ResponseEntity<IssueDTO> create(@ApiParam(value = "项目id", required = true)
//                                           @PathVariable(name = "project_id") Long projectId,
//                                           @ApiParam(value = "创建issue对象", required = true)
//                                           @RequestBody IssueCreateDTO issueCreateDTO) {
//        return Optional.ofNullable(testCaseService.insert(projectId, issueCreateDTO))
//                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
//                .orElseThrow(() -> new CommonException("error.Issue.createIssue"));
//    }
//
//    @Permission(level = ResourceLevel.PROJECT)
//    @ApiOperation("删除测试")
//    @DeleteMapping("/{issueId}")
//    public ResponseEntity<Boolean> delete(@ApiParam(value = "项目id", required = true)
//                                          @PathVariable(name = "project_id") Long projectId,
//                                          @ApiParam(value = "issueId", required = true)
//                                          @PathVariable Long issueId) {
//        testCaseService.delete(projectId, issueId);
//		return new ResponseEntity<>(true, HttpStatus.NO_CONTENT);
//    }
//
//    @Permission(level = ResourceLevel.PROJECT)
//    @ApiOperation("修改测试")
//    @PutMapping
//    public ResponseEntity<IssueDTO> update(@ApiParam(value = "项目id", required = true) @PathVariable(name = "project_id") Long projectId,
//                                           @ApiParam(value = "更新issue对象", required = true)
//                                           @RequestBody JSONObject issueUpdate) {
//        return testCaseService.update(projectId, issueUpdate);
//    }
//
//    @Permission(level = ResourceLevel.PROJECT)
//    @ApiOperation("查询一个测试")
//    @GetMapping("/query/{issueId}")
//    public ResponseEntity<IssueDTO> queryOne(@ApiParam(value = "项目id", required = true)
//                                             @PathVariable(name = "project_id") Long projectId,
//                                             @ApiParam(value = "issueId", required = true)
//                                             @PathVariable Long issueId) {
//        return testCaseService.query(projectId, issueId);
//    }
//
//    @Permission(level = ResourceLevel.PROJECT)
//    @ApiOperation("分页过滤查询issue列表(不包含子任务)")
//    @CustomPageRequest
//	@PostMapping(value = "/query/no_sub")
//	public ResponseEntity<Page<IssueListDTO>> listIssueWithoutSub(@ApiIgnore
//                                                                    @ApiParam(value = "分页信息", required = true)
//                                                                    @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
//                                                                            PageRequest pageRequest,
//                                                                    @ApiParam(value = "项目id", required = true)
//																  @PathVariable(name = "project_id") Long projectId,
//																  @RequestBody SearchDTO searchDTO) {
//		return testCaseService.listIssueWithoutSub(projectId, searchDTO, pageRequest);
//    }



}
