package io.choerodon.test.manager.api.controller.v1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.choerodon.test.manager.api.vo.TestCaseInfoVO;
import io.choerodon.test.manager.api.vo.TestCaseRepVO;
import io.choerodon.test.manager.api.vo.TestCaseVO;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.util.VerifyUpdateUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.agile.api.vo.SearchDTO;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.oauth.DetailsHelper;
import org.springframework.data.domain.Pageable;
import io.choerodon.core.annotation.Permission;
import org.springframework.data.web.SortDefault;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.util.ExcelUtil;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */

@RestController
@RequestMapping(value = "/v1/projects/{project_id}/case")
public class TestCaseController {
    @Autowired
    ReporterFormService reporterFormService;

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    ExcelService excelService;

    @Autowired
    private TestCaseService testCaseService;

    private ExcelServiceHandler excelServiceHandler;

    @Autowired
    private VerifyUpdateUtil verifyUpdateUtil;

    @Autowired
    public TestCaseController(ExcelServiceHandler excelServiceHandler) {
        this.excelServiceHandler = excelServiceHandler;
    }

    public void setExcelServiceHandler(ExcelServiceHandler excelServiceHandler) {
        this.excelServiceHandler = excelServiceHandler;
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成报表从issue到缺陷")
    @PostMapping("/get/reporter/from/issue")
    public ResponseEntity createFormsFromIssueToDefect(@PathVariable(name = "project_id") Long projectId,
                                                       @RequestBody
                                                               SearchDTO searchDTO,
                                                       @ApiIgnore
                                                       @ApiParam(value = "分页信息", required = true)
                                                       @SortDefault(value = "issueId", direction = Sort.Direction.DESC) Pageable pageable,
                                                       @RequestParam Long organizationId) {

        return Optional.ofNullable(reporterFormService.createFromIssueToDefect(projectId, searchDTO, pageable, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.createForm.toDefect"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("通过IssueId生成issue到缺陷的报表")
    @PostMapping("/get/reporter/from/issue/by/issueId")
    public ResponseEntity createFormsFromIssueToDefectByIssueId(@PathVariable(name = "project_id") Long projectId,
                                                                @RequestBody Long[] issueIds,
                                                                @RequestParam Long organizationId) {

        return Optional.ofNullable(reporterFormService.createFromIssueToDefect(projectId, issueIds, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.createForm.toDefect.byId"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("通过缺陷Id生成报表从缺陷到issue")
    @PostMapping("/get/reporter/from/defect/by/issueId")
    public ResponseEntity createFormDefectFromIssueById(@PathVariable(name = "project_id") Long projectId,
                                                        @RequestBody Long[] issueIds,
                                                        @RequestParam Long organizationId) {

        return Optional.ofNullable(reporterFormService.createFormDefectFromIssue(projectId, issueIds, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.queryForm.toIssue.byId"));
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成报表从缺陷到issue")
    @PostMapping("/get/reporter/from/defect")
    public ResponseEntity createFormDefectFromIssue(@PathVariable(name = "project_id") Long projectId, @RequestBody SearchDTO searchDTO,
                                                    @SortDefault(value = "issueId", direction = Sort.Direction.DESC) Pageable pageable,
                                                    @RequestParam Long organizationId) {

        return Optional.ofNullable(reporterFormService.createFormDefectFromIssue(projectId, searchDTO, pageable, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.createForm.toDefect"));
    }

    // Todo: 重构：导出用例只能选择文件夹下的所有用例,该接口需要删除
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成整个项目的excel")
    @GetMapping("/download/excel")
    public ResponseEntity downLoadByProject(@PathVariable(name = "project_id") Long projectId,
                                            HttpServletRequest request,
                                            HttpServletResponse response,
                                            @RequestParam Long organizationId) {
        excelServiceHandler.exportCaseByProject(projectId, request, response, organizationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Todo: 重构：导出用例只能选择文件夹下的所有用例,该接口需要删除
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成整个版本的excel")
    @GetMapping("/download/excel/version")
    public ResponseEntity downLoadByVersion(@PathVariable(name = "project_id") Long projectId,
                                            @RequestParam(name = "versionId") Long versionId,
                                            HttpServletRequest request,
                                            HttpServletResponse response,
                                            @RequestParam Long organizationId) {
        excelServiceHandler.exportCaseByVersion(projectId, versionId, request, response, organizationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Todo：只导出选择文件夹下的所有用例
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成整个文件夹的excel")
    @GetMapping("/download/excel/folder")
    public ResponseEntity downLoadByFolder(@PathVariable(name = "project_id") Long projectId,
                                           @RequestParam(name = "folder_id") Long folderId,
                                           HttpServletRequest request,
                                           HttpServletResponse response,
                                           @RequestParam Long organizationId) {
        excelServiceHandler.exportCaseByFolder(projectId, folderId, request, response, organizationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成excel模板")
    @GetMapping("/download/excel/template")
    public void downLoadTemplate(@PathVariable(name = "project_id") Long projectId,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        excelService.exportCaseTemplate(projectId, request, response);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("导出之前失败过的excel")
    @GetMapping("/download/excel/fail")
    public ResponseEntity downExcelFail(@PathVariable(name = "project_id") Long projectId,
                                        @RequestParam(name = "historyId") Long historyId) {
        excelServiceHandler.exportFailCase(projectId, historyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("生成excel导入模板")
    @GetMapping("/download/excel/import_template")
    public void downloadImportTemplate(@PathVariable("project_id") Long projectId,
                                       HttpServletRequest request,
                                       HttpServletResponse response,
                                       @RequestParam Long organizationId) {
        excelImportService.downloadImportTemp(request, response, organizationId, projectId);
    }

    // Todo: 重构，导入用例不需要指定版本号
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("从excel导入模板导入issue以及测试步骤")
    @PostMapping("/import/testCase")
    public ResponseEntity importIssues(@PathVariable("project_id") Long projectId,
                                       @RequestParam("file") MultipartFile excelFile,
                                       @RequestParam("folder_id") Long folderId,
                                       @RequestParam("version_id") Long versionId,
                                       @RequestParam Long organizationId) {
        excelImportService.importIssueByExcel(organizationId, projectId, folderId,versionId,
                DetailsHelper.getUserDetails().getUserId(),
                ExcelUtil.getWorkbookFromMultipartFile(ExcelUtil.Mode.XSSF, excelFile));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建测试用例")
    @PostMapping("/create")
    public ResponseEntity<TestCaseVO> createTestCase(@PathVariable("project_id") Long projectId,
                                                     @RequestBody
                                                             TestCaseVO testCaseVO) {
        return new ResponseEntity<>(testCaseService.createTestCase(projectId, testCaseVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询用例详情")
    @GetMapping("/{case_id}/info")
    public ResponseEntity<TestCaseInfoVO> queryCaseInfo(@PathVariable("project_id") Long projectId,
                                                        @PathVariable(name = "case_id", required = true) Long caseId) {
        return new ResponseEntity<>(testCaseService.queryCaseInfo(projectId, caseId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除测试用例")
    @DeleteMapping("/{case_id}/delete")
    public ResponseEntity deleteCase(@PathVariable("project_id") Long projectId,
                                     @PathVariable(name = "case_id", required = true) Long caseId) {
        testCaseService.deleteCase(projectId, caseId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询当前文件夹下面所有子文件夹中用例")
    @PostMapping("/list_by_folder_id")
    public ResponseEntity<PageInfo<TestCaseRepVO>> listCaseByFolderId(@PathVariable("project_id") Long projectId,
                                                                      @RequestParam(name = "folder_id") Long folderId,
                                                                      @SortDefault Pageable pageable,
                                                                      @RequestBody(required = false) SearchDTO searchDTO) {
        return new ResponseEntity<>(testCaseService.listAllCaseByFolderId(projectId, folderId, pageable, searchDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改测试用例")
    @PutMapping("/update")
    public ResponseEntity<TestCaseRepVO> updateCase(@PathVariable("project_id") Long projectId,
                                                    @RequestBody JSONObject caseUpdate) {
        TestCaseRepVO testCaseRepVO = new TestCaseRepVO();
        List<String> fieldList = verifyUpdateUtil.verifyUpdateData(caseUpdate, testCaseRepVO);
        return new ResponseEntity<>(testCaseService.updateCase(projectId, testCaseRepVO, fieldList.toArray(new String[fieldList.size()])), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("批量移动测试用例")
    @PutMapping("/batch_move")
    public ResponseEntity batchMoveCase(@PathVariable("project_id") Long projectId,
                                        @RequestParam(name = "folder_id") Long folderId,
                                         @RequestBody List<TestCaseRepVO> testCaseRepVOS) {

        testCaseService.batchMove(projectId, folderId,testCaseRepVOS);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
