package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.agile.api.dto.IssueInfoDTO;
import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
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

	List<TestCycleCaseE> testCycleCaseES = new ArrayList<>();

	List<TestCycleCaseStepE> testCycleCaseStepES = new ArrayList<>();

	TestCycleCaseRepository testCycleCaseRepository;

	TestCycleCaseStepRepository testCycleCaseStepRepository;

	TestCaseService testCaseService;

	public DefectReporterFormE(IssueListDTO issueListDTO) {
		issueInfosDTO = new IssueInfosDTO(issueListDTO);
		testCycleCaseRepository = ApplicationContextHelper.getContext().getBean(TestCycleCaseRepositoryImpl.class);
		testCycleCaseStepRepository = ApplicationContextHelper.getContext().getBean(TestCycleCaseStepRepositoryImpl.class);
		testCaseService = ApplicationContextHelper.getContext().getBean(TestCaseServiceImpl.class);
	}

	public DefectReporterFormE createReporter(TestCaseFeignClient testCaseFeignClient, Long projectId) {
		return populateTestWithDefect().populateIssue(testCaseFeignClient, projectId);
	}

	private DefectReporterFormE populateTestWithDefect() {
		TestCycleCaseDefectRelE testCycleCaseDefectRelE = TestCycleCaseDefectRelEFactory.create();
		testCycleCaseDefectRelE.setIssueId(this.issueInfosDTO.getIssueId());
		List<TestCycleCaseDefectRelE> list = testCycleCaseDefectRelE.querySelf();
		Long[] caseIds = list.stream()
				.filter(u -> u.getDefectType().equals(TestCycleCaseDefectRelE.CYCLE_CASE)).map(v -> v.getDefectLinkId()).toArray(Long[]::new);
		if (caseIds.length > 0) {
			testCycleCaseES = testCycleCaseRepository.queryCycleCaseForReporter(caseIds);

		}
		Long[] stepIds = list.stream()
				.filter(u -> u.getDefectType().equals(TestCycleCaseDefectRelE.CASE_STEP)).map(v -> v.getDefectLinkId()).toArray(Long[]::new);
		if (stepIds.length > 0) {
			testCycleCaseStepES = testCycleCaseStepRepository.queryCycleCaseForReporter(stepIds);
		}
		return this;
	}

	private DefectReporterFormE populateIssue(TestCaseFeignClient testCaseFeignClient, Long projectId) {
		List<Long> id = new ArrayList<>();
		id.addAll(testCycleCaseES.stream().map(v -> v.getIssueId()).collect(Collectors.toList()));
		id.addAll(testCycleCaseStepES.stream().map(v -> v.getIssueId()).collect(Collectors.toList()));
		Map<Long, IssueInfosDTO> map = testCaseService.getIssueInfoMap(projectId, id.stream().toArray(Long[]::new));
		for (TestCycleCaseE caseE : testCycleCaseES) {
			caseE.setIssueInfosDTO(map.get(caseE.getIssueId()));
		}

		for (TestCycleCaseStepE stepE : testCycleCaseStepES) {
			stepE.setIssueInfosDTO(map.get(stepE.getIssueId()));
		}

		return this;
	}



	public List<TestCycleCaseE> getTestCycleCaseES() {
		return testCycleCaseES;
	}

	public void setTestCycleCaseES(List<TestCycleCaseE> testCycleCaseES) {
		this.testCycleCaseES = testCycleCaseES;
	}

	public List<TestCycleCaseStepE> getTestCycleCaseStepES() {
		return testCycleCaseStepES;
	}

	public void setTestCycleCaseStepES(List<TestCycleCaseStepE> testCycleCaseStepES) {
		this.testCycleCaseStepES = testCycleCaseStepES;
	}

	public IssueInfosDTO getIssueInfosDTO() {
		return issueInfosDTO;
	}

	public void setIssueInfosDTO(IssueInfosDTO issueInfosDTO) {
		this.issueInfosDTO = issueInfosDTO;
	}
}
