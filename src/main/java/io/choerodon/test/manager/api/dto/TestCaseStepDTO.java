package io.choerodon.test.manager.api.dto;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public class TestCaseStepDTO {
	private Long stepId;

	private String rank;

	private Long issueId;

	private String testStep;

	private String testData;

	private String expectedResult;

	private Long objectVersionNumber;

	private String lastRank;

	private String nextRank;

	private String attachUrl;

	private String attachName;

	private Long attachId;

	public Long getStepId() {
		return stepId;
	}

	public void setStepId(Long stepId) {
		this.stepId = stepId;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public Long getIssueId() {
		return issueId;
	}

	public void setIssueId(Long issueId) {
		this.issueId = issueId;
	}

	public String getTestStep() {
		return testStep;
	}

	public void setTestStep(String testStep) {
		this.testStep = testStep;
	}

	public String getTestData() {
		return testData;
	}

	public void setTestData(String testData) {
		this.testData = testData;
	}

	public String getExpectedResult() {
		return expectedResult;
	}

	public void setExpectedResult(String expectedResult) {
		this.expectedResult = expectedResult;
	}

	public Long getObjectVersionNumber() {
		return objectVersionNumber;
	}

	public void setObjectVersionNumber(Long objectVersionNumber) {
		this.objectVersionNumber = objectVersionNumber;
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

	public void setAttachUrl(String attachUrl) {
		this.attachUrl = attachUrl;
	}

	public void setAttachName(String attachName) {
		this.attachName = attachName;
	}

	public void setAttachId(Long attachId) {
		this.attachId = attachId;
	}

	public String getAttachUrl() {
		return attachUrl;
	}

	public String getAttachName() {
		return attachName;
	}

	public Long getAttachId() {
		return attachId;
	}
}
