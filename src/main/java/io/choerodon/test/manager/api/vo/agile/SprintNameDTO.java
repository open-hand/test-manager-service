package io.choerodon.test.manager.api.vo.agile;

import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;

/**
 * Created by jian_zhang02@163.com on 2018/5/17.
 */
public class SprintNameDTO {
    @Encrypt
    private Long sprintId;
    private String sprintName;
    private Date startDate;
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
