package io.choerodon.test.manager.infra.feign;

import io.choerodon.test.manager.infra.feign.callback.SprintClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author chihao.ran@hand-china.com
 * 2021/04/15 11:52
 */
@Component
@FeignClient(value = "agile-service", fallbackFactory = SprintClientFallback.class)
public interface SprintClient {

    @PostMapping(value = "/v1/projects/{project_id}/sprint/names")
    ResponseEntity<String> queryNameByOptions(@PathVariable(name = "project_id") Long projectId);

}
