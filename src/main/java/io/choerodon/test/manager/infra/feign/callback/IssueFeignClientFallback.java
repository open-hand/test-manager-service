package io.choerodon.test.manager.infra.feign.callback;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.dto.IssueTypeDTO;
import io.choerodon.test.manager.infra.feign.IssueFeignClient;

@Component
public class IssueFeignClientFallback implements IssueFeignClient {
    @Override
    public ResponseEntity<IssueTypeDTO> queryIssueType(Long projectId, String applyType, Long organizationId) {
        throw new CommonException("error.query.issue.type");
    }
}
