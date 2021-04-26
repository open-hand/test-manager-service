package io.choerodon.test.manager.infra.feign.callback;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.feign.SprintClient;

/**
 * @author chihao.ran@hand-china.com
 * 2021/04/15 11:52
 */
@Component
public class SprintClientFallback implements SprintClient {

    private static final String QUERY_ERROR = "error.sprint.query";

    @Override
    public ResponseEntity<String> queryNameByOptions(Long projectId) {
        throw new CommonException(QUERY_ERROR);
    }
}
