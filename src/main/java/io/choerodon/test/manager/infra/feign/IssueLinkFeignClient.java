package io.choerodon.test.manager.infra.feign;

import java.util.List;

import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import io.choerodon.agile.api.vo.IssueTypeVO;
import io.choerodon.test.manager.api.vo.IssueLinkFixVO;
import io.choerodon.test.manager.infra.feign.callback.IssueLinkFeignClientFallback;

/**
 * @author: 25499
 * @date: 2019/11/21 14:43
 * @description:
 */
@Component
@FeignClient(value = "agile-service", fallback = IssueLinkFeignClientFallback.class)
public interface IssueLinkFeignClient {
    @PostMapping("/v1/projects/{project_id}/issue_links/queryByIds")
    ResponseEntity<List<IssueLinkFixVO>> listIssueLinkByIssueIds(@ApiParam(value = "项目id", required = true)
                                                              @PathVariable(name = "project_id") Long projectId,
                                                                 @ApiParam(value = "issueIds", required = true)
                                                              @RequestBody List<Long> issueIds);

}
