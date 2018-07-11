package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.infra.mapper.TestCycleMapper;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/12/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/cycle")
public class TestCycleController {
	@Autowired
	TestCycleService testCycleService;

	@Autowired
	TestCycleMapper testCycleMapper;


	@Permission(level = ResourceLevel.PROJECT)
	@ApiOperation("增加测试循环")
	@PostMapping
	public ResponseEntity<TestCycleDTO> insert(@RequestBody TestCycleDTO testCycleDTO) {
		return Optional.ofNullable(testCycleService.insert(testCycleDTO))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycle.insert"));

	}

	@Permission(level = ResourceLevel.PROJECT)
	@ApiOperation("删除测试循环")
	@DeleteMapping("/delete/{cycleId}")
	void delete(@PathVariable(name = "cycleId") Long cycleId) {
		TestCycleDTO cycleDTO = new TestCycleDTO();
		cycleDTO.setCycleId(cycleId);
		testCycleService.delete(cycleDTO);
	}

	@Permission(level = ResourceLevel.PROJECT)
	@ApiOperation("修改测试循环")
	@PutMapping
	ResponseEntity<TestCycleDTO> update(@RequestBody TestCycleDTO testCycleDTO) {
		return Optional.ofNullable(testCycleService.update(testCycleDTO))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.error.testCycle.update"));
	}

	@Permission(level = ResourceLevel.PROJECT)
	@ApiOperation("查询测试循环")
	@GetMapping("/query/one/{cycleId}")
	TestCycleDTO queryOne(@PathVariable(name = "cycleId") Long cycleId) {
		return testCycleService.getOneCycle(cycleId);
	}

	@Permission(level = ResourceLevel.PROJECT)
	@ApiOperation("查询version下的测试循环")
	@GetMapping("/query")
	ResponseEntity getTestCycle(@PathVariable(name = "project_id") Long projectId) {
		return Optional.ofNullable(testCycleService.getTestCycle(projectId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycle.query.getTestCycleByVersionId"));
	}

	@Permission(level = ResourceLevel.PROJECT)
	@ApiOperation("过滤version下的测试循环")
	@PostMapping("/filter")
	ResponseEntity filterTestCycle(@RequestBody String parameters) {

		return Optional.ofNullable(testCycleService.filterCycleWithBar(parameters))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycle.query.filterCycleWithBar"));
	}


	@Permission(level = ResourceLevel.PROJECT)
	@ApiOperation("查询项目下的计划")
	@PostMapping("/query/version")
	ResponseEntity<Page<ProductVersionPageDTO>> getTestCycleVersion(@PathVariable(name = "project_id") Long projectId, @RequestBody Map<String, Object> searchParamMap) {
		return testCycleService.getTestCycleVersion(projectId, searchParamMap);
	}


	@Permission(level = ResourceLevel.PROJECT)
	@ApiOperation("克隆循环")
	@PostMapping("/clone/cycle/{cycleId}")
	ResponseEntity cloneCycle(@PathVariable(name = "cycleId") Long cycleId, @RequestBody TestCycleDTO testCycleDTO, @PathVariable(name = "project_id") Long projectId) {
		return Optional.ofNullable(testCycleService.cloneCycle(cycleId, testCycleDTO.getCycleName(), projectId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
				.orElseThrow(() -> new CommonException("error.testCycle.query.cloneCycle"));

	}

	@Permission(level = ResourceLevel.PROJECT)
	@ApiOperation("克隆文件夹")
	@PostMapping("/clone/folder/{cycleId}")
	ResponseEntity cloneFolder(
			@ApiParam(value = "循环id", required = true)
			@PathVariable(name = "cycleId") Long cycleId,
			@PathVariable(name = "project_id") Long projectId
			, @RequestBody TestCycleDTO testCycleDTO) {
		return Optional.ofNullable(testCycleService.cloneFolder(cycleId, testCycleDTO, projectId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
				.orElseThrow(() -> new CommonException("error.testCycle.query.cloneFolder"));

	}
}
