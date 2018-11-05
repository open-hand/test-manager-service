package io.choerodon.test.manager.infra.feign;

import io.choerodon.test.manager.infra.feign.callback.TestCaseFeignClientFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
@Component
@FeignClient(value = "notify-service", fallback = TestCaseFeignClientFallback.class)
public interface NotifyFeignClient {

    @PostMapping("/v1/notices/ws/{code}/{id}")
    void postWebSocket(@PathVariable("code") String code,
                              @PathVariable("id") String id,
                              @RequestBody String message);
}
