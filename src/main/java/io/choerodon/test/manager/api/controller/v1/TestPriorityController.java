package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import java.util.Optional;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.app.service.TestPriorityService;
import io.swagger.annotations.ApiParam;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import io.choerodon.test.manager.infra.dto.TestPriorityDTO;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;

/**
 *  管理 API
 *
 * @author jiaxu.cui@hand-china.com 2020-08-19 17:25:29
 */
@RestController("testPriorityController.v1")
@RequestMapping("/v1/organizations/{organization_id}/test_priority")
public class TestPriorityController extends BaseController {

    @Autowired
    private TestPriorityService testPriorityService;

    @ApiOperation(value = "优先级列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<List<TestPriorityDTO>> list(@PathVariable("organization_id") Long organizationId,
                                                      TestPriorityDTO testPriorityDTO) {
        testPriorityDTO.setOrganizationId(organizationId);
        return Results.success(testPriorityService.list(organizationId, testPriorityDTO));
    }

    @ApiOperation(value = "创建优先级")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<TestPriorityDTO> create(@PathVariable("organization_id") Long organizationId,
                                                  @RequestBody TestPriorityDTO testPriorityDTO) {
        validObject(testPriorityDTO);
        testPriorityDTO.setOrganizationId(organizationId);
        return Results.success(testPriorityService.create(organizationId, testPriorityDTO));
    }

    @ApiOperation(value = "更新优先级")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<TestPriorityDTO> update(@PathVariable("organization_id") Long organizationId,
                                                  @RequestBody TestPriorityDTO testPriorityDTO) {
        validObject(testPriorityDTO);
        testPriorityDTO.setOrganizationId(organizationId);
        return Results.success(testPriorityService.update(organizationId, testPriorityDTO));
    }

    @ApiOperation(value = "删除")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> remove(@PathVariable("organization_id") Long organizationId,
                                        @RequestBody TestPriorityDTO testPriorityDTO) {
        testPriorityService.delete(organizationId, testPriorityDTO);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "生效/失效优先级")
    @PostMapping("/{id}/enabled")
    public ResponseEntity<Void> enablePriority(@PathVariable("organization_id") Long organizationId,
                                               @ApiParam(value = "id", required = true)
                                               @PathVariable @Encrypt Long id) {
        testPriorityService.changePriorityEnabled(organizationId, id, true);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "生效/失效优先级")
    @PostMapping("/{id}/disabled")
    public ResponseEntity<Void> disablePriority(@PathVariable("organization_id") Long organizationId,
                                                @ApiParam(value = "id", required = true)
                                                @PathVariable @Encrypt Long id) {
        testPriorityService.changePriorityEnabled(organizationId, id, false);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "更新展示顺序")
    @PutMapping(value = "/sequence")
    public ResponseEntity<List<TestPriorityDTO>> updateByList(@PathVariable("organization_id") Long organizationId,
                                                              @RequestBody List<TestPriorityDTO> list) {

        return new ResponseEntity<>(testPriorityService.updateByList(list, organizationId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验优先级名字是否未被使用")
    @GetMapping(value = "/check_name")
    public ResponseEntity<Boolean> checkName(@PathVariable("organization_id") Long organizationId,
                                             @RequestParam("name") String name) {
        return Optional.ofNullable(testPriorityService.checkName(organizationId, name))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.priorityName.check"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验删除优先级")
    @GetMapping("/check_delete/{id}")
    public ResponseEntity<Long> checkDelete(@PathVariable("organization_id") Long organizationId,
                                            @ApiParam(value = "id", required = true)
                                            @PathVariable @Encrypt Long id) {
        return new ResponseEntity<>(testPriorityService.checkDelete(organizationId, id), HttpStatus.OK);
    }
}
