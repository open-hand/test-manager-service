package io.choerodon.test.manager.api.vo;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.test.manager.api.vo.agile.IssueInfoDTO;
import io.choerodon.test.manager.infra.dto.UserMessageDTO;

/**
 * @author: 25499
 * @date: 2019/11/28 9:35
 * @description:
 */
public class TestCycleCaseInfoVO {
    @ApiModelProperty(value = "测试执行ID")
    private Long executeId;

    @ApiModelProperty(value = "caseNum")
    private String caseNum;

    @ApiModelProperty(value = "cycleID")
    private Long cycleId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "source")
    private String source;

    @ApiModelProperty(value = "用例ID")
    private Long caseId;

    @ApiModelProperty(value = "用例文件夹")
    private Long caseFolderId;

    @ApiModelProperty(value = "用例case名称")
    private String summary;

    @ApiModelProperty(value = "排序值")
    private String rank;

    @ApiModelProperty(value = "计划状态")
    private String planStatus;

    @ApiModelProperty(value = "执行状态")
    private Long executionStatus;

    @ApiModelProperty(value = "执行状态名")
    private String executionStatusName;

    @ApiModelProperty(value = "执行人id")
    private Long assignedTo;

    @ApiModelProperty(value = "执行人")
    private UserMessageDTO executor ;


    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;


    @ApiModelProperty(value = "执行日期")
    private Date executorDate;

    @ApiModelProperty(value = "上一个执行")
    private Long previousExecuteId;

    @ApiModelProperty(value = "下一个执行")
    private Long nextExecuteId;

    @ApiModelProperty(value = "测试用例关联的附件信息")
    private List<TestCycleCaseAttachmentRelVO> attachment;

    public Long getExecuteId() {
        return executeId;
    }

    public void setExecuteId(Long executeId) {
        this.executeId = executeId;
    }

    public String getCaseNum() {
        return caseNum;
    }

    public Long getCaseFolderId() {
        return caseFolderId;
    }

    public void setCaseFolderId(Long caseFolderId) {
        this.caseFolderId = caseFolderId;
    }

    public void setCaseNum(String caseNum) {
        this.caseNum = caseNum;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getPreviousExecuteId() {
        return previousExecuteId;
    }

    public void setPreviousExecuteId(Long previousExecuteId) {
        this.previousExecuteId = previousExecuteId;
    }

    public Long getNextExecuteId() {
        return nextExecuteId;
    }

    public void setNextExecuteId(Long nextExecuteId) {
        this.nextExecuteId = nextExecuteId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public UserMessageDTO getExecutor() {
        return executor;
    }

    public void setExecutor(UserMessageDTO executor) {
        this.executor = executor;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Date getExecutorDate() {
        return executorDate;
    }

    public void setExecutorDate(Date executorDate) {
        this.executorDate = executorDate;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public List<TestCycleCaseAttachmentRelVO> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<TestCycleCaseAttachmentRelVO> attachment) {
        this.attachment = attachment;
    }

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getPlanStatus() {
        return planStatus;
    }
    public void setPlanStatus(String planStatus) {
        this.planStatus = planStatus;
    }
}
