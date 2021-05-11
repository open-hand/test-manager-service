package io.choerodon.test.manager.infra.feign;

import java.util.List;

import io.choerodon.test.manager.api.vo.ExecutionUpdateIssueVO;
import io.choerodon.test.manager.api.vo.IssueQueryVO;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import io.choerodon.test.manager.infra.feign.callback.IssueFeignClientFallback;

@Component
@FeignClient(value = "agile-service", fallback = IssueFeignClientFallback.class)
public interface IssueFeignClient {

    @PostMapping("/v1/projects/{project_id}/issues/query_issue_ids")
    ResponseEntity<String> queryIssues(@ApiParam(value = "项目id", required = true)
                                                  @PathVariable(name = "project_id") Long projectId,
                                                  @ApiParam(value = "issue编号", required = true)
                                                  @RequestBody List<Long> issueIds);

    @PostMapping("/v1/projects/{project_id}/issues/paged_query")
    ResponseEntity<String> pagedQueryIssueByOptions(@ApiParam(value = "项目id", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               @RequestParam Integer page,
                                                               @RequestParam Integer size,
                                                               @RequestBody IssueQueryVO issueQueryVO);


    @GetMapping("/v1/projects/{project_id}/issues/agile/summary")
    ResponseEntity<String> queryIssueByOptionForAgile(@RequestParam int page,
                                                                 @RequestParam int size,
                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                 @RequestParam Long issueId,
                                                                 @RequestParam String issueNum,
                                                                 @RequestParam Boolean self,
                                                                 @RequestParam String content);

    @GetMapping("/v1/projects/{project_id}/project_info")
    ResponseEntity<String> queryProjectInfoByProjectId(@ApiParam(value = "项目id", required = true)
                                                                     @PathVariable(name = "project_id") Long projectId);

    @PostMapping("/v1/projects/{project_id}/issues/execution_update_status")
    ResponseEntity<String> executionUpdateStatus(@ApiParam(value = "项目id", required = true)
                                         @PathVariable(name = "project_id") Long projectId,
                                         @ApiParam(value = "issueId", required = true)
                                         @RequestParam @Encrypt Long issueId,
                                         @RequestBody ExecutionUpdateIssueVO executionUpdateIssueVO);
}
