package io.choerodon.test.manager.api.controller.v1;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.TestPlanService;
import io.choerodon.test.manager.infra.dto.TestPlanDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author zhaotianxin
 * @since 2019/11/26
 */
@RestController
@RequestMapping("/v1/projects/{project_id}/plan")
public class TestPlanController {

    @Autowired
    private TestPlanService testPlanService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("创建测试计划")
    @PostMapping
    public ResponseEntity<TestPlanDTO> create(@ApiParam(value = "项目id", required = true)
                                              @PathVariable("project_id") Long projectId,
                                              @ApiParam(value = "测试计划创建vo", required = true)
                                              @RequestBody TestPlanVO testPlanVO) {
        return new ResponseEntity<>(testPlanService.create(projectId, testPlanVO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("测试计划树形展示")
    @GetMapping("/tree")
    public ResponseEntity<TestTreeIssueFolderVO> queryTree(@ApiParam(value = "项目id", required = true)
                                                           @PathVariable("project_id") Long projectId,
                                                           @ApiParam(value = "测试计划状态编码", required = true)
                                                           @RequestParam("status_code") String statusCode) {
        return new ResponseEntity<>(testPlanService.buildPlanTree(projectId, statusCode), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("更新测试计划")
    @PutMapping
    public ResponseEntity<TestPlanVO> update(@ApiParam(value = "项目id", required = true)
                                             @PathVariable("project_id") Long projectId,
                                             @ApiParam(value = "测试计划更新vo", required = true)
                                             @RequestBody TestPlanVO testPlanVO) {
        return new ResponseEntity<>(testPlanService.update(projectId, testPlanVO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("修改计划时查询计划的详情")
    @GetMapping("/{plan_id}/query")
    public ResponseEntity<TestPlanVO> query(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "测试计划id", required = true)
                                            @PathVariable(name = "plan_id")
                                            @Encrypt Long planId) {
        return new ResponseEntity<>(testPlanService.queryPlanInfo(projectId, planId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询计划的详情")
    @GetMapping("/{plan_id}/info")
    public ResponseEntity<TestPlanVO> queryInfo(@ApiParam(value = "项目id", required = true)
                                                @PathVariable(name = "project_id") Long projectId,
                                                @ApiParam(value = "测试计划id", required = true)
                                                @PathVariable(name = "plan_id")
                                                @Encrypt Long planId) {
        return new ResponseEntity<>(testPlanService.queryPlan(projectId, planId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("改变计划的状态")
    @PostMapping("/update_status")
    public ResponseEntity<Void> updateStatus(@ApiParam(value = "项目id", required = true)
                                             @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "计划更新vo", required = true)
                                             @RequestBody TestPlanDTO testPlanDTO) {
        testPlanService.updateStatusCode(projectId, testPlanDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除测试计划")
    @DeleteMapping("/{plan_id}/delete")
    public ResponseEntity<Void> deletePlan(@ApiParam(value = "项目id", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @ApiParam(value = "计划id", required = true)
                                           @PathVariable(name = "plan_id")
                                           @Encrypt Long planId) {
        testPlanService.delete(projectId, planId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("复制计划")
    @PostMapping("/{plan_id}/clone")
    public ResponseEntity<TestPlanVO> clonePlan(@ApiParam(value = "项目id", required = true)
                                                @PathVariable(name = "project_id") Long projectId,
                                                @ApiParam(value = "复制的计划id", required = true)
                                                @PathVariable(name = "plan_id")
                                                @Encrypt Long planId,
                                                @ApiParam(value = "复制名称", required = true)
                                                @RequestParam("name") String name) {

        return new ResponseEntity<>(testPlanService.clone(projectId, planId, name), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询项目下所有计划")
    @GetMapping("/project_plan")
    public ResponseEntity<List<TestPlanVO>> allPlan(@ApiParam(value = "项目id", required = true)
                                                    @PathVariable(name = "project_id") Long projectId) {

        return new ResponseEntity<>(testPlanService.projectPlan(projectId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询项目下状态总数")
    @GetMapping("/form_status/{plan_id}")
    public ResponseEntity<List<FormStatusVO>> formStatus(@ApiParam(value = "项目id", required = true)
                                                         @PathVariable(name = "project_id") Long projectId,
                                                         @ApiParam(value = "计划id", required = true)
                                                         @PathVariable(name = "plan_id")
                                                         @Encrypt Long planId) {
        return new ResponseEntity<>(testPlanService.planStatus(projectId, planId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询测试报告概览信息")
    @GetMapping("/{plan_id}/reporter/info")
    public ResponseEntity<TestPlanReporterInfoVO> reporterInfo(@ApiParam(value = "项目id", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               @ApiParam(value = "计划id", required = true)
                                                               @PathVariable(name = "plan_id") @Encrypt Long planId) {
        return new ResponseEntity<>(testPlanService.reporterInfo(projectId, planId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询测试报告未通过的问题项")
    @PostMapping("/{plan_id}/reporter/issue")
    public ResponseEntity<Page<TestPlanReporterIssueVO>> pagedQueryIssues(@ApiParam(value = "项目id", required = true)
                                                                          @PathVariable(name = "project_id") Long projectId,
                                                                          @ApiParam(value = "计划id", required = true)
                                                                          @PathVariable(name = "plan_id") @Encrypt Long planId,
                                                                          @ApiParam(value = "分页信息", required = true) @ApiIgnore
                                                                          @SortDefault PageRequest pageRequest,
                                                                          @ApiParam(value = "查询参数", required = true)
                                                                          @RequestBody TestPlanReporterIssueVO query) {
        return ResponseEntity.ok(testPlanService.pagedQueryIssues(projectId, planId, pageRequest, query));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询测试报告缺陷")
    @PostMapping("/{plan_id}/reporter/bug")
    public ResponseEntity<Page<TestPlanReporterIssueVO>> pagedQueryBugs(@ApiParam(value = "项目id", required = true)
                                                                        @PathVariable(name = "project_id") Long projectId,
                                                                        @ApiParam(value = "计划id", required = true)
                                                                        @PathVariable(name = "plan_id") @Encrypt Long planId,
                                                                        @ApiParam(value = "分页信息", required = true) @ApiIgnore
                                                                        @SortDefault PageRequest pageRequest,
                                                                        @ApiParam(value = "查询参数", required = true)
                                                                        @RequestBody TestPlanReporterIssueVO query) {
        return ResponseEntity.ok(testPlanService.pagedQueryBugs(projectId, planId, pageRequest, query));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验计划名称是否重复")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@ApiParam(value = "项目id", required = true)
                                             @PathVariable("project_id") Long projectId,
                                             @ApiParam(value = "校验名称", required = true)
                                             @RequestParam("name") String name) {
        return new ResponseEntity<>(testPlanService.checkName(projectId, name), HttpStatus.OK);
    }
}
