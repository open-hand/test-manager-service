package io.choerodon.test.manager.infra.feign;

import java.util.List;

import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import io.choerodon.agile.api.vo.UserDO;
import io.choerodon.agile.api.vo.UserDTO;
import io.choerodon.test.manager.infra.feign.callback.UserFeignClientFallback;

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
    @GetMapping(value = "/v1/organizations/{organization_id}/users/{id}")
    ResponseEntity<UserDO> query(@PathVariable(name = "organization_id") Long organizationId,
                                 @PathVariable("id") Long id);

    @PostMapping(value = "/v1/users/ids")
    ResponseEntity<List<UserDO>> listUsersByIds(@RequestBody Long[] ids,
                                                @RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled);


    @GetMapping(value = "/v1/projects/{project_id}/users")
    ResponseEntity<PageInfo<UserDTO>> list(@PathVariable(name = "project_id") Long id,
                                           @RequestParam int page,
                                           @RequestParam int size);
}

