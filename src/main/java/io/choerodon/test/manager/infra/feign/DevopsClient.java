package io.choerodon.test.manager.infra.feign;

import io.choerodon.test.manager.infra.feign.callback.DevopsClientFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(value = "devops-service", fallback = DevopsClientFallback.class)
public interface DevopsClient {



}