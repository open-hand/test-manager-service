package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.app.service.impl.TestCycleCaseServiceImpl;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.service.impl.ITestCycleCaseDefectRelServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by jialongZuo@hand-china.com on 7/13/18.
 */
public class ReporterFormE {

	private Long issueId;
	private String issueName;
	private String issueStatus;
	private String issueColor;
	private Long defectCount = new Long(0);
	private String summary;

	private List<LinkedTestIssue> linkedTestIssues;


	public ReporterFormE populateIssue(IssueListDTO issueListDTO) {
		this.issueName = issueListDTO.getIssueNum();
		this.issueId = issueListDTO.getIssueId();
		this.issueStatus = issueListDTO.getStatusName();
		this.issueColor = issueListDTO.getStatusColor();
		this.summary = issueListDTO.getSummary();
		return this;
	}

	public ReporterFormE populateLinkedTest(List<IssueLinkDTO> linkedTestIssues, Long projectId) {
		List<LinkedTestIssue> list = new ArrayList<>();
		linkedTestIssues.stream().forEach(v -> {
			list.add(new LinkedTestIssue(v.getIssueId(), v.getIssueNum(), v.getSummary(), projectId));
		});
		this.linkedTestIssues = list;
		return this;
	}


	private class LinkedTestIssue {
		private Long issueId;
		private String issueName;
		private String summary;

		private List<TestCycleCaseDTO> testCycleCaseES;

		private List<TestCycleCaseDefectRelDTO> testCycleCaseDefectRelES = new ArrayList<>();

		public LinkedTestIssue(Long issueId, String issueName, String summary, Long projectId) {
			this.issueId = issueId;
			this.issueName = issueName;
			this.summary = summary;
			testCycleCaseES = ApplicationContextHelper.getContext().getBean(TestCycleCaseServiceImpl.class).queryByIssuse(issueId, projectId);
			ITestCycleCaseDefectRelService defectRelService = ApplicationContextHelper.getContext().getBean(ITestCycleCaseDefectRelServiceImpl.class);

			testCycleCaseES.forEach(v -> {
				Optional.ofNullable(v.getDefects()).ifPresent(u -> {
					testCycleCaseDefectRelES
							.addAll(u);
				});
				Optional.ofNullable(defectRelService.query(v.getExecuteId(), TestCycleCaseDefectRelE.CASE_STEP, projectId))
						.ifPresent(u -> testCycleCaseDefectRelES.addAll(ConvertHelper.convertList(u, TestCycleCaseDefectRelDTO.class)));
				defectCount = Long.valueOf(testCycleCaseDefectRelES.size());
			});
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

	public String getIssueStatus() {
		return issueStatus;
	}

	public void setIssueStatus(String issueStatus) {
		this.issueStatus = issueStatus;
	}

	public String getIssueColor() {
		return issueColor;
	}

	public void setIssueColor(String issueColor) {
		this.issueColor = issueColor;
	}

	public Long getDefectCount() {
		return defectCount;
	}

	public void setDefectCount(Long defectCount) {
		this.defectCount = defectCount;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public List<LinkedTestIssue> getLinkedTestIssues() {
		return linkedTestIssues;
	}

	public void setLinkedTestIssues(List<LinkedTestIssue> linkedTestIssues) {
		this.linkedTestIssues = linkedTestIssues;
	}
}
