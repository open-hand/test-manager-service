package io.choerodon.test.manager.infra.feign.callback;

import io.choerodon.agile.api.dto.ProjectDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.feign.ProjectFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Created by 842767365@qq.com on 8/10/18.
 */
@Component
public class ProjectFeignClientFallback implements ProjectFeignClient {

	private static final String QUERY_ERROR = "error.Projecteign.query";

	@Override
	public ResponseEntity<ProjectDTO> query(Long id) {
		throw new CommonException(QUERY_ERROR);
	}
}
