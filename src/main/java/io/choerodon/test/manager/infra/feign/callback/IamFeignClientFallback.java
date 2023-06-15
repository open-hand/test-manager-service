package io.choerodon.test.manager.infra.feign.callback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import io.choerodon.core.utils.FeignFallbackUtil;
import io.choerodon.test.manager.infra.feign.IamFeignClient;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/24
 */
@Component
public class IamFeignClientFallback implements FallbackFactory<IamFeignClient> {

    @Override
    public IamFeignClient create(Throwable cause) {
        return FeignFallbackUtil.get(cause, IamFeignClient.class);
    }
}
