package io.choerodon.test.manager.infra.feign.callback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.IssueLinkFixVO;
import io.choerodon.test.manager.infra.feign.IssueLinkFeignClient;

/**
 * @author: 25499
 * @date: 2019/11/21 14:44
 * @description:
 */
@Component
public class IssueLinkFeignClientFallback implements IssueLinkFeignClient {
    @Override
    public ResponseEntity<List<IssueLinkFixVO>> listIssueLinkByIssueIds(Long projectId, List<Long> issueIds) {
        throw new CommonException("error.issueFeignClient.queryIssueLinks");
    }
}
