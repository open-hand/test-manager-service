package io.choerodon.test.manager.infra.feign;

import java.util.List;

import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.choerodon.agile.api.vo.IssueTypeVO;
import io.choerodon.agile.api.vo.PriorityVO;
import io.choerodon.test.manager.infra.feign.callback.IssueFeignClientFallback;

@Component
@FeignClient(value = "agile-service", fallback = IssueFeignClientFallback.class)
public interface IssueFeignClient {

    @GetMapping("/v1/projects/{project_id}/schemes/query_issue_types_with_sm_id")
    ResponseEntity<List<IssueTypeVO>> queryIssueType(@PathVariable("project_id") Long projectId,
                                                     @RequestParam("apply_type") String applyType,
                                                     @RequestParam("organizationId") Long organizationId);

    @GetMapping("/v1/projects/{project_id}/priority/default")
    ResponseEntity<PriorityVO> queryDefaultPriority(@PathVariable("project_id") Long projectId,
                                                    @RequestParam("organizationId") Long organizationId);

    @GetMapping("/v1/organizations/{organization_id}/priority/list_by_org")
    ResponseEntity<List<PriorityVO>> queryByOrganizationIdList(@ApiParam(value = "组织id", required = true)
                                                                @PathVariable("organization_id") Long organizationId);
}
