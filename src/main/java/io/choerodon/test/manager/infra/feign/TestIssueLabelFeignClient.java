package io.choerodon.test.manager.infra.feign;

import java.util.List;

import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.choerodon.agile.api.vo.IssueDTO;
import io.choerodon.agile.api.vo.IssueLabelDTO;
import io.choerodon.agile.api.vo.LabelIssueRelDTO;
import io.choerodon.test.manager.infra.feign.callback.TestCaseFeignClientFallback;

/**
 * @author: 25499
 * @date: 2019/11/20 11:50
 * @description:
 */
@Component
@FeignClient(value = "agile-service", fallback = TestCaseFeignClientFallback.class)
public interface TestIssueLabelFeignClient {

    @GetMapping(value = "/v1/projects/{project_id}/issue_labels/all")
    ResponseEntity<List<IssueLabelDTO>> listAllLabel();

}
