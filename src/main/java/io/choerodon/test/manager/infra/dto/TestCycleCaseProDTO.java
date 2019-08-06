package io.choerodon.test.manager.infra.dto;

import java.util.Date;
import java.util.List;

public class TestCycleCaseProDTO {
    private Long executeId;

    private Long cycleId;

    private Long issueId;

    private String rank;

    private Long executionStatus;

    private String executionStatusName;

    private Long assignedTo;

    private String comment;

    private String lastRank;

    private String nextRank;

    List<TestCycleCaseAttachmentRelDTO> caseAttachment;

    List<TestCycleCaseDefectRelDTO> caseDefect;

    List<TestCycleCaseDefectRelDTO> subStepDefects;

    List<TestCaseStepDTO> testCaseSteps;

    private Long objectVersionNumber;

    private Long lastUpdatedBy;

    private Date lastUpdateDate;

    private String cycleName;

    private String folderName;

    private Long versionId;

    private Long lastExecuteId;

    private Long nextExecuteId;

    private Long createdBy;

    private Date creationDate;

    private Long projectId;

    List<TestCycleCaseStepDTO> cycleCaseStep;

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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Long getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(Long executionStatus) {
        this.executionStatus = executionStatus;
    }

    public String getExecutionStatusName() {
        return executionStatusName;
    }

    public void setExecutionStatusName(String executionStatusName) {
        this.executionStatusName = executionStatusName;
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

    public void setCaseAttachment(List<TestCycleCaseAttachmentRelDTO> caseAttachment) {
        this.caseAttachment = caseAttachment;
    }

    public List<TestCycleCaseDefectRelDTO> getCaseDefect() {
        return caseDefect;
    }

    public void setCaseDefect(List<TestCycleCaseDefectRelDTO> caseDefect) {
        this.caseDefect = caseDefect;
    }

    public List<TestCycleCaseDefectRelDTO> getSubStepDefects() {
        return subStepDefects;
    }

    public void setSubStepDefects(List<TestCycleCaseDefectRelDTO> subStepDefects) {
        this.subStepDefects = subStepDefects;
    }

    public List<TestCaseStepDTO> getTestCaseSteps() {
        return testCaseSteps;
    }

    public void setTestCaseSteps(List<TestCaseStepDTO> testCaseSteps) {
        this.testCaseSteps = testCaseSteps;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<TestCycleCaseStepDTO> getCycleCaseStep() {
        return cycleCaseStep;
    }

    public void setCycleCaseStep(List<TestCycleCaseStepDTO> cycleCaseStep) {
        this.cycleCaseStep = cycleCaseStep;
    }

    public boolean isPassed() {
        return "通过".equals(executionStatusName);
    }
}
