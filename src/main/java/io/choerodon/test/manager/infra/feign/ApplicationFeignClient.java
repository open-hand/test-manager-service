package io.choerodon.test.manager.infra.feign;


import io.choerodon.asgard.api.dto.QuartzTask;
import io.choerodon.asgard.api.dto.ScheduleMethodDTO;
import io.choerodon.asgard.api.dto.ScheduleTaskDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.devops.api.dto.ApplicationRepDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.infra.feign.callback.ScheduleFeignClientFallback;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
            @PathVariable Long applicationId);
}

