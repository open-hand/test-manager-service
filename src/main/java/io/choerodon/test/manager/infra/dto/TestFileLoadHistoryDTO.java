package io.choerodon.test.manager.infra.dto;

import javax.persistence.*;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
@VersionAudit
@ModifyAudit
@Table(name = "test_fileload_history")
public class TestFileLoadHistoryDTO extends AuditDomain {

    @Id
    @GeneratedValue
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

    private Long createdBy;

    @Transient
    private String name;

    @Transient
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Long getCreatedBy() {
        return createdBy;
    }

    @Override
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

    public void setActionType(Long actionType) {
        this.actionType = actionType;
    }

    public Long getSourceType() {
        return sourceType;
    }

    public void setSourceType(Long sourceType) {
        this.sourceType = sourceType;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
