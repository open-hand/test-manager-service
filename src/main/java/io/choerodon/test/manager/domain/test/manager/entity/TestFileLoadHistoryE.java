package io.choerodon.test.manager.domain.test.manager.entity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TestFileLoadHistoryE {

    public enum Action{
        UPLOAD_ISSUE(1l),DOWNLOAD_ISSUE(2l),DOWNLOAD_CYCLE(3L);
        private Long action;
        public Long getTypeValue(){
            return action;
        }
        Action(Long action) {
            this.action = action;
        }
    }

    public enum Source{
        PROJECT(1L),VERSION(2L),CYCLE(3L);
        private Long source;
        public Long getTypeValue(){
            return source;
        }
        Source(Long source) {
            this.source = source;
        }
    }

    public enum Status{
        SUSPENDING (1L),SUCCESS(2L),FAILURE(2L);
        private Long status;
        public Long getTypeValue(){
            return status;
        }
        Status(Long status) {
            this.status = status;
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

    private String fileStream;

    private Long objectVersionNumber;

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

    public String getFileStream() {
        return fileStream;
    }

    public void setFileStream(String fileStream) {
        this.fileStream = fileStream;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
