package io.choerodon.test.manager.infra.feign;

import java.util.List;

import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.vo.TenantVO;
import io.choerodon.test.manager.api.vo.agile.UserDO;
import io.choerodon.test.manager.infra.feign.callback.IamFeignClientFallback;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/24
 */
@Component
@FeignClient(value = HZeroService.Iam.NAME, fallbackFactory = IamFeignClientFallback.class)
public interface IamFeignClient {

    @PostMapping(value = "/choerodon/v1/users/ids")
    ResponseEntity<List<UserDO>> listUsersByIds(@RequestBody Long[] ids,
                                                @RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled);

    /**
     * 分页查询所有组织
     * @param pageRequest
     * @return
     */
    @GetMapping("/choerodon/v1/organizations/all")
    ResponseEntity<Page<TenantVO>> getAllOrgs(@RequestParam("page") int page,
                                              @RequestParam("size") int size,
                                              @RequestParam("sort") String sort);
}

