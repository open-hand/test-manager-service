package io.choerodon.test.manager.infra.feign.callback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.agile.api.vo.TestVersionFixVO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.feign.TestVersionFeignClient;

/**
 * @author: 25499
 * @date: 2019/11/26 16:54
 * @description:
 */
@Component
public class TestVersionFeignClientFallback implements TestVersionFeignClient {
    private static final String QUERY_ERROR = "error.baseFeign.query";

    @Override
    public ResponseEntity<List<TestVersionFixVO>> migrateVersion() {
        throw new CommonException(QUERY_ERROR);
    }
}
