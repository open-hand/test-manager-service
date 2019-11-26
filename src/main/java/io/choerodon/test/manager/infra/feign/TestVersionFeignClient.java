package io.choerodon.test.manager.infra.feign;

import java.util.List;

import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.choerodon.agile.api.vo.DataLogFixVO;
import io.choerodon.agile.api.vo.TestVersionFixVO;
import io.choerodon.test.manager.infra.feign.callback.TestCaseFeignClientFallback;
import io.choerodon.test.manager.infra.feign.callback.TestVersionFeignClientFallback;

/**
 * Created by 842767365@qq.com on 6/13/18.
 */
@Component
@FeignClient(value = "agile-service", fallback = TestVersionFeignClientFallback.class)
public interface TestVersionFeignClient {

    /**
     * 迁移数据专用
     */
    @GetMapping(value = "/v1/fix_data/migrate_version")
    ResponseEntity<List<TestVersionFixVO>> migrateVersion();

}
