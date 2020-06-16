package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.api.vo.TestStatusVO;
import io.choerodon.test.manager.app.service.TestStatusService;
import io.choerodon.test.manager.infra.constant.EncryptKeyConstants;
import io.swagger.annotations.ApiOperation;
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
    public ResponseEntity<List<TestStatusVO>> query(@PathVariable(name = "project_id") Long projectId,
                                                    @RequestBody TestStatusVO testStatusVO) {
        return Optional.ofNullable(testStatusService.query(projectId, testStatusVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testStatus.query"));

    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("更新状态")
    @PutMapping("/update")
    public ResponseEntity<TestStatusVO> update(@PathVariable(name = "project_id") Long projectId,
                                               @RequestBody TestStatusVO testStatusVO) {
        return Optional.ofNullable(testStatusService.update(testStatusVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testStatus.update"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除状态")
    @DeleteMapping("/{statusId}")
    public ResponseEntity<Boolean> delete(@PathVariable(name = "project_id") Long projectId,
                                          @PathVariable(name = "statusId")
                                          @Encrypt(/**EncryptKeyConstants.TEST_STATUS**/) Long statusId) {
        TestStatusVO dto = new TestStatusVO();
        dto.setStatusId(statusId);
        return Optional.ofNullable(testStatusService.delete(dto))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testStatus.delete"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("插入状态")
    @PostMapping
    public ResponseEntity<TestStatusVO> insert(@PathVariable(name = "project_id") Long projectId,
                                               @RequestBody TestStatusVO testStatusVO) {
        return Optional.ofNullable(testStatusService.insert(testStatusVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testStatus.insert"));
    }
}
