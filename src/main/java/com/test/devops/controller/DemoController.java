package com.test.devops.controller;

import com.test.devops.service.DemoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.swagger.annotation.Permission;

@RestController
@RequestMapping("/v1")
public class DemoController {

    @Autowired
    DemoService demoService;

    @Permission(permissionPublic = true)
    @ApiOperation(value = "运行的Demo")
    @RequestMapping(value = "/demo", method = RequestMethod.GET)
    public String getVersion() {
        return demoService.hello();
    }
}
