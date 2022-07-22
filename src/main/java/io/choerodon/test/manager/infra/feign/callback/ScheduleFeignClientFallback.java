package io.choerodon.test.manager.infra.feign.callback;

import io.choerodon.test.manager.infra.feign.ScheduleFeignClient;
import io.choerodon.test.manager.infra.util.FeignFallbackUtil;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Created by zongw.lee@gmail.com on 23/11/2018
 */
@Component
public class ScheduleFeignClientFallback implements FallbackFactory<ScheduleFeignClient> {

    @Override
    public ScheduleFeignClient create(Throwable cause) {
        return FeignFallbackUtil.get(cause, ScheduleFeignClient.class);
    }

}
