package com.test.devops.api.controller;

import com.test.devops.api.dto.TestCycleCaseDTO;
import com.test.devops.app.service.TestCycleCaseService;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
	public Page<TestCycleCaseDTO> query(@RequestBody TestCycleCaseDTO testCycleCaseDTO, PageRequest pageRequest) {
		return testCycleCaseService.query(testCycleCaseDTO, pageRequest);
	}

	@Permission(permissionPublic = true)
	@ApiOperation("查询测试组下循环用例")
	@GetMapping("/query/{cycleId}")
	public List<TestCycleCaseDTO> queryByCycle(@PathVariable(name = "cycleId") Long cycleId) {

		return testCycleCaseService.queryByCycle(cycleId);
	}

	@Permission(permissionPublic = true)
	@ApiOperation("查询一个循环用例")
	@GetMapping("/query/one/{executeId}")
	public TestCycleCaseDTO queryOne(Long executeId) {
		return testCycleCaseService.queryOne(executeId);
	}


	@Permission(permissionPublic = true)
	@ApiOperation("增加|修改一个测试组下循环用例")
	@PostMapping("/{projectId}/change")
	public void changeOneCase(@RequestBody TestCycleCaseDTO testCycleCaseDTO, @PathVariable(name = "projectId") Long projectId) {
		testCycleCaseService.changeOneCase(testCycleCaseDTO, projectId);
	}
}
