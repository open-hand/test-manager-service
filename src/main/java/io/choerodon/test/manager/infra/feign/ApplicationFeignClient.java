package io.choerodon.test.manager.infra.feign;

import java.util.List;
import java.util.Map;

import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.vo.devops.AppServiceDeployVO;
import io.choerodon.test.manager.api.vo.devops.AppServiceVersionRespVO;
import io.choerodon.test.manager.api.vo.devops.ApplicationRepDTO;
import io.choerodon.test.manager.api.vo.devops.InstanceValueVO;
import io.choerodon.test.manager.infra.feign.callback.ApplicationFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @author zongw.lee@gmail.com
 * @since 2018/11/26
 */
@Component
@FeignClient(value = "devops-service", fallbackFactory = ApplicationFeignClientFallback.class)
public interface ApplicationFeignClient {
    /**
     * 根据版本id获取版本values
     *
     * @param projectId 项目ID
     * @param versionId 应用服务版本ID
     * @return String
     */
    @GetMapping(value = "/v1/projects/{project_id}/app_service_versions/{versionId}/queryValue")
    ResponseEntity<String> getVersionValue(@PathVariable(value = "project_id") Long projectId,
                                           @PathVariable(value = "versionId") Long versionId);

    /**
     * 根据版本id查询版本信息
     *
     * @param projectId  项目ID
     * @param versionIds 应用版本ID
     * @return ApplicationVersionRepDTO
     */
    @PostMapping(value = "/v1/projects/{project_id}/app_service_versions/list_by_versionIds")
    ResponseEntity<List<AppServiceVersionRespVO>> getAppversion(
            @PathVariable(value = "project_id") Long projectId,
            @RequestParam(value = "versionIds") Long[] versionIds);

    /**
     * 分页查询服务版本
     *
     * @param projectId
     * @param page
     * @param size
     * @param orders
     * @param appServiceId
     * @param searchParam
     * @return
     */
    @PostMapping(value = "/v1/projects/{project_id}/app_service_versions/page_by_options")
    ResponseEntity<Page<AppServiceVersionRespVO>> pageByOptions(
            @PathVariable(value = "project_id") Long projectId,
            @RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
            @RequestParam(value = "deploy_only") Boolean deployOnly,
            @RequestParam(name = "orders") String orders,
            @RequestParam(required = false, name = "app_service_id") Long appServiceId,
            @RequestBody(required = false) String searchParam);


    /**
     * 项目下查询单个应用服务信息
     *
     * @param projectId    项目id
     * @param appServiceId 应用Id
     * @return ApplicationRepDTO
     */
    @GetMapping("/v1/projects/{project_id}/app_service/{app_service_id}")
    ResponseEntity<ApplicationRepDTO> queryByAppId(
            @PathVariable(value = "project_id") Long projectId,
            @PathVariable(value = "app_service_id") Long appServiceId);

    /**
     * 查询预览value
     *
     * @param projectId       项目id
     * @param instanceValueVO 部署value
     * @return InstanceValueVO
     */
    @PostMapping("/v1/projects/{project_id}/app_service_instances/preview_value")
    ResponseEntity<InstanceValueVO> previewValues(
            @PathVariable(value = "project_id") Long projectId,
            @RequestBody InstanceValueVO instanceValueVO,
            @RequestParam(value = "versionId") Long versionId);

    /**
     * 部署自动化测试应用
     *
     * @param projectId          项目id
     * @param appServiceDeployVO 部署信息
     * @return ApplicationInstanceDTO
     */
    @PostMapping("/v1/projects/{project_id}/app_service_instances/deploy_test_app")
    void deployTestApp(@PathVariable(value = "project_id") Long projectId,
                       @RequestBody AppServiceDeployVO appServiceDeployVO);

    /**
     * 查询自动化测试应用实例状态
     *
     * @param releaseName
     */
    @PostMapping("/webhook/get_test_status")
    void getTestStatus(
            @RequestBody Map<Long, List<String>> releaseName);
}

