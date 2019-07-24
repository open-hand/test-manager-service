package io.choerodon.test.manager.infra.feign.callback;

import io.choerodon.core.exception.CommonException;
import io.choerodon.devops.api.dto.ApplicationRepDTO;
import io.choerodon.devops.api.dto.ApplicationVersionRepDTO;
import io.choerodon.devops.api.dto.DevopsApplicationDeployDTO;
import io.choerodon.devops.api.dto.ReplaceResult;
import io.choerodon.test.manager.infra.feign.ApplicationFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zongw.lee@gmail.com on 26/11/2018
 */
@Component
public class ApplicationFeignClientFallback implements ApplicationFeignClient {

    private static final String QUERY_VERSION_VALUE_ERROR = "error.ApplicationFeign.query.version.value";
    private static final String QUERY_REPLACE_VERSION_VALUE_ERROR = "error.ApplicationFeign.query.replace.version.value";
    private static final String QUERY_APP_ERROR = "error.ApplicationFeign.query.app";
    private static final String QUERY_VERSION_ERROR = "error.ApplicationFeign.query.version";
    private static final String CREATE_APPLICATION_ERROR = "error.ApplicationFeign.deploy.application";

    @Override
    public ResponseEntity<String> getVersionValue(Long projectId, Long appVersionId) {
        throw new CommonException(QUERY_VERSION_VALUE_ERROR);
    }

    @Override
    public ResponseEntity<ApplicationRepDTO> queryByAppId(Long projectId, Long applicationId) {
        throw new CommonException(QUERY_APP_ERROR);
    }

    @Override
    public ResponseEntity<List<ApplicationVersionRepDTO>> getAppversion(Long projectId, Long[] appVersionIds) {
        throw new CommonException(QUERY_VERSION_ERROR);
    }

    @Override
    public ResponseEntity<ReplaceResult> previewValues(Long projectId, ReplaceResult replaceResult, Long appVersionId) {
        throw new CommonException(QUERY_REPLACE_VERSION_VALUE_ERROR);
    }

    @Override
    public void deployTestApp(Long projectId, DevopsApplicationDeployDTO applicationDeployDTO) {
        throw new CommonException(CREATE_APPLICATION_ERROR);
    }
}
