package io.choerodon.test.manager.api.vo.devops;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author younger
 * @date 2018/3/30
 */
public class ApplicationRepDTO {

    @ApiModelProperty(value = "应用仓库id")
    private Long id;
    @ApiModelProperty(value = "仓库名称")
    private String name;
    @ApiModelProperty(value = "仓库编码")
    private String code;
    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "应用模板id")
    private Long applictionTemplateId;
    @ApiModelProperty(value = "仓库链接")
    private String repoUrl;
    @ApiModelProperty(value = "是否同步")
    private Boolean isSynchro;
    @ApiModelProperty(value = "是否活跃")
    private Boolean isActive;
    @ApiModelProperty(value = "发布level")
    private String publishLevel;
    @ApiModelProperty(value = "贡献者")
    private String contributor;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "sonarUrl")
    private String sonarUrl;
    @ApiModelProperty(value = "是否公平")
    private Boolean isFail;
    @ApiModelProperty(value = "类型")
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getApplictionTemplateId() {
        return applictionTemplateId;
    }

    public void setApplictionTemplateId(Long applictionTemplateId) {
        this.applictionTemplateId = applictionTemplateId;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public Boolean getSynchro() {
        return isSynchro;
    }

    public void setSynchro(Boolean synchro) {
        isSynchro = synchro;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getPublishLevel() {
        return publishLevel;
    }

    public void setPublishLevel(String publishLevel) {
        this.publishLevel = publishLevel;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getSonarUrl() {
        return sonarUrl;
    }

    public void setSonarUrl(String sonarUrl) {
        this.sonarUrl = sonarUrl;
    }

    public Boolean getFail() {
        return isFail;
    }

    public void setFail(Boolean fail) {
        isFail = fail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
