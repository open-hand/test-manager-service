package io.choerodon.test.manager.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import io.choerodon.test.manager.infra.feign.callback.SprintClientFallback;

/**
 * @author chihao.ran@hand-china.com
 * 2021/04/15 11:52
 */
@Component
@FeignClient(value = "agile-service", fallback = SprintClientFallback.class)
public interface SprintClient {

    @PostMapping(value = "/v1/projects/{project_id}/sprint/names")
    ResponseEntity<String> queryNameByOptions(@PathVariable(name = "project_id") Long projectId);

}
