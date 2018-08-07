package io.choerodon.test.manager.infra.feign;

import io.choerodon.test.manager.infra.feign.callback.TestCaseFeignClientFallback;
import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/13/18.
 */
@Component
@FeignClient(value = "agile-service", fallback = TestCaseFeignClientFallback.class)
public interface TestCaseFeignClient {

    @PostMapping("/v1/projects/{project_id}/issues")
    public ResponseEntity<IssueDTO> createIssue(@PathVariable(name = "project_id") Long projectId,
                                                @RequestBody IssueCreateDTO issueCreateDTO);

    @PutMapping("/v1/projects/{project_id}/issues")
    public ResponseEntity<IssueDTO> updateIssue(@PathVariable(name = "project_id") Long projectId,
                                                @RequestBody JSONObject issueUpdate);

    @DeleteMapping(value = "/v1/projects/{project_id}/issues/{issueId}")
    public ResponseEntity deleteIssue(@PathVariable(name = "project_id") Long projectId,
                                      @PathVariable("issueId") Long issueId);

    @GetMapping(value = "/v1/projects/{project_id}/issues/{issueId}")
    ResponseEntity<IssueDTO> queryIssue(@PathVariable(name = "project_id") Long projectId,
                                        @PathVariable("issueId") Long issueId);

    @PostMapping(value = "/v1/projects/{project_id}/issues/no_sub")
    ResponseEntity<Page<IssueListDTO>> listIssueWithoutSub(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
                                                           @RequestParam(name = "orders") String orders,
                                                           @PathVariable(name = "project_id") Long projectId,
                                                           @RequestBody(required = false) SearchDTO searchDTO);

    @PostMapping(value = "/v1/projects/{project_id}/issues/type_code/{typeCode}")
    ResponseEntity<Page<IssueCommonDTO>> listByOptions(
            @PathVariable(name = "project_id") Long projectId,
            @PathVariable(name = "typeCode") String typeCode,
            @RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
            @RequestParam(name = "orders") String orders);


	@PostMapping(value = "/v1/projects/{project_id}/issues/test_component/no_sub")
	ResponseEntity<Page<IssueCommonDTO>> listIssueWithoutSubToTestComponent(
			@PathVariable(name = "project_id") Long projectId,
			@RequestBody(required = false) SearchDTO searchDTO,
			@RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
			@RequestParam(name = "orders") String orders);


	@GetMapping(value = "/v1/projects/{project_id}/issue_links/{issueId}")
	public ResponseEntity<List<IssueLinkDTO>> listIssueLinkByIssueId(@ApiParam(value = "项目id", required = true)
																	 @PathVariable(name = "project_id") Long projectId,
																	 @ApiParam(value = "issueId", required = true)
																	 @PathVariable(name = "issueId") Long issueId);

	@PostMapping("/v1/projects/{project_id}/issues/issue_infos")
	ResponseEntity<List<IssueInfoDTO>> listByIssueIds(@ApiParam(value = "项目id", required = true)
													  @PathVariable(name = "project_id") Long projectId,
													  @ApiParam(value = "issue ids", required = true)
													  @RequestBody List<Long> issueIds);


	@PostMapping("/v1/projects/{project_id}/issue_links/issues")
	ResponseEntity<List<IssueLinkDTO>> listIssueLinkByBatch(@ApiParam(value = "项目id", required = true)
																   @PathVariable(name = "project_id") Long projectId,
																   @ApiParam(value = "issueIds", required = true)
																   @RequestBody List<Long> issueIds);
}
