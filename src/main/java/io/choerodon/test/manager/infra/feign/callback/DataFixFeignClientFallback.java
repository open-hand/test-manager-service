package io.choerodon.test.manager.infra.feign.callback;

import io.choerodon.core.utils.FeignFallbackUtil;
import io.choerodon.test.manager.infra.feign.DataFixFeignClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author: 25499
 * @date: 2019/11/25 18:55
 * @description:
 */
@Component
public class DataFixFeignClientFallback implements FallbackFactory<DataFixFeignClient> {

    @Override
    public DataFixFeignClient create(Throwable cause) {
        return FeignFallbackUtil.get(cause, DataFixFeignClient.class);
    }
}
