package io.choerodon.asgard.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

public class ScheduleTaskDTO {

    @ApiModelProperty(value = "执行任务方法id")
    @NotNull(message = "error.scheduleTask.methodNull")
    private Long methodId;

    @ApiModelProperty(value = "输入参数的map形式")
    private Map<String, Object> params;

    @ApiModelProperty(value = "定时任务名")
    @NotEmpty(message = "error.scheduleTask.nameEmpty")
    private String name;

    @ApiModelProperty(value = "定时任务描述")
    private String description;

    @ApiModelProperty(value = "定时任务开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "定时任务结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty(value = "定时任务结束类型。simple-trigger或cron-trigger")
    @NotEmpty(message = "error.scheduleTask.triggerTypeEmpty")
    private String triggerType;

    @ApiModelProperty(value = "simple-trigger的重复次数")
    private Integer simpleRepeatCount;

    @ApiModelProperty(value = "simple-trigger的重复间隔")
    private Long simpleRepeatInterval;

    @ApiModelProperty(value = "simple-trigger的重复间隔单位")
    private String simpleRepeatIntervalUnit;

    @ApiModelProperty(value = "cron-trigger的cron表达式")
    private String cronExpression;

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
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

    public String getSimpleRepeatIntervalUnit() {
        return simpleRepeatIntervalUnit;
    }

    public void setSimpleRepeatIntervalUnit(String simpleRepeatIntervalUnit) {
        this.simpleRepeatIntervalUnit = simpleRepeatIntervalUnit;
    }
}
