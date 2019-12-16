package io.choerodon.test.manager.api.vo.agile;


import io.choerodon.test.manager.infra.util.StringUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 敏捷开发Issue
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 20:30:48
 */
public class IssueDTO {

    private Long issueId;

    private String issueNum;

    private String typeCode;

    private Long statusId;

    private String summary;

    private Long reporterId;

    private String reporterName;

    private String description;

    private Long assigneeId;

    private String assigneeName;

    private Long projectId;

    private Long epicId;

    private Long parentIssueId;

    private Integer storyPoints;

    private List<VersionIssueRelVO> versionIssueRelVOList;

    private SprintNameDTO activeSprint;

    private List<SprintNameDTO> closeSprint;

    private List<LabelIssueRelVO> labelIssueRelVOList;

    private List<ComponentIssueRelVO> componentIssueRelVOList;

    private List<IssueAttachmentDTO> issueAttachmentDTOList;

    private List<IssueSubListDTO> subIssueDTOList;

    private Long objectVersionNumber;

    private Date creationDate;

    private Date lastUpdateDate;

    private BigDecimal estimateTime;

    private BigDecimal remainingTime;

    private String epicName;

    private String color;

    private String epicColor;

    private String sprintName;

    private String parentIssueNum;

    private String assigneeImageUrl;

    private String reporterImageUrl;

    private Long priorityId;

    private Long issueTypeId;

    private PriorityVO priorityVO;

    private IssueTypeVO issueTypeVO;

    private StatusVO statusVO;

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public StatusVO getStatusVO() {
        return statusVO;
    }

    public void setStatusVO(StatusVO statusVO) {
        this.statusVO = statusVO;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
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

    public Long getParentIssueId() {
        return parentIssueId;
    }

    public void setParentIssueId(Long parentIssueId) {
        this.parentIssueId = parentIssueId;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
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

    public List<IssueAttachmentDTO> getIssueAttachmentDTOList() {
        return issueAttachmentDTOList;
    }

    public void setIssueAttachmentDTOList(List<IssueAttachmentDTO> issueAttachmentDTOList) {
        this.issueAttachmentDTOList = issueAttachmentDTOList;
    }

    public List<IssueSubListDTO> getSubIssueDTOList() {
        return subIssueDTOList;
    }

    public void setSubIssueDTOList(List<IssueSubListDTO> subIssueDTOList) {
        this.subIssueDTOList = subIssueDTOList;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public BigDecimal getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(BigDecimal estimateTime) {
        this.estimateTime = estimateTime;
    }

    public BigDecimal getRemainingTime() {
        return remainingTime;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEpicColor() {
        return epicColor;
    }

    public void setEpicColor(String epicColor) {
        this.epicColor = epicColor;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public String getParentIssueNum() {
        return parentIssueNum;
    }

    public void setParentIssueNum(String parentIssueNum) {
        this.parentIssueNum = parentIssueNum;
    }

    public SprintNameDTO getActiveSprint() {
        return activeSprint;
    }

    public void setActiveSprint(SprintNameDTO activeSprint) {
        this.activeSprint = activeSprint;
    }

    public List<SprintNameDTO> getCloseSprint() {
        return closeSprint;
    }

    public void setCloseSprint(List<SprintNameDTO> closeSprint) {
        this.closeSprint = closeSprint;
    }

    public String getAssigneeImageUrl() {
        return assigneeImageUrl;
    }

    public void setAssigneeImageUrl(String assigneeImageUrl) {
        this.assigneeImageUrl = assigneeImageUrl;
    }

    public String getReporterImageUrl() {
        return reporterImageUrl;
    }

    public void setReporterImageUrl(String reporterImageUrl) {
        this.reporterImageUrl = reporterImageUrl;
    }

    public void setPriorityVO(PriorityVO priorityVO) {
        this.priorityVO = priorityVO;
    }

    public PriorityVO getPriorityVO() {
        return priorityVO;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setIssueTypeVO(IssueTypeVO issueTypeVO) {
        this.issueTypeVO = issueTypeVO;
    }

    public IssueTypeVO getIssueTypeVO() {
        return issueTypeVO;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}