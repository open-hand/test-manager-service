package com.test.devops.api.controller;

import com.test.devops.api.dto.TestCaseStepDTO;
import com.test.devops.app.service.TestCaseStepService;
import com.test.devops.domain.entity.TestCaseStepE;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@RestController
@RequestMapping(value = "/test/case/step")
public class TestCaseStepController {

	@Autowired
	TestCaseStepService iTestCaseStepService;


//	@Permission(permissionPublic = true)
//	@ApiOperation("批量变动测试步骤(添加|修改)")
//	@PutMapping("/changes")
//	public boolean changeStep(@RequestBody List<TestCaseStepDTO> testCaseStepDTO){
//		return iTestCaseStepService.changeStep(testCaseStepDTO);
//	}

	@Permission(permissionPublic = true)
	@ApiOperation("变动一个测试步骤(添加|修改)")
	@PutMapping("/change")
	public void changeOneStep(@RequestBody TestCaseStepDTO testCaseStepDTO) {
		iTestCaseStepService.changeStep(testCaseStepDTO);
	}


	@Permission(permissionPublic = true)
	@ApiOperation("删除测试步骤")
	@DeleteMapping
	public void removeStep(@RequestBody TestCaseStepDTO testCaseStepDTO) {
		iTestCaseStepService.removeStep(testCaseStepDTO);
	}

}
