package io.choerodon.test.manager.infra.feign.callback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.agile.api.vo.LabelIssueRelFixVO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.feign.TestIssueLabelRelFeignClient;

/**
 * @author: 25499
 * @date: 2019/11/20 14:31
 * @description:
 */
@Component
public class TestIssueLabelRelFeignClientFallback implements TestIssueLabelRelFeignClient {

    private static final String QUERY_ERROR = "error.baseFeign.query";
    private static final String UPDATE_ERROR = "error.baseFeign.update";
    private static final String CREATE_ERROR = "error.baseFeign.create";

    @Override
    public ResponseEntity<List<LabelIssueRelFixVO>> queryIssueLabelRelList(Long projectId,List<Long> issueIds) {
        throw new CommonException(QUERY_ERROR);
    }
}
