package com.test.devops.infra.feign;

import com.test.devops.infra.feign.callback.ProductionVersionClientFallback;
import com.test.devops.infra.feign.callback.TestCaseFeignClientFallback;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * Created by jialongZuo@hand-china.com on 6/13/18.
 */
@Component
@FeignClient(value = "agile-service", fallback = ProductionVersionClientFallback.class)
public interface ProductionVersionClient {

	@PostMapping(value = "/v1/project/{project_id}/product_version/versions")
	public ResponseEntity<Page<ProductVersionPageDTO>> listByOptions(@PathVariable(name = "project_id") Long projectId,
																	 @RequestBody(required = false) Map<String, Object> searchParamMap);
}
