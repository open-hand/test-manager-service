package io.choerodon.test.manager.api.controller.v1;

import java.util.List;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.choerodon.test.manager.api.vo.FormStatusVO;
import io.choerodon.test.manager.api.vo.TestPlanVO;
import io.choerodon.test.manager.api.vo.TestTreeIssueFolderVO;
import io.choerodon.test.manager.app.service.TestPlanServcie;
import io.choerodon.test.manager.infra.constant.EncryptKeyConstants;
import io.choerodon.test.manager.infra.dto.TestPlanDTO;
import io.swagger.annotations.ApiOperation;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaotianxin
 * @since 2019/11/26
 */
@RestController
@RequestMapping("/v1/projects/{project_id}/plan")
public class TestPlanController {

    @Autowired
    private TestPlanServcie testPlanServcie;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("创建测试计划")
    @PostMapping
    public ResponseEntity<TestPlanDTO> create(@PathVariable("project_id") Long projectId,
                                              @RequestBody TestPlanVO testPlanVO) {
        return new ResponseEntity<>(testPlanServcie.create(projectId, testPlanVO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("测试计划树形展示")
    @GetMapping("/tree")
    public ResponseEntity<TestTreeIssueFolderVO> queryTree(@PathVariable("project_id") Long projectId,
                                                           @RequestParam("status_code") String statusCode) {
        return new ResponseEntity<>(testPlanServcie.buildPlanTree(projectId, statusCode), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("更新测试计划")
    @PutMapping
    public ResponseEntity<TestPlanVO> update(@PathVariable("project_id") Long projectId,
                                             @RequestBody TestPlanVO testPlanVO) {
        return new ResponseEntity<>(testPlanServcie.update(projectId, testPlanVO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("修改计划时查询计划的详情")
    @GetMapping("/{plan_id}/query")
    public ResponseEntity<TestPlanVO> query(@PathVariable(name = "project_id") Long projectId,
                                            @PathVariable(name = "plan_id")
                                            @Encrypt(/**EncryptKeyConstants.TEST_ISSUE_FOLDER**/) Long planId) {
        return new ResponseEntity<>(testPlanServcie.queryPlanInfo(projectId, planId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询计划的详情")
    @GetMapping("/{plan_id}/info")
    public ResponseEntity<TestPlanVO> queryInfo(@PathVariable(name = "project_id") Long projectId,
                                                @PathVariable(name = "plan_id")
                                                @Encrypt(/**EncryptKeyConstants.TEST_ISSUE_FOLDER**/) Long planId) {
        return new ResponseEntity<>(testPlanServcie.queryPlan(projectId, planId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("改变计划的状态")
    @PostMapping("/update_status")
    public ResponseEntity updateStatus(@PathVariable(name = "project_id") Long projectId,
                                       @RequestBody TestPlanDTO testPlanDTO) {
        testPlanServcie.updateStatusCode(projectId, testPlanDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除测试计划")
    @DeleteMapping("/{plan_id}/delete")
    public ResponseEntity deletePlan(@PathVariable(name = "project_id") Long projectId,
                                     @PathVariable(name = "plan_id")
                                     @Encrypt(/**EncryptKeyConstants.TEST_ISSUE_FOLDER**/) Long planId) {
        testPlanServcie.delete(projectId, planId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("复制计划")
    @PostMapping("/{plan_id}/clone")
    public ResponseEntity<TestPlanVO> clonePlan(@PathVariable(name = "project_id") Long projectId,
                                                @PathVariable(name = "plan_id")
                                                @Encrypt(/**EncryptKeyConstants.TEST_ISSUE_FOLDER**/) Long planId) {

        return new ResponseEntity<>(testPlanServcie.clone(projectId, planId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询项目下所有计划")
    @GetMapping("/project_plan")
    public ResponseEntity<List<TestPlanVO>> allPlan(@PathVariable(name = "project_id") Long projectId) {

        return new ResponseEntity<>(testPlanServcie.projectPlan(projectId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询项目下状态总数")
    @GetMapping("/form_status/{plan_id}")
    public ResponseEntity<List<FormStatusVO>> formStatus(@PathVariable(name = "project_id") Long projectId,
                                                         @PathVariable(name = "plan_id")
                                                         @Encrypt(/**EncryptKeyConstants.TEST_PLAN**/) Long planId) {
        return new ResponseEntity<>(testPlanServcie.planStatus(projectId, planId), HttpStatus.OK);
    }
}
