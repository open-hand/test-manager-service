package io.choerodon.test.manager.api.vo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Transient;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.test.manager.infra.dto.UserMessageDTO;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author: 25499
 * @date: 2019/11/29 11:29
 * @description:
 */
public class TestFolderCycleCaseVO {
    @ApiModelProperty(value = "执行ID")
    @Encrypt
    private Long executeId;

    @ApiModelProperty(value = "循环Id")
    @Encrypt
    private Long cycleId;

    @ApiModelProperty(value = "用例ID")
    @Encrypt
    private Long caseId;

    @ApiModelProperty(value = "排序")
    private String rank;
    @ApiModelProperty(value = "执行状态")
    @Encrypt
    private Long executionStatus;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "用例名")
    private String summary;

    @ApiModelProperty(value = "测试来源")
    private String source;

    @ApiModelProperty(value = "指派人id")
    @Encrypt
    private Long assignedTo;

    @ApiModelProperty(value = "经办人")
    private UserMessageDTO assignedUser;

    @ApiModelProperty(value = "状态名")
    private String executionStatusName;

    @ApiModelProperty(value = "状态颜色")
    private String statusColor;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "更新时间")
    private Date lastUpdateDate;

    @ApiModelProperty(value = "是否更新")
    private Boolean hasChange;

    @ApiModelProperty(value = "更新人")
    private UserMessageDTO lastUpdateUser;

    private Long versionNum;

    private Boolean changeCase;

    private Boolean changeStep;

    private Boolean changeAttach;

    private Long priorityId;

    private String priorityName;

    private BigDecimal sequence;

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public BigDecimal getSequence() {
        return sequence;
    }

    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
    }

    public Long getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(Long versionNum) {
        this.versionNum = versionNum;
    }

    public Boolean getHasChange() {
        return hasChange;
    }

    public void setHasChange(Boolean hasChange) {
        this.hasChange = hasChange;
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

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public UserMessageDTO getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(UserMessageDTO assignedUser) {
        this.assignedUser = assignedUser;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getExecutionStatusName() {
        return executionStatusName;
    }

    public void setExecutionStatusName(String executionStatusName) {
        this.executionStatusName = executionStatusName;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {

        this.statusColor = statusColor;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Boolean getChangeCase() {
        return changeCase;
    }

    public void setChangeCase(Boolean changeCase) {
        this.changeCase = changeCase;
    }

    public Boolean getChangeStep() {
        return changeStep;
    }

    public void setChangeStep(Boolean changeStep) {
        this.changeStep = changeStep;
    }

    public Boolean getChangeAttach() {
        return changeAttach;
    }

    public void setChangeAttach(Boolean changeAttach) {
        this.changeAttach = changeAttach;
    }

    public UserMessageDTO getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(UserMessageDTO lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }
}