package io.choerodon.test.manager.infra.feign.callback;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.vo.TenantVO;
import io.choerodon.test.manager.api.vo.agile.ProjectDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.test.manager.api.vo.agile.UserDO;
import io.choerodon.test.manager.api.vo.agile.UserDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.feign.BaseFeignClient;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/24
 */
@Component
public class BaseFeignClientFallback implements BaseFeignClient {

    private static final String QUERY_ERROR = "error.baseFeign.query";
    private static final String BATCH_QUERY_ERROR = "error.baseFeign.queryList";

    @Override
    public ResponseEntity<List<UserDO>> listUsersByIds(Long[] ids, Boolean onlyEnabled) {
        throw new CommonException(BATCH_QUERY_ERROR);
    }

    @Override
    public ResponseEntity<Page<UserDTO>> list(Long id, int page, int size) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<ProjectDTO> queryProject(Long id) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<ProjectDTO>> listProjectsByOrgId(Long organizationId) {
        throw new CommonException("error.iamServiceFeignFallback.listProjectsByOrgId");
    }

    @Override
    public ResponseEntity<Page<TenantVO>> getAllOrgs(int page, int size) {
        throw new CommonException(QUERY_ERROR);
    }
}