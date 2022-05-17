package io.choerodon.test.manager.api.vo.event;


import io.swagger.annotations.ApiModelProperty;

/**
 * @author superlee
 * @since 2021-01-08
 */
public class ProjectEventCategory {

    @ApiModelProperty(value = "项目id")
    private Long id;
    @ApiModelProperty(value = "项目名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "项目编码")
    private String code;

    @ApiModelProperty(value = "组织id")
    private Long organizationId;

    @ApiModelProperty(value = "是否展示")
    private Boolean displayFlag;

    @ApiModelProperty(value = "builtInFlag")
    private Boolean builtInFlag;

    @ApiModelProperty(value = "标签编码")
    private String labelCode;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean getDisplayFlag() {
        return displayFlag;
    }

    public void setDisplayFlag(Boolean displayFlag) {
        this.displayFlag = displayFlag;
    }

    public Boolean getBuiltInFlag() {
        return builtInFlag;
    }

    public void setBuiltInFlag(Boolean builtInFlag) {
        this.builtInFlag = builtInFlag;
    }

    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }
}
