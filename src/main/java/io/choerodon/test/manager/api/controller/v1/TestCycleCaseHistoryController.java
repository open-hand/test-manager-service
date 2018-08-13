package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseHistoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/28/18.
 */

@RestController
@RequestMapping(value = "/v1/projects/{project_id}/cycle/case/history/{cycleCaseId}")
public class TestCycleCaseHistoryController {
	@Autowired
	TestCycleCaseHistoryService testCycleCaseHistoryService;

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation("查询循环历史")
	@GetMapping
	public ResponseEntity<Page<TestCycleCaseHistoryDTO>> query(@PathVariable(name = "project_id") Long projectId,
															   @ApiIgnore
															   @ApiParam(value = "分页信息", required = true)
															   @SortDefault(value = "id", direction = Sort.Direction.DESC)
																	   PageRequest pageRequest,
															   @PathVariable(name = "cycleCaseId") Long cycleCaseId) {

		return Optional.ofNullable(testCycleCaseHistoryService.query(cycleCaseId, pageRequest))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.history.query"));

	}
}
