package io.choerodon.test.manager.infra.feign;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.vo.agile.*;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import io.choerodon.test.manager.infra.feign.callback.TestCaseFeignClientFallback;

/**
 * Created by 842767365@qq.com on 6/13/18.
 */
@Component
@FeignClient(value = "agile-service", fallback = TestCaseFeignClientFallback.class)
public interface TestCaseFeignClient {

    @PostMapping("/v1/projects/{project_id}/issues")
    ResponseEntity<String> createIssue(@PathVariable(name = "project_id") Long projectId,
                                         @RequestParam(value = "applyType") String applyType,
                                         @RequestBody IssueCreateDTO issueCreateDTO);


    @GetMapping(value = "/v1/projects/{project_id}/issues/{issueId}")
    ResponseEntity<String> queryIssue(@PathVariable(name = "project_id") Long projectId,
                                        @PathVariable("issueId") Long issueId,
                                        @RequestParam("organizationId") Long organizationId);


    @PostMapping(value = "/v1/projects/{project_id}/issues/test_component/no_sub")
    ResponseEntity<String> listIssueWithoutSubToTestComponent(
            @PathVariable(name = "project_id") Long projectId,
            @RequestBody(required = false) SearchDTO searchDTO,
            @RequestParam(name = "organizationId") Long organizationId,
            @RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
            @RequestParam(name = "sort") String sort);

    @PostMapping("/v1/projects/{project_id}/issues/issue_infos")
    ResponseEntity<String> listByIssueIds(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId,
                                                      @ApiParam(value = "issue ids", required = true)
                                                      @RequestBody List<Long> issueIds);


    @PostMapping("/v1/projects/{project_id}/issue_links/issues")
    ResponseEntity<String> listIssueLinkByBatch(@ApiParam(value = "项目id", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "issueIds", required = true)
                                                            @RequestBody List<Long> issueIds);

    @PostMapping(value = "/v1/projects/{project_id}/issues/test_component/no_sub_detail")
    ResponseEntity<String> listIssueWithoutSubDetail(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
                                                                               @RequestParam(name = "orders") String orders,
                                                                               @ApiParam(value = "项目id", required = true)
                                                                                @PathVariable(name = "project_id") Long projectId,
                                                                               @ApiParam(value = "查询参数", required = true)
                                                                                @RequestBody(required = false) SearchDTO searchDTO,
                                                                               @RequestParam(name = "organizationId") Long organizationId);

    /**
     * @param projectId 缺陷到issue报表过滤接口
     * @param searchDTO
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/issues/issue_ids")
    ResponseEntity<String> queryIssueIdsByOptions(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId,
                                                      @ApiParam(value = "查询参数", required = true)
                                                      @RequestBody SearchDTO searchDTO);

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
    ResponseEntity<String> listIssueWithLinkedIssues(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
                                                                                          @RequestParam(name = "orders") String orders,
                                                                                          @PathVariable(name = "project_id") Long projectId,
                                                                                          @RequestBody(required = false) SearchDTO searchDTO,
                                                                                          @RequestParam(name = "organizationId") Long organizationId);

    @GetMapping(value = "/v1/projects/{project_id}/issue_status/list")
    ResponseEntity<String> listStatusByProjectId(@PathVariable(name = "project_id") Long projectId);

    @PostMapping(value = "/v1/projects/{project_id}/issue_link_types/query_all")
    ResponseEntity<String> listIssueLinkType(@PathVariable(name = "project_id") Long projectId,

                                                                 @RequestParam(name = "issueLinkTypeId",required = false) Long issueLinkTypeId,
                                                                 @RequestBody IssueLinkTypeSearchDTO issueLinkTypeSearchDTO);

    @PostMapping(value = "/v1/projects/{project_id}/issues/query_by_issue_num")
    ResponseEntity<String> queryIssueByIssueNum(@ApiParam(value = "项目id", required = true)
                                                     @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "issue编号", required = true)
                                                     @RequestBody String issueNum);

    @GetMapping(value = "/v1/lookup_values/{typeCode}")
    ResponseEntity<String> queryLookupValueByCode(@PathVariable(name = "typeCode") String typeCode);



}
