package io.choerodon.test.manager.api.vo;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author jiaxu.cui@hand-china.com 2020/8/25 下午5:06
 */
public class PriorityVO {

    @ApiModelProperty(value = "优先级")
    @Encrypt
    private Long id;
    @ApiModelProperty(value = "优先级名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "颜色")
    private String colour;
    @ApiModelProperty(value = "组织id")
    @Encrypt
    private Long organizationId;
    @ApiModelProperty(value = "是否默认")
    private Boolean defaultFlag;
    @ApiModelProperty(value = "排序")
    private BigDecimal sequence;
    @ApiModelProperty(value = "是否启用")
    private Boolean enableFlag;

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
