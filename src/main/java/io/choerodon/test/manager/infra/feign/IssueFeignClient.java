package io.choerodon.test.manager.infra.feign;

import java.util.List;

import io.choerodon.test.manager.api.vo.IssueLinkVO;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import io.choerodon.test.manager.api.vo.agile.IssueTypeVO;
import io.choerodon.test.manager.api.vo.agile.PriorityVO;
import io.choerodon.test.manager.infra.feign.callback.IssueFeignClientFallback;

@Component
@FeignClient(value = "agile-service", fallback = IssueFeignClientFallback.class)
public interface IssueFeignClient {

    @PostMapping("/v1/projects/{project_id}/issues/query_issue_ids")
    ResponseEntity<List<IssueLinkVO>> queryIssues(@ApiParam(value = "项目id", required = true)
                                                         @PathVariable(name = "project_id") Long projectId,
                                                         @ApiParam(value = "issue编号", required = true)
                                                         @RequestBody List<Long> issueIds);
}
