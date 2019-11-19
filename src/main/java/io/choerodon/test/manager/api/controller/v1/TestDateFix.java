package io.choerodon.test.manager.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.app.service.TestIssueFolderService;

/**
 * @author: 25499
 * @date: 2019/11/18 10:36
 * @description:
 */
@RestController
@RequestMapping(value = "/v1/projects/fix")
public class TestDateFix {
    @Autowired
    private TestIssueFolderService testIssueFolderService;
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("迁移issueFolder数据")
    @GetMapping
    public ResponseEntity delete() {
        testIssueFolderService.fixVersionFolder();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
