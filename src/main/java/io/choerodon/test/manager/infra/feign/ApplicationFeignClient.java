package io.choerodon.test.manager.infra.feign;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import io.choerodon.devops.api.dto.ApplicationRepDTO;
import io.choerodon.devops.api.dto.ApplicationVersionRepDTO;
import io.choerodon.devops.api.dto.DevopsApplicationDeployDTO;
import io.choerodon.devops.api.dto.ReplaceResult;
import io.choerodon.test.manager.infra.feign.callback.ScheduleFeignClientFallback;

/**
 * @author zongw.lee@gmail.com
 * @since 2018/11/26
 */
@Component
@FeignClient(value = "devops-service", fallback = ScheduleFeignClientFallback.class)
public interface ApplicationFeignClient {
    /**
     * 根据版本id获取版本values
     *
     * @param projectId    项目ID
     * @param appVersionId 应用版本ID
     * @return String
     */
    @GetMapping(value = "/v1/projects/{project_id}/app_versions/{app_verisonId}/queryValue")
    ResponseEntity<String> getVersionValue(@PathVariable(value = "project_id") Long projectId,
                                           @PathVariable(value = "app_verisonId") Long appVersionId);

    /**
     * 项目下查询单个应用信息
     *
     * @param projectId     项目id
     * @param applicationId 应用Id
     * @return ApplicationRepDTO
     */
    @GetMapping("/v1/projects/{project_id}/apps/{applicationId}/detail")
    ResponseEntity<ApplicationRepDTO> queryByAppId(
            @PathVariable(value = "project_id") Long projectId,
            @PathVariable(value = "applicationId") Long applicationId);

    /**
     * 根据版本id查询版本信息
     *
     * @param projectId     项目ID
     * @param appVersionIds 应用版本ID
     * @return ApplicationVersionRepDTO
     */
    @PostMapping(value = "/v1/projects/{project_id}/app_versions/list_by_appVersionIds")
    ResponseEntity<List<ApplicationVersionRepDTO>> getAppversion(
            @PathVariable(value = "project_id") Long projectId,
            @RequestParam("appVersionIds") Long[] appVersionIds);

    /**
     * @param projectId     项目id
     * @param replaceResult 部署value
     * @return ReplaceResult
     */
    @PostMapping("/v1/projects/{project_id}/app_instances/previewValue")
    ResponseEntity<ReplaceResult> previewValues(
            @PathVariable(value = "project_id") Long projectId,
            @RequestBody ReplaceResult replaceResult,
            @RequestParam(value = "appVersionId") Long appVersionId);

    /**
     * 部署自动化测试应用
     *
     * @param projectId            项目id
     * @param applicationDeployDTO 部署信息
     * @return ApplicationInstanceDTO
     */
    @PostMapping("/v1/projects/{project_id}/app_instances/deploy_test_app")
    void deployTestApp(@PathVariable(value = "project_id") Long projectId,
                       @RequestBody DevopsApplicationDeployDTO applicationDeployDTO);

    @PostMapping("/webhook/get_test_status")
    void getTestStatus(
            @RequestBody Map<Long, List<String>> releaseName);

    @PostMapping(value = "/v1/projects/{project_id}/app_versions/list_by_options")
    ResponseEntity<PageInfo<ApplicationVersionRepDTO>> pageByOptions(
            @PathVariable(value = "project_id") Long projectId,
            @RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
            @RequestParam(name = "orders") String orders,
            @RequestParam(required = false, name = "appId") Long appId,
            @RequestBody(required = false) String searchParam);

}

