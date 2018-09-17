package io.choerodon.test.manager.infra.feign;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.infra.feign.callback.TestCaseFeignClientFallback;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @PostMapping("/v1/projects/{project_id}/issues")
    ResponseEntity<IssueDTO> createIssue(@PathVariable(name = "project_id") Long projectId,
                                         @RequestBody IssueCreateDTO issueCreateDTO);

    @PutMapping("/v1/projects/{project_id}/issues")
    ResponseEntity<IssueDTO> updateIssue(@PathVariable(name = "project_id") Long projectId,
                                         @RequestBody JSONObject issueUpdate);

    @DeleteMapping(value = "/v1/projects/{project_id}/issues/{issueId}")
    ResponseEntity deleteIssue(@PathVariable(name = "project_id") Long projectId,
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
    ResponseEntity<List<IssueLinkDTO>> listIssueLinkByIssueId(@ApiParam(value = "项目id", required = true)
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

    @PostMapping(value = "/v1/projects/{project_id}/issues/test_component/no_sub_detail")
    ResponseEntity<Page<IssueComponentDetailDTO>> listIssueWithoutSubDetail(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
                                                                            @RequestParam(name = "orders") String orders,
                                                                            @ApiParam(value = "项目id", required = true)
                                                                            @PathVariable(name = "project_id") Long projectId,
                                                                            @ApiParam(value = "查询参数", required = true)
                                                                            @RequestBody(required = false) SearchDTO searchDTO);

    /**
     * @param projectId 缺陷到issue报表过滤接口
     * @param searchDTO
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/issues/issue_ids")
    ResponseEntity<List<Long>> queryIssueIdsByOptions(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId,
                                                      @ApiParam(value = "查询参数", required = true)
                                                      @RequestBody SearchDTO searchDTO);

    /**
     * 克隆issue
     * @param projectId
     * @param issueId
     * @param copyConditionDTO  克隆条件
     * @return
     */
	@PostMapping(value = "/v1/projects/{project_id}/issues/{issueId}/clone_issue")
	ResponseEntity<IssueDTO> cloneIssueByIssueId(@ApiParam(value = "项目id", required = true)
														@PathVariable(name = "project_id") Long projectId,
														@ApiParam(value = "issueId", required = true)
														@PathVariable(name = "issueId") Long issueId,
														@ApiParam(value = "复制条件", required = true)
														@RequestBody CopyConditionDTO copyConditionDTO);

    /**
     *  将issues的version改为目标version
     * @param projectId
     * @param versionId 目标version
     * @param issueIds
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/issues/to_version/{versionId}")
    ResponseEntity<List<IssueSearchDTO>> batchIssueToVersion(@ApiParam(value = "项目id", required = true)
                                                             @PathVariable(name = "project_id") Long projectId,
                                                             @ApiParam(value = "versionId", required = true)
                                                             @PathVariable(name = "versionId") Long versionId,
                                                             @ApiParam(value = "issue id", required = true)
                                                             @RequestBody List<Long> issueIds);

    /**
     * 克隆issue并将他们的version改为目标version
     * @param projectId
     * @param versionId
     * @param issueIds
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/issues/batch_clone_issue/{versionId}")
    ResponseEntity<List<Long>> batchCloneIssue(@ApiParam(value = "项目id", required = true)
                                                           @PathVariable(name = "project_id") Long projectId,
                                                           @ApiParam(value = "versionId", required = true)
                                                           @PathVariable(name = "versionId") Long versionId,
                                                           @ApiParam(value = "issueIds", required = true)
                                                           @RequestBody Long[] issueIds);

    /**
     *  批量删除issue
     * @param projectId
     * @param issueIds
     * @return
     */
    @DeleteMapping(value = "/v1/projects/{project_id}/issues/to_version_test")
    ResponseEntity batchDeleteIssues(@ApiParam(value = "项目id", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "issue id", required = true)
                                            @RequestBody List<Long> issueIds);

    /**
     *  批量替换issue的version
     * @param projectId
     * @param versionId
     * @param issueIds
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/issues/to_version_test/{versionId}")
    ResponseEntity batchIssueToVersionTest(@ApiParam(value = "项目id", required = true)
                                                  @PathVariable(name = "project_id") Long projectId,
                                                  @ApiParam(value = "versionId", required = true)
                                                  @PathVariable(name = "versionId") Long versionId,
                                                  @ApiParam(value = "issue id", required = true)
                                                  @RequestBody List<Long> issueIds);


    /** 报表从issue到缺陷获取初始issue
     * @param page
     * @param size
     * @param orders
     * @param projectId
     * @param searchDTO
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/issues/test_component/filter_linked")
    ResponseEntity<Page<IssueListDTO>> listIssueWithLinkedIssues(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
                                                                        @RequestParam(name = "orders") String orders,
                                                                        @ApiParam(value = "项目id", required = true)
                                                                        @PathVariable(name = "project_id") Long projectId,
                                                                        @ApiParam(value = "查询参数", required = true)
                                                                        @RequestBody(required = false) SearchDTO searchDTO) ;

}
