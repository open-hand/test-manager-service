package io.choerodon.test.manager.infra.feign.callback;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.vo.agile.IssueNumDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.IssueLinkVO;
import io.choerodon.test.manager.infra.feign.IssueFeignClient;

@Component
public class IssueFeignClientFallback implements IssueFeignClient {
    @Override
    public ResponseEntity<List<IssueLinkVO>> queryIssues(Long projectId, List<Long> issueIds) {
        throw new CommonException("error.query.queryIssueLinkList");
    }

    @Override
    public ResponseEntity<Page<IssueNumDTO>> queryIssueByOptionForAgile(int page, int size, Long projectId, Long issueId, String issueNum, Boolean self, String content) {
        throw new CommonException("error.agile.queryIssueByOptionForAgile");
    }
}
