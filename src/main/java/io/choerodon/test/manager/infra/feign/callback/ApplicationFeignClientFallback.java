package io.choerodon.test.manager.infra.feign.callback;

import io.choerodon.test.manager.infra.feign.ApplicationFeignClient;
import io.choerodon.test.manager.infra.util.FeignFallbackUtil;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Created by zongw.lee@gmail.com on 26/11/2018
 */
@Component
public class ApplicationFeignClientFallback implements FallbackFactory<ApplicationFeignClient> {

    @Override
    public ApplicationFeignClient create(Throwable cause) {
        return FeignFallbackUtil.get(cause, ApplicationFeignClient.class);
    }
}
