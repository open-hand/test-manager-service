package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name ="test_app_instance")
@ModifyAudit
@VersionAudit
public class TestAppInstanceE extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;

    private String code;

    private Long appId;
    private Long appVersionId;
    private Long projectVersionId;
    private Long envId;
    private Long commandId;
    private Long projectId;
    private Long podStatus;
    private Long podName;
    private Long containerName;
    private Long logId;

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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getPodStatus() {
        return podStatus;
    }

    public void setPodStatus(Long podStatus) {
        this.podStatus = podStatus;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getPodName() {
        return podName;
    }

    public void setPodName(Long podName) {
        this.podName = podName;
    }

    public Long getContainerName() {
        return containerName;
    }

    public void setContainerName(Long containerName) {
        this.containerName = containerName;
    }

    public TestAppInstanceE(String code, Long appVersionId, Long projectVersionId, Long envId, Long commandId, Long projectId, Long podStatus) {
        this.code = code;
        this.appVersionId = appVersionId;
        this.projectVersionId = projectVersionId;
        this.envId = envId;
        this.commandId = commandId;
        this.projectId = projectId;
        this.podStatus = podStatus;
    }

    public TestAppInstanceE() {
    }
}
