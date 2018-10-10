package io.choerodon.test.manager.infra.dataobject;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.*;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@ModifyAudit
@VersionAudit
@Table(name = "test_cycle_case")
public class TestCycleCaseDO extends AuditDomain {
    @Id
    @GeneratedValue
    private Long executeId;

    private Long cycleId;

    private Long issueId;

    private String rank;

	private Long executionStatus;

	private Long assignedTo;

	@Column(name = "description")
	private String comment;

	@Transient
	private String executionStatusName;

    @Transient
    private String cycleName;

    @Transient
    private String folderName;

	@Transient
	private Long versionId;

	@Transient
    private Long lastExecuteId;

	@Transient
    private Long nextExecuteId;

    @Transient
    List<TestCycleCaseAttachmentRelDO> caseAttachment;

    @Transient
    List<TestCycleCaseDefectRelDO>caseDefect;

    @Transient
    List<TestCycleCaseDefectRelDO>subStepDefects;

	@Transient
	List<TestCycleCaseStepDO> cycleCaseStep;

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

	public String getExecutionStatusName() {
		return executionStatusName;
	}

	public void setExecutionStatusName(String executionStatusName) {
		this.executionStatusName = executionStatusName;
	}

	public Long getExecutionStatus() {
        return executionStatus;
    }

	public void setExecutionStatus(Long executionStatus) {
        this.executionStatus = executionStatus;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public List<TestCycleCaseAttachmentRelDO> getCaseAttachment() {
        return caseAttachment;
    }

    public void setCaseAttachment(List<TestCycleCaseAttachmentRelDO> caseAttachment) {
        this.caseAttachment = caseAttachment;
    }

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<TestCycleCaseDefectRelDO> getCaseDefect() {
        return caseDefect;
    }

    public void setCaseDefect(List<TestCycleCaseDefectRelDO> caseDefect) {
        this.caseDefect = caseDefect;
    }

    public List<TestCycleCaseDefectRelDO> getSubStepDefects() {
        return subStepDefects;
    }

    public void setSubStepDefects(List<TestCycleCaseDefectRelDO> subStepDefects) {
        this.subStepDefects = subStepDefects;
    }

	public List<TestCycleCaseStepDO> getCycleCaseStep() {
		return cycleCaseStep;
	}

	public void setCycleCaseStep(List<TestCycleCaseStepDO> cycleCaseStep) {
		this.cycleCaseStep = cycleCaseStep;
	}

	public Long getVersionId() {
		return versionId;
	}

	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

    public Long getLastExecuteId() {
        return lastExecuteId;
    }

    public void setLastExecuteId(Long lastExecuteId) {
        this.lastExecuteId = lastExecuteId;
    }

    public Long getNextExecuteId() {
        return nextExecuteId;
    }

    public void setNextExecuteId(Long nextExecuteId) {
        this.nextExecuteId = nextExecuteId;
    }
}
