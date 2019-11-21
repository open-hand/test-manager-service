package io.choerodon.test.manager.infra.feign.callback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.agile.api.vo.LabelFixVO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.feign.TestIssueLabelFeignClient;

/**
 * @author: 25499
 * @date: 2019/11/20 14:29
 * @description:
 */
@Component
public class TestIssueLabelFeignClientFallback implements TestIssueLabelFeignClient {
    private static final String QUERY_ERROR = "error.baseFeign.query";
    private static final String UPDATE_ERROR = "error.baseFeign.update";
    private static final String CREATE_ERROR = "error.baseFeign.create";

    @Override
    public ResponseEntity<List<LabelFixVO>> listAllLabel(Long projectId) {
        throw new CommonException(QUERY_ERROR);
    }
}
