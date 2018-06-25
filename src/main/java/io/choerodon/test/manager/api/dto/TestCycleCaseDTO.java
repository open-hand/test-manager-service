package io.choerodon.test.manager.api.dto;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO;

import javax.persistence.Transient;
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

	private List<TestCycleCaseAttachmentRelDTO> caseAttachment;

	private List<TestCycleCaseDefectRelDTO> defects;

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

	public List<TestCycleCaseAttachmentRelDTO> getCaseAttachment() {
		return caseAttachment;
	}

	public void setCaseAttachment(List<TestCycleCaseAttachmentRelDO> caseAttachment) {
		this.caseAttachment = ConvertHelper.convertList(caseAttachment, TestCycleCaseAttachmentRelDTO.class);
	}

	public List<TestCycleCaseDefectRelDTO> getDefects() {
		return defects;
	}

	public void setDefects(List<TestCycleCaseDefectRelE> defects) {
		this.defects = ConvertHelper.convertList(defects, TestCycleCaseDefectRelDTO.class);
		;
	}
}
