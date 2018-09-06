package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 7/13/18.
 */
public class ReporterFormE {

	private IssueInfosDTO defectInfo;

	private Long defectCount = 0L;

	private List<LinkedTestIssue> linkedTestIssues = new ArrayList<>();


	public ReporterFormE(IssueInfosDTO issueInfosDTO) {
		defectInfo = issueInfosDTO;
	}

	public ReporterFormE populateLinkedTest(List<IssueLinkDTO> linkedTestIssues) {
		if (ObjectUtils.isEmpty(linkedTestIssues)) {
			return this;
		}
		for (IssueLinkDTO link : linkedTestIssues) {
			if (defectInfo.getIssueId().equals(link.getLinkedIssueId())) {
				this.linkedTestIssues.add(new LinkedTestIssue(link.getIssueId(), link.getIssueNum(), link.getSummary()));
			}
		}
		return this;
	}

	public ReporterFormE populateLinkedIssueCycle(List<TestCycleCaseDTO> caseDTOS) {
		if(caseDTOS.isEmpty()){
			return this;
		}
		for (LinkedTestIssue issue : linkedTestIssues) {
			for (TestCycleCaseDTO cases : caseDTOS) {
				if (issue.getIssueId().equals(cases.getIssueId())) {
					issue.getTestCycleCaseES().add(cases);
				}
			}
		}
		return this;
	}

	public ReporterFormE countDefect(){
		for(LinkedTestIssue issue: linkedTestIssues){
			List<TestCycleCaseDTO> ls=Optional.ofNullable(issue.getTestCycleCaseES()).orElseGet(ArrayList::new);
			for(TestCycleCaseDTO dot:ls){
				Optional.ofNullable(dot.getDefects()).ifPresent(v->defectCount=defectCount+v.size());
				Optional.ofNullable(dot.getSubStepDefects()).ifPresent(v->defectCount=defectCount+v.size());
			}
		}
		return this;
	}


	private class LinkedTestIssue {
		private Long issueId;
		private String issueName;
		private String summary;

		private List<TestCycleCaseDTO> testCycleCaseES = new ArrayList<>();


		public LinkedTestIssue(Long issueId, String issueName, String summary) {
			this.issueId = issueId;
			this.issueName = issueName;
			this.summary = summary;
		}

		public List<TestCycleCaseDTO> getTestCycleCaseES() {
			return testCycleCaseES;
		}

		public void setTestCycleCaseES(List<TestCycleCaseDTO> testCycleCaseES) {
			this.testCycleCaseES = testCycleCaseES;
		}


		public Long getIssueId() {
			return issueId;
		}

		public void setIssueId(Long issueId) {
			this.issueId = issueId;
		}

		public String getIssueName() {
			return issueName;
		}

		public void setIssueName(String issueName) {
			this.issueName = issueName;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}
	}

	public IssueInfosDTO getDefectInfo() {
		return defectInfo;
	}

	public ReporterFormE setDefectInfo(IssueInfosDTO defectInfo) {
		this.defectInfo = defectInfo;
		return this;
	}

	public Long getDefectCount() {
		return defectCount;
	}

	public void setDefectCount(Long defectCount) {
		this.defectCount = defectCount;
	}

	public List<LinkedTestIssue> getLinkedTestIssues() {
		return linkedTestIssues;
	}

	public void setLinkedTestIssues(List<LinkedTestIssue> linkedTestIssues) {
		this.linkedTestIssues = linkedTestIssues;
	}
}
