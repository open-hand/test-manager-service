package io.choerodon.test.manager.api.controller.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.vo.TestAutomationResultVO;
import io.choerodon.test.manager.app.service.TestAutomationHistoryService;
import io.choerodon.test.manager.app.service.TestAutomationResultService;

@RestController
@RequestMapping("/v1/projects/{project_id}/automation/result")
public class TestAutomationResultController {

    @Autowired
    private TestAutomationResultService testAutomationResultService;

    @Autowired
    private TestAutomationHistoryService testAutomationHistoryService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询")
    @GetMapping("/query/{id}")
    public ResponseEntity<Map<String, Object>> query(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable("project_id") Long projectId,
                                                     @ApiParam(value = "测试报告id", required = true)
                                                     @PathVariable("id")
                                                     @Encrypt Long id) {
        Map<String, Object> result = new HashMap<>(2);
        String framework = testAutomationHistoryService.queryFrameworkByResultId(projectId, id);
        TestAutomationResultVO testAutomationResultVO = new TestAutomationResultVO();
        testAutomationResultVO.setId(id);
        List<TestAutomationResultVO> list = testAutomationResultService.query(testAutomationResultVO);
        if (!list.isEmpty()) {
            result.put("framework", framework);
            result.put("json", list.get(0).getResult());
            result.put("creationDate", list.get(0).getCreationDate());
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("变动一个测试报告(增加|修改)")
    @PutMapping("/change")
    public ResponseEntity<TestAutomationResultVO> changeOneAutomationResult(@ApiParam(value = "项目id", required = true)
                                                                            @PathVariable("project_id") Long projectId,
                                                                            @ApiParam(value = "更新vo", required = true)
                                                                            @RequestBody TestAutomationResultVO testAutomationResultVO) {
        return Optional.ofNullable(testAutomationResultService.changeAutomationResult(testAutomationResultVO, projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.automationResult.update"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除一个测试报告")
    @DeleteMapping("/remove")
    public ResponseEntity removeAutomationResult(@ApiParam(value = "项目id", required = true)
                                                 @PathVariable("project_id") Long projectId,
                                                 @ApiParam(value = "测试报告vo", required = true)
                                                 @RequestBody TestAutomationResultVO testAutomationResultVO) {
        testAutomationResultService.removeAutomationResult(testAutomationResultVO);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
