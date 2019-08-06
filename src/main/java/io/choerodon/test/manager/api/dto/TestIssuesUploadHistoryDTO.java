package io.choerodon.test.manager.api.dto;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

public class TestIssuesUploadHistoryDTO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "操作类型：导入用例(1L), 导出用例(2L), 导出循环、阶段(3L)")
    private Long actionType;

    @ApiModelProperty(value = "数据源类型：PROJECT(1L), VERSION(2L), CYCLE(3L), FOLDER(4L)")
    private Long sourceType;

    @ApiModelProperty(value = "数据源关联ID")
    private Long linkedId;

    @ApiModelProperty(value = "文件minioURL")
    private String fileUrl;

    @ApiModelProperty(value = "状态")
    private Long status;

    @ApiModelProperty(value = "成功个数")
    private Long successfulCount;

    @ApiModelProperty(value = "失败个数")
    private Long failedCount;

    @ApiModelProperty(value = "文件流")
    private String fileStream;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "创建人")
    private Long createdBy;

    @ApiModelProperty(value = "创建日期")
    private Date creationDate;

    @ApiModelProperty(value = "最后更新日期")
    private Date lastUpdateDate;

    @ApiModelProperty(value = "数据源名称")
    private String name;

    @ApiModelProperty(value = "敏捷版本名称")
    private String versionName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}