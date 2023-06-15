package io.choerodon.test.manager.infra.dto;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author zhaotianxin
 * @date 2021-05-07 13:50
 */
@Table(name = "test_list_layout_column_rel")
@ModifyAudit
@VersionAudit
public class ListLayoutColumnRelDTO extends AuditDomain {
    @Id
    @GeneratedValue
    @Encrypt
    private Long id;

    @Encrypt
    private Long layoutId;

    @Encrypt
    private Long fieldId;

    private String columnCode;

    private Integer width;

    private Boolean display;

    private Integer sort;

    private Long projectId;

    private Long organizationId;

    @ApiModelProperty(value = "额外配置：工时包含子任务")
    private Boolean extraConfig;

    public ListLayoutColumnRelDTO() {
    }

    public ListLayoutColumnRelDTO(Long layoutId, Long projectId, Long organizationId) {
        this.layoutId = layoutId;
        this.projectId = projectId;
        this.organizationId = organizationId;
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

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
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
