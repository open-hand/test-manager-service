package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.agile.api.dto.IssueInfoDTO;
import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.impl.TestCaseServiceImpl;
import io.choerodon.test.manager.domain.repository.TestCycleCaseRepository;
import io.choerodon.test.manager.domain.repository.TestCycleCaseStepRepository;
import io.choerodon.test.manager.domain.service.impl.ITestCycleCaseDefectRelServiceImpl;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import io.choerodon.test.manager.infra.repository.impl.TestCycleCaseRepositoryImpl;
import io.choerodon.test.manager.infra.repository.impl.TestCycleCaseStepRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jialongZuo@hand-china.com on 7/16/18.
 */
public class DefectReporterFormE {

	private IssueInfosDTO issueInfosDTO;

	List<TestCycleCaseDTO> testCycleCaseES = new ArrayList<>();

	List<TestCycleCaseStepDTO> testCycleCaseStepES = new ArrayList<>();

	TestCycleCaseRepository testCycleCaseRepository;

	TestCycleCaseStepRepository testCycleCaseStepRepository;

	TestCaseService testCaseService;

	public DefectReporterFormE(IssueListDTO issueListDTO) {
		issueInfosDTO = new IssueInfosDTO(issueListDTO);
		initEnv();
	}

	public DefectReporterFormE(IssueInfosDTO issueInfosDTO) {
		this.issueInfosDTO = issueInfosDTO;
		initEnv();
	}

	private void initEnv() {
		testCycleCaseRepository = ApplicationContextHelper.getContext().getBean(TestCycleCaseRepositoryImpl.class);
		testCycleCaseStepRepository = ApplicationContextHelper.getContext().getBean(TestCycleCaseStepRepositoryImpl.class);
		testCaseService = ApplicationContextHelper.getContext().getBean(TestCaseServiceImpl.class);
	}


	public DefectReporterFormE createReporter(Long projectId) {
		return populateTestWithDefect().populateIssue(projectId);
	}

	private DefectReporterFormE populateTestWithDefect() {
		TestCycleCaseDefectRelE testCycleCaseDefectRelE = TestCycleCaseDefectRelEFactory.create();
		testCycleCaseDefectRelE.setIssueId(this.issueInfosDTO.getIssueId());
		List<TestCycleCaseDefectRelE> list = testCycleCaseDefectRelE.querySelf();
		Long[] caseIds = list.stream()
				.filter(u -> u.getDefectType().equals(TestCycleCaseDefectRelE.CYCLE_CASE)).map(v -> v.getDefectLinkId()).toArray(Long[]::new);
		if (caseIds.length > 0) {
			testCycleCaseES = ConvertHelper.convertList(testCycleCaseRepository.queryCycleCaseForReporter(caseIds), TestCycleCaseDTO.class);

		}
		Long[] stepIds = list.stream()
				.filter(u -> u.getDefectType().equals(TestCycleCaseDefectRelE.CASE_STEP)).map(v -> v.getDefectLinkId()).toArray(Long[]::new);
		if (stepIds.length > 0) {
			testCycleCaseStepES = ConvertHelper.convertList(testCycleCaseStepRepository.queryCycleCaseForReporter(stepIds), TestCycleCaseStepDTO.class);
		}
		return this;
	}

	private DefectReporterFormE populateIssue(Long projectId) {
		List<Long> id = new ArrayList<>();
		id.addAll(testCycleCaseES.stream().map(v -> v.getIssueId()).collect(Collectors.toList()));
		id.addAll(testCycleCaseStepES.stream().map(v -> v.getIssueId()).collect(Collectors.toList()));
		Map<Long, IssueInfosDTO> map = testCaseService.getIssueInfoMap(projectId, id.stream().toArray(Long[]::new));
		for (TestCycleCaseDTO caseE : testCycleCaseES) {
			caseE.setIssueInfosDTO(map.get(caseE.getIssueId()));
			caseE.setIssueLinkDTOS(testCaseService.getLinkIssueFromTestToIssue(projectId, caseE.getIssueId()));
		}

		for (TestCycleCaseStepDTO stepE : testCycleCaseStepES) {
			stepE.setIssueInfosDTO(map.get(stepE.getIssueId()));
			stepE.setIssueLinkDTOS(testCaseService.getLinkIssueFromTestToIssue(projectId, stepE.getIssueId()));

		}

		return this;
	}

	public List<TestCycleCaseDTO> getTestCycleCaseES() {
		return testCycleCaseES;
	}

	public void setTestCycleCaseES(List<TestCycleCaseDTO> testCycleCaseES) {
		this.testCycleCaseES = testCycleCaseES;
	}

	public List<TestCycleCaseStepDTO> getTestCycleCaseStepES() {
		return testCycleCaseStepES;
	}

	public void setTestCycleCaseStepES(List<TestCycleCaseStepDTO> testCycleCaseStepES) {
		this.testCycleCaseStepES = testCycleCaseStepES;
	}

	public IssueInfosDTO getIssueInfosDTO() {
		return issueInfosDTO;
	}

	public void setIssueInfosDTO(IssueInfosDTO issueInfosDTO) {
		this.issueInfosDTO = issueInfosDTO;
	}
}
