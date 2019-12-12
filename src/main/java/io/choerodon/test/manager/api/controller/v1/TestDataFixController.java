package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.test.manager.api.vo.DemoPayload;
import io.choerodon.test.manager.app.eventhandler.DemoDataEventHandler;
import io.choerodon.test.manager.app.service.DemoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
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

    @Autowired
    private DemoService demoService;

    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR, InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("迁移数据")
    @GetMapping
    public ResponseEntity fix() {
        dataMigrationService.fixData();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR, InitRoleCode.SITE_DEVELOPER})
    @ApiOperation("demo")
    @PostMapping("/demo")
    public ResponseEntity demoInit(@RequestBody DemoPayload demoPayload) {
        demoService.demoInit(demoPayload);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
