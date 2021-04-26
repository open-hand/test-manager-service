package io.choerodon.test.manager.infra.feign;

import io.choerodon.test.manager.api.vo.agile.ProductVersionDTO;
import io.choerodon.test.manager.infra.feign.callback.ProductionVersionClientFallback;
import io.choerodon.test.manager.api.vo.agile.ProductVersionPageDTO;
import io.choerodon.core.domain.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/13/18.
 */
@Component
@FeignClient(value = "agile-service", fallback = ProductionVersionClientFallback.class)
public interface ProductionVersionClient {

    @GetMapping(value = "/v1/projects/{project_id}/product_version/versions")
    ResponseEntity<List<ProductVersionDTO>> listByProjectId(@PathVariable(name = "project_id") Long projectId);

    @PostMapping(value = "/v1/projects/{project_id}/product_version/names")
    ResponseEntity<String> queryNameByOptions(@PathVariable(name = "project_id") Long projectId);
}
