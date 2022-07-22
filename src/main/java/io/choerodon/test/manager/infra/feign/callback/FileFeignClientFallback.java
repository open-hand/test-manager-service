package io.choerodon.test.manager.infra.feign.callback;

import io.choerodon.test.manager.infra.feign.FileFeignClient;
import io.choerodon.test.manager.infra.util.FeignFallbackUtil;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang on 2018/1/15.
 */
@Component
public class FileFeignClientFallback implements FallbackFactory<FileFeignClient> {

    @Override
    public FileFeignClient create(Throwable cause) {
        return FeignFallbackUtil.get(cause, FileFeignClient.class);
    }
}
