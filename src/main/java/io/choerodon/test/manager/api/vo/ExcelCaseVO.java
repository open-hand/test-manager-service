package io.choerodon.test.manager.api.vo;

import java.util.List;

import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCaseStepDTO;

/**
 * @author: 25499
 * @date: 2019/12/16 19:26
 * @description:
 */
public class ExcelCaseVO {
    private String projectCode;
    private String folderName;
    private Long caseId;
    private String caseNum;
    private String summary;
    private String description;
    private String rank;
    private Long folderId;
    private Long versionNum;
    private Long projectId;
    private Long lastUpdatedBy;
    private Long createdBy;
    private String executor;
    private List<TestCaseStepVO> caseSteps;
    private String priorityName;
    private String customNum;

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
}
