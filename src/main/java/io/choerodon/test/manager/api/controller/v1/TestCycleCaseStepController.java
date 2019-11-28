package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.api.vo.TestCycleCaseStepVO;
import io.choerodon.test.manager.api.vo.ExecutionStatusVO;
import io.choerodon.test.manager.app.service.TestCycleCaseStepService;

/**
 * Created by 842767365@qq.com on 6/14/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/cycle/case/step")
public class TestCycleCaseStepController {

    @Autowired
    TestCycleCaseStepService testCycleCaseStepService;

    /**
     * 更新循环步骤
     *
     * @param testCycleCaseStepVO
     * @return
     */
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新一个循环步骤")
    @PutMapping
    public ResponseEntity<List<TestCycleCaseStepVO>> update(@RequestBody List<TestCycleCaseStepVO> testCycleCaseStepVO) {
        return Optional.ofNullable(testCycleCaseStepService.update(testCycleCaseStepVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testCycleCaseStep.update"));
    }

    /**
     * 查询循环测试步骤
     *
     * @param cycleCaseId cycleCaseId
     * @return TestCycleCaseStepVO
     */
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询循环步骤")
    @GetMapping("/query/{cycleCaseId}")
    public ResponseEntity<List<TestCycleCaseStepVO>> querySubStep(@PathVariable(name = "project_id") Long projectId,
                                                                  @ApiParam(value = "cycleCaseId", required = true)
                                                                  @PathVariable(name = "cycleCaseId") Long cycleCaseId,
                                                                  @RequestParam Long organizationId) {
        return Optional.ofNullable(testCycleCaseStepService.querySubStep(cycleCaseId, projectId, organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycleCaseStep.query"));

    }
}
