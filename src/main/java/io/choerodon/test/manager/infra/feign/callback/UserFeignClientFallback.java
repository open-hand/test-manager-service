package io.choerodon.test.manager.infra.feign.callback;

import java.util.List;

import com.github.pagehelper.PageInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.agile.api.dto.UserDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.feign.UserFeignClient;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/24
 */
@Component
public class UserFeignClientFallback implements UserFeignClient {

    private static final String QUERY_ERROR = "error.UserFeign.query";
    private static final String BATCH_QUERY_ERROR = "error.UserFeign.queryList";

    @Override
    public ResponseEntity<UserDO> query(Long organizationId, Long id) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<UserDO>> listUsersByIds(Long[] ids, Boolean onlyEnabled) {
        throw new CommonException(BATCH_QUERY_ERROR);
    }

    @Override
    public ResponseEntity<PageInfo<UserDTO>> list(Long id, int page, int size) {
        throw new CommonException(QUERY_ERROR);
    }
}