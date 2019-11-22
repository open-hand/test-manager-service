package io.choerodon.test.manager.infra.feign;

import java.util.List;

import io.swagger.annotations.ApiParam;
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

    @GetMapping(value = "/v1/fix_data/migrate_issue_Label_rel/{project_id}")
    ResponseEntity<List<LabelIssueRelFixVO>> queryIssueLabelRelList(@ApiParam(value = "项目id", required = true)
                                                                    @PathVariable(name = "project_id") Long projectId);

}
