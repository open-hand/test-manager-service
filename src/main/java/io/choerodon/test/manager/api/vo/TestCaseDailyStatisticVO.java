package io.choerodon.test.manager.api.vo;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * @author superlee
 * @since 2022-05-30
 */
public class TestCaseDailyStatisticVO {

    private Set<Long> projectIds;

    @NotNull(message = "error.startDate.null")
    private Date startDate;
    @NotNull(message = "error.endDate.null")
    private Date endDate;

    private Date dailyStartDate;

    private Date dailyEndDate;

    public Date getDailyStartDate() {
        return dailyStartDate;
    }

    public void setDailyStartDate(Date dailyStartDate) {
        this.dailyStartDate = dailyStartDate;
    }

    public Date getDailyEndDate() {
        return dailyEndDate;
    }

    public void setDailyEndDate(Date dailyEndDate) {
        this.dailyEndDate = dailyEndDate;
    }

    public Set<Long> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(Set<Long> projectIds) {
        this.projectIds = projectIds;
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
