package io.choerodon.test.manager.domain.test.manager.entity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope("prototype")
public class TestFileLoadHistoryE {

    public enum Action {
        UPLOAD_ISSUE(1L), DOWNLOAD_ISSUE(2L), DOWNLOAD_CYCLE(3L);
        private Long actionFlag;

        public Long getTypeValue() {
            return actionFlag;
        }

        Action(Long action) {
            this.actionFlag = action;
        }
    }

    public enum Source {
        PROJECT(1L), VERSION(2L), CYCLE(3L), FOLDER(4L);
        private Long sourceFlag;

        public Long getTypeValue() {
            return sourceFlag;
        }

        Source(Long source) {
            this.sourceFlag = source;
        }
    }

    public enum Status {
        SUSPENDING(1L), SUCCESS(2L), FAILURE(3L);
        private Long statusFlag;

        public Long getTypeValue() {
            return statusFlag;
        }

        Status(Long status) {
            this.statusFlag = status;
        }
    }

    private Long id;

    private Long projectId;

    private Long actionType;

    private Long sourceType;

    private Long linkedId;

    private String fileUrl;

    private Long status;

    private Long successfulCount;

    private Long failedCount;

    private byte[] fileStream;

    private Long objectVersionNumber;

    private Long createdBy;

    private String name;

    private Double rate;

    private Date creationDate;

    private Date lastUpdateDate;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getActionType() {
        return actionType;
    }

    public void setActionType(Action actionType) {
        this.actionType = actionType.getTypeValue();
    }

    public Long getSourceType() {
        return sourceType;
    }

    public void setSourceType(Source sourceType) {
        this.sourceType = sourceType.getTypeValue();
    }

    public Long getLinkedId() {
        return linkedId;
    }

    public void setLinkedId(Long linkedId) {
        this.linkedId = linkedId;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status.getTypeValue();
    }

    public void setActionType(Long actionType) {
        this.actionType = actionType;
    }

    public void setSourceType(Long sourceType) {
        this.sourceType = sourceType;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getSuccessfulCount() {
        return successfulCount;
    }

    public void setSuccessfulCount(Long successfulCount) {
        this.successfulCount = successfulCount;
    }

    public Long getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Long failedCount) {
        this.failedCount = failedCount;
    }

    public byte[] getFileStream() {
        return fileStream;
    }

    public void setFileStream(byte[] fileStream) {
        this.fileStream = fileStream;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public TestFileLoadHistoryE() {
    }

    public TestFileLoadHistoryE(Long projectId, Action actionType, Source sourceType, Long linkedId, Status status) {
        this.projectId = projectId;
        this.actionType = actionType.getTypeValue();
        this.sourceType = sourceType.getTypeValue();
        this.linkedId = linkedId;
        this.status = status.getTypeValue();
    }
}
