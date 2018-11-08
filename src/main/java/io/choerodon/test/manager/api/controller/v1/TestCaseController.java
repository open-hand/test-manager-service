package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.app.service.ExcelImportService;
import io.choerodon.test.manager.app.service.ExcelService;
import io.choerodon.test.manager.app.service.ReporterFormService;
import io.choerodon.agile.api.dto.*;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.infra.common.utils.ExcelUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    private ExcelService excelService;

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    public TestCaseController(ExcelService excelService) {
        this.excelService = excelService;
    }

    public void setExcelService(ExcelService excelService) {
        this.excelService = excelService;
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成报表从issue到缺陷")
    @PostMapping("/get/reporter/from/issue")
    public ResponseEntity createFormsFromIssueToDefect(@PathVariable(name = "project_id") Long projectId,
                                                       @RequestBody
                                                               SearchDTO searchDTO,
                                                       @ApiIgnore
                                                       @ApiParam(value = "分页信息", required = true)
                                                       @SortDefault(value = "issueId", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                       @RequestParam Long organizationId) {

        return Optional.ofNullable(reporterFormService.createFromIssueToDefect(projectId, searchDTO, pageRequest,organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.createForm.toDefect"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("通过IssueId生成issue到缺陷的报表")
    @PostMapping("/get/reporter/from/issue/by/issueId")
    public ResponseEntity createFormsFromIssueToDefectByIssueId(@PathVariable(name = "project_id") Long projectId,
                                                                @RequestBody Long[] issueIds,
                                                                @RequestParam Long organizationId) {

        return Optional.ofNullable(reporterFormService.createFromIssueToDefect(projectId, issueIds,organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.createForm.toDefect.byId"));
    }

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("通过缺陷Id生成报表从缺陷到issue")
	@PostMapping("/get/reporter/from/defect/by/issueId")
	public ResponseEntity createFormDefectFromIssueById(@PathVariable(name = "project_id") Long projectId,
														@RequestBody Long[] issueIds,
                                                        @RequestParam Long organizationId) {
		return Optional.ofNullable(reporterFormService.createFormDefectFromIssue(projectId, issueIds,organizationId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.Issue.queryForm.toIssue.byId"));
	}


    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成报表从缺陷到issue")
    @PostMapping("/get/reporter/from/defect")
    public ResponseEntity createFormDefectFromIssue(@PathVariable(name = "project_id") Long projectId, @RequestBody SearchDTO searchDTO, @SortDefault(value = "issueId", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                    @RequestParam Long organizationId) {

        return Optional.ofNullable(reporterFormService.createFormDefectFromIssue(projectId, searchDTO, pageRequest,organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.createForm.toDefect"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成整个项目的excel")
    @GetMapping("/download/excel")
    public ResponseEntity downLoadByProject(@PathVariable(name = "project_id") Long projectId,
                         HttpServletRequest request,
                         HttpServletResponse response,
                                  @RequestParam Long organizationId) {
        excelService.exportCaseByProject(projectId, request, response,organizationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成整个版本的excel")
    @GetMapping("/download/excel/version")
    public ResponseEntity downLoadByVersion(@PathVariable(name = "project_id") Long projectId,
                                  @RequestParam(name = "versionId") Long versionId,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  @RequestParam Long organizationId) {
        excelService.exportCaseByVersion(projectId, versionId, request, response,organizationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成整个文件夹的excel")
    @GetMapping("/download/excel/folder")
    public ResponseEntity downLoadByFolder(@PathVariable(name = "project_id") Long projectId,
                                  @RequestParam(name = "folderId") Long folderId,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                 @RequestParam Long organizationId) {
        excelService.exportCaseByFolder(projectId,folderId, request, response,organizationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成excel模板")
    @GetMapping("/download/excel/template")
    public void downLoadTemplate(@PathVariable(name = "project_id") Long projectId,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        excelService.exportCaseTemplate(projectId, request, response);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("导出之前失败过的excel")
    @GetMapping("/download/excel/fail")
    public ResponseEntity downLoadByFolder(@PathVariable(name = "project_id") Long projectId,
                                           @RequestParam(name = "historyId") Long historyId) {
        excelService.exportFailCase(projectId,historyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成excel导入模板")
    @GetMapping("/download/excel/import_template")
    public void downloadImportTemplate(@PathVariable("project_id") Long projectId,
                                       HttpServletResponse response) {
        excelImportService.downloadImportTemp(response);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("从excel导入模板导入issue以及测试用例")
    @PostMapping("/import/testCase")
    public ResponseEntity importIssues(@PathVariable("project_id") Long projectId,
                                       @RequestParam Long versionId,
                                       @RequestParam("file") MultipartFile excelFile) {
        excelImportService.importIssueByExcel(projectId, versionId,
                DetailsHelper.getUserDetails().getUserId(),
                ExcelUtil.getWorkbookFromMultipartFile(ExcelUtil.Mode.XSSF, excelFile));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
