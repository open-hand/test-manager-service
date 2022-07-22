package io.choerodon.test.manager.infra.feign.callback;

import io.choerodon.test.manager.infra.feign.IssueFeignClient;
import io.choerodon.test.manager.infra.util.FeignFallbackUtil;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class IssueFeignClientFallback implements FallbackFactory<IssueFeignClient> {

    @Override
    public IssueFeignClient create(Throwable cause) {
        return FeignFallbackUtil.get(cause, IssueFeignClient.class);
    }
}
