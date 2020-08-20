package io.choerodon.test.manager.api.controller.v1;


import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Created by HuangFuqiang@choerodon.io on 2018/11/13.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/fix_data")
public class FixDataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixDataController.class);

//    @Autowired
//    private NotifyFeignClient notifyFeignClient;

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("【0.24】修复测试用例数据数据")
    @GetMapping("/fix_data_test_case")
    public ResponseEntity fixDataStateMachine() {
//        fixDataService.fixDateStateMachine();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
