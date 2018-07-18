package io.choerodon.test.manager.infra.feign.callback;

import io.choerodon.agile.api.dto.ProductVersionDTO;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/13/18.
 */
@Component
public class ProductionVersionClientFallback implements ProductionVersionClient {
    private static final String QUERY_ERROR = "error.production.version.query";

    @Override
    public ResponseEntity<Page<ProductVersionPageDTO>> listByOptions(Long projectId, Map<String, Object> searchParamMap) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductVersionDTO>> listByProjectId(Long projectId) {
        throw new CommonException(QUERY_ERROR);

    }

    @Override
    public ResponseEntity<List<Long>> listAllVersionId(Long projectId) {
        throw new CommonException(QUERY_ERROR);
    }
}
