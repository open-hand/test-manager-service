package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.dto.TestFileLoadHistoryDTO;
import io.choerodon.test.manager.app.service.TestFileLoadHistoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/v1/projects/{project_id}/test/fileload/history")
public class TestFileLoadHistoryController {
    @Autowired
    TestFileLoadHistoryService testFileLoadHistoryService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询上传历史")
    @GetMapping
    public ResponseEntity<List<TestFileLoadHistoryDTO>> query(@PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(testFileLoadHistoryService.query(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.filehistory.query"));
    }

}
