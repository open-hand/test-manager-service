package com.test.devops.infra.dataobject;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@ModifyAudit
@VersionAudit
@Table(name = "test_case_step")
public class TestCaseStepDO extends AuditDomain {

	@Id
	@GeneratedValue
	private Long stepId;

	private String rank;

	private Long issueId;

	private String testStep;

	private String testData;

	private String expectedResult;

	@Transient
	private String attachUrl;
	@Transient
	private String attachName;
	@Transient
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
