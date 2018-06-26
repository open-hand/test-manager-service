package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.api.dto.TestCaseStepDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCaseStepEFactory;
import io.choerodon.test.manager.domain.service.ITestCaseStepService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import io.choerodon.agile.api.dto.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */

@Component
public class TestCaseServiceImpl implements TestCaseService {


	@Autowired
	TestCaseFeignClient testCaseFeignClient;

	@Autowired
	private ITestCaseStepService iTestCaseStepService;


	@Transactional(rollbackFor = Exception.class)
	@Override
	public IssueDTO insert(Long projectId, IssueCreateDTO issueCreateDTO) {

		ResponseEntity<IssueDTO> responseEntity = testCaseFeignClient.createIssue(projectId, issueCreateDTO);

		List<TestCaseStepDTO> testCaseStepDTO = issueCreateDTO.getTestCaseStepDTOS();
		IssueDTO testCaseDto = responseEntity.getBody();
		testCaseStepDTO.forEach(v -> v.setIssueId(testCaseDto.getIssueId()));
		testCaseStepDTO = ConvertHelper.convertList(iTestCaseStepService.batchInsertStep(ConvertHelper.convertList(testCaseStepDTO, TestCaseStepE.class)), TestCaseStepDTO.class);
		testCaseDto.setTestCaseStepDTOS(testCaseStepDTO);
		return testCaseDto;
	}

	@Override
	public void delete(Long projectId, Long issueId) {
		TestCaseStepDTO dto = new TestCaseStepDTO();
		dto.setIssueId(issueId);
		iTestCaseStepService.removeStep(ConvertHelper.convert(dto, TestCaseStepE.class));
		testCaseFeignClient.deleteIssue(projectId, issueId);
	}

	@Override
	public ResponseEntity<IssueDTO> update(Long projectId, JSONObject issueUpdate) {
		return testCaseFeignClient.updateIssue(projectId, issueUpdate);
	}

	@Override
	public ResponseEntity<IssueDTO> query(Long projectId, Long issueId) {
		ResponseEntity<IssueDTO> responseEntity = testCaseFeignClient.queryIssue(projectId, issueId);
		IssueDTO issueDTO = responseEntity.getBody();
		if (issueDTO == null) {
			return responseEntity;
		}
		TestCaseStepE testCaseStepE = TestCaseStepEFactory.create();
		testCaseStepE.setIssueId(issueDTO.getIssueId());
		issueDTO.setTestCaseStepDTOS(ConvertHelper.convertList(iTestCaseStepService.query(testCaseStepE), TestCaseStepDTO.class));
		return responseEntity;
	}

	@Override
	public ResponseEntity<Page<IssueCommonDTO>> listIssueWithoutSub(Long projectId, String typeCode, PageRequest pageRequest) {
		return testCaseFeignClient.listByOptions(projectId, typeCode, pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort().toString());
	}
}
