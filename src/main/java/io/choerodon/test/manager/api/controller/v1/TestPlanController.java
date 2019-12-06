package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.api.vo.TestPlanVO;
import io.choerodon.test.manager.api.vo.TestTreeIssueFolderVO;
import io.choerodon.test.manager.app.service.TestPlanServcie;
import io.choerodon.test.manager.infra.dto.TestPlanDTO;
import io.swagger.annotations.ApiOperation;
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

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建测试计划")
    @PostMapping
    public ResponseEntity<TestPlanDTO> create(@PathVariable("project_id") Long projectId,
                                              @RequestBody TestPlanVO testPlanVO) {
        return new ResponseEntity<>(testPlanServcie.create(projectId, testPlanVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("测试计划树形展示")
    @GetMapping("/tree")
    public ResponseEntity<TestTreeIssueFolderVO> queryTree(@PathVariable("project_id") Long projectId,
                                                           @RequestParam("status_code") String statusCode) {
        return new ResponseEntity<>(testPlanServcie.ListPlanAndFolderTree(projectId, statusCode), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新测试计划")
    @PutMapping
    public ResponseEntity<TestPlanVO> update(@PathVariable("project_id") Long projectId,
                                              @RequestBody TestPlanVO testPlanVO) {
        return new ResponseEntity<>(testPlanServcie.update(projectId, testPlanVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改计划时查询计划的详情")
    @GetMapping("/{plan_id}/query")
    public ResponseEntity<TestPlanVO> query(@PathVariable(name = "project_id") Long projectId,
                                               @PathVariable(name = "plan_id")Long planId){
       return new ResponseEntity<>(testPlanServcie.queryPlanInfo(projectId,planId),HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询计划的详情")
    @GetMapping("/{plan_id}/info")
    public ResponseEntity<TestPlanVO> queryInfo(@PathVariable(name = "project_id") Long projectId,
                                            @PathVariable(name = "plan_id")Long planId){
        return new ResponseEntity<>(testPlanServcie.queryPlan(projectId,planId),HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("改变计划的状态")
    @PostMapping("/update_status")
    public ResponseEntity updateStatus(@PathVariable(name = "project_id") Long projectId,
                                                 @RequestBody TestPlanDTO testPlanDTO){
        testPlanServcie.updateStatusCode(projectId,testPlanDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除测试计划")
    @DeleteMapping("/{plan_id}/delete")
    public ResponseEntity deletePlan(@PathVariable(name = "project_id") Long projectId,
                                     @PathVariable(name = "plan_id") Long planId){
        testPlanServcie.delete(projectId,planId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("复制计划")
    @DeleteMapping("/{plan_id}/clone")
    public ResponseEntity<TestPlanVO> clonePlan(@PathVariable(name = "project_id") Long projectId,
                                     @PathVariable(name = "plan_id") Long planId){

        return new ResponseEntity<>(testPlanServcie.clone(projectId,planId),HttpStatus.OK);
    }
}
