package io.choerodon.test.manager.app.service.impl;

/**
 * Created by zongw.lee@gmail.com on 22/11/2018
 */
public class TestAppInstanceDTO {
    private Long id;
    private String code;
    private Long appId;
    private Long appVersionId;
    private Long projectVersionId;
    private Long envId;
    private Long commandId;
    private Long podStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getAppVersionId() {
        return appVersionId;
    }

    public void setAppVersionId(Long appVersionId) {
        this.appVersionId = appVersionId;
    }

    public Long getProjectVersionId() {
        return projectVersionId;
    }

    public void setProjectVersionId(Long projectVersionId) {
        this.projectVersionId = projectVersionId;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public Long getCommandId() {
        return commandId;
    }

    public void setCommandId(Long commandId) {
        this.commandId = commandId;
    }

    public Long getPodStatus() {
        return podStatus;
    }

    public void setPodStatus(Long podStatus) {
        this.podStatus = podStatus;
    }
}
