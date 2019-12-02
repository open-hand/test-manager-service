package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.agile.api.vo.SearchDTO;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.ExcelServiceHandler;
import io.choerodon.test.manager.app.service.TestCycleCaseService;

/**
 * Created by 842767365@qq.com on 6/12/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/cycle/case")
public class TestCycleCaseController {

    @Autowired
    TestCycleCaseService testCycleCaseService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除测试循环用例")
    @DeleteMapping
    public ResponseEntity delete(@PathVariable(name = "project_id") Long projectId,
                                 Long cycleCaseId) {
        testCycleCaseService.delete(cycleCaseId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询测试组下循环用例")
    @GetMapping("/query/issue/{issueId}")
    public ResponseEntity<List<TestCycleCaseVO>> queryByIssuse(@PathVariable(name = "project_id") Long projectId,
                                                               @PathVariable(name = "issueId") Long issueId,
                                                               @RequestParam Long organizationId) {
        return Optional.ofNullable(testCycleCaseService.queryByIssuse(issueId, projectId, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycleCase.query.issueId"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询测试用例下循环case")
    @PostMapping("/query/cycleId")
    public ResponseEntity<PageInfo<TestCycleCaseVO>> queryByCycle(@PathVariable(name = "project_id") Long projectId,
                                                                  @ApiIgnore
                                                                  @ApiParam(value = "分页信息", required = true)
                                                                  @SortDefault(value = "cycle_id,rank", direction = Sort.Direction.ASC)
                                                                          Pageable pageable,
                                                                  @RequestBody TestCycleCaseVO dto,
                                                                  @RequestParam Long organizationId) {
        Assert.notNull(dto.getCycleId(), "error.queryByCycle.cycleId.not.null");
        return Optional.ofNullable(testCycleCaseService.queryByCycle(dto, pageable, projectId, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycleCase.query.cycleId"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("过滤查询测试用例下循环case")
    @PostMapping("/query/filtered/{cycleId}")
    public ResponseEntity<PageInfo<TestCycleCaseVO>> queryByCycleWithFilterArgs(@PathVariable(name = "project_id") Long projectId,
                                                                                @PathVariable(name = "cycleId") Long cycleId,
                                                                                @RequestBody TestCycleCaseVO searchDTO,
                                                                                @ApiIgnore
                                                                                @ApiParam(value = "分页信息", required = true)
                                                                                @SortDefault(value = "rank", direction = Sort.Direction.ASC)
                                                                                        Pageable pageable) {
        return Optional.ofNullable(testCycleCaseService.queryByCycleWithFilterArgs(cycleId, pageable, projectId, searchDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycleCase.query.cycleId"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询一个循环用例")
    @GetMapping("/query/one/{executeId}")
    public ResponseEntity<TestCycleCaseVO> queryOne(@PathVariable(name = "project_id") Long projectId,
                                                    @PathVariable(name = "executeId") Long executeId,
                                                    @RequestParam(name = "cycleId") Long cycleId,
                                                    @RequestParam Long organizationId) {
        return Optional.ofNullable(testCycleCaseService.queryOne(executeId, projectId, cycleId, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycleCase.query.executeId"));
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("增加一个测试组下循环用例")
    @PostMapping("/insert")
    public ResponseEntity insertOneCase(@RequestBody TestCycleCaseVO testCycleCaseVO, @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(testCycleCaseService.create(testCycleCaseVO, projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testCycleCase.insert"));

    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改一个测试组下循环用例")
    @PostMapping("/update")
    public ResponseEntity updateOneCase(@RequestBody TestCycleCaseVO testCycleCaseVO,
                                        @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(testCycleCaseService.changeOneCase(testCycleCaseVO, projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testCycleCase.update"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("获取时间内case活跃度")
    @PostMapping("/range/{day}/{range}")
    public ResponseEntity getActiveCase(@PathVariable(name = "range") Long range, @PathVariable(name = "project_id") Long projectId, @PathVariable(name = "day") String day) {
        return Optional.ofNullable(testCycleCaseService.getActiveCase(range, projectId, day))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testCycleCase.get.range"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("统计未执行测试")
    @GetMapping("/countCaseNotRun")
    public ResponseEntity countCaseNotRun(@PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(
                testCycleCaseService.countCaseNotRun(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycleCase.query.countCaseNotRun"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("统计未计划测试")
    @GetMapping("/countCaseNotPlain")
    public ResponseEntity countCaseNotPlain(@PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(
                testCycleCaseService.countCaseNotPlain(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycleCase.query.countCaseNotPlain"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("统计测试总数")
    @GetMapping("/countCaseSum")
    public ResponseEntity countCaseSum(@PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(
                testCycleCaseService.countCaseSum(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycleCase.query.countCaseSum"));
    }

    private ExcelServiceHandler excelServiceHandler;

    @Autowired
    public TestCycleCaseController(ExcelServiceHandler excelServiceHandler) {
        this.excelServiceHandler = excelServiceHandler;
    }

    public void setExcelServiceHandler(ExcelServiceHandler excelServiceHandler) {
        this.excelServiceHandler = excelServiceHandler;
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("excel")
    @GetMapping("/download/excel/{cycleId}")
    public ResponseEntity downLoad(@PathVariable(name = "project_id") Long projectId,
                                   @PathVariable(name = "cycleId") Long cycleId,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   @RequestParam Long organizationId) {
        excelServiceHandler.exportCycleCaseInOneCycle(cycleId, projectId, request, response, organizationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询测试执行详情")
    @GetMapping("/{executeId}/info")
    public ResponseEntity<TestCycleCaseInfoVO> queryCaseInfo(@PathVariable("project_id") Long projectId,
                                                             @PathVariable(name = "executeId", required = true) Long executeId) {
        return new ResponseEntity<>(testCycleCaseService.queryCycleCaseInfo(projectId, executeId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询计划下的执行状态总览")
    @GetMapping("/query/status")
    public ResponseEntity<ExecutionStatusVO> queryExecutionStatus(@PathVariable(name = "project_id") Long projectId,
                                                                  @ApiParam(value = "plan_id", required = false)
                                                                  @RequestParam(name = "plan_id") Long planId,
                                                                  @ApiParam(value = "folder_id", required = false)
                                                                  @RequestParam(name = "folder_id") Long folderId) {
        return Optional.ofNullable(testCycleCaseService.queryExecuteStatus(projectId, planId, folderId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.plan.status.query"));

    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新测试执行")
    @PutMapping
    public ResponseEntity update(@PathVariable(name = "project_id") Long projectId,
                                 @RequestBody TestCycleCaseUpdateVO testCycleCaseUpdateVO) {
        testCycleCaseService.update(testCycleCaseUpdateVO);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询当前文件夹下面所有子文件夹中用例")
    @PostMapping("/query/caseList")
    public ResponseEntity<PageInfo<TestFolderCycleCaseVO>> listCaseByFolderId(@PathVariable("project_id") Long projectId,
                                                                              @RequestParam(name = "folder_id") Long folderId,
                                                                              @RequestParam(name = "plan_id") Long planId,
                                                                              @SortDefault Pageable pageable,
                                                                              @RequestBody(required = false) SearchDTO searchDTO) {
        return new ResponseEntity<>(testCycleCaseService.listAllCaseByFolderId(projectId, planId, folderId, pageable, searchDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("批量指派用例")
    @PostMapping("/batchAssign/cycleCase")
    public ResponseEntity batchAssignCase(@PathVariable("project_id") Long projectId,
                                          @RequestParam(name = "assign_user_id") Long assignUserId,
                                          @RequestBody(required = true) List<Long>  cycleCaseIds) {
        testCycleCaseService.batchAssignCycleCase(projectId, assignUserId, cycleCaseIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
