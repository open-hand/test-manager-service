package io.choerodon.test.manager.infra.feign;


import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.agile.api.dto.UserDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.infra.feign.callback.UserFeignClientFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/24
 */
@Component
@FeignClient(value = "iam-service", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {

	/**
	 * 查询用户信息
	 *
	 * @param organizationId organizationId
	 * @param id             id
	 * @return UserDO
	 */
	@RequestMapping(value = "/v1/organizations/{organization_id}/users/{id}", method = RequestMethod.GET)
	ResponseEntity<UserDO> query(@PathVariable(name = "organization_id") Long organizationId,
								 @PathVariable("id") Long id);

	@RequestMapping(value = "/v1/users/ids", method = RequestMethod.POST)
	ResponseEntity<List<UserDO>> listUsersByIds(@RequestBody Long[] ids);


	@GetMapping(value = "/v1/projects/{project_id}/users")
	public ResponseEntity<Page<UserDTO>> list(@PathVariable(name = "project_id") Long id,
											  @RequestParam(required = false, name = "id") Long userId,
											  @RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
											  @RequestParam(required = false, name = "param") String param);
}

