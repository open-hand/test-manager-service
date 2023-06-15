package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.api.vo.TestStatusVO;
import io.choerodon.test.manager.app.service.TestStatusService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/status")
public class TestStatusController {

    @Autowired
    TestStatusService testStatusService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询状态")
    @PostMapping("/query")
    public ResponseEntity<List<TestStatusVO>> query(@ApiParam(value = "项目id", required = true)
                                                    @PathVariable(name = "project_id") Long projectId,
                                                    @ApiParam(value = "查询参数", required = true)
                                                    @RequestBody TestStatusVO testStatusVO) {
        return Optional.ofNullable(testStatusService.query(projectId, testStatusVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testStatus.query"));

    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("更新状态")
    @PutMapping("/update")
    public ResponseEntity<TestStatusVO> update(@ApiParam(value = "项目id", required = true)
                                               @PathVariable(name = "project_id") Long projectId,
                                               @ApiParam(value = "更新vo", required = true)
                                               @RequestBody TestStatusVO testStatusVO) {
        return Optional.ofNullable(testStatusService.update(testStatusVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testStatus.update"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除状态")
    @DeleteMapping("/{statusId}")
    public ResponseEntity<Boolean> delete(@ApiParam(value = "项目id", required = true)
                                          @PathVariable(name = "project_id") Long projectId,
                                          @ApiParam(value = "状态id", required = true)
                                          @PathVariable(name = "statusId")
                                          @Encrypt Long statusId) {
        TestStatusVO dto = new TestStatusVO();
        dto.setStatusId(statusId);
        return Optional.ofNullable(testStatusService.delete(dto))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testStatus.delete"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("插入状态")
    @PostMapping
    public ResponseEntity<TestStatusVO> insert(@ApiParam(value = "项目id", required = true)
                                               @PathVariable(name = "project_id") Long projectId,
                                               @ApiParam(value = "创建vo", required = true)
                                               @RequestBody TestStatusVO testStatusVO) {
        return Optional.ofNullable(testStatusService.insert(testStatusVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testStatus.insert"));
    }
}
