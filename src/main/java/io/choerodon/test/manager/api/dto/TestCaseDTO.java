package io.choerodon.test.manager.api.dto;


import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public class TestCaseDTO {
	private Long issueId;

	private String summary;

	private String reporterId;

	private String description;

	private String linkedIssueType;

	private String fixVersions;

	private String labels;

	private String issues;

	private String epics;

	private String sprints;

	private Long objectVersionNumber;

	private List<TestCaseStepDTO> testCaseStepDTOS;

	public Long getIssueId() {
		return issueId;
	}

	public void setIssueId(Long issueId) {
		this.issueId = issueId;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getReporterId() {
		return reporterId;
	}

	public void setReporterId(String reporterId) {
		this.reporterId = reporterId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLinkedIssueType() {
		return linkedIssueType;
	}

	public void setLinkedIssueType(String linkedIssueType) {
		this.linkedIssueType = linkedIssueType;
	}

	public String getFixVersions() {
		return fixVersions;
	}

	public void setFixVersions(String fixVersions) {
		this.fixVersions = fixVersions;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public String getIssues() {
		return issues;
	}

	public void setIssues(String issues) {
		this.issues = issues;
	}

	public String getEpics() {
		return epics;
	}

	public void setEpics(String epics) {
		this.epics = epics;
	}

	public String getSprints() {
		return sprints;
	}

	public void setSprints(String sprints) {
		this.sprints = sprints;
	}

	public Long getObjectVersionNumber() {
		return objectVersionNumber;
	}

	public void setObjectVersionNumber(Long objectVersionNumber) {
		this.objectVersionNumber = objectVersionNumber;
	}

	public List<TestCaseStepDTO> getTestCaseStepDTOS() {
		return testCaseStepDTOS;
	}

	public void setTestCaseStepDTOS(List<TestCaseStepDTO> testCaseStepDTOS) {
		this.testCaseStepDTOS = testCaseStepDTOS;
	}
}
