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
	public ResponseEntity<Page<IssueListDTO>> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
		Assert.notNull(projectId, "error.query.issue.projectId.not.null");
		return testCaseFeignClient.listIssueWithoutSub(pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort().toString(), projectId, searchDTO);
    }

	@Override
	public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
		return listIssueWithoutSub(projectId, searchDTO, pageRequest).getBody().stream().collect(Collectors.toMap(IssueListDTO::getIssueId, v -> new IssueInfosDTO(v)));
	}

	@Override
	public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO) {
		PageRequest pageRequest = new PageRequest();
		pageRequest.setSize(999999999);
		pageRequest.setPage(0);
		pageRequest.setSort(new Sort(Sort.Direction.ASC, new String[]{"issueId"}));
		return listIssueWithoutSub(projectId, searchDTO, pageRequest).getBody().stream().collect(Collectors.toMap(IssueListDTO::getIssueId, v -> new IssueInfosDTO(v)));
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
				.filter(u -> u.getTypeCode().equals("issue_test") && u.getWard().equals("阻塞")).collect(Collectors.toList());
	}



//
//    @Autowired
//	private TestCaseStepService iTestCaseStepService;
//
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public IssueDTO insert(Long projectId, IssueCreateDTO issueCreateDTO) {
//
//        ResponseEntity<IssueDTO> responseEntity = testCaseFeignClient.createIssue(projectId, issueCreateDTO);
//
//        List<TestCaseStepDTO> testCaseStepDTO = issueCreateDTO.getTestCaseStepDTOS();
//        IssueDTO testCaseDto = responseEntity.getBody();
//        testCaseStepDTO.forEach(v -> v.setIssueId(testCaseDto.getIssueId()));
//		testCaseStepDTO = ConvertHelper.convertList(iTestCaseStepService.batchInsertStep(testCaseStepDTO, projectId), TestCaseStepDTO.class);
//        testCaseDto.setTestCaseStepDTOS(testCaseStepDTO);
//        return testCaseDto;
//    }
//
//	@Transactional(rollbackFor = Exception.class)
//	@Override
//	public void delete(Long projectId, Long issueId) {
//		TestCaseStepDTO dto = new TestCaseStepDTO();
//		dto.setIssueId(issueId);
//		iTestCaseStepService.removeStep(dto);
//		testCaseFeignClient.deleteIssue(projectId, issueId);
//	}
//
//	@Transactional(rollbackFor = Exception.class)
//	@Override
//	public ResponseEntity<IssueDTO> update(Long projectId, JSONObject issueUpdate) {
//		return testCaseFeignClient.updateIssue(projectId, issueUpdate);
//	}
//
//    @Override
//    public ResponseEntity<IssueDTO> query(Long projectId, Long issueId) {
//        ResponseEntity<IssueDTO> responseEntity = testCaseFeignClient.queryIssue(projectId, issueId);
//        IssueDTO issueDTO = responseEntity.getBody();
//        if (issueDTO == null) {
//            return responseEntity;
//        }
//		TestCaseStepDTO testCaseStepE = new TestCaseStepDTO();
//        testCaseStepE.setIssueId(issueDTO.getIssueId());
//        issueDTO.setTestCaseStepDTOS(ConvertHelper.convertList(iTestCaseStepService.query(testCaseStepE), TestCaseStepDTO.class));
//        return responseEntity;
//    }
//
//    @Override
//	public ResponseEntity<Page<IssueListDTO>> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
//		return testCaseFeignClient.listIssueWithoutSub(pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort().toString(), projectId, searchDTO);
//    }
}
