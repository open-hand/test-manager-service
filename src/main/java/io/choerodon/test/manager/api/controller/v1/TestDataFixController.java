package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.swagger.annotation.Permission;

import io.choerodon.test.manager.app.service.DataMigrationService;

/**
 * @author: 25499
 * @date: 2019/11/18 10:36
 * @description:
 */
@RestController
@RequestMapping(value = "/v1/projects/fix")
public class TestDataFixController {

    @Autowired
    private DataMigrationService dataMigrationService;

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("迁移数据")
    @GetMapping
    public ResponseEntity fix() {
        dataMigrationService.fixData();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("【0.24】修复测试用例数据数据")
    @GetMapping("/fix_data_test_case")
    public ResponseEntity<Void> fixDataTestCasePriority() {
        dataMigrationService.fixDataTestCasePriority();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
