package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.test.manager.api.dto.TestCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCaseStepService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@RestController
@RequestMapping(value = "/case/step")
public class TestCaseStepController {

    @Autowired
    TestCaseStepService iTestCaseStepService;


//    @Permission(permissionPublic = true)
//    @ApiOperation("批量变动测试步骤(添加|修改)")
//    @PutMapping("/changes")
//    public boolean changeStep(@RequestBody List<TestCaseStepDTO> testCaseStepDTO) {
//        return iTestCaseStepService.changeStep(testCaseStepDTO);
//    }

    @Permission(permissionPublic = true)
    @ApiOperation("变动一个测试步骤(添加|修改)")
    @PutMapping("/change")
    public ResponseEntity<Boolean> changeOneStep(@RequestBody TestCaseStepDTO testCaseStepDTO) {
        iTestCaseStepService.changeStep(testCaseStepDTO);
        return new ResponseEntity<>(true, HttpStatus.NO_CONTENT);
    }


    @Permission(permissionPublic = true)
    @ApiOperation("删除测试步骤")
    @DeleteMapping
    public ResponseEntity<Boolean> removeStep(@RequestBody TestCaseStepDTO testCaseStepDTO) {
        iTestCaseStepService.removeStep(testCaseStepDTO);
        return new ResponseEntity<>(true, HttpStatus.NO_CONTENT);
    }

}
