package io.choerodon.test.manager.api.vo;

import java.util.List;

import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCaseStepDTO;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author: 25499
 * @date: 2019/12/16 19:26
 * @description:
 */
public class ExcelCaseVO {
    @ApiModelProperty(value = "项目编码")
    private String projectCode;
    @ApiModelProperty(value = "文件夹名称")
    private String folderName;
    @ApiModelProperty(value = "用例id")
    private Long caseId;
    @ApiModelProperty(value = "用例编号")
    private String caseNum;
    @ApiModelProperty(value = "概要")
    private String summary;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "rank")
    private String rank;
    @ApiModelProperty(value = "文件夹id")
    private Long folderId;
    @ApiModelProperty(value = "版本编号")
    private Long versionNum;
    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "更新人")
    private Long lastUpdatedBy;
    @ApiModelProperty(value = "创建人")
    private Long createdBy;
    @ApiModelProperty(value = "执行")
    private String executor;
    @ApiModelProperty(value = "用例步骤")
    private List<TestCaseStepVO> caseSteps;
    @ApiModelProperty(value = "优先级名称")
    private String priorityName;
    @ApiModelProperty(value = "自定义编号")
    private String customNum;
    @ApiModelProperty(value = "关联工作项")
    private String releatedIssues;
    @ApiModelProperty(value = "关联工作项id集合")
    private List<Long> releatedIssueIds;

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getCaseNum() {
        return caseNum;
    }

    public void setCaseNum(String caseNum) {
        this.caseNum = caseNum;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(Long versionNum) {
        this.versionNum = versionNum;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public List<TestCaseStepVO> getCaseSteps() {
        return caseSteps;
    }

    public void setCaseSteps(List<TestCaseStepVO> caseSteps) {
        this.caseSteps = caseSteps;
    }

    public String getCustomNum() {
        return customNum;
    }

    public void setCustomNum(String customNum) {
        this.customNum = customNum;
    }

    public String getReleatedIssues() {
        return releatedIssues;
    }

    public void setReleatedIssues(String releatedIssues) {
        this.releatedIssues = releatedIssues;
    }

    public List<Long> getReleatedIssueIds() {
        return releatedIssueIds;
    }

    public void setReleatedIssueIds(List<Long> releatedIssueIds) {
        this.releatedIssueIds = releatedIssueIds;
    }
}
