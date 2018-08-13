package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.app.service.ExcelService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/12/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/cycle/case")
public class TestCycleCaseController {

	@Autowired
	TestCycleCaseService testCycleCaseService;

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("删除测试循环用例")
	@DeleteMapping
	public ResponseEntity delete(@PathVariable(name = "project_id") Long projectId,
								 Long cycleCaseId) {
		testCycleCaseService.delete(cycleCaseId, projectId);
		return new ResponseEntity<>(true, HttpStatus.NO_CONTENT);
	}


	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("查询测试组下循环用例")
	@GetMapping("/query/issue/{issueId}")
	public ResponseEntity<List<TestCycleCaseDTO>> queryByIssuse(@PathVariable(name = "project_id") Long projectId,
																@PathVariable(name = "issueId") Long issueId
	) {
		return Optional.ofNullable(testCycleCaseService.queryByIssuse(issueId, projectId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycleCase.query.issueId"));
	}

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("查询测试用例下循环case")
	@PostMapping("/query/cycleId")
	public ResponseEntity<Page<TestCycleCaseDTO>> queryByCycle(@PathVariable(name = "project_id") Long projectId,
															   @ApiIgnore
															   @ApiParam(value = "分页信息", required = true)
															   @SortDefault(value = "rank", direction = Sort.Direction.ASC)
																	   PageRequest pageRequest,
															   @RequestBody TestCycleCaseDTO dto) {
		Assert.notNull(dto.getCycleId(),"error.queryByCycle.cycleId.not.null");
		return Optional.ofNullable(testCycleCaseService.queryByCycle(dto, pageRequest, projectId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycleCase.query.cycleId"));
	}

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("过滤查询测试用例下循环case")
	@PostMapping("/query/filtered/{cycleId}")
	public ResponseEntity<Page<TestCycleCaseDTO>> queryByCycleWithFilterArgs(@PathVariable(name = "project_id") Long projectId,
																			 @PathVariable(name = "cycleId") Long cycleId,
																			 @RequestBody TestCycleCaseDTO searchDTO,
																			 @ApiIgnore
																			 @ApiParam(value = "分页信息", required = true)
																			 @SortDefault(value = "rank", direction = Sort.Direction.ASC)
																					 PageRequest pageRequest) {
		return Optional.ofNullable(testCycleCaseService.queryByCycleWithFilterArgs(cycleId, pageRequest, projectId, searchDTO))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycleCase.query.cycleId"));
	}

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("查询一个循环用例")
	@GetMapping("/query/one/{executeId}")
	public ResponseEntity<TestCycleCaseDTO> queryOne(@PathVariable(name = "project_id") Long projectId,
													 @PathVariable(name = "executeId") Long executeId) {
		return Optional.ofNullable(testCycleCaseService.queryOne(executeId, projectId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycleCase.query.executeId"));
	}


	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("增加一个测试组下循环用例")
	@PostMapping("/insert")
	public ResponseEntity insertOneCase(@RequestBody TestCycleCaseDTO testCycleCaseDTO, @PathVariable(name = "project_id") Long projectId) {
		return Optional.ofNullable(testCycleCaseService.create(testCycleCaseDTO, projectId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
				.orElseThrow(() -> new CommonException("error.testCycleCase.insert"));

	}

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("批量添加循环用例")
	@PostMapping("/insert/batch")
	public ResponseEntity batchInsertCase(@RequestBody List<TestCycleCaseDTO> testCycleCaseDTOS, @PathVariable(name = "project_id") Long projectId) {
		List<TestCycleCaseDTO> testCycleCaseList = new ArrayList<>();
		String rank = null;
		for (TestCycleCaseDTO testCycleCaseDTO : testCycleCaseDTOS) {
			if (testCycleCaseDTO.getLastRank() == null) {
				testCycleCaseDTO.setLastRank(rank);
			}
			TestCycleCaseDTO dto = testCycleCaseService.create(testCycleCaseDTO, projectId);
			rank = dto.getRank();
			testCycleCaseList.add(dto);
		}
		return Optional.ofNullable(testCycleCaseList)
				.map(result -> new ResponseEntity<>(testCycleCaseList, HttpStatus.CREATED))
				.orElseThrow(() -> new CommonException("error.testCycleCase.batch.insert"));

	}

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("修改一个测试组下循环用例")
	@PostMapping("/update")
	public ResponseEntity updateOneCase(@RequestBody TestCycleCaseDTO testCycleCaseDTO,
										@PathVariable(name = "project_id") Long projectId) {
		return Optional.ofNullable(testCycleCaseService.changeOneCase(testCycleCaseDTO, projectId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
				.orElseThrow(() -> new CommonException("error.testCycleCase.update"));
	}

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("获取时间内case活跃度")
	@PostMapping("/range/{day}/{range}")
	public ResponseEntity getActiveCase(@PathVariable(name = "range") Long range, @PathVariable(name = "project_id") Long projectId, @PathVariable(name = "day") String day) {
		return Optional.ofNullable(testCycleCaseService.getActiveCase(range, projectId, day))
				.map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
				.orElseThrow(() -> new CommonException("error.testCycleCase.get.range"));
	}

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("根据循环过滤创建心的cycle")
	@PostMapping("/insert/case/filter/{fromCycleId}/to/{toCycleId}/assigneeTo/{assignee}")
	public ResponseEntity createFilteredCycleCaseInCycle(@ApiIgnore
														 @ApiParam(value = "分页信息", required = true)
														 @SortDefault(value = "issueId", direction = Sort.Direction.DESC)
																 PageRequest pageRequest,
														 @ApiParam(value = "项目id", required = true)
														 @PathVariable(name = "project_id") Long projectId,
														 @ApiParam(value = "循环id", required = true)
														 @PathVariable(name = "fromCycleId") Long fromCycleId,
														 @ApiParam(value = "循环id", required = true)
														 @PathVariable(name = "toCycleId") Long toCycleId,
														 @ApiParam(value = "指派人", required = true)
														 @PathVariable(name = "assignee") Long assignee,
														 @ApiParam(value = "查询参数", required = true)
														 @RequestBody(required = false) SearchDTO searchDTO) {
		return Optional.ofNullable(
				testCycleCaseService.createFilteredCycleCaseInCycle(projectId, fromCycleId, toCycleId, assignee, searchDTO))
				.map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
				.orElseThrow(() -> new CommonException("error.testCycleCase.create.filtered"));
	}

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("统计未执行测试")
	@GetMapping("/countCaseNotRun")
	public ResponseEntity countCaseNotRun(@PathVariable(name = "project_id") Long projectId) {
		return Optional.ofNullable(
				testCycleCaseService.countCaseNotRun(projectId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycleCase.query.countCaseNotRun"));
	}

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("统计未计划测试")
	@GetMapping("/countCaseNotPlain")
	public ResponseEntity countCaseNotPlain(@PathVariable(name = "project_id") Long projectId) {
		return Optional.ofNullable(
				testCycleCaseService.countCaseNotPlain(projectId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycleCase.query.countCaseNotPlain"));
	}

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("统计测试总数")
	@GetMapping("/countCaseSum")
	public ResponseEntity countCaseSum(@PathVariable(name = "project_id") Long projectId) {
		return Optional.ofNullable(
				testCycleCaseService.countCaseSum(projectId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycleCase.query.countCaseSum"));
	}

	@Autowired
	ExcelService excelService;

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("excel")
	@GetMapping("/download/excel/{cycleId}")
	public void downLoad(@PathVariable(name = "project_id") Long projectId, @PathVariable(name = "cycleId") Long cycleId,
						 HttpServletRequest request,
						 HttpServletResponse response) {
		excelService.exportCycleCaseInOneCycle(cycleId, projectId, request, response);
	}
}
