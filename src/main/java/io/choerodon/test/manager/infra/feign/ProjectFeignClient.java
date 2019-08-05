package io.choerodon.test.manager.infra.feign;

import io.choerodon.agile.api.vo.ProjectDTO;
import io.choerodon.test.manager.infra.feign.callback.ProjectFeignClientFallback;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by 842767365@qq.com on 8/10/18.
 */

@Component
@FeignClient(value = "iam-service", fallback = ProjectFeignClientFallback.class)
public interface ProjectFeignClient {
    @GetMapping(value = "/v1/projects/{project_id}")
    ResponseEntity<ProjectDTO> query(@PathVariable(name = "project_id") Long id);
}
