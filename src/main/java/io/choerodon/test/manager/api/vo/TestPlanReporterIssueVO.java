package io.choerodon.test.manager.api.vo;

import io.choerodon.test.manager.api.vo.agile.StatusVO;
import io.choerodon.test.manager.infra.dto.UserMessageDTO;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

/**
 * @author superlee
 * @since 2020-12-11
 */
public class TestPlanReporterIssueVO {

    @ApiModelProperty(value = "工作项id")
    @Encrypt
    private Long issueId;

    @ApiModelProperty(value = "概要")
    private String summary;

    @ApiModelProperty(value = "状态")
    private StatusVO statusMapVO;

    @ApiModelProperty(value = "经办人")
    private UserMessageDTO assignee;

    @ApiModelProperty(value = "关联的测试用例")
    private List<TestFolderCycleCaseVO> testFolderCycleCases;

    @ApiModelProperty(value = "状态id")
    @Encrypt
    private Long statusId;

    @ApiModelProperty(value = "用户id")
    @Encrypt
    private Long userId;

    @ApiModelProperty(value = "用例概要")
    private String caseSummary;

    @ApiModelProperty(value = "执行状态")
    @Encrypt
    private Long executionStatus;

    @ApiModelProperty(value = "通过状态id")
    private Long passStatusId;

    @ApiModelProperty(value = "通过用例计数")
    private Integer passedCaseCount;

    public Integer getPassedCaseCount() {
        return passedCaseCount;
    }

    public void setPassedCaseCount(Integer passedCaseCount) {
        this.passedCaseCount = passedCaseCount;
    }

    public Long getPassStatusId() {
        return passStatusId;
    }

    public void setPassStatusId(Long passStatusId) {
        this.passStatusId = passStatusId;
    }

    public String getCaseSummary() {
        return caseSummary;
    }

    public void setCaseSummary(String caseSummary) {
        this.caseSummary = caseSummary;
    }

    public Long getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(Long executionStatus) {
        this.executionStatus = executionStatus;
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

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public StatusVO getStatusMapVO() {
        return statusMapVO;
    }

    public void setStatusMapVO(StatusVO statusMapVO) {
        this.statusMapVO = statusMapVO;
    }

    public UserMessageDTO getAssignee() {
        return assignee;
    }

    public void setAssignee(UserMessageDTO assignee) {
        this.assignee = assignee;
    }

    public List<TestFolderCycleCaseVO> getTestFolderCycleCases() {
        return testFolderCycleCases;
    }

    public void setTestFolderCycleCases(List<TestFolderCycleCaseVO> testFolderCycleCases) {
        this.testFolderCycleCases = testFolderCycleCases;
    }
}
