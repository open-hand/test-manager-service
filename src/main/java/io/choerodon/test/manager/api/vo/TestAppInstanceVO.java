package io.choerodon.test.manager.api.vo;

import io.choerodon.test.manager.infra.constant.EncryptKeyConstants;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Created by zongw.lee@gmail.com on 22/11/2018
 */
public class TestAppInstanceVO {

    @ApiModelProperty(value = "主键ID")
    @Encrypt(/**EncryptKeyConstants.TEST_APP_INSTANCE**/)
    private Long id;

    @ApiModelProperty(value = "应用code")
    private String code;

    @ApiModelProperty(value = "应用ID")
    private Long appId;

    @ApiModelProperty(value = "应用版本ID")
    private Long appVersionId;

    @ApiModelProperty(value = "项目版本ID")
    private Long projectVersionId;

    @ApiModelProperty(value = "运行环境ID")
    private Long envId;

    @ApiModelProperty(value = "运行配置ID")
    private Long commandId;

    @ApiModelProperty(value = "pod状态")
    private Long podStatus;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "pod名")
    private String podName;

    @ApiModelProperty(value = "容器名")
    private String containerName;

    @ApiModelProperty(value = "日志id")
    private Long logId;

    @ApiModelProperty(value = "应用版本名")
    private String appVersionName;

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }
}
