package io.choerodon.test.manager.app.service.impl;

import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */

@Component
public class TestCaseServiceImpl implements TestCaseService {


    @Autowired
    TestCaseFeignClient testCaseFeignClient;


	@Override
	public ResponseEntity<Page<IssueCommonDTO>> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
		Assert.notNull(projectId, "error.query.issue.projectId.not.null");
		return testCaseFeignClient.listIssueWithoutSubToTestComponent(projectId, searchDTO,pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort().toString());
    }

	@Override
	public ResponseEntity<IssueDTO> queryIssue(Long projectId, Long issueId) {
		return testCaseFeignClient.queryIssue(projectId, issueId);
	}

	@Override
	public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
		return listIssueWithoutSub(projectId, searchDTO, pageRequest).getBody().stream().collect(Collectors.toMap(IssueCommonDTO::getIssueId, v -> new IssueInfosDTO(v)));
	}

	@Override
	public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO) {
		PageRequest pageRequest = new PageRequest();
		pageRequest.setSize(999999999);
		pageRequest.setPage(0);
		pageRequest.setSort(new Sort(Sort.Direction.ASC, new String[]{"issueId"}));
		return listIssueWithoutSub(projectId, searchDTO, pageRequest).getBody().stream().collect(Collectors.toMap(IssueCommonDTO::getIssueId, v -> new IssueInfosDTO(v)));
	}

	@Override
	public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, Long[] issueIds) {
		Assert.notNull(issueIds, "error.getIssueWithIssueIds.issueId.not.null");
		return getIssueInfoMap(projectId, buildIdsSearchDTO(issueIds));
	}

	@Override
	public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, Long[] issueIds, PageRequest pageRequest) {
		Assert.notNull(issueIds, "error.getIssueWithIssueIds.issueId.not.null");
		return getIssueInfoMap(projectId, buildIdsSearchDTO(issueIds), pageRequest);
	}

	private SearchDTO buildIdsSearchDTO(Long[] issueIds) {
		SearchDTO searchDTO = new SearchDTO();
		Map map = new HashMap();
		map.put("issueIds", issueIds);
		searchDTO.setOtherArgs(map);
		return searchDTO;
	}


	@Override
	public List<IssueLinkDTO> listIssueLinkByIssueId(Long projectId, Long issueId) {
		Assert.notNull(projectId, "error.get.linkId.projectId.not.null");
		Assert.notNull(issueId, "error.get.linkId.issueId.not.null");
		return testCaseFeignClient.listIssueLinkByIssueId(projectId, issueId).getBody();
	}

	@Override
	public List<IssueLinkDTO> getLinkIssueFromIssueToTest(Long projectId, Long issueId) {
		return listIssueLinkByIssueId(projectId, issueId).stream()
				.filter(u -> u.getTypeCode().equals("issue_test") && u.getWard().equals("被阻塞")).collect(Collectors.toList());
	}

	@Override
	public List<IssueLinkDTO> getLinkIssueFromTestToIssue(Long projectId, Long issueId) {
		return listIssueLinkByIssueId(projectId, issueId).stream()
				.filter(u -> u.getWard().equals("阻塞")).collect(Collectors.toList());
	}


}
