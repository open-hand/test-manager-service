package io.choerodon.test.manager.infra.feign.callback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import io.choerodon.agile.api.vo.ProjectInfoFixVO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.feign.ProjectInfoFeignClient;

/**
 * @author: 25499
 * @date: 2019/11/22 9:24
 * @description:
 */
@Component
public class ProjectInfoFeignClientFallback implements ProjectInfoFeignClient {
    private static final String QUERY_ERROR = "error.projectInfo.query";

    @Override
    public ResponseEntity<List<ProjectInfoFixVO>> queryAllProjectInfo() {
        throw new CommonException(QUERY_ERROR);
    }
}
