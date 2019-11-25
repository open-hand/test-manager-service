package io.choerodon.test.manager.infra.feign;

import java.util.List;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import io.choerodon.agile.api.vo.*;
import io.choerodon.test.manager.api.vo.TestCaseMigrateDTO;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.feign.callback.TestCaseFeignClientFallback;

/**
 * Created by 842767365@qq.com on 6/13/18.
 */
@Component
@FeignClient(value = "agile-service", fallback = TestCaseFeignClientFallback.class)
public interface TestDataLogFeignClient {

    /**
     * 迁移附件数据专用
     */
    @GetMapping(value = "/v1/fix_data/migrate_data_log/{project_id}")
    ResponseEntity<List<DataLogFixVO>> migrateDataLog(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId);

}
