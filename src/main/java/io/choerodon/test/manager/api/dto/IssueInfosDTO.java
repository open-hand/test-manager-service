package io.choerodon.test.manager.api.dto;

import io.choerodon.agile.api.dto.*;

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

	private String typeCode;

	private Long objectVersionNumber;

	private List<LabelIssueRelDTO> labelIssueRelDTOList;

	private List<ComponentIssueRelDTO> componentIssueRelDTOList;

	public IssueInfosDTO() {
	}

	public IssueInfosDTO(IssueCommonDTO issueCommonDTO) {
		issueName = issueCommonDTO.getIssueNum();
		issueStatusName = issueCommonDTO.getStatusName();
		issueId = issueCommonDTO.getIssueId();
		issueColor = issueCommonDTO.getStatusColor();
		summary = issueCommonDTO.getSummary();
		projectId = issueCommonDTO.getProjectId();
		statusCode = issueCommonDTO.getStatusCode();
		typeCode = issueCommonDTO.getTypeCode();
	}

	public IssueInfosDTO(IssueComponentDetailDTO issueCommonDTO) {
		issueName = issueCommonDTO.getIssueNum();
		issueStatusName = issueCommonDTO.getStatusName();
		issueId = issueCommonDTO.getIssueId();
		issueColor = issueCommonDTO.getStatusColor();
		summary = issueCommonDTO.getSummary();
		projectId = issueCommonDTO.getProjectId();
		statusCode = issueCommonDTO.getStatusCode();
		typeCode = issueCommonDTO.getTypeCode();
		labelIssueRelDTOList = issueCommonDTO.getLabelIssueRelDTOList();
		componentIssueRelDTOList = issueCommonDTO.getComponentIssueRelDTOList();
	}

	public IssueInfosDTO(IssueListDTO issueListDTO) {
		issueName = issueListDTO.getIssueNum();
		issueStatusName = issueListDTO.getStatusName();
		issueId = issueListDTO.getIssueId();
		issueColor = issueListDTO.getStatusColor();
		summary = issueListDTO.getSummary();
		projectId = issueListDTO.getProjectId();
		statusCode = issueListDTO.getStatusCode();
		typeCode = issueListDTO.getTypeCode();

	}

	public IssueInfosDTO(IssueDTO issueDTO) {
		issueName = issueDTO.getIssueNum();
		issueStatusName = issueDTO.getStatusName();
		issueId = issueDTO.getIssueId();
		issueColor = issueDTO.getStatusColor();
		summary = issueDTO.getSummary();
		projectId = issueDTO.getProjectId();
		statusCode = issueDTO.getStatusCode();
		typeCode = issueDTO.getTypeCode();
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
}
