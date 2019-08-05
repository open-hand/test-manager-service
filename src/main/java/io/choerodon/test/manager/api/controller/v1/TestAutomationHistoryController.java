package io.choerodon.test.manager.api.controller.v1;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.github.pagehelper.PageInfo;

import io.choerodon.base.domain.Sort;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.annotation.Permission;
import io.choerodon.mybatis.annotation.SortDefault;
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
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    public ResponseEntity<PageInfo<TestAutomationHistoryVO>> queryWithInstance(@RequestBody(required = false) Map map,
                                                                               @SortDefault(value = "id", direction = Sort.Direction.DESC)
                                                                                       PageRequest pageRequest,
                                                                               @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(testAutomationHistoryService.queryWithInstance(map, pageRequest, projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.queryWithInstance"));
    }

    @GetMapping("/queryLog/{logId}")
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    public ResponseEntity queryLog(@PathVariable("logId") Long logId, @PathVariable("project_id") Long projectId) {
        TestAppInstanceLogDTO logE = new TestAppInstanceLogDTO();
        logE.setId(logId);

        return Optional.ofNullable(testAppInstanceLogService.queryLog(logId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.queryWithInstance"));
    }
}
