package io.choerodon.test.manager.api.vo;

import io.choerodon.test.manager.api.vo.agile.UserDO;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author superlee
 * @since 2020-12-10
 */
public class TestPlanReporterInfoVO {

    @ApiModelProperty(value = "管理员")
    private UserDO manager;

    @ApiModelProperty(value = "开始时间")
    private Date startDate;

    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    @ApiModelProperty(value = "用例总数")
    private Integer totalCaseCount;

    @ApiModelProperty(value = "关联用例总数")
    private Integer relatedIssueCount;

    @ApiModelProperty(value = "缺陷总数")
    private Integer totalBugCount;

    @ApiModelProperty(value = "已解决缺陷总数")
    private Integer solvedBugCount;

    @ApiModelProperty(value = "完成工作项总数")
    private Integer passedIssueCount;

    public Integer getPassedIssueCount() {
        return passedIssueCount;
    }

    public void setPassedIssueCount(Integer passedIssueCount) {
        this.passedIssueCount = passedIssueCount;
    }

    public UserDO getManager() {
        return manager;
    }

    public void setManager(UserDO manager) {
        this.manager = manager;
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

    public Integer getTotalCaseCount() {
        return totalCaseCount;
    }

    public void setTotalCaseCount(Integer totalCaseCount) {
        this.totalCaseCount = totalCaseCount;
    }

    public Integer getRelatedIssueCount() {
        return relatedIssueCount;
    }

    public void setRelatedIssueCount(Integer relatedIssueCount) {
        this.relatedIssueCount = relatedIssueCount;
    }

    public Integer getTotalBugCount() {
        return totalBugCount;
    }

    public void setTotalBugCount(Integer totalBugCount) {
        this.totalBugCount = totalBugCount;
    }

    public Integer getSolvedBugCount() {
        return solvedBugCount;
    }

    public void setSolvedBugCount(Integer solvedBugCount) {
        this.solvedBugCount = solvedBugCount;
    }
}
