package io.choerodon.test.manager.infra.feign.callback;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.devops.AppServiceDeployVO;
import io.choerodon.test.manager.api.vo.devops.AppServiceVersionRespVO;
import io.choerodon.test.manager.api.vo.devops.ApplicationRepDTO;
import io.choerodon.test.manager.api.vo.devops.InstanceValueVO;
import io.choerodon.test.manager.infra.feign.ApplicationFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by zongw.lee@gmail.com on 26/11/2018
 */
@Component
public class ApplicationFeignClientFallback implements ApplicationFeignClient {
    @Override
    public ResponseEntity<String> getVersionValue(Long projectId, Long versionId) {
        throw new CommonException("error.ApplicationFeignClient.getVersionValue");
    }

    @Override
    public ResponseEntity<List<AppServiceVersionRespVO>> getAppversion(Long projectId, Long[] versionIds) {
        throw new CommonException("error.ApplicationFeignClient.getAppversion");
    }

    @Override
    public ResponseEntity<PageInfo<AppServiceVersionRespVO>> pageByOptions(Long projectId, int page, int size, Boolean deployOnly, String orders, Long appServiceId, String searchParam) {
        throw new CommonException("error.ApplicationFeignClient.pageByOptions");
    }

    @Override
    public ResponseEntity<ApplicationRepDTO> queryByAppId(Long projectId, Long appServiceId) {
        throw new CommonException("error.ApplicationFeignClient.queryByAppId");
    }

    @Override
    public ResponseEntity<InstanceValueVO> previewValues(Long projectId, InstanceValueVO instanceValueVO, Long versionId) {
        throw new CommonException("error.ApplicationFeignClient.previewValues");
    }

    @Override
    public void deployTestApp(Long projectId, AppServiceDeployVO appServiceDeployVO) {
        throw new CommonException("error.ApplicationFeignClient.deployTestApp");
    }

    @Override
    public void getTestStatus(Map<Long, List<String>> testReleases) {
        throw new CommonException("error.ApplicationFeignClient.getTestStatus");
    }
}
