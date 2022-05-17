package io.choerodon.test.manager.api.vo.devops;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

public class ApplicationVersionRepDTO implements Serializable {

    @ApiModelProperty(value = "应用仓库id")
    private Long id;
    @ApiModelProperty(value = "版本")
    private String version;
    @ApiModelProperty(value = "提交")
    private String commit;
    @ApiModelProperty(value = "应用名称")
    private String appName;
    @ApiModelProperty(value = "应用编码")
    private String appCode;
    @ApiModelProperty(value = "应用id")
    private Long appId;
    @ApiModelProperty(value = "应用状态")
    private Boolean appStatus;
    @ApiModelProperty(value = "创建时间")
    private Date creationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Boolean getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(Boolean appStatus) {
        this.appStatus = appStatus;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
