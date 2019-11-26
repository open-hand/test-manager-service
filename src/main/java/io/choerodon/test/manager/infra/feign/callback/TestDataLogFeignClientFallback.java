package io.choerodon.test.manager.infra.feign.callback;

import java.util.List;

import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import io.choerodon.agile.api.vo.DataLogFixVO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.feign.TestDataLogFeignClient;

/**
 * @author: 25499
 * @date: 2019/11/25 18:55
 * @description:
 */
@Component
public class TestDataLogFeignClientFallback implements TestDataLogFeignClient {
    private static final String QUERY_ERROR = "error.baseFeign.query";
    @Override
    public ResponseEntity<List<DataLogFixVO>> migrateDataLog(@ApiParam(value = "项目id", required = true)
                                                                 @PathVariable(name = "project_id") Long projectId) {
        throw new CommonException(QUERY_ERROR);
    }
}
