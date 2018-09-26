package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseStepService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/14/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/cycle/case/step")
public class TestCycleCaseStepController {

	@Autowired
	TestCycleCaseStepService testCycleCaseStepService;

	/**
	 * 更新循环步骤
	 *
	 * @param testCycleCaseStepDTO
	 * @return
	 */
	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("更新一个循环步骤")
	@PutMapping
	ResponseEntity<List<TestCycleCaseStepDTO>> update(@RequestBody List<TestCycleCaseStepDTO> testCycleCaseStepDTO) {
		return Optional.ofNullable(testCycleCaseStepService.update(testCycleCaseStepDTO))
				.map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
				.orElseThrow(() -> new CommonException("error.testCycleCaseStep.update"));

	}


	/**
	 * 查询循环测试步骤
	 *
	 * @param cycleCaseId
	 * @return
	 */
	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("查询循环步骤")
	@GetMapping("/query/{cycleCaseId}")
	ResponseEntity<Page<TestCycleCaseStepDTO>> querySubStep(@PathVariable(name = "project_id") Long projectId,
															@ApiParam(value = "cycleCaseId", required = true)
															@PathVariable(name = "cycleCaseId") Long cycleCaseId,
															@ApiIgnore
															@ApiParam(value = "分页信息", required = true)
															@SortDefault(value = "rank", direction = Sort.Direction.DESC)
																	PageRequest pageRequest) {

		return Optional.ofNullable(testCycleCaseStepService.querySubStep(cycleCaseId, pageRequest, projectId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycleCaseStep.query"));

	}

}
