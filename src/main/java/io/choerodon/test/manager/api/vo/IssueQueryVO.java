package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author superlee
 * @since 2020-12-11
 */
public class IssueQueryVO {

    @ApiModelProperty(value = "issue ids")
    private List<Long> issueIds;

    @ApiModelProperty(value = "概要")
    private String summary;

    @ApiModelProperty(value = "状态id")
    private Long statusId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    public IssueQueryVO() {}

    public IssueQueryVO(List<Long> issueIds,
                         TestPlanReporterIssueVO testPlanReporterIssueVO) {
        this.issueIds = issueIds;
        this.summary = testPlanReporterIssueVO.getSummary();
        this.statusId = testPlanReporterIssueVO.getStatusId();
        this.userId = testPlanReporterIssueVO.getUserId();
    }

    public List<Long> getIssueIds() {
        return issueIds;
    }

    public void setIssueIds(List<Long> issueIds) {
        this.issueIds = issueIds;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
