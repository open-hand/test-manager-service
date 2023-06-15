package io.choerodon.test.manager.api.vo.event;

import io.swagger.annotations.ApiModelProperty;

public class InstancePayload {

    @ApiModelProperty(value = "发布名称")
    String releaseNames;
    @ApiModelProperty(value = "状态")
    Long status;
    @ApiModelProperty(value = "日志文件")
    String logFile;
    @ApiModelProperty(value = "节点名称")
    String podName;
    @ApiModelProperty(value = "conName")
    String conName;

    public String getReleaseNames() {
        return releaseNames;
    }

    public void setReleaseNames(String releaseNames) {
        this.releaseNames = releaseNames;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getConName() {
        return conName;
    }

    public void setConName(String conName) {
        this.conName = conName;
    }
}
