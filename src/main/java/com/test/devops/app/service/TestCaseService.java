package com.test.devops.app.service;

import com.test.devops.api.dto.TestCaseDTO;
import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCaseService {
	IssueDTO insert(Long projectId, IssueCreateDTO issueCreateDTO);

	void delete(Long projectId, Long issueId);

	ResponseEntity<IssueDTO> update(Long projectId, JSONObject issueUpdate);

	ResponseEntity<IssueDTO> query(Long projectId, Long issueId);

	ResponseEntity<Page<IssueCommonDTO>> listIssueWithoutSub(Long projectId, String typeCode, PageRequest pageRequest);
}
