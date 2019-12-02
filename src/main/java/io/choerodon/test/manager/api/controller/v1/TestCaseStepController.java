
package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.api.vo.TestCaseStepVO;
import io.choerodon.test.manager.app.service.TestCaseStepService;
import io.choerodon.core.annotation.Permission;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/case/step")
public class TestCaseStepController {

    @Autowired
    TestCaseStepService testCaseStepService;


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询")
    @GetMapping("/query/{caseId}")
    public ResponseEntity<List<TestCaseStepVO>> query(@PathVariable(name = "project_id") Long projectId,
                                                      @PathVariable(name = "caseId") Long caseId) {
        TestCaseStepVO testCaseStepVO = new TestCaseStepVO();
        testCaseStepVO.setIssueId(caseId);
        return Optional.ofNullable(testCaseStepService.query(testCaseStepVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testCycleCase.query.cycleId"));
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("变动一个测试步骤(添加|修改)")
    @PutMapping("/change")
    public ResponseEntity<TestCaseStepVO> changeOneStep(@PathVariable(name = "project_id") Long projectId,
                                                        @RequestBody TestCaseStepVO testCaseStepVO) {


        return Optional.ofNullable(testCaseStepService.changeStep(testCaseStepVO, projectId,true))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testCycleCase.update"));
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除测试步骤")
    @DeleteMapping
    public ResponseEntity<Boolean> removeStep(@PathVariable(name = "project_id") Long projectId,
                                              @RequestBody TestCaseStepVO testCaseStepVO) {
        testCaseStepService.removeStep(testCaseStepVO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("克隆")
    @PostMapping("/clone")
    public ResponseEntity clone(@PathVariable(name = "project_id") Long projectId,
                                @RequestBody TestCaseStepVO testCaseStepVO) {
        return Optional.ofNullable(testCaseStepService.clone(testCaseStepVO, projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testCycleCase.clone"));
    }


}
