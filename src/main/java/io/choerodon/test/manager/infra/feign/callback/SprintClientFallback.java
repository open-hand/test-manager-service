package io.choerodon.test.manager.infra.feign.callback;

import io.choerodon.test.manager.infra.feign.SprintClient;
import io.choerodon.test.manager.infra.util.FeignFallbackUtil;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author chihao.ran@hand-china.com
 * 2021/04/15 11:52
 */
@Component
public class SprintClientFallback implements FallbackFactory<SprintClient> {

    @Override
    public SprintClient create(Throwable cause) {
        return FeignFallbackUtil.get(cause, SprintClient.class);
    }

}
