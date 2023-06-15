
package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import java.util.Optional;

import io.choerodon.core.iam.ResourceLevel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestCaseStepVO;
import io.choerodon.test.manager.app.service.TestCaseStepService;
import io.choerodon.swagger.annotation.Permission;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/case/step")
public class TestCaseStepController {

    @Autowired
    TestCaseStepService testCaseStepService;


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询")
    @GetMapping("/query/{caseId}")
    public ResponseEntity<List<TestCaseStepVO>> query(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId,
                                                      @ApiParam(value = "用例id", required = true)
                                                      @PathVariable(name = "caseId")
                                                      @Encrypt Long caseId) {
        TestCaseStepVO testCaseStepVO = new TestCaseStepVO();
        testCaseStepVO.setIssueId(caseId);
        return Optional.ofNullable(testCaseStepService.query(testCaseStepVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testCycleCase.query.cycleId"));
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("变动一个测试步骤(添加|修改)")
    @PutMapping("/change")
    public ResponseEntity<TestCaseStepVO> changeOneStep(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId,
                                                        @ApiParam(value = "测试步骤", required = true)
                                                        @RequestBody TestCaseStepVO testCaseStepVO) {


        return Optional.ofNullable(testCaseStepService.changeStep(testCaseStepVO, projectId,true))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testCycleCase.update"));
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除测试步骤")
    @DeleteMapping
    public ResponseEntity<Boolean> removeStep(@ApiParam(value = "项目id", required = true)
                                              @PathVariable(name = "project_id") Long projectId,
                                              @ApiParam(value = "测试步骤", required = true)
                                              @RequestBody TestCaseStepVO testCaseStepVO) {
        testCaseStepService.removeStep(projectId,testCaseStepVO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("克隆")
    @PostMapping("/clone")
    public ResponseEntity<TestCaseStepVO> clone(@ApiParam(value = "项目id", required = true)
                                                @PathVariable(name = "project_id") Long projectId,
                                                @ApiParam(value = "复制的测试步骤", required = true)
                                                @RequestBody TestCaseStepVO testCaseStepVO) {
        return Optional.ofNullable(testCaseStepService.clone(testCaseStepVO, projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testCycleCase.clone"));
    }


}
