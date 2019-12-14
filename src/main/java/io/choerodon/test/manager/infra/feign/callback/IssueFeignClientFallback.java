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
    public ResponseEntity<List<IssueTypeVO>> queryIssueType(Long projectId, String applyType, Long organizationId) {
        throw new CommonException("error.issueFeignClient.queryIssueType");
    }

    @Override
    public ResponseEntity<PriorityVO> queryDefaultPriority(Long projectId, Long organizationId) {
        throw new CommonException("error.issueFeignClient.queryDefaultPriority");
    }

    @Override
    public ResponseEntity<List<PriorityVO>> queryByOrganizationIdList(Long organizationId) {
        throw new CommonException("error.priorityList.get");
    }

    @Override
    public ResponseEntity<List<IssueLinkVO>> queryIssues(Long projectId, List<Long> issueIds) {
        throw new CommonException("error.query.queryIssueLinkList");
    }
}
