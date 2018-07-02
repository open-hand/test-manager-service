package io.choerodon.test.manager.app.service;

import io.choerodon.agile.api.dto.UserDO;

/**
 * Created by jialongZuo@hand-china.com on 7/2/18.
 */
public interface UserService {
	UserDO query(Long userId);
}
