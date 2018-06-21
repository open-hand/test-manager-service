package com.test.devops.api.dto;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Transient;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */

public class TestCycleCaseStepDTO {
	private Long executeStepId;
	private Long executeId;
	private Long stepId;
	private String comment;
	private Long objectVersionNumber;


	private String testStep;

	private String testData;

	private String expectedResult;

	private String caseAttachUrl;

	private String caseAttachName;

	private Long caseAttachId;


	private String stepAttachUrl;

	private String stepAttachName;

	private Long stepAttachId;


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

	public Long getObjectVersionNumber() {
		return objectVersionNumber;
	}

	public void setObjectVersionNumber(Long objectVersionNumber) {
		this.objectVersionNumber = objectVersionNumber;
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
