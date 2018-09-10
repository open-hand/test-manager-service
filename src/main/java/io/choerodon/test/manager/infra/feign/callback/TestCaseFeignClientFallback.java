package io.choerodon.test.manager.infra.feign.callback;

import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/13/18.
 */
@Component
public class TestCaseFeignClientFallback implements TestCaseFeignClient {
    private static final String QUERY_ERROR = "error.UserFeign.query";
    private static final String UPDATE_ERROR = "error.UserFeign.update";
    private static final String CREATE_ERROR = "error.UserFeign.create";
    private static final String DELETE_ERROR = "error.UserFeign.delete";
    private static final String BATCH_QUERY_ERROR = "error.UserFeign.queryList";

    @Override
    public ResponseEntity<IssueDTO> createIssue(Long projectId, IssueCreateDTO issueCreateDTO) {
        throw new CommonException(CREATE_ERROR);
    }

    @Override
    public ResponseEntity<IssueDTO> updateIssue(Long projectId, JSONObject issueUpdate) {
        throw new CommonException(UPDATE_ERROR);
    }

    @Override
    public ResponseEntity deleteIssue(Long projectId, Long issueId) {
        throw new CommonException(DELETE_ERROR);
    }

    @Override
    public ResponseEntity<IssueDTO> queryIssue(Long projectId, Long issueId) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<Page<IssueListDTO>> listIssueWithoutSub(int page, int size,
                                                                  String orders, Long projectId, SearchDTO searchDTO) {
        throw new CommonException(BATCH_QUERY_ERROR);
    }

    @Override
    public ResponseEntity<Page<IssueCommonDTO>> listByOptions(Long projectId, String typeCode, int page, int size, String orders) {
        throw new CommonException(BATCH_QUERY_ERROR);
    }

    @Override
    public ResponseEntity<Page<IssueCommonDTO>> listIssueWithoutSubToTestComponent(Long projectId, SearchDTO searchDTO, int page, int size, String orders) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<List<IssueLinkDTO>> listIssueLinkByIssueId(Long projectId, Long issueId) {
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
	public ResponseEntity<Page<IssueComponentDetailDTO>> listIssueWithoutSubDetail(int page, int size, String orders, Long projectId, SearchDTO searchDTO) {
		throw new CommonException(QUERY_ERROR);
	}

    @Override
    public ResponseEntity<List<Long>> queryIssueIdsByOptions(Long projectId, SearchDTO searchDTO) {
        throw new CommonException(QUERY_ERROR);
    }

    @Override
    public ResponseEntity<IssueDTO> cloneIssueByIssueId(Long projectId, Long issueId, CopyConditionDTO copyConditionDTO) {
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
}
