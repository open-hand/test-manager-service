package io.choerodon.test.manager.app.service;

import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCaseService {
	//
	ResponseEntity<Page<IssueCommonDTO>> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest);

	ResponseEntity<Page<IssueComponentDetailDTO>> listIssueWithoutSubDetail(Long projectId, SearchDTO searchDTO, PageRequest pageRequest);

	ResponseEntity<IssueDTO> queryIssue(Long projectId, Long issueId);

	Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, PageRequest pageRequest);

	<T> Map<Long, IssueInfosDTO> getIssueInfoMapAndPopulatePageInfo(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Page<T> page);

	Object getIssueInfoMap(Long projectId, SearchDTO searchDTO, boolean needDetail);

	Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, Long[] issueIds, boolean needDetail);

	Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, Long[] issueIds, PageRequest pageRequest);

	List<IssueLinkDTO> getLinkIssueFromIssueToTest(Long projectId, List<Long> issueId);

	List<IssueLinkDTO> getLinkIssueFromTestToIssue(Long projectId, List<Long> issueId);

	List<IssueLinkDTO> listIssueLinkByIssueId(Long projectId, List<Long> issueId);

	Map<Long, ProductVersionDTO> getVersionInfo(Long projectId);

	ProjectDTO getProjectInfo(Long projectId);
}
