package io.choerodon.test.manager.infra.feign.callback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.agile.api.dto.IssueTypeDTO;
import io.choerodon.agile.api.dto.PriorityDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.feign.IssueFeignClient;

@Component
public class IssueFeignClientFallback implements IssueFeignClient {
    @Override
    public ResponseEntity<List<IssueTypeDTO>> queryIssueType(Long projectId, String applyType, Long organizationId) {
        throw new CommonException("error.issueFeignClient.queryIssueType");
    }

    @Override
    public ResponseEntity<PriorityDTO> queryDefaultPriority(Long projectId, Long organizationId) {
        throw new CommonException("error.issueFeignClient.queryDefaultPriority");
    }

    @Override
    public ResponseEntity<List<PriorityDTO>> queryByOrganizationIdList(Long organizationId) {
        throw new CommonException("error.priorityList.get");
    }
}
