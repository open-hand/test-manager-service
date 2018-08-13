package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.agile.api.dto.UserDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.app.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by 842767365@qq.com on 7/2/18.
 */

@RestController
@RequestMapping(value = "/v1/projects/{project_id}")
public class UserController {


	@Autowired
	UserService userService;

	@Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
	@ApiOperation(value = "根据项目id分页查询该项目下的用户，可以进行模糊查询name和realName")
	@GetMapping(value = "/users")
	@CustomPageRequest
	public ResponseEntity<Page<UserDTO>> getUserList(@PathVariable(name = "project_id") Long projectId,
													 @RequestParam(required = false, name = "id") Long userId,
													 @ApiIgnore
													 @ApiParam(value = "分页信息", required = true)
													 @SortDefault(value = "id", direction = Sort.Direction.DESC)
															 PageRequest pageRequest,
													 @RequestParam(required = false) String param) {
		return userService.list(pageRequest, projectId, param, userId);
	}
}
