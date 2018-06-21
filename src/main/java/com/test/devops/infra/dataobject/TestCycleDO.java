package com.test.devops.infra.dataobject;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@ModifyAudit
@VersionAudit
@Table(name = "test_cycle")
public class TestCycleDO extends AuditDomain {
	@Id
	@GeneratedValue
	private Long cycleId;

	private Long parentCycleId;

	private String cycleName;

	private Long versionId;

	private String description;

	private String build;

	private String environment;

	private Date fromDate;

	private Date toDate;

	private String type;

	public Long getCycleId() {
		return cycleId;
	}

	public void setCycleId(Long cycleId) {
		this.cycleId = cycleId;
	}

	public Long getParentCycleId() {
		return parentCycleId;
	}

	public void setParentCycleId(Long parentCycleId) {
		this.parentCycleId = parentCycleId;
	}

	public String getCycleName() {
		return cycleName;
	}

	public void setCycleName(String cycleName) {
		this.cycleName = cycleName;
	}

	public Long getVersionId() {
		return versionId;
	}

	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
