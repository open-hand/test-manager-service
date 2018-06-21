package com.test.devops.api.dto;

import com.test.devops.domain.entity.TestCycleCaseStepE;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public class TestCycleCaseDTO {
	private Long executeId;

	private Long cycleId;

	private Long issueId;

	private String rank;

	private String executionStatus;

	private String assignedTo;

	private String comment;

	private Long objectVersionNumber;

	private String lastRank;

	private String nextRank;

	private String attachUrl;

	private String attachName;

	private Long attachId;

	private List<TestCycleCaseStepE> testCycleCaseStepES;

	public List<TestCycleCaseStepE> getTestCycleCaseStepES() {
		return testCycleCaseStepES;
	}

	public void setTestCycleCaseStepES(List<TestCycleCaseStepE> testCycleCaseStepES) {
		this.testCycleCaseStepES = testCycleCaseStepES;
	}

	public Long getExecuteId() {
		return executeId;
	}

	public void setExecuteId(Long executeId) {
		this.executeId = executeId;
	}

	public Long getCycleId() {
		return cycleId;
	}

	public void setCycleId(Long cycleId) {
		this.cycleId = cycleId;
	}

	public Long getIssueId() {
		return issueId;
	}

	public void setIssueId(Long issueId) {
		this.issueId = issueId;
	}

	public String getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(String executionStatus) {
		this.executionStatus = executionStatus;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Long getObjectVersionNumber() {
		return objectVersionNumber;
	}

	public void setObjectVersionNumber(Long objectVersionNumber) {
		this.objectVersionNumber = objectVersionNumber;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getLastRank() {
		return lastRank;
	}

	public void setLastRank(String lastRank) {
		this.lastRank = lastRank;
	}

	public String getNextRank() {
		return nextRank;
	}

	public void setNextRank(String nextRank) {
		this.nextRank = nextRank;
	}

	public String getAttachUrl() {
		return attachUrl;
	}

	public void setAttachUrl(String attachUrl) {
		this.attachUrl = attachUrl;
	}

	public String getAttachName() {
		return attachName;
	}

	public void setAttachName(String attachName) {
		this.attachName = attachName;
	}

	public Long getAttachId() {
		return attachId;
	}

	public void setAttachId(Long attachId) {
		this.attachId = attachId;
	}
}
