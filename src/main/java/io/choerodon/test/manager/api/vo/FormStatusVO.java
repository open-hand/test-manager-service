package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author: 25499
 * @date: 2019/12/12 12:51
 * @description:
 */
public class FormStatusVO {
    @ApiModelProperty(value = "状态id")
    private Long statusId;
    @ApiModelProperty(value = "状态名称")
    private String statusName;
    @ApiModelProperty(value = "状态颜色")
    private String statusColor;
    @ApiModelProperty(value = "总数")
    private Long counts;

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    public Long getCounts() {
        return counts;
    }

    public void setCounts(Long counts) {
        this.counts = counts;
    }
}
