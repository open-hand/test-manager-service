package io.choerodon.test.manager.api.vo;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.agile.api.vo.IssueInfoDTO;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.dto.UserMessageDTO;

/**
 * @author: 25499
 * @date: 2019/11/28 9:35
 * @description:
 */
public class TestCycleCaseInfoVO {
    @ApiModelProperty(value = "测试执行ID")
    private Long executeId;

    @ApiModelProperty(value = "循环ID")
    private Long cycleId;

    @ApiModelProperty(value = "用例ID")
    private Long caseId;

    @ApiModelProperty(value = "用例case名称")
    private String summary;

    @ApiModelProperty(value = "排序值")
    private String rank;

    @ApiModelProperty(value = "执行状态")
    private Long executionStatus;

    @ApiModelProperty(value = "执行状态名")
    private String executionStatusName;

    @ApiModelProperty(value = "执行人")
    private UserMessageDTO executor ;


    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;


    @ApiModelProperty(value = "执行日期")
    private Date executorDate;


    @ApiModelProperty(value = "测试用例关联的附件信息")
    private List<TestCycleCaseAttachmentRelVO> attachment;

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

}
