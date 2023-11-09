package io.choerodon.test.manager.api.vo.agile;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;

/**
 * Created by jian_zhang02@163.com on 2018/5/17.
 */
public class SprintNameDTO {
    @ApiModelProperty(value = "冲刺id")
    @Encrypt
    private Long sprintId;
    @ApiModelProperty(value = "冲刺名称")
    private String sprintName;
    @ApiModelProperty(value = "开始时间")
    private Date startDate;
    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
