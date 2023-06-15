package io.choerodon.test.manager.infra.feign;

import java.util.List;
import java.util.Set;

import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.vo.TenantVO;
import io.choerodon.test.manager.api.vo.agile.ProjectDTO;
import io.choerodon.test.manager.api.vo.agile.UserDO;
import io.choerodon.test.manager.api.vo.agile.UserDTO;
import io.choerodon.test.manager.infra.feign.callback.BaseFeignClientFallback;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/24
 */
@Component
@FeignClient(value = "choerodon-base", fallbackFactory = BaseFeignClientFallback.class)
public interface BaseFeignClient {


    @GetMapping(value = "/choerodon/v1/projects/{project_id}/users")
    ResponseEntity<Page<UserDTO>> list(@PathVariable(name = "project_id") Long id,
                                           @RequestParam(value = "page") int page,
                                           @RequestParam(value = "size") int size);

    @GetMapping(value = "/choerodon/v1/projects/{project_id}")
    ResponseEntity<String> queryProject(@PathVariable(name = "project_id") Long id);
    /**
     * 根据组织id查询所有项目
     *
     * @param organizationId
     * @return
     */
    @GetMapping(value = "/choerodon/v1/organizations/{organization_id}/projects/all")
    ResponseEntity<List<ProjectDTO>> listProjectsByOrgId(@PathVariable("organization_id") Long organizationId);

    @PostMapping(value = "/choerodon/v1/projects/ids")
    ResponseEntity<List<ProjectDTO>> queryProjects(Set<Long> ids);

    @GetMapping("/choerodon/v1/organizations/{organization_id}/users/{user_id}/projects")
    ResponseEntity<List<ProjectDTO>> queryOrgProjects(@PathVariable("organization_id") Long organizationId,
                                                      @PathVariable("user_id") Long userId);
}

