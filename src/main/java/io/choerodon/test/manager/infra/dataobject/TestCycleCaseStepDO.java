package io.choerodon.test.manager.infra.dataobject;

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
@Table(name = "test_cycle_case_step")
public class TestCycleCaseStepDO extends AuditDomain {
	@Id
	@GeneratedValue
	private Long executeStepId;
	private Long executeId;
	private Long stepId;
	private String comment;


	@Transient
	private String testStep;
	@Transient
	private String testData;
	@Transient
	private String expectedResult;

	@Transient
	private String caseAttachUrl;
	@Transient
	private String caseAttachName;
	@Transient
	private Long caseAttachId;

	@Transient
	private String stepAttachUrl;
	@Transient
	private String stepAttachName;
	@Transient
	private Long stepAttachId;

	public Long getExecuteStepId() {
		return executeStepId;
	}

	public void setExecuteStepId(Long executeStepId) {
		this.executeStepId = executeStepId;
	}

	public Long getExecuteId() {
		return executeId;
	}

	public void setExecuteId(Long executeId) {
		this.executeId = executeId;
	}

	public Long getStepId() {
		return stepId;
	}

	public void setStepId(Long stepId) {
		this.stepId = stepId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public String getCaseAttachUrl() {
		return caseAttachUrl;
	}

	public void setCaseAttachUrl(String caseAttachUrl) {
		this.caseAttachUrl = caseAttachUrl;
	}

	public String getCaseAttachName() {
		return caseAttachName;
	}

	public void setCaseAttachName(String caseAttachName) {
		this.caseAttachName = caseAttachName;
	}

	public Long getCaseAttachId() {
		return caseAttachId;
	}

	public void setCaseAttachId(Long caseAttachId) {
		this.caseAttachId = caseAttachId;
	}

	public String getStepAttachUrl() {
		return stepAttachUrl;
	}

	public void setStepAttachUrl(String stepAttachUrl) {
		this.stepAttachUrl = stepAttachUrl;
	}

	public String getStepAttachName() {
		return stepAttachName;
	}

	public void setStepAttachName(String stepAttachName) {
		this.stepAttachName = stepAttachName;
	}

	public Long getStepAttachId() {
		return stepAttachId;
	}

	public void setStepAttachId(Long stepAttachId) {
		this.stepAttachId = stepAttachId;
	}
}
