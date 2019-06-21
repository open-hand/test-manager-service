package io.choerodon.test.manager.infra.feign.callback;


import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.devops.api.dto.ApplicationVersionRepDTO;
import io.choerodon.test.manager.infra.feign.DevopsClient;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public class DevopsClientFallback implements DevopsClient {

    private static final String QUERY_ERROR = "error.devops.fegin.query";

    @Override
    public void getTestStatus(Map<Long,List<String>> releaseName) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<PageInfo<ApplicationVersionRepDTO>> pageByOptions(Long projectId, int page, int size, String orders, Long appId, String searchParam) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<ApplicationVersionRepDTO>> getAppversion(Long projectId, Long[] appVersionIds) {
        throw new CommonException(QUERY_ERROR);
    }
}
