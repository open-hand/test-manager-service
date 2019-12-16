package io.choerodon.test.manager.infra.feign.callback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.test.manager.api.vo.agile.DataLogFixVO;
import io.choerodon.test.manager.api.vo.agile.ProductVersionDTO;
import io.choerodon.test.manager.api.vo.agile.ProjectInfoFixVO;
import io.choerodon.test.manager.api.vo.agile.TestVersionFixVO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.IssueLinkFixVO;
import io.choerodon.test.manager.api.vo.TestCaseMigrateDTO;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.feign.DataFixFeignClient;

/**
 * @author: 25499
 * @date: 2019/11/25 18:55
 * @description:
 */
@Component
public class DataFixFeignClientFallback implements DataFixFeignClient {

    private static final String QUERY_ERROR = "error.baseFeign.query";

    @Override
    public ResponseEntity<List<DataLogFixVO>> migrateDataLog(Long projectId) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<TestVersionFixVO>> migrateVersion() {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<IssueLinkFixVO>> listIssueLinkByIssueIds(Long projectId) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<ProjectInfoFixVO>> queryAllProjectInfo() {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductVersionDTO>> queryForTestManager(Long projectId) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<TestCaseMigrateDTO>> migrateTestCase(Long projectId) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<Long>> queryIds() {
        return null;
    }

    @Override
    public ResponseEntity<List<TestCaseAttachmentDTO>> migrateAttachment() {
        return null;
    }
}
