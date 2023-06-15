package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.validation.constraints.NotNull;

/**
 * @author zhaotianxin
 * @date 2021-05-07 14:13
 */
public class ListLayoutColumnRelVO {
    @Encrypt
    @ApiModelProperty(value = "id")
    private Long id;

    @Encrypt
    @ApiModelProperty(value = "布局id")
    private Long layoutId;
    @Encrypt
    @ApiModelProperty(value = "字段id")
    private Long fieldId;
    @NotNull(message = "error.layout.column.code.null")
    @ApiModelProperty(value = "列编码")
    private String columnCode;
    @NotNull(message = "error.layout.column.width.null")
    @ApiModelProperty(value = "宽度")
    private Integer width;
    @NotNull(message = "error.layout.column.sort.null")
    @ApiModelProperty(value = "排序")
    private Integer sort;
    @ApiModelProperty(value = "是否展示")
    @NotNull(message = "error.layout.column.display.null")
    private Boolean display;
    @ApiModelProperty(value = "字段项目名称")
    private String fieldProjectName;
    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "额外配置：工时包含子任务")
    private Boolean extraConfig;

    public String getFieldProjectName() {
        return fieldProjectName;
    }

    public void setFieldProjectName(String fieldProjectName) {
        this.fieldProjectName = fieldProjectName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(Long layoutId) {
        this.layoutId = layoutId;
    }

    public String getColumnCode() {
        return columnCode;
    }

    public void setColumnCode(String columnCode) {
        this.columnCode = columnCode;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public Boolean getExtraConfig() {
        return extraConfig;
    }

    public void setExtraConfig(Boolean extraConfig) {
        this.extraConfig = extraConfig;
    }
}
