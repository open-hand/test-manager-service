package io.choerodon.test.manager.api.controller.v1;

import java.util.Map;
import java.util.Optional;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.infra.constant.EncryptKeyConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.choerodon.core.domain.Page;


import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.test.manager.api.vo.TestAutomationHistoryVO;
import io.choerodon.test.manager.app.service.TestAppInstanceLogService;
import io.choerodon.test.manager.app.service.TestAutomationHistoryService;
import io.choerodon.test.manager.infra.dto.TestAppInstanceLogDTO;

@RestController
@RequestMapping(value = "/v1/projects/{project_id}/test/automation")
public class TestAutomationHistoryController {

    @Autowired
    TestAutomationHistoryService testAutomationHistoryService;

    @Autowired
    TestAppInstanceLogService testAppInstanceLogService;

    @PostMapping("/queryWithHistroy")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<Page<TestAutomationHistoryVO>> queryWithInstance(@RequestBody(required = false) Map map,
                                                                               @SortDefault(value = "id", direction = Sort.Direction.DESC)
                                                                                       PageRequest pageRequest,
                                                                               @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(testAutomationHistoryService.queryWithInstance(map, pageRequest, projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.queryWithInstance"));
    }

    @GetMapping("/queryLog/{logId}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<String> queryLog(@PathVariable("logId") @Encrypt(EncryptKeyConstants.TEST_APP_INSTANCE_LOG) Long logId, @PathVariable("project_id") Long projectId) {
        TestAppInstanceLogDTO logE = new TestAppInstanceLogDTO();
        logE.setId(logId);

        return Optional.ofNullable(testAppInstanceLogService.queryLog(logId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.queryWithInstance"));
    }
}
