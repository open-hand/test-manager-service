package io.choerodon.test.manager.infra.feign;

import com.github.pagehelper.PageInfo;
import io.choerodon.devops.api.dto.ApplicationVersionRepDTO;
import io.choerodon.test.manager.infra.feign.callback.DevopsClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Component
@FeignClient(value = "devops-service", fallback = DevopsClientFallback.class)
public interface DevopsClient {


    @PostMapping("/webhook/get_test_status")
    void getTestStatus(
            @RequestBody Map<Long,List<String>> releaseName);

    @PostMapping(value = "/v1/projects/{project_id}/app_versions/list_by_options")
    ResponseEntity<PageInfo<ApplicationVersionRepDTO>> pageByOptions(
            @PathVariable(value = "project_id") Long projectId,
            @RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
            @RequestParam(name = "orders") String orders,
            @RequestParam(required = false,name = "appId") Long appId,
            @RequestBody(required = false) String searchParam);



    @PostMapping(value = "/v1/projects/{project_id}/app_versions/list_by_appVersionIds")
     ResponseEntity<List<ApplicationVersionRepDTO>> getAppversion(
            @PathVariable(value = "project_id") Long projectId,
            @RequestParam Long[] appVersionIds);

}