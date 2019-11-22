package io.choerodon.test.manager.infra.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.choerodon.agile.api.vo.ProjectInfoFixVO;
import io.choerodon.test.manager.infra.feign.callback.ProjectInfoFeignClientFallback;

/**
 * @author: 25499
 * @date: 2019/11/20 11:50
 * @description:
 */
@Component
@FeignClient(value = "agile-service", fallback = ProjectInfoFeignClientFallback.class)
public interface ProjectInfoFeignClient {

    @GetMapping(value = "/v1/projects/{project_id}/project_info/all")
    ResponseEntity<List<ProjectInfoFixVO>> queryAllProjectInfo(@PathVariable(name = "project_id") Long projectId);

}
