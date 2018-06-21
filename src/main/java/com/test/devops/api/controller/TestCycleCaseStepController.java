package com.test.devops.api.controller;

import com.test.devops.api.dto.TestCycleCaseDTO;
import com.test.devops.api.dto.TestCycleCaseStepDTO;
import com.test.devops.app.service.TestCycleCaseStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/14/18.
 */
@RestController
@RequestMapping(value = "/test/cycle/case/step")
public class TestCycleCaseStepController {

	@Autowired
	TestCycleCaseStepService testCycleCaseStepService;

	/**
	 * 更新循环步骤
	 *
	 * @param testCycleCaseStepDTO
	 * @return
	 */
	@PutMapping
	List<TestCycleCaseStepDTO> update(List<TestCycleCaseStepDTO> testCycleCaseStepDTO) {
		return testCycleCaseStepService.update(testCycleCaseStepDTO);
	}


	/**
	 * 查询循环测试步骤
	 *
	 * @param cycleCaseId
	 * @return
	 */
	@GetMapping("/query/{CycleCaseId}")
	List<TestCycleCaseStepDTO> querySubStep(@PathVariable Long cycleCaseId) {

		return testCycleCaseStepService.querySubStep(cycleCaseId);
	}

//	/**启动循环测试下所有步骤
//	 * @param testCycleCaseDTO
//	 */
//	void createTestCycleCaseStep(TestCycleCaseDTO testCycleCaseDTO){
//		testCycleCaseStepService.createTestCycleCaseStep(testCycleCaseDTO);
//	}

//	/**删除CycleCase下所有Step
//	 * @param testCycleCaseDTO
//	 */
//	void deleteByTestCycleCase(TestCycleCaseDTO testCycleCaseDTO);

}
