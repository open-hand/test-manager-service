package io.choerodon.test.manager.infra.feign.callback;


import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.feign.DevopsClient;
import org.springframework.http.ResponseEntity;

public class DevopsClientFallback implements DevopsClient {

    private static final String QUERY_ERROR = "error.listCluster.query";

}
