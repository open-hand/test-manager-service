package io.choerodon.test.manager.infra.feign.callback;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.vo.IssueQueryVO;
import io.choerodon.test.manager.api.vo.agile.IssueNumDTO;
import io.choerodon.test.manager.api.vo.agile.ProjectInfoVO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.IssueLinkVO;
import io.choerodon.test.manager.infra.feign.IssueFeignClient;

@Component
public class IssueFeignClientFallback implements IssueFeignClient {
    @Override
    public ResponseEntity<String> queryIssues(Long projectId, List<Long> issueIds) {
        throw new CommonException("error.query.queryIssueLinkList");
    }

    @Override
    public ResponseEntity<String> pagedQueryIssueByOptions(Long projectId, Integer page, Integer size, IssueQueryVO issueQueryVO) {
        throw new CommonException("error.agile.pagedQueryIssueByOptions");
    }

    @Override
    public ResponseEntity<String> queryIssueByOptionForAgile(int page, int size, Long projectId, Long issueId, String issueNum, Boolean self, String content) {
        throw new CommonException("error.agile.queryIssueByOptionForAgile");
    }

    @Override
    public ResponseEntity<String> queryProjectInfoByProjectId(Long projectId) {
        throw new CommonException("error.query.agile.projectInfo");
    }
}
