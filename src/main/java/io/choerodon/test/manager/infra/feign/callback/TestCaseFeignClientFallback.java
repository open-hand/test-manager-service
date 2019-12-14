package io.choerodon.test.manager.infra.feign.callback;

import java.util.List;

import com.github.pagehelper.PageInfo;
import io.choerodon.test.manager.api.vo.agile.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;

/**
 * Created by 842767365@qq.com on 6/13/18.
 */
@Component
public class TestCaseFeignClientFallback implements TestCaseFeignClient {

    private static final String QUERY_ERROR = "error.baseFeign.query";
    private static final String UPDATE_ERROR = "error.baseFeign.update";
    private static final String CREATE_ERROR = "error.baseFeign.create";

    @Override
    public ResponseEntity<IssueDTO> createIssue(Long projectId, String applyType, IssueCreateDTO issueCreateDTO) {
        throw new CommonException(CREATE_ERROR);
    }


    @Override
    public ResponseEntity<IssueDTO> queryIssue(Long projectId, Long issueId, Long organizationId) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<PageInfo<IssueListTestVO>> listIssueWithoutSubToTestComponent(Long projectId, SearchDTO searchDTO, Long organizationId, int page, int size, String orders) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<IssueInfoDTO>> listByIssueIds(Long projectId, List<Long> issueIds) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<IssueLinkDTO>> listIssueLinkByBatch(Long projectId, List<Long> issueIds) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<PageInfo<IssueComponentDetailVO>> listIssueWithoutSubDetail(int page, int size, String orders, Long projectId, SearchDTO searchDTO, Long organizationId) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<Long>> queryIssueIdsByOptions(Long projectId, SearchDTO searchDTO) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<IssueSearchDTO>> batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds) {
        throw new CommonException(UPDATE_ERROR);
    }

    @Override
    public ResponseEntity<List<Long>> batchCloneIssue(Long projectId, Long versionId, Long[] issueIds) {
        throw new CommonException(CREATE_ERROR);
    }

    @Override
    public ResponseEntity batchDeleteIssues(Long projectId, List<Long> issueIds) {
        throw new CommonException(UPDATE_ERROR);
    }

    @Override
    public ResponseEntity batchIssueToVersionTest(Long projectId, Long versionId, List<Long> issueIds) {
        throw new CommonException(UPDATE_ERROR);
    }

    @Override
    public ResponseEntity<PageInfo<IssueListTestWithSprintVersionDTO>> listIssueWithLinkedIssues(int page, int size, String orders, Long projectId, SearchDTO searchDTO, Long organizationId) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<PageInfo<ComponentForListDTO>> listByProjectId(Long projectId, SearchDTO searchDTO) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<IssueLabelDTO>> listIssueLabel(Long projectId) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<LookupTypeWithValuesDTO> queryLookupValueByCode(String typeCode) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<IssueStatusDTO>> listStatusByProjectId(Long projectId) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<PageInfo<IssueLinkTypeDTO>> listIssueLinkType(Long projectId, Long issueLinkTypeId, IssueLinkTypeSearchDTO issueLinkTypeSearchDTO) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<IssueNumDTO> queryIssueByIssueNum(Long projectId, String issueNum) {
        throw new CommonException(QUERY_ERROR);
    }
}
