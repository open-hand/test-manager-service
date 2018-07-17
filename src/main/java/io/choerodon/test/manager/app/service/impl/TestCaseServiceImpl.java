package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.api.dto.TestCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCaseStepService;
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
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

	public Map<Long, IssueListDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
		return listIssueWithoutSub(projectId, searchDTO, pageRequest).getBody().stream().collect(Collectors.toMap(IssueListDTO::getIssueId, Function.identity()));
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
