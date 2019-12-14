package io.choerodon.test.manager.api.vo;

import java.util.Date;
import java.util.List;

import io.choerodon.test.manager.api.vo.agile.*;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by 842767365@qq.com on 7/17/18.
 */
public class IssueInfosVO {

    @ApiModelProperty(value = "issue名称")
    private String issueName;

    @ApiModelProperty(value = "issue状态名")
    private String issueStatusName;

    @ApiModelProperty(value = "issueID")
    private Long issueId;

    @ApiModelProperty(value = "issue颜色")
    private String issueColor;

    @ApiModelProperty(value = "概要")
    private String summary;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "状态码")
    private String statusCode;

    @ApiModelProperty(value = "状态MAP")
    private StatusVO statusVO;

    @ApiModelProperty(value = "类型码")
    private String typeCode;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "状态ID")
    private Long statusId;

    @ApiModelProperty(value = "优先级码")
    private String priorityCode;

    @ApiModelProperty(value = "指派人id")
    private Long assigneeId;

    @ApiModelProperty(value = "指派人工号+名字")
    private String assigneeName;

    @ApiModelProperty(value = "指派人工号")
    private String assigneeLoginName;

    @ApiModelProperty(value = "指派人名字")
    private String assigneeRealName;

    @ApiModelProperty(value = "指派人头像url")
    private String assigneeImageUrl;

    @ApiModelProperty(value = "issue类型DTO")
    private IssueTypeVO issueTypeVO;

    @ApiModelProperty(value = "优先级DTO")
    private PriorityVO priorityVO;

    @ApiModelProperty(value = "状态名")
    private String statusName;

    @ApiModelProperty(value = "issue编号")
    private String issueNum;

    @ApiModelProperty(value = "报告人id")
    private Long reporterId;

    @ApiModelProperty(value = "报告人工号+名字")
    private String reporterName;

    @ApiModelProperty(value = "报告人工号")
    private String reporterLoginName;

    @ApiModelProperty(value = "报告人名字")
    private String reporterRealName;

    @ApiModelProperty(value = "报告人头像url")
    private String reporterImageUrl;

    @ApiModelProperty(value = "最后更新日期")
    private Date lastUpdateDate;

    @ApiModelProperty(value = "创建日期")
    private Date creationDate;

    @ApiModelProperty(value = "史诗名称")
    private String epicName;

    @ApiModelProperty(value = "史诗颜色")
    private String epicColor;

    @ApiModelProperty(value = "状态颜色")
    private String statusColor;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "issue关联的冲刺")
    private List<IssueSprintDTO> sprintDTOList;

    @ApiModelProperty(value = "版本关联DTOList")
    private List<VersionIssueRelVO> versionIssueRelVOList;

    @ApiModelProperty(value = "标签关联DTOList")
    private List<LabelIssueRelVO> labelIssueRelVOList;

    @ApiModelProperty(value = "模块关联DTOList")
    private List<ComponentIssueRelVO> componentIssueRelVOList;

    public IssueInfosVO() {
    }

    public IssueInfosVO(IssueComponentDetailVO issueComponentDetailVO) {
        this.issueName = issueComponentDetailVO.getIssueNum();
        this.statusVO = issueComponentDetailVO.getStatusVO();
        this.issueId = issueComponentDetailVO.getIssueId();
        this.summary = issueComponentDetailVO.getSummary();
        this.description = issueComponentDetailVO.getDescription();
        this.projectId = issueComponentDetailVO.getProjectId();
        this.statusCode = issueComponentDetailVO.getStatusVO().getCode();
        this.typeCode = issueComponentDetailVO.getTypeCode();
        this.statusId = issueComponentDetailVO.getStatusId();
        this.assigneeId = issueComponentDetailVO.getAssigneeId();
        this.assigneeName = issueComponentDetailVO.getAssigneeName();
        this.assigneeLoginName = issueComponentDetailVO.getAssigneeLoginName();
        this.assigneeRealName = issueComponentDetailVO.getAssigneeRealName();
        this.assigneeImageUrl = issueComponentDetailVO.getAssigneeImageUrl();
        this.priorityVO = issueComponentDetailVO.getPriorityVO();
        this.statusName = issueComponentDetailVO.getStatusVO().getName();
        this.issueNum = issueComponentDetailVO.getIssueNum();
        this.reporterId = issueComponentDetailVO.getReporterId();
        this.reporterName = issueComponentDetailVO.getReporterName();
        this.reporterLoginName = issueComponentDetailVO.getReporterLoginName();
        this.reporterRealName = issueComponentDetailVO.getReporterRealName();
        this.reporterImageUrl = issueComponentDetailVO.getReporterImageUrl();
        this.lastUpdateDate = issueComponentDetailVO.getLastUpdateDate();
        this.creationDate = issueComponentDetailVO.getCreationDate();
        this.epicName = issueComponentDetailVO.getEpicName();
        this.epicColor = issueComponentDetailVO.getEpicColor();
        this.versionIssueRelVOList = issueComponentDetailVO.getVersionIssueRelVOList();
        this.labelIssueRelVOList = issueComponentDetailVO.getLabelIssueRelVOList();
        this.componentIssueRelVOList = issueComponentDetailVO.getComponentIssueRelVOList();
        this.issueTypeVO = issueComponentDetailVO.getIssueTypeVO();
    }

    public IssueInfosVO(IssueListTestVO issueListTestVO) {
        issueName = issueListTestVO.getIssueNum();
        issueId = issueListTestVO.getIssueId();
        summary = issueListTestVO.getSummary();
        projectId = issueListTestVO.getProjectId();
        statusVO = issueListTestVO.getStatusVO();
        typeCode = issueListTestVO.getTypeCode();
        issueTypeVO = issueListTestVO.getIssueTypeVO();
    }

    public IssueInfosVO(IssueListTestWithSprintVersionDTO listTestWithSprintVersionDTO) {
        issueName = listTestWithSprintVersionDTO.getIssueNum();
        issueId = listTestWithSprintVersionDTO.getIssueId();
        summary = listTestWithSprintVersionDTO.getSummary();
        projectId = listTestWithSprintVersionDTO.getProjectId();
        statusVO = listTestWithSprintVersionDTO.getStatusVO();
        typeCode = listTestWithSprintVersionDTO.getTypeCode();
        issueTypeVO = listTestWithSprintVersionDTO.getIssueTypeVO();
        versionIssueRelVOList = listTestWithSprintVersionDTO.getVersionDTOList();
        sprintDTOList = listTestWithSprintVersionDTO.getSprintDTOList();
    }

    public IssueInfosVO(IssueDTO issueDTO) {
        issueName = issueDTO.getIssueNum();
        issueId = issueDTO.getIssueId();
        summary = issueDTO.getSummary();
        projectId = issueDTO.getProjectId();
        typeCode = issueDTO.getTypeCode();
        issueTypeVO = issueDTO.getIssueTypeVO();
        statusVO = issueDTO.getStatusVO();
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getIssueName() {
        return issueName;
    }

    public void setIssueName(String issueName) {
        this.issueName = issueName;
    }

    public String getIssueStatusName() {
        return issueStatusName;
    }

    public void setIssueStatusName(String issueStatusName) {
        this.issueStatusName = issueStatusName;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getIssueColor() {
        return issueColor;
    }

    public void setIssueColor(String issueColor) {
        this.issueColor = issueColor;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
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

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getPriorityCode() {
        return priorityCode;
    }

    public void setPriorityCode(String priorityCode) {
        this.priorityCode = priorityCode;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getAssigneeImageUrl() {
        return assigneeImageUrl;
    }

    public void setAssigneeImageUrl(String assigneeImageUrl) {
        this.assigneeImageUrl = assigneeImageUrl;
    }


    public StatusVO getStatusVO() {
        return statusVO;
    }

    public void setStatusVO(StatusVO statusVO) {
        this.statusVO = statusVO;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReporterImageUrl() {
        return reporterImageUrl;
    }

    public void setReporterImageUrl(String reporterImageUrl) {
        this.reporterImageUrl = reporterImageUrl;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public String getEpicColor() {
        return epicColor;
    }

    public void setEpicColor(String epicColor) {
        this.epicColor = epicColor;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    public List<IssueSprintDTO> getSprintDTOList() {
        return sprintDTOList;
    }

    public void setSprintDTOList(List<IssueSprintDTO> sprintDTOList) {
        this.sprintDTOList = sprintDTOList;
    }

    public List<VersionIssueRelVO> getVersionIssueRelVOList() {
        return versionIssueRelVOList;
    }

    public void setVersionIssueRelVOList(List<VersionIssueRelVO> versionIssueRelVOList) {
        this.versionIssueRelVOList = versionIssueRelVOList;
    }

    public PriorityVO getPriorityVO() {
        return priorityVO;
    }

    public void setPriorityVO(PriorityVO priorityVO) {
        this.priorityVO = priorityVO;
    }

    public IssueTypeVO getIssueTypeVO() {
        return issueTypeVO;
    }

    public void setIssueTypeVO(IssueTypeVO issueTypeVO) {
        this.issueTypeVO = issueTypeVO;
    }

    public String getAssigneeLoginName() {
        return assigneeLoginName;
    }

    public void setAssigneeLoginName(String assigneeLoginName) {
        this.assigneeLoginName = assigneeLoginName;
    }

    public String getAssigneeRealName() {
        return assigneeRealName;
    }

    public void setAssigneeRealName(String assigneeRealName) {
        this.assigneeRealName = assigneeRealName;
    }

    public String getReporterLoginName() {
        return reporterLoginName;
    }

    public void setReporterLoginName(String reporterLoginName) {
        this.reporterLoginName = reporterLoginName;
    }

    public String getReporterRealName() {
        return reporterRealName;
    }

    public void setReporterRealName(String reporterRealName) {
        this.reporterRealName = reporterRealName;
    }
}
