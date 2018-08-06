package io.choerodon.test.manager.domain.test.manager.entity;


import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jialongZuo@hand-china.com on 7/16/18.
 */
public class DefectReporterFormE {

	private IssueInfosDTO issueInfosDTO;

	List<TestCycleCaseDTO> testCycleCaseES = new ArrayList<>();

	List<TestCycleCaseStepDTO> testCycleCaseStepES = new ArrayList<>();


	public DefectReporterFormE(IssueListDTO issueListDTO) {
		issueInfosDTO = new IssueInfosDTO(issueListDTO);
	}

	public DefectReporterFormE(IssueInfosDTO issueInfosDTO) {
		this.issueInfosDTO = issueInfosDTO;
	}


	public void populateCycleCase(List<TestCycleCaseDTO> list) {
		for (TestCycleCaseDTO cases : list) {
			if (cases.getIssueId().equals(issueInfosDTO.getIssueId())) {
				testCycleCaseES.add(cases);
			}
		}
	}

	public void populateCycleCaseStep(List<TestCycleCaseStepDTO> list) {
		for (TestCycleCaseStepDTO cases : list) {
			if (cases.getIssueId().equals(issueInfosDTO.getIssueId())) {
				testCycleCaseStepES.add(cases);
			}
		}
	}

	public void populateIssueInfo(Map<Long, IssueInfosDTO> infos) {
		for (TestCycleCaseDTO caseE : testCycleCaseES) {
			caseE.setIssueInfosDTO(infos.get(caseE.getIssueId()));
		}

		for (TestCycleCaseStepDTO stepE : testCycleCaseStepES) {
			stepE.setIssueInfosDTO(infos.get(stepE.getIssueId()));
		}
	}


	public void populateIssueLink(Long projectId, TestCaseService testCaseService) {
		for (TestCycleCaseDTO caseE : testCycleCaseES) {
			caseE.setIssueLinkDTOS(testCaseService.getLinkIssueFromTestToIssue(projectId, caseE.getIssueId()));
		}

		for (TestCycleCaseStepDTO stepE : testCycleCaseStepES) {
			stepE.setIssueLinkDTOS(testCaseService.getLinkIssueFromTestToIssue(projectId, stepE.getIssueId()));
		}
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

	public Long getIssueId() {
		return issueInfosDTO.getIssueId();
	}
}
