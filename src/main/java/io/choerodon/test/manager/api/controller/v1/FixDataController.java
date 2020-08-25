package io.choerodon.test.manager.api.controller.v1;


import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.app.service.FixDataService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 数据修复controller
 */
@RestController
@RequestMapping(value = "/v1/fix_data")
public class FixDataController {

    @Autowired
    private FixDataService fixDataService;

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("【0.24】修复测试用例数据数据")
    @GetMapping("/fix_data_test_case")
    public ResponseEntity<Void> fixDataStateMachine() {
        fixDataService.fixDataTestCase();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
