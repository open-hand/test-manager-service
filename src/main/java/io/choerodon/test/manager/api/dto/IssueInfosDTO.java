package io.choerodon.test.manager.api.dto;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.agile.api.dto.*;

/**
 * Created by 842767365@qq.com on 7/17/18.
 */
public class IssueInfosDTO {

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
    private StatusMapDTO statusMapDTO;

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

    @ApiModelProperty(value = "指派人名字")
    private String assigneeName;

    @ApiModelProperty(value = "指派人头像url")
    private String assigneeImageUrl;

    @ApiModelProperty(value = "issue类型DTO")
    private IssueTypeDTO issueTypeDTO;

    @ApiModelProperty(value = "优先级DTO")
    private PriorityDTO priorityDTO;

    @ApiModelProperty(value = "状态名")
    private String statusName;

    @ApiModelProperty(value = "issue编号")
    private String issueNum;

    @ApiModelProperty(value = "报告人id")
    private Long reporterId;

    @ApiModelProperty(value = "报告人名字")
    private String reporterName;

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

    @ApiModelProperty(value = "版本关联DTOList")
    private List<VersionIssueRelDTO> versionIssueRelDTOList;

    @ApiModelProperty(value = "标签关联DTOList")
    private List<LabelIssueRelDTO> labelIssueRelDTOList;

    @ApiModelProperty(value = "模块关联DTOList")
    private List<ComponentIssueRelDTO> componentIssueRelDTOList;

    public IssueInfosDTO() {
    }

    public IssueInfosDTO(IssueComponentDetailDTO issueComponentDetailDTO) {
        this.statusMapDTO = issueComponentDetailDTO.getStatusMapDTO();
        this.issueId = issueComponentDetailDTO.getIssueId();
        this.summary = issueComponentDetailDTO.getSummary();
        this.description = issueComponentDetailDTO.getDescription();
        this.projectId = issueComponentDetailDTO.getProjectId();
        this.statusCode = issueComponentDetailDTO.getStatusMapDTO().getCode();
        this.typeCode = issueComponentDetailDTO.getTypeCode();
        this.statusId = issueComponentDetailDTO.getStatusId();
        this.assigneeId = issueComponentDetailDTO.getAssigneeId();
        this.assigneeName = issueComponentDetailDTO.getAssigneeName();
        this.assigneeImageUrl = issueComponentDetailDTO.getAssigneeImageUrl();
        priorityDTO = issueComponentDetailDTO.getPriorityDTO();
        this.statusName = issueComponentDetailDTO.getStatusMapDTO().getName();
        this.issueNum = issueComponentDetailDTO.getIssueNum();
        this.reporterId = issueComponentDetailDTO.getReporterId();
        this.reporterName = issueComponentDetailDTO.getReporterName();
        this.reporterImageUrl = issueComponentDetailDTO.getReporterImageUrl();
        this.lastUpdateDate = issueComponentDetailDTO.getLastUpdateDate();
        this.creationDate = issueComponentDetailDTO.getCreationDate();
        this.epicName = issueComponentDetailDTO.getEpicName();
        this.epicColor = issueComponentDetailDTO.getEpicColor();
        this.versionIssueRelDTOList = issueComponentDetailDTO.getVersionIssueRelDTOList();
        this.labelIssueRelDTOList = issueComponentDetailDTO.getLabelIssueRelDTOList();
        this.componentIssueRelDTOList = issueComponentDetailDTO.getComponentIssueRelDTOList();
        issueTypeDTO = issueComponentDetailDTO.getIssueTypeDTO();
    }

    public IssueInfosDTO(IssueListDTO issueListDTO) {
        issueName = issueListDTO.getIssueNum();
        issueId = issueListDTO.getIssueId();
        summary = issueListDTO.getSummary();
        projectId = issueListDTO.getProjectId();
        statusMapDTO = issueListDTO.getStatusMapDTO();
        typeCode = issueListDTO.getTypeCode();
        issueTypeDTO = issueListDTO.getIssueTypeDTO();
    }

    public IssueInfosDTO(IssueDTO issueDTO) {
        issueName = issueDTO.getIssueNum();
        issueId = issueDTO.getIssueId();
        summary = issueDTO.getSummary();
        projectId = issueDTO.getProjectId();
        typeCode = issueDTO.getTypeCode();
        issueTypeDTO = issueDTO.getIssueTypeDTO();
        statusMapDTO = issueDTO.getStatusMapDTO();
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

    public List<LabelIssueRelDTO> getLabelIssueRelDTOList() {
        return labelIssueRelDTOList;
    }

    public void setLabelIssueRelDTOList(List<LabelIssueRelDTO> labelIssueRelDTOList) {
        this.labelIssueRelDTOList = labelIssueRelDTOList;
    }

    public List<ComponentIssueRelDTO> getComponentIssueRelDTOList() {
        return componentIssueRelDTOList;
    }

    public void setComponentIssueRelDTOList(List<ComponentIssueRelDTO> componentIssueRelDTOList) {
        this.componentIssueRelDTOList = componentIssueRelDTOList;
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


    public StatusMapDTO getStatusMapDTO() {
        return statusMapDTO;
    }

    public void setStatusMapDTO(StatusMapDTO statusMapDTO) {
        this.statusMapDTO = statusMapDTO;
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

    public List<VersionIssueRelDTO> getVersionIssueRelDTOList() {
        return versionIssueRelDTOList;
    }

    public void setVersionIssueRelDTOList(List<VersionIssueRelDTO> versionIssueRelDTOList) {
        this.versionIssueRelDTOList = versionIssueRelDTOList;
    }

    public PriorityDTO getPriorityDTO() {
        return priorityDTO;
    }

    public void setPriorityDTO(PriorityDTO priorityDTO) {
        this.priorityDTO = priorityDTO;
    }

    public IssueTypeDTO getIssueTypeDTO() {
        return issueTypeDTO;
    }

    public void setIssueTypeDTO(IssueTypeDTO issueTypeDTO) {
        this.issueTypeDTO = issueTypeDTO;
    }
}
