package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by zongw.lee@gmail.com on 22/11/2018
 */
public class ApplicationDeployVO {

    @ApiModelProperty(value = "应用ID")
    private Long appId;

    @ApiModelProperty(value = "应用版本ID")
    private Long appVersionId;

    @ApiModelProperty(value = "环境ID")
    private Long environmentId;

    @ApiModelProperty(value = "项目版本ID")
    private Long projectVersionId;

    @ApiModelProperty(value = "历史记录ID")
    private Long historyId;

    @ApiModelProperty(value = "应用code")
    private String code;

    @ApiModelProperty(value = "values.yaml配置")
    private String values;

    @ApiModelProperty(value = "操作类型")
    private String commandType;

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
