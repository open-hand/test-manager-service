package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
@RestController
@RequestMapping(value = "/test/cycle/case")
public class TestCycleCaseController {

	@Autowired
	TestCycleCaseService testCycleCaseService;

	@Permission(permissionPublic = true)
	@ApiOperation("删除测试循环用例")
	@DeleteMapping
	public void delete(Long cycleCaseId) {
		testCycleCaseService.delete(cycleCaseId);
	}

	@Permission(permissionPublic = true)
	@ApiOperation("查询所有测试循环用例")
	@PostMapping("/query")
	public ResponseEntity<Page<TestCycleCaseDTO>> query(@RequestBody TestCycleCaseDTO testCycleCaseDTO, PageRequest pageRequest) {
		return Optional.ofNullable(testCycleCaseService.query(testCycleCaseDTO, pageRequest))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycleCase.query"));
	}

	@Permission(permissionPublic = true)
	@ApiOperation("查询测试组下循环用例")
	@GetMapping("/query/{cycleId}")
	public ResponseEntity<List<TestCycleCaseDTO>> queryByCycle(@PathVariable(name = "cycleId") Long cycleId) {
		return Optional.ofNullable(testCycleCaseService.queryByCycle(cycleId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycleCase.query.cycleId"));
	}

	@Permission(permissionPublic = true)
	@ApiOperation("查询一个循环用例")
	@GetMapping("/query/one/{executeId}")
	public ResponseEntity<TestCycleCaseDTO> queryOne(Long executeId) {
		return Optional.ofNullable(testCycleCaseService.queryOne(executeId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycleCase.query.executeId"));
	}


	@Permission(permissionPublic = true)
	@ApiOperation("增加|修改一个测试组下循环用例")
	@PostMapping("/{projectId}/change")
	public void changeOneCase(@RequestBody TestCycleCaseDTO testCycleCaseDTO, @PathVariable(name = "projectId") Long projectId) {
		testCycleCaseService.changeOneCase(testCycleCaseDTO, projectId);
	}
}
