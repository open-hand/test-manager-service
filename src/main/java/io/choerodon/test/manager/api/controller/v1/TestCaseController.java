package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.app.service.ExcelService;
import io.choerodon.test.manager.app.service.ReporterFormService;
import io.choerodon.agile.api.dto.*;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */

@RestController
@RequestMapping(value = "/v1/projects/{project_id}/case")
public class TestCaseController {
    @Autowired
    ReporterFormService reporterFormService;

    @Autowired
    ExcelService excelService;

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
	@ApiOperation("通过缺陷Id生成报表从缺陷到issue")
	@PostMapping("/get/reporter/from/defect/by/issueId")
	public ResponseEntity createFormDefectFromIssueById(@PathVariable(name = "project_id") Long projectId,
														@RequestBody Long[] issueIds) {

		return Optional.ofNullable(reporterFormService.createFormDefectFromIssue(projectId, issueIds))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.Issue.queryForm.toIssue.byId"));
	}


    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成报表从缺陷到issue")
    @PostMapping("/get/reporter/from/defect")
    public ResponseEntity createFormDefectFromIssue(@PathVariable(name = "project_id") Long projectId, @RequestBody SearchDTO searchDTO, @SortDefault(value = "issueId", direction = Sort.Direction.DESC) PageRequest pageRequest) {

        return Optional.ofNullable(reporterFormService.createFormDefectFromIssue(projectId, searchDTO, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.createForm.toDefect"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成整个项目的excel")
    @GetMapping("/download/excel")
    public String downLoadByProject(@PathVariable(name = "project_id") Long projectId,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        return excelService.exportCaseByProject(projectId, request, response);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成整个版本的excel")
    @GetMapping("/download/excel/version")
    public String downLoadByVersion(@PathVariable(name = "project_id") Long projectId,
                                  @RequestParam(name = "versionId") Long versionId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        return excelService.exportCaseByVersion(projectId, versionId, request, response);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成整个文件夹的excel")
    @GetMapping("/download/excel/folder")
    public String downLoadByFolder(@PathVariable(name = "project_id") Long projectId,
                                  @RequestParam(name = "folderId") Long folderId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        return excelService.exportCaseByFolder(projectId,folderId, request, response);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成excel模板")
    @GetMapping("/download/excel/template")
    public void downLoadTemplate(@PathVariable(name = "project_id") Long projectId,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        excelService.exportCaseTemplate(projectId, request, response);
    }
}
