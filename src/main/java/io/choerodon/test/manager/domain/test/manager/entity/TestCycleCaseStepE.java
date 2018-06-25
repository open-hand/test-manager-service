package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.test.manager.domain.repository.TestCycleCaseStepRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@Component
@Scope("prototype")
public class TestCycleCaseStepE {
	private Long executeStepId;

	private Long executeId;

	private Long stepId;

	private String comment;

	private String testStep;

	private String testData;

	private String expectedResult;

	private Long objectVersionNumber;


	private String caseAttachUrl;

	private String caseAttachName;

	private Long caseAttachId;


	private String stepAttachUrl;

	private String stepAttachName;

	private Long stepAttachId;

	@Autowired
	private TestCycleCaseStepRepository testCycleCaseStepRepository;

	public Page<TestCycleCaseStepE> querySelf(PageRequest pageRequest) {
		return testCycleCaseStepRepository.query(this, pageRequest);
	}

	public List<TestCycleCaseStepE> querySelf() {
		return testCycleCaseStepRepository.query(this);
	}

	public TestCycleCaseStepE addSelf() {
		return testCycleCaseStepRepository.insert(this);
	}

	public TestCycleCaseStepE updateSelf() {
		return testCycleCaseStepRepository.update(this);
	}

	public void deleteSelf() {
		testCycleCaseStepRepository.delete(this);
	}

	public Long getExecuteStepId() {
		return executeStepId;
	}

	public Long getExecuteId() {
		return executeId;
	}

	public Long getStepId() {
		return stepId;
	}

	public String getComment() {
		return comment;
	}

	public Long getObjectVersionNumber() {
		return objectVersionNumber;
	}

	public void setExecuteStepId(Long executeStepId) {
		this.executeStepId = executeStepId;
	}

	public void setExecuteId(Long executeId) {
		this.executeId = executeId;
	}

	public void setStepId(Long stepId) {
		this.stepId = stepId;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setObjectVersionNumber(Long objectVersionNumber) {
		this.objectVersionNumber = objectVersionNumber;
	}

	public void setTestCycleCaseStepRepository(TestCycleCaseStepRepository testCycleCaseStepRepository) {
		this.testCycleCaseStepRepository = testCycleCaseStepRepository;
	}

//	public String getRank() {
//		return rank;
//	}
//
//	public void setRank(String parentStepId) {
//		this.rank = rank;
//	}

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

	public TestCycleCaseStepRepository getTestCycleCaseStepRepository() {
		return testCycleCaseStepRepository;
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
