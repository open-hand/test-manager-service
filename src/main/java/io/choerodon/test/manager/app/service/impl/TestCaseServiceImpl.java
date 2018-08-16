package io.choerodon.test.manager.app.service.impl;


import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.test.manager.infra.feign.ProjectFeignClient;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */

@Component
public class TestCaseServiceImpl implements TestCaseService {


	@Autowired
	TestCaseFeignClient testCaseFeignClient;

	@Autowired
	ProductionVersionClient productionVersionClient;

	@Autowired
	ProjectFeignClient projectFeignClient;

	@Override
	public ResponseEntity<Page<IssueCommonDTO>> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
		Assert.notNull(projectId, "error.query.issue.projectId.not.null");
		return testCaseFeignClient.listIssueWithoutSubToTestComponent(projectId, searchDTO, pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort().toString());
	}

	@Override
	public ResponseEntity<Page<IssueComponentDetailDTO>> listIssueWithoutSubDetail(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
		Assert.notNull(projectId, "error.query.issue.projectId.not.null");
		return testCaseFeignClient.listIssueWithoutSubDetail(pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort().toString(), projectId, searchDTO);
	}

	@Override
	public ResponseEntity<IssueDTO> queryIssue(Long projectId, Long issueId) {
		return testCaseFeignClient.queryIssue(projectId, issueId);
	}

	@Override
	public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
		return listIssueWithoutSub(projectId, searchDTO, pageRequest).getBody().stream().collect(Collectors.toMap(IssueCommonDTO::getIssueId, IssueInfosDTO::new));
	}

	/**
	 * 获取issue信息并且更新分页信息
	 *
	 * @param projectId
	 * @param searchDTO
	 * @param pageRequest
	 * @return
	 */
	public <T> Map<Long, IssueInfosDTO> getIssueInfoMapAndPopulatePageInfo(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Page<T> page) {
		Assert.notNull(page, "error.populatePage.page.not.be.null");
		Page<IssueCommonDTO> returnDto = listIssueWithoutSub(projectId, searchDTO, pageRequest).getBody();

		page.setTotalElements(returnDto.getTotalElements());
		page.setSize(returnDto.getSize());
		page.setNumber(returnDto.getNumber());
		page.setTotalPages((int) (returnDto.getTotalElements() - 1L) / returnDto.getSize() + 1);

		return returnDto.stream().collect(Collectors.toMap(IssueCommonDTO::getIssueId, IssueInfosDTO::new));

	}

	@Override
	public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, boolean needDetail) {
		PageRequest pageRequest = new PageRequest();
		pageRequest.setSize(999999999);
		pageRequest.setPage(0);
		pageRequest.setSort(new Sort(Sort.Direction.ASC, "issueId"));
		if (needDetail) {
			return listIssueWithoutSubDetail(projectId, searchDTO, pageRequest).getBody().stream().collect(Collectors.toMap(IssueComponentDetailDTO::getIssueId, IssueInfosDTO::new));
		} else {
			return listIssueWithoutSub(projectId, searchDTO, pageRequest).getBody().stream().collect(Collectors.toMap(IssueCommonDTO::getIssueId, IssueInfosDTO::new));
		}
	}

	@Override
	public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, Long[] issueIds, boolean needDetail) {
		Assert.notNull(issueIds, "error.getIssueWithIssueIds.issueId.not.null");
		return getIssueInfoMap(projectId, buildIdsSearchDTO(issueIds), needDetail);
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
	public List<IssueLinkDTO> listIssueLinkByIssueId(Long projectId, List<Long> issueId) {
		Assert.notNull(projectId, "error.get.linkId.projectId.not.null");
		Assert.notEmpty(issueId, "error.get.linkId.issueId.not.null");
		return testCaseFeignClient.listIssueLinkByBatch(projectId, issueId).getBody();
	}

	@Override
	public List<IssueLinkDTO> getLinkIssueFromIssueToTest(Long projectId, List<Long> issueId) {
		return listIssueLinkByIssueId(projectId, issueId).stream()
				.filter(u -> u.getTypeCode().equals("issue_test") && u.getWard().equals("被阻塞")).collect(Collectors.toList());
	}

	@Override
	public List<IssueLinkDTO> getLinkIssueFromTestToIssue(Long projectId, List<Long> issueId) {
		if (ObjectUtils.isEmpty(issueId)) {
			return new ArrayList<>();
		}
		return listIssueLinkByIssueId(projectId, issueId).stream()
				.filter(u -> u.getWard().equals("阻塞")).collect(Collectors.toList());
	}

	@Override
	public Map<Long, ProductVersionDTO> getVersionInfo(Long projectId) {
		Assert.notNull(projectId, "error.projectId.not.be.null");
		return productionVersionClient.listByProjectId(projectId).getBody().stream().collect(Collectors.toMap(ProductVersionDTO::getVersionId, Function.identity()));
	}

	@Override
	public ProjectDTO getProjectInfo(Long projectId) {
		Assert.notNull(projectId, "error.projectId.not.be.null");
		return projectFeignClient.query(projectId).getBody();
	}
}
