package io.choerodon.test.manager.infra.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.choerodon.agile.api.vo.LabelFixVO;
import io.choerodon.test.manager.infra.feign.callback.TestIssueLabelFeignClientFallback;

/**
 * @author: 25499
 * @date: 2019/11/20 11:50
 * @description:
 */
@Component
@FeignClient(value = "agile-service", fallback = TestIssueLabelFeignClientFallback.class)
public interface TestIssueLabelFeignClient {

    @GetMapping(value = "/v1/fix_data/migrate_issue_Label/{project_id}")
    ResponseEntity<List<LabelFixVO>> listAllLabel(@PathVariable(name = "project_id") Long projectId);

}
