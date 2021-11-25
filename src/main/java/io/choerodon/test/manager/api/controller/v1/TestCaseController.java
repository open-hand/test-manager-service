package io.choerodon.test.manager.api.controller.v1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.api.vo.agile.IssueListFieldKVVO;
import io.choerodon.test.manager.infra.util.EncryptUtil;
import io.choerodon.test.manager.infra.util.VerifyUpdateUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.springframework.beans.factory.annotation.Autowired;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.test.manager.api.vo.agile.SearchDTO;
import io.choerodon.mybatis.pagehelper.domain.Sort;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.test.manager.app.service.*;

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
    private EncryptUtil encryptUtil;

    @Autowired
    private TestCaseAsyncService testCaseAsyncService;

    @Autowired
    public TestCaseController(ExcelServiceHandler excelServiceHandler) {
        this.excelServiceHandler = excelServiceHandler;
    }

    public void setExcelServiceHandler(ExcelServiceHandler excelServiceHandler) {
        this.excelServiceHandler = excelServiceHandler;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("生成报表从issue到缺陷")
    @PostMapping("/get/reporter/from/issue")
    public ResponseEntity<Page<ReporterFormVO>> createFormsFromIssueToDefect(@PathVariable(name = "project_id") Long projectId,
                                                                             @RequestBody SearchDTO searchDTO,
                                                                             @ApiIgnore
                                                                             @ApiParam(value = "分页信息", required = true)
                                                                             @SortDefault(value = "issueId", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                             @RequestParam Long organizationId) {
        return Optional.ofNullable(reporterFormService.createFromIssueToDefect(projectId, searchDTO, pageRequest, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.createForm.toDefect"));
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("通过缺陷Id生成报表从缺陷到issue")
    @PostMapping("/get/reporter/from/defect/by/issueId")
    public ResponseEntity<List<DefectReporterFormVO>> createFormDefectFromIssueById(@PathVariable(name = "project_id") Long projectId,
                                                                                    @RequestBody Long[] issueIds,
                                                                                    @RequestParam Long organizationId) {

        return Optional.ofNullable(reporterFormService.createFormDefectFromIssue(projectId, issueIds, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.queryForm.toIssue.byId"));
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("生成报表从缺陷到issue")
    @PostMapping("/get/reporter/from/defect")
    public ResponseEntity<Page<ReporterFormVO>> createFormDefectFromIssue(@PathVariable(name = "project_id") Long projectId,
                                                                          @RequestBody SearchDTO searchDTO,
                                                                          @SortDefault(value = "issueId", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                          @RequestParam Long organizationId) {

        return Optional.ofNullable(reporterFormService.createFormDefectFromIssue(projectId, searchDTO, pageRequest, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.createForm.toDefect"));
    }


    /**
     * 导出选择文件夹下的所有用例
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("生成整个文件夹的excel")
    @GetMapping("/download/excel/folder")
    public ResponseEntity downLoadByFolder(@PathVariable(name = "project_id") Long projectId,
                                           @RequestParam(name = "folder_id")
                                           @Encrypt Long folderId,
                                           HttpServletRequest request,
                                           HttpServletResponse response,
                                           @RequestParam Long organizationId) {
        excelServiceHandler.exportCaseByFolder(projectId, folderId, request, response, organizationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("生成excel模板")
    @GetMapping("/download/excel/template")
    public void downLoadTemplate(@PathVariable(name = "project_id") Long projectId,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        excelService.exportCaseTemplate(projectId, request, response);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("导出之前失败过的excel")
    @GetMapping("/download/excel/fail")
    public ResponseEntity downExcelFail(@PathVariable(name = "project_id") Long projectId,
                                        @RequestParam(name = "historyId")
                                        @Encrypt Long historyId) {
        excelServiceHandler.exportFailCase(projectId, historyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("生成excel导入模板")
    @GetMapping("/download/excel/import_template")
    public void downloadImportTemplate(@PathVariable("project_id") Long projectId,
                                       HttpServletRequest request,
                                       HttpServletResponse response,
                                       @RequestParam Long organizationId) {
        excelImportService.downloadImportTemp(request, response, organizationId, projectId);
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("从excel导入模板导入issue以及测试步骤")
    @PostMapping("/import/testCase")
    public ResponseEntity importIssues(@PathVariable("project_id") Long projectId,
                                       @RequestParam("file") MultipartFile excelFile,
                                       @RequestParam("folder_id")
                                       @Encrypt Long folderId) {
        excelImportService.validateFileSize(excelFile);
        InputStream inputStream;
        try {
            inputStream = excelFile.getInputStream();
        } catch (IOException e) {
            throw new CommonException("error.io.new.workbook", e);
        }
        excelImportService.importIssueByExcel(projectId, folderId,
                DetailsHelper.getUserDetails().getUserId(),
                inputStream, EncryptContext.encryptType(), RequestContextHolder.currentRequestAttributes());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("创建测试用例")
    @PostMapping("/create")
    public ResponseEntity<TestCaseRepVO> createTestCase(@PathVariable("project_id") Long projectId,
                                                        @RequestBody TestCaseVO testCaseVO) {
        return new ResponseEntity<>(testCaseService.createTestCase(projectId, testCaseVO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询用例详情")
    @GetMapping("/{case_id}/info")
    public ResponseEntity<TestCaseInfoVO> queryCaseInfo(@PathVariable("project_id") Long projectId,
                                                        @PathVariable(name = "case_id")
                                                        @Encrypt Long caseId) {
        return new ResponseEntity<>(testCaseService.queryCaseInfo(projectId, caseId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除测试用例")
    @DeleteMapping("/{case_id}/delete")
    public ResponseEntity deleteCase(@PathVariable("project_id") Long projectId,
                                     @PathVariable(name = "case_id")
                                     @Encrypt Long caseId) {
        testCaseService.deleteCase(projectId, caseId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询当前文件夹下面所有子文件夹中用例")
    @PostMapping("/list_by_folder_id")
    public ResponseEntity<Page<TestCaseRepVO>> listCaseByFolderId(@PathVariable("project_id") Long projectId,
                                                                  @RequestParam(name = "folder_id")
                                                                  @Encrypt Long folderId,
                                                                  @SortDefault PageRequest pageRequest,
                                                                  @RequestParam(name = "plan_id", required = false)
                                                                  @Encrypt Long planId,
                                                                  @RequestBody(required = false) SearchDTO searchDTO) {
        encryptUtil.decryptSearchDTO(searchDTO);
        return new ResponseEntity<>(testCaseService.listAllCaseByFolderId(projectId, folderId, pageRequest, searchDTO, planId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("修改测试用例")
    @PutMapping("/update")
    public ResponseEntity<TestCaseRepVO> updateCase(@PathVariable("project_id") Long projectId,
                                                    @RequestBody JSONObject caseUpdate) {
        TestCaseRepVO testCaseRepVO = new TestCaseRepVO();
        List<String> fieldList = verifyUpdateUtil.verifyUpdateData(caseUpdate, testCaseRepVO);
        return new ResponseEntity<>(testCaseService.updateCase(projectId, testCaseRepVO, fieldList.toArray(new String[fieldList.size()])), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("批量移动测试用例")
    @PostMapping("/batch_move")
    public ResponseEntity batchMoveCase(@PathVariable("project_id") Long projectId,
                                        @RequestParam(name = "folder_id")
                                        @Encrypt Long folderId,
                                        @RequestBody List<TestCaseRepVO> testCaseRepVOS) {

        testCaseService.batchMove(projectId, folderId, testCaseRepVOS);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("批量复制测试用例")
    @PostMapping("/batch_clone")
    public ResponseEntity batchCloneCase(@PathVariable("project_id") Long projectId,
                                         @RequestParam(name = "folder_id")
                                         @Encrypt Long folderId,
                                         @RequestBody List<TestCaseRepVO> testCaseRepVOS) {

        testCaseService.batchCopy(projectId, folderId, testCaseRepVOS);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("分页搜索查询未关联case列表")
    @CustomPageRequest
    @GetMapping("/case/summary")
    public ResponseEntity<Page<TestCaseVO>> queryCaseByContent(@PathVariable("project_id") Long projectId,
                                                               @SortDefault(value = "caseId", direction = Sort.Direction.DESC)
                                                                       PageRequest pageRequest,
                                                               @ApiParam(value = "搜索内容")
                                                               @RequestParam(required = false) String content,
                                                               @RequestParam("issueId") @Encrypt Long issueId) {
        return new ResponseEntity<>(testCaseService.queryCaseByContent(projectId, pageRequest, content, issueId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("分页查询未关联问题列表")
    @PostMapping(value = "/agile/un_link_issue/{case_id}")
    public ResponseEntity<Page<IssueListFieldKVVO>> listUnLinkIssue(@ApiIgnore
                                                                     @ApiParam(value = "分页信息", required = true)
                                                                     @SortDefault(value = "issueNum", direction = Sort.Direction.DESC)
                                                                             PageRequest pageRequest,
                                                                     @ApiParam(value = "项目id", required = true)
                                                                     @PathVariable(name = "project_id") Long projectId,
                                                                     @ApiParam(value = "项目id", required = true)
                                                                        @PathVariable(name = "case_id") @Encrypt(ignoreValue = "0") Long caseId,
                                                                     @ApiParam(value = "查询参数", required = true)
                                                                     @RequestBody(required = false) SearchDTO searchDTO,
                                                                     @ApiParam(value = "查询参数", required = true)
                                                                     @RequestParam(required = false) Long organizationId) {
        encryptUtil.decryptSearchDTO(searchDTO);
        return Optional.ofNullable(testCaseService.listUnLinkIssue(caseId, projectId, searchDTO, pageRequest, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.Issue.listUnLinkIssue"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("异步批量删除测试用例")
    @PostMapping("/batch_delete_async")
    public ResponseEntity batchDeleteAsync(@ApiParam(value = "项目id", required = true)
                                           @PathVariable("project_id") Long projectId,
                                           @ApiParam(value = "caseIds", required = true)
                                           @RequestBody @Encrypt List<Long> caseIds) {
        testCaseAsyncService.batchDeleteAsync(projectId, caseIds);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
