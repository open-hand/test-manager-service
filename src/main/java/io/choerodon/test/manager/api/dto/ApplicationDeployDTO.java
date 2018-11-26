package io.choerodon.test.manager.api.dto;

/**
 * Created by zongw.lee@gmail.com on 22/11/2018
 */
public class ApplicationDeployDTO {
    private Long appId;
    private Long appVerisonId;
    private Long environmentId;
    private Long projectVersionId;
    private Long historyId;
    private String code;
    private String values;
    private String commandType;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getAppVerisonId() {
        return appVerisonId;
    }

    public void setAppVerisonId(Long appVerisonId) {
        this.appVerisonId = appVerisonId;
    }

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getProjectVersionId() {
        return projectVersionId;
    }

    public void setProjectVersionId(Long projectVersionId) {
        this.projectVersionId = projectVersionId;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }
}
