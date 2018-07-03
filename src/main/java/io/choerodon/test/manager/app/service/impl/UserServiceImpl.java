package io.choerodon.test.manager.app.service.impl;

import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.agile.api.dto.UserDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.infra.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 7/2/18.
 */
@Component
public class UserServiceImpl implements UserService {
	@Autowired
	private UserFeignClient userFeignClient;

	@Override
	public UserDO query(Long userId) {
		CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
		//UserDO userDO = userFeignClient.query(customUserDetails.getOrganizationId(), userId).getBody();
		UserDO userDO = userFeignClient.query(new Long(1), userId).getBody();
		return userDO;
	}

	public List<UserDO> query(Long[] ids) {
		return userFeignClient.listUsersByIds(ids).getBody();
	}

	@Override
	public ResponseEntity<Page<UserDTO>> list(PageRequest pageRequest, Long projectId, String param, Long userId) {
		return userFeignClient.list(projectId, userId, pageRequest.getPage(), pageRequest.getSize(), param);
	}
}
