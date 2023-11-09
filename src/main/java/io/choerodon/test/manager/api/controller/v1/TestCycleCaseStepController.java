package io.choerodon.test.manager.api.controller.v1;

import java.util.Optional;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.swagger.annotation.Permission;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestCycleCaseStepVO;
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
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("更新一个循环步骤")
    @PutMapping
    public ResponseEntity update(@ApiParam(value = "循环步骤vo", required = true)
                                 @RequestBody TestCycleCaseStepVO testCycleCaseStepVO) {
        testCycleCaseStepService.update(testCycleCaseStepVO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 查询循环测试步骤
     *
     * @param cycleCaseId cycleCaseId
     * @return TestCycleCaseStepVO
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询循环步骤")
    @GetMapping("/query/{cycleCaseId}")
    public ResponseEntity<Page<TestCycleCaseStepVO>> querySubStep(@ApiParam(value = "项目id", required = true)
                                                                  @PathVariable(name = "project_id") Long projectId,
                                                                  @ApiParam(value = "cycleCaseId", required = true)
                                                                  @PathVariable(name = "cycleCaseId")
                                                                  @Encrypt Long cycleCaseId,
                                                                  @ApiParam(value = "组织id", required = true)
                                                                  @RequestParam Long organizationId,
                                                                  @ApiParam(value = "分页信息", required = true)
                                                                  PageRequest pageRequest) {
        return Optional.ofNullable(testCycleCaseStepService.querySubStep(cycleCaseId, projectId, organizationId,pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycleCaseStep.query"));

    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询循环步骤")
    @GetMapping("/query_list/{execute_id}")
    public ResponseEntity<Page<TestCycleCaseStepVO>> queryCaseStep(@ApiParam(value = "项目id", required = true)
                                                                   @PathVariable(name = "project_id") Long projectId,
                                                                   @ApiParam(value = "execute_id", required = true)
                                                                   @PathVariable(name = "execute_id") @Encrypt Long execute_id,
                                                                   @ApiParam(value = "分页信息", required = true)
                                                                   PageRequest pageRequest) {
        return Optional.ofNullable(testCycleCaseStepService.queryCaseStep(execute_id, projectId, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycleCaseStep.query"));

    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除一个循环步骤")
    @DeleteMapping("/{execute_step_id}")
    public ResponseEntity delete(@ApiParam(value = "项目id", required = true)
                                 @PathVariable(name = "project_id") Long projectId,
                                 @ApiParam(value = "执行步骤id", required = true)
                                 @PathVariable(name = "execute_step_id")
                                 @Encrypt Long executeStepId) {
        testCycleCaseStepService.delete(executeStepId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("创建一个循环步骤")
    @PostMapping
    public ResponseEntity create(@ApiParam(value = "项目id", required = true)
                                 @PathVariable(name = "project_id") Long projectId,
                                 @ApiParam(value = "循环步骤创建vo", required = true)
                                 @RequestBody TestCycleCaseStepVO testCycleCaseStepVO) {
        testCycleCaseStepService.create(testCycleCaseStepVO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}