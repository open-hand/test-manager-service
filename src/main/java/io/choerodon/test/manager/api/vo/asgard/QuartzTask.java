package io.choerodon.test.manager.api.vo.asgard;

import javax.persistence.*;
import java.util.Date;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;

@VersionAudit
@ModifyAudit
@Table(name = "ASGARD_QUARTZ_TASK")
public class QuartzTask extends AuditDomain {

    @ApiModelProperty(value = "主键id")
    @Id
    @GeneratedValue
    private Long id;

    @ApiModelProperty(value = "定时任务名称")
    private String name;

    @ApiModelProperty(value = "定时任务描述")
    private String description;

    @ApiModelProperty(value = "定时任务开始时间")
    private Date startTime;

    @ApiModelProperty(value = "定时任务结束时间")
    private Date endTime;

    @ApiModelProperty(value = "执行参数")
    private String executeParams;

    @ApiModelProperty(value = "执行方法")
    private String executeMethod;

    @ApiModelProperty(value = "触发类型")
    private String triggerType;

    @ApiModelProperty(value = "重复次数")
    private Integer simpleRepeatCount;

    @ApiModelProperty(value = "重复间隔")
    private Long simpleRepeatInterval;

    @ApiModelProperty(value = "重复间隔单位")
    private String simpleRepeatIntervalUnit;

    @ApiModelProperty(value = "cron Expression")
    private String cronExpression;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "层级")
    @Column(name = "FD_LEVEL")
    private String level;

    @ApiModelProperty(value = "来源id")
    private Long sourceId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getExecuteParams() {
        return executeParams;
    }

    public void setExecuteParams(String executeParams) {
        this.executeParams = executeParams;
    }

    public String getExecuteMethod() {
        return executeMethod;
    }

    public void setExecuteMethod(String executeMethod) {
        this.executeMethod = executeMethod;
    }

    public Integer getSimpleRepeatCount() {
        return simpleRepeatCount;
    }

    public void setSimpleRepeatCount(Integer simpleRepeatCount) {
        this.simpleRepeatCount = simpleRepeatCount;
    }

    public Long getSimpleRepeatInterval() {
        return simpleRepeatInterval;
    }

    public void setSimpleRepeatInterval(Long simpleRepeatInterval) {
        this.simpleRepeatInterval = simpleRepeatInterval;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSimpleRepeatIntervalUnit() {
        return simpleRepeatIntervalUnit;
    }

    public void setSimpleRepeatIntervalUnit(String simpleRepeatIntervalUnit) {
        this.simpleRepeatIntervalUnit = simpleRepeatIntervalUnit;
    }

    @Override
    public String toString() {
        return "QuartzTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", executeParams='" + executeParams + '\'' +
                ", executeMethod='" + executeMethod + '\'' +
                ", triggerType='" + triggerType + '\'' +
                ", simpleRepeatCount=" + simpleRepeatCount +
                ", simpleRepeatInterval=" + simpleRepeatInterval +
                ", simpleRepeatIntervalUnit='" + simpleRepeatIntervalUnit + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", status='" + status + '\'' +
                ", level='" + level + '\'' +
                ", sourceId='" + sourceId + '\'' +
                '}';
    }
}
