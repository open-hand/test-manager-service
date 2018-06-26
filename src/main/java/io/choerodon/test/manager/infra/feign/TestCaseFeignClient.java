package io.choerodon.test.manager.infra.feign;

import io.choerodon.test.manager.infra.feign.callback.TestCaseFeignClientFallback;
import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * Created by 842767365@qq.com on 6/13/18.
 */
@Component
@FeignClient(value = "agile-service", fallback = TestCaseFeignClientFallback.class)
public interface TestCaseFeignClient {

    @PostMapping("/v1/project/{project_id}/issues")
    public ResponseEntity<IssueDTO> createIssue(@PathVariable(name = "project_id") Long projectId,
                                                @RequestBody IssueCreateDTO issueCreateDTO);

    @PutMapping("/v1/project/{project_id}/issues")
    public ResponseEntity<IssueDTO> updateIssue(@PathVariable(name = "project_id") Long projectId,
                                                @RequestBody JSONObject issueUpdate);

    @DeleteMapping(value = "/v1/project/{project_id}/issues/{issueId}")
    public ResponseEntity deleteIssue(@PathVariable(name = "project_id") Long projectId,
                                      @PathVariable("issueId") Long issueId);

    @GetMapping(value = "/v1/project/{project_id}/issues/{issueId}")
    ResponseEntity<IssueDTO> queryIssue(@PathVariable(name = "project_id") Long projectId,
                                        @PathVariable("issueId") Long issueId);

    @PostMapping(value = "/v1/project/{project_id}/issues/no_sub")
    ResponseEntity<Page<IssueListDTO>> listIssueWithoutSub(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
                                                           @RequestParam(name = "orders") String orders,
                                                           @PathVariable(name = "project_id") Long projectId,
                                                           @RequestBody(required = false) SearchDTO searchDTO);

    @PostMapping(value = "/v1/project/{project_id}/issues/type_code/{typeCode}")
    ResponseEntity<Page<IssueCommonDTO>> listByOptions(
            @PathVariable(name = "project_id") Long projectId,
            @PathVariable(name = "typeCode") String typeCode,
            @RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
            @RequestParam(name = "orders") String orders);
}
