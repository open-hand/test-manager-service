package io.choerodon.test.manager.app.service;

import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCaseService {
//
	ResponseEntity<Page<IssueListDTO>> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest);

	Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, PageRequest pageRequest);

	Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO);

	Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, Long[] issueIds);
}
