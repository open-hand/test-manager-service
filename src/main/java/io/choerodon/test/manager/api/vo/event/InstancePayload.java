package io.choerodon.test.manager.api.vo.event;

public class InstancePayload {

    String releaseNames;
    Long status;
    String logFile;
    String podName;
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
