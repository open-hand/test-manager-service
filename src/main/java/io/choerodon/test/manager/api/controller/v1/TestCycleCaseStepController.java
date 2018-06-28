package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseStepService;
import io.choerodon.core.exception.CommonException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/14/18.
 */
@RestController
@RequestMapping(value = "/v1/cycle/case/step")
public class TestCycleCaseStepController {

    @Autowired
    TestCycleCaseStepService testCycleCaseStepService;

    /**
     * 更新循环步骤
     *
     * @param testCycleCaseStepDTO
     * @return
     */
	@Permission(permissionPublic = true)
	@ApiOperation("更新循环步骤")
	@PutMapping
	ResponseEntity<List<TestCycleCaseStepDTO>> update(@RequestBody List<TestCycleCaseStepDTO> testCycleCaseStepDTO) {
        return Optional.ofNullable(testCycleCaseStepService.update(testCycleCaseStepDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycleCaseStep.update"));

    }


    /**
     * 查询循环测试步骤
     *
     * @param cycleCaseId
     * @return
     */
	@Permission(permissionPublic = true)
	@ApiOperation("查询循环步骤")
	@GetMapping("/query/{CycleCaseId}")
	ResponseEntity<List<TestCycleCaseStepDTO>> querySubStep(@ApiParam(value = "log id", required = true)
															@PathVariable Long cycleCaseId) {

        return Optional.ofNullable(testCycleCaseStepService.querySubStep(cycleCaseId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycleCaseStep.query"));

    }

//    /**
//     * 启动循环测试下所有步骤
//     *
//     * @param testCycleCaseDTO
//     */
//    void createTestCycleCaseStep(TestCycleCaseDTO testCycleCaseDTO) {
//        testCycleCaseStepService.createTestCycleCaseStep(testCycleCaseDTO);
//    }
//
//    /**
//     * 删除CycleCase下所有Step
//     *
//     * @param testCycleCaseDTO
//     */
//    void deleteByTestCycleCase(TestCycleCaseDTO testCycleCaseDTO);

}
