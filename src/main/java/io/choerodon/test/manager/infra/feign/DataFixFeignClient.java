package io.choerodon.test.manager.infra.feign;

import java.util.List;

import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.choerodon.agile.api.vo.DataLogFixVO;
import io.choerodon.agile.api.vo.ProductVersionDTO;
import io.choerodon.agile.api.vo.ProjectInfoFixVO;
import io.choerodon.agile.api.vo.TestVersionFixVO;
import io.choerodon.test.manager.api.vo.IssueLinkFixVO;
import io.choerodon.test.manager.api.vo.TestCaseMigrateDTO;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.feign.callback.TestCaseFeignClientFallback;

/**
 * Created by 842767365@qq.com on 6/13/18.
 */
@Component
@FeignClient(value = "agile-service", fallback = TestCaseFeignClientFallback.class)
public interface DataFixFeignClient {

    /**
     * 迁移日志
     */
    @GetMapping(value = "/v1/fix_data/migrate_data_log/{project_id}")
    ResponseEntity<List<DataLogFixVO>> migrateDataLog(@ApiParam(value = "项目id", required = true)
                                                      @PathVariable(name = "project_id") Long projectId);
    /**
     * 迁移版本
     */
    @GetMapping(value = "/v1/fix_data/migrate_version")
    ResponseEntity<List<TestVersionFixVO>> migrateVersion();
    /**
     * 迁移用例
     */
    @GetMapping("/v1/fix_data/migrate_issueLink/{project_id}")
    ResponseEntity<List<IssueLinkFixVO>> listIssueLinkByIssueIds(@ApiParam(value = "项目id", required = true)
                                                                 @PathVariable(name = "project_id") Long projectId);

    /**
     * 迁移项目
     * @return
     */
    @GetMapping(value = "/v1/fix_data/migrate_project_info")
    ResponseEntity<List<ProjectInfoFixVO>> queryAllProjectInfo();

    /**
     *
     * @param projectId
     * @return
     */
    @GetMapping(value = "/v1/projects/{project_id}/product_version/versions/all")
    ResponseEntity<List<ProductVersionDTO>> queryForTestManager(@PathVariable(name = "project_id") Long projectId);
    /***
     * 迁移测试用例数据专用
     * @return
     */
    @GetMapping(value = "/v1/fix_data/migrate_issue/{project_id}")
    ResponseEntity<List<TestCaseMigrateDTO>> migrateTestCase(@PathVariable("project_id")Long projectId);

    /**
     * 迁移数据专用
     * @return
     */
    @GetMapping(value = "/v1/fix_data/project_ids")
    ResponseEntity<List<Long>> queryIds();

    /**
     * 迁移附件数据专用
     */
    @GetMapping(value = "/v1/fix_data/migrate_attachment")
    ResponseEntity<List<TestCaseAttachmentDTO>> migrateAttachment();
}
