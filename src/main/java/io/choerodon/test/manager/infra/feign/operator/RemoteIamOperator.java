package io.choerodon.test.manager.infra.feign.operator;

import io.choerodon.test.manager.api.vo.agile.ProjectDTO;
import io.choerodon.test.manager.infra.feign.BaseFeignClient;
import org.hzero.core.util.ResponseUtils;
import org.springframework.stereotype.Component;

/**
 * Copyright (c) 2022. Hand Enterprise Solution Company. All right reserved.
 *
 * @author zongqi.hao@zknow.com
 * @since 2022/8/3
 */
@Component
public class RemoteIamOperator {

    private final BaseFeignClient baseFeignClient;


    public RemoteIamOperator(BaseFeignClient baseFeignClient) {
        this.baseFeignClient = baseFeignClient;
    }

    public ProjectDTO getProjectById(Long projectId) {
        return ResponseUtils.getResponse(baseFeignClient.queryProject(projectId), ProjectDTO.class);
    }


}
