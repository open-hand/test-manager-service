package io.choerodon.test.manager.infra.feign;

import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.dto.IssueProjectDTO;
import io.choerodon.test.manager.infra.feign.callback.TestCaseFeignClientFallback;
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
    ResponseEntity<IssueDTO> createIssue(@PathVariable(name = "project_id") Long projectId,
                                         @RequestParam(value = "applyType") String applyType,
                                         @RequestBody IssueCreateDTO issueCreateDTO);


    @GetMapping(value = "/v1/projects/{project_id}/issues/{issueId}")
    ResponseEntity<IssueDTO> queryIssue(@PathVariable(name = "project_id") Long projectId,
                                        @PathVariable("issueId") Long issueId ,
                                        @RequestParam("organizationId")Long organizationId);


    @PostMapping(value = "/v1/projects/{project_id}/issues/test_component/no_sub")
    ResponseEntity<Page<IssueListDTO>> listIssueWithoutSubToTestComponent(
            @PathVariable(name = "project_id") Long projectId,
            @RequestBody(required = false) SearchDTO searchDTO,
            @RequestParam("organizationId")Long organizationId,
            @RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
            @RequestParam(name = "orders") String orders);

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
                                                                            @RequestBody(required = false) SearchDTO searchDTO,
                                                                            @RequestParam("organizationId")Long organizationId);

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
     * 将issues的version改为目标version
     *
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
     *
     * @param projectId
     * @param versionId
     * @param issueIds
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/issues/batch_clone_issue/{versionId}")
    ResponseEntity<List<Long>> batchCloneIssue(@PathVariable(name = "project_id") Long projectId,
                                               @PathVariable(name = "versionId") Long versionId,
                                               @RequestBody Long[] issueIds);

    /**
     * 批量删除issue
     *
     * @param projectId
     * @param issueIds
     * @return
     */
    @DeleteMapping(value = "/v1/projects/{project_id}/issues/to_version_test")
    ResponseEntity batchDeleteIssues(@PathVariable(name = "project_id") Long projectId,
                                     @RequestBody List<Long> issueIds);

    /**
     * 批量替换issue的version
     *
     * @param projectId
     * @param versionId
     * @param issueIds
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/issues/to_version_test/{versionId}")
    ResponseEntity batchIssueToVersionTest(@PathVariable(name = "project_id") Long projectId,
                                           @PathVariable(name = "versionId") Long versionId,
                                           @RequestBody List<Long> issueIds);


    /**
     * 报表从issue到缺陷获取初始issue
     *
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
                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                 @RequestBody(required = false) SearchDTO searchDTO,
                                                                 @RequestParam("organizationId")Long organizationId);

    /**
     * 得到所有projectId各自的component
     *
     * @param projectId
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/component/query_all")
    ResponseEntity<Page<ComponentForListDTO>> listByProjectId(@PathVariable(name = "project_id") Long projectId,
                                                              @RequestParam(name = "componentId",required = false) Long componentId,
                                                              @RequestParam(required = false, name = "no_issue_test", defaultValue = "false") Boolean noIssueTest,
                                                              @RequestBody(required = false) SearchDTO searchDTO,
                                                              @RequestParam(name = "page") int page,
                                                              @RequestParam(name = "size") int size,
                                                              @RequestParam(name = "orders") String orders);


    @GetMapping(value = "/v1/projects/{project_id}/issue_labels")
    ResponseEntity<List<IssueLabelDTO>> listIssueLabel(@PathVariable(name = "project_id") Long projectId);

    @GetMapping(value = "/v1/projects/{project_id}/lookup_values/{typeCode}")
    ResponseEntity<LookupTypeWithValuesDTO> queryLookupValueByCode(@PathVariable(name = "project_id") Long projectId,
                                                                   @PathVariable(name = "typeCode") String typeCode);

    @GetMapping(value = "/v1/projects/{project_id}/issue_status/list")
    public ResponseEntity<List<IssueStatusDTO>> listStatusByProjectId(@PathVariable(name = "project_id") Long projectId);

}
