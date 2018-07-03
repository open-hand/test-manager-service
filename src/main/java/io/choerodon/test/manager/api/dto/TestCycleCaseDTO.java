package io.choerodon.test.manager.api.dto;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO;

import java.util.Date;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public class TestCycleCaseDTO {
    private Long executeId;

    private Long cycleId;

    private Long issueId;

    private String rank;

    private String executionStatus;

    private Long assignedTo;

    private String comment;

    private Long objectVersionNumber;

    private String lastRank;

    private String nextRank;

	private String reporterRealName;

	private Long reporterJobNumber;

	private String assignedUserRealName;

	private Long assignedUserJobNumber;

    private Long lastUpdateBy;

    private Date lastUpdateDate;

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

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }

	public String getReporterRealName() {
		return reporterRealName;
	}

	public void setReporterRealName(String reporterRealName) {
		this.reporterRealName = reporterRealName;
	}

	public Long getReporterJobNumber() {
		return reporterJobNumber;
    }

	public void setReporterJobNumber(Long reporterJobNumber) {
		this.reporterJobNumber = reporterJobNumber;
    }

	public Long getAssignedUserJobNumber() {
		return assignedUserJobNumber;
	}

	public void setAssignedUserJobNumber(Long assignedUserJobNumber) {
		this.assignedUserJobNumber = assignedUserJobNumber;
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
    }

    public Long getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(Long lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

	public String getAssignedUserRealName() {
		return assignedUserRealName;
	}

	public void setAssignedUserRealName(String assignedUserRealName) {
		this.assignedUserRealName = assignedUserRealName;
	}
}
