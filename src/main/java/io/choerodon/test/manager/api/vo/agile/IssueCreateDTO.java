package io.choerodon.test.manager.api.vo.agile;

import io.choerodon.test.manager.infra.util.StringUtil;
import io.choerodon.test.manager.infra.dto.TestCaseLinkDTO;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 */
public class IssueCreateDTO {

    private String typeCode;

    private String summary;

    private String priorityCode;

    private String description;
    @Encrypt
    private Long assigneeId;
    @Encrypt
    private Long reporterId;

    private Long projectId;
    @Encrypt(ignoreValue = "0")
    private Long epicId;
    @Encrypt(ignoreValue = "0")
    private Long sprintId;
    @Encrypt
    private Long priorityId;
    @Encrypt
    private Long issueTypeId;
    @Encrypt
    private Long folderId;

    private String relateIssueNums;

    private List<VersionIssueRelVO> versionIssueRelVOList;

    private List<LabelIssueRelVO> labelIssueRelVOList;

    private List<ComponentIssueRelVO> componentIssueRelVOList;

    private List<TestCaseLinkDTO> testCaseLinkDTOList;

    private BigDecimal remainingTime;

    private BigDecimal estimateTime;

    private String epicName;

    private List<IssueLinkCreateVO> issueLinkCreateVOList;

    public List<IssueLinkCreateVO> getIssueLinkCreateVOList() {
        return issueLinkCreateVOList;
    }

    public void setIssueLinkCreateVOList(List<IssueLinkCreateVO> issueLinkCreateVOList) {
        this.issueLinkCreateVOList = issueLinkCreateVOList;
    }

    public List<TestCaseLinkDTO> getTestCaseLinkDTOList() {
        return testCaseLinkDTOList;
    }

    public void setTestCaseLinkDTOList(List<TestCaseLinkDTO> testCaseLinkDTOList) {
        this.testCaseLinkDTOList = testCaseLinkDTOList;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPriorityCode() {
        return priorityCode;
    }

    public void setPriorityCode(String priorityCode) {
        this.priorityCode = priorityCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public List<VersionIssueRelVO> getVersionIssueRelVOList() {
        return versionIssueRelVOList;
    }

    public void setVersionIssueRelVOList(List<VersionIssueRelVO> versionIssueRelVOList) {
        this.versionIssueRelVOList = versionIssueRelVOList;
    }

    public List<LabelIssueRelVO> getLabelIssueRelVOList() {
        return labelIssueRelVOList;
    }

    public void setLabelIssueRelVOList(List<LabelIssueRelVO> labelIssueRelVOList) {
        this.labelIssueRelVOList = labelIssueRelVOList;
    }

    public List<ComponentIssueRelVO> getComponentIssueRelVOList() {
        return componentIssueRelVOList;
    }

    public void setComponentIssueRelVOList(List<ComponentIssueRelVO> componentIssueRelVOList) {
        this.componentIssueRelVOList = componentIssueRelVOList;
    }

    public BigDecimal getRemainingTime() {
        return remainingTime;
    }

    public BigDecimal getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(BigDecimal estimateTime) {
        this.estimateTime = estimateTime;
    }

    public void setRemainingTime(BigDecimal remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getRelateIssueNums() {
        return relateIssueNums;
    }

    public void setRelateIssueNums(String relateIssueNums) {
        this.relateIssueNums = relateIssueNums;
    }
}
