package io.choerodon.test.manager.infra.feign;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.vo.IssueLinkVO;
import io.choerodon.test.manager.api.vo.IssueQueryVO;
import io.choerodon.test.manager.api.vo.agile.IssueNumDTO;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import io.choerodon.test.manager.infra.feign.callback.IssueFeignClientFallback;

@Component
@FeignClient(value = "agile-service", fallback = IssueFeignClientFallback.class)
public interface IssueFeignClient {

    @PostMapping("/v1/projects/{project_id}/issues/query_issue_ids")
    ResponseEntity<List<IssueLinkVO>> queryIssues(@ApiParam(value = "项目id", required = true)
                                                  @PathVariable(name = "project_id") Long projectId,
                                                  @ApiParam(value = "issue编号", required = true)
                                                  @RequestBody List<Long> issueIds);

    @PostMapping("/v1/projects/{project_id}/issues/paged_query")
    ResponseEntity<Page<IssueLinkVO>> pagedQueryIssueByOptions(@ApiParam(value = "项目id", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               @RequestParam Integer page,
                                                               @RequestParam Integer size,
                                                               @RequestBody IssueQueryVO issueQueryVO);


    @GetMapping("/v1/projects/{project_id}/issues/agile/summary")
    ResponseEntity<Page<IssueNumDTO>> queryIssueByOptionForAgile(@RequestParam int page,
                                                                 @RequestParam int size,
                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                 @RequestParam Long issueId,
                                                                 @RequestParam String issueNum,
                                                                 @RequestParam Boolean self,
                                                                 @RequestParam String content);
}
