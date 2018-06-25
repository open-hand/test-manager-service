package com.test.devops.api.controller;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by jialongZuo@hand-china.com on 6/25/18.
 */
@RestController
@RequestMapping(value = "/test/defect")
public class TestCycleCaseDefectRelController {

	@Autowired
	TestCycleCaseDefectRelService testCycleCaseDefectRelService;

	@PostMapping
	public ResponseEntity<TestCycleCaseDefectRelDTO> insert(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTOS) {
		return Optional.ofNullable(testCycleCaseDefectRelService.insert(testCycleCaseDefectRelDTOS))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testStatus.insert"));
	}

	@DeleteMapping
	public void remove(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO) {
		testCycleCaseDefectRelService.delete(testCycleCaseDefectRelDTO);
	}
}
