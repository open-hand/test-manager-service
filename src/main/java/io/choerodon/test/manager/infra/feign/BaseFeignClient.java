package io.choerodon.test.manager.infra.feign;

import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.vo.TenantVO;
import io.choerodon.test.manager.api.vo.agile.ProjectDTO;
import io.choerodon.test.manager.api.vo.agile.UserDO;
import io.choerodon.test.manager.api.vo.agile.UserDTO;
import io.choerodon.test.manager.infra.feign.callback.BaseFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/24
 */
@Component
@FeignClient(value = "hzero-iam", fallback = BaseFeignClientFallback.class)
public interface BaseFeignClient {

    @PostMapping(value = "/choerodon/v1/users/ids")
    ResponseEntity<List<UserDO>> listUsersByIds(@RequestBody Long[] ids,
                                                @RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled);


    @GetMapping(value = "/choerodon/v1/projects/{project_id}/users")
    ResponseEntity<Page<UserDTO>> list(@PathVariable(name = "project_id") Long id,
                                           @RequestParam(value = "page") int page,
                                           @RequestParam(value = "size") int size);

    @GetMapping(value = "/choerodon/v1/projects/{project_id}")
    ResponseEntity<ProjectDTO> queryProject(@PathVariable(name = "project_id") Long id);
    /**
     * 根据组织id查询所有项目
     *
     * @param organizationId
     * @return
     */
    @GetMapping(value = "/choerodon/v1/organizations/{organization_id}/projects/all")
    ResponseEntity<List<ProjectDTO>> listProjectsByOrgId(@PathVariable("organization_id") Long organizationId);

    /**
     * 分页查询所有组织
     * @param pageRequest
     * @return
     */
    @GetMapping("/choerodon/v1/organizations/all")
    ResponseEntity<Page<TenantVO>> getAllOrgs(@RequestParam("page") int page,
                                              @RequestParam("size") int size);

    @GetMapping(value = "/choerodon/v1/projects/ids")
    ResponseEntity<List<ProjectDTO>> queryProjects(Set<Long> ids);
}

