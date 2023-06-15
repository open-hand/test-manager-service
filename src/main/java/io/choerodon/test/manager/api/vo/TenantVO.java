package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author jiaxu.cui@hand-china.com 2020/8/25 下午3:21
 */
public class TenantVO {

    @ApiModelProperty(value = "组织id")
    private Long tenantId;
    @ApiModelProperty(value = "组织编号")
    private String tenantNum;
    @ApiModelProperty(value = "组织名称")
    private String tenantName;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantNum() {
        return tenantNum;
    }

    public void setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
