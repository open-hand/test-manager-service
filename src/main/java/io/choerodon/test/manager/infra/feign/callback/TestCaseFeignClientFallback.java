package io.choerodon.test.manager.infra.feign.callback;

import io.choerodon.core.utils.FeignFallbackUtil;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class TestCaseFeignClientFallback implements FallbackFactory<TestCaseFeignClient> {

    @Override
    public TestCaseFeignClient create(Throwable cause) {
        return FeignFallbackUtil.get(cause, TestCaseFeignClient.class);
    }
}
