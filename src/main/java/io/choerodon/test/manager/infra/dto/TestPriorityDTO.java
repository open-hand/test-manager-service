package io.choerodon.test.manager.infra.dto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import io.choerodon.mybatis.domain.AuditDomain;
import java.math.BigDecimal;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 *
 * @author jiaxu.cui@hand-china.com 2020-08-19 17:25:29
 */
@ApiModel("测试用例优先级")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "test_priority")
public class TestPriorityDTO extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_ENABLE_FLAG = "enableFlag";
    public static final String FIELD_DEFAULT_FLAG = "defaultFlag";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_COLOUR = "colour";
    public static final String FIELD_SEQUENCE = "sequence";

    @ApiModelProperty("主键id")
    @Id
    @GeneratedValue
    @Encrypt
    private Long id;
    @ApiModelProperty(value = "优先级名称",required = true)
    @NotBlank
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "颜色",required = true)
    @NotBlank
    private String colour;
    @ApiModelProperty(value = "组织id",required = true)
    @NotNull
    private Long organizationId;
    @ApiModelProperty(value = "是否默认",required = true)
    @NotNull
    @Column(name = "is_default")
    private Boolean defaultFlag;
    @ApiModelProperty(value = "排序",required = true)
    private BigDecimal sequence;
    @ApiModelProperty(value = "是否启用",required = true)
    @Column(name = "is_enable")
    private Boolean enableFlag;
    @ApiModelProperty(value = "搜索参数")
    @Transient
    private String param;
    @Transient
    @ApiModelProperty(value = "移交的优先级id")
    @Encrypt
    private Long changePriorityId;

    public Long getChangePriorityId() {
        return changePriorityId;
    }

    public void setChangePriorityId(Long changePriorityId) {
        this.changePriorityId = changePriorityId;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

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

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(Boolean defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public BigDecimal getSequence() {
        return sequence;
    }

    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
    }

    public Boolean getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(Boolean enableFlag) {
        this.enableFlag = enableFlag;
    }
}
