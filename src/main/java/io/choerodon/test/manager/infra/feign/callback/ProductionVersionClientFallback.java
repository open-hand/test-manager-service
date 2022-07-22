package io.choerodon.test.manager.infra.feign.callback;

import io.choerodon.core.utils.FeignFallbackUtil;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Created by 842767365@qq.com on 6/13/18.
 */
@Component
public class ProductionVersionClientFallback implements FallbackFactory<ProductionVersionClient> {

    @Override
    public ProductionVersionClient create(Throwable cause) {
        return FeignFallbackUtil.get(cause, ProductionVersionClient.class);

    }
}
