package io.choerodon.test.manager.api.vo.testng;

import io.swagger.annotations.ApiModelProperty;

/*
 * @author shinan.chen
 * @since 2019/1/21
 */
public class TestNgCase {
    @ApiModelProperty(value = "用例名称")
    private String name;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "签名")
    private String signature;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "开始时间")
    private String startedAt;
    @ApiModelProperty(value = "结束时间")
    private String finishedAt;
    @ApiModelProperty(value = "持续时间")
    private Long durationMs;
    @ApiModelProperty(value = "输入数据")
    private String inputData;
    @ApiModelProperty(value = "预期数据")
    private String expectData;
    @ApiModelProperty(value = "错误信息")
    private String exceptionMessage;

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getExpectData() {
        return expectData;
    }

    public void setExpectData(String expectData) {
        this.expectData = expectData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(String finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }
}
