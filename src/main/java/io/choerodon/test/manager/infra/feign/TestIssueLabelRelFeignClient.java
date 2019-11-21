package io.choerodon.test.manager.infra.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.choerodon.agile.api.vo.LabelIssueRelFixVO;
import io.choerodon.test.manager.infra.feign.callback.TestIssueLabelRelFeignClientFallback;

/**
 * @author: 25499
 * @date: 2019/11/20 11:50
 * @description:
 */
@Component
@FeignClient(value = "agile-service", fallback = TestIssueLabelRelFeignClientFallback.class)
public interface TestIssueLabelRelFeignClient {

    @GetMapping(value = "/v1/projects/{project_id}/issue_labels_rel/query")
    ResponseEntity<List<LabelIssueRelFixVO>> queryIssueLabelRelList(@PathVariable(name = "project_id") Long projectId);

}
