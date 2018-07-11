package io.choerodon.test.manager.app.service;

import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.agile.api.dto.UserDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by jialongZuo@hand-china.com on 7/2/18.
 */
public interface UserService {
	UserDO query(Long userId);

	Map<Long, UserDO> query(Long[] ids);

	ResponseEntity<Page<UserDTO>> list(PageRequest pageRequest, Long projectId, String param, Long userId);
}
