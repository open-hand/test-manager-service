package io.choerodon.test.manager.infra.feign.callback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.test.manager.api.vo.agile.IssueTypeVO;
import io.choerodon.test.manager.api.vo.agile.PriorityVO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.IssueLinkVO;
import io.choerodon.test.manager.infra.feign.IssueFeignClient;

@Component
public class IssueFeignClientFallback implements IssueFeignClient {
    @Override
    public ResponseEntity<List<IssueLinkVO>> queryIssues(Long projectId, List<Long> issueIds) {
        throw new CommonException("error.query.queryIssueLinkList");
    }
}
