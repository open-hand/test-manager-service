package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.test.manager.api.dto.TestStatusDTO;
import io.choerodon.test.manager.app.service.TestStatusService;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
@RestController
@RequestMapping(value = "/test/status")
public class TestStatusController {

	@Autowired
	TestStatusService testStatusService;

	@PostMapping("/query")
	public ResponseEntity<List<TestStatusDTO>> query(TestStatusDTO testStatusDTO) {
		return Optional.ofNullable(testStatusService.query(testStatusDTO))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testStatus.query"));

	}

	@DeleteMapping
	public void delete(TestStatusDTO testStatusDTO) {
		testStatusService.delete(testStatusDTO);
	}

	@PostMapping
	public ResponseEntity<TestStatusDTO> insert(TestStatusDTO testStatusDTO) {
		return Optional.ofNullable(testStatusService.insert(testStatusDTO))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testStatus.insert"));
	}

	@PutMapping
	public ResponseEntity<TestStatusDTO> update(TestStatusDTO testStatusDTO) {
		return Optional.ofNullable(testStatusService.update(testStatusDTO))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testStatus.update"));
	}
}
