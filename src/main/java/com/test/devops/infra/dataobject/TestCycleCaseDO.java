package com.test.devops.infra.dataobject;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@ModifyAudit
@VersionAudit
@Table(name = "test_cycle_case")
public class TestCycleCaseDO extends AuditDomain {
	@Id
	@GeneratedValue
	private Long executeId;

	private Long cycleId;

	private Long issueId;

	private String rank;

	private String executionStatus;

	private String assignedTo;

	private String comment;

	@Transient
	private String attachUrl;
	@Transient
	private String attachName;
	@Transient
	private Long attachId;

	public Long getExecuteId() {
		return executeId;
	}

	public void setExecuteId(Long executeId) {
		this.executeId = executeId;
	}

	public Long getCycleId() {
		return cycleId;
	}

	public void setCycleId(Long cycleId) {
		this.cycleId = cycleId;
	}

	public Long getIssueId() {
		return issueId;
	}

	public void setIssueId(Long issueId) {
		this.issueId = issueId;
	}

	public String getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(String executionStatus) {
		this.executionStatus = executionStatus;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getAttachUrl() {
		return attachUrl;
	}

	public void setAttachUrl(String attachUrl) {
		this.attachUrl = attachUrl;
	}

	public String getAttachName() {
		return attachName;
	}

	public void setAttachName(String attachName) {
		this.attachName = attachName;
	}

	public Long getAttachId() {
		return attachId;
	}

	public void setAttachId(Long attachId) {
		this.attachId = attachId;
	}
}
