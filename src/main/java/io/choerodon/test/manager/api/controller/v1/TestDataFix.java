package io.choerodon.test.manager.api.controller.v1;

import com.netflix.discovery.converters.Auto;
import io.choerodon.test.manager.app.service.DataMigrationService;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
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
import io.choerodon.test.manager.app.service.TestCaseLabelRelService;
import io.choerodon.test.manager.app.service.TestCaseLabelService;
import io.choerodon.test.manager.app.service.TestIssueFolderService;

/**
 * @author: 25499
 * @date: 2019/11/18 10:36
 * @description:
 */
@RestController
@RequestMapping(value = "/v1/projects/fix")
public class TestDataFix {

    @Autowired
    private TestIssueFolderService testIssueFolderService;
    @Autowired
    private TestCaseLabelRelService testCaseLabelRelService;
    @Autowired
    private TestCaseLabelService testCaseLabelService;


    @Autowired
    private DataMigrationService dataMigrationService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("迁移数据")
    @GetMapping
    public ResponseEntity fix() {
        dataMigrationService.fixData();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("迁移用例数据")
    @GetMapping("/migrate_issue")
    public ResponseEntity fixIssue() {
        dataMigrationService.migrateIssue();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("迁移附件数据")
    @GetMapping("/migrate_attachment")
    public ResponseEntity migrateAttachment() {
        dataMigrationService.migrateAttachment();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
