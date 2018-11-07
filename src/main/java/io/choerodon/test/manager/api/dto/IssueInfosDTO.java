package io.choerodon.test.manager.api.dto;

import io.choerodon.agile.api.dto.*;

import java.util.Date;
import java.util.List;

/**
 * Created by 842767365@qq.com on 7/17/18.
 */
public class IssueInfosDTO {

	private String issueName;

	private String issueStatusName;

	private Long issueId;

	private String issueColor;

	private String summary;

	private Long projectId;

	private String statusCode;

	private StatusMapDTO statusMapDTO;

	private String typeCode;

	private Long objectVersionNumber;

	private Long statusId;

	private String priorityCode;

	private Long assigneeId;

	private String assigneeName;

	private String assigneeImageUrl;

	private IssueTypeDTO issueTypeDTO;
	 private PriorityDTO priorityDTO;

	private String statusName;

	private String issueNum;

	private Long reporterId;

	private String reporterName;

	private String reporterImageUrl;

	private Date lastUpdateDate;

	private Date creationDate;

	private String epicName;

	private String epicColor;

	private String statusColor;

	private List<VersionIssueRelDTO> versionIssueRelDTOList;

	private List<LabelIssueRelDTO> labelIssueRelDTOList;

	private List<ComponentIssueRelDTO> componentIssueRelDTOList;

	public IssueInfosDTO() {
	}

	public IssueInfosDTO(IssueComponentDetailDTO issueComponentDetailDTO) {
		this.statusMapDTO = issueComponentDetailDTO.getStatusMapDTO();
		this.issueId = issueComponentDetailDTO.getIssueId();
		this.summary = issueComponentDetailDTO.getSummary();
		this.projectId = issueComponentDetailDTO.getProjectId();
		this.statusCode = issueComponentDetailDTO.getStatusMapDTO().getCode();
		this.typeCode = issueComponentDetailDTO.getTypeCode();
		this.statusId = issueComponentDetailDTO.getStatusId();
		this.assigneeId = issueComponentDetailDTO.getAssigneeId();
		this.assigneeName = issueComponentDetailDTO.getAssigneeName();
		this.assigneeImageUrl = issueComponentDetailDTO.getAssigneeImageUrl();
		priorityDTO=issueComponentDetailDTO.getPriorityDTO();
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
		issueTypeDTO=issueComponentDetailDTO.getIssueTypeDTO();
	}

	public IssueInfosDTO(IssueListDTO issueListDTO) {
		issueName = issueListDTO.getIssueNum();
//		issueStatusName = issueListDTO.getStatusName();
		issueId = issueListDTO.getIssueId();
		summary = issueListDTO.getSummary();
		projectId = issueListDTO.getProjectId();
		statusMapDTO = issueListDTO.getStatusMapDTO();
		typeCode = issueListDTO.getTypeCode();
		issueTypeDTO=issueListDTO.getIssueTypeDTO();
	}

	public IssueInfosDTO(IssueDTO issueDTO) {
		issueName = issueDTO.getIssueNum();
		issueId = issueDTO.getIssueId();
		summary = issueDTO.getSummary();
		projectId = issueDTO.getProjectId();
		typeCode = issueDTO.getTypeCode();
		issueTypeDTO = issueDTO.getIssueTypeDTO();
		statusMapDTO=issueDTO.getStatusMapDTO();
	}

	public Long getObjectVersionNumber() {
		return objectVersionNumber;
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
