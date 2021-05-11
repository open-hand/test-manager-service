package io.choerodon.test.manager.infra.dto;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author zhaotianxin
 * @date 2021-05-10 16:39
 */
@Table(name = "fd_execution_case_status_change_setting")
@ModifyAudit
@VersionAudit
public class ExecutionCaseStatusChangeSettingDTO extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;

    @Encrypt
    private Long agileIssueTypeId;

    @Encrypt
    private Long agileStatusId;

    @Encrypt
    private Long testStatusId;

    private Long projectId;

    private Long organizationId;

    public ExecutionCaseStatusChangeSettingDTO() {
    }

    public ExecutionCaseStatusChangeSettingDTO(Long testStatusId, Long projectId, Long organizationId) {
        this.testStatusId = testStatusId;
        this.projectId = projectId;
        this.organizationId = organizationId;
    }

    public ExecutionCaseStatusChangeSettingDTO(Long agileIssueTypeId, Long agileStatusId, Long projectId, Long organizationId) {
        this.agileIssueTypeId = agileIssueTypeId;
        this.agileStatusId = agileStatusId;
        this.projectId = projectId;
        this.organizationId = organizationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgileIssueTypeId() {
        return agileIssueTypeId;
    }

    public void setAgileIssueTypeId(Long agileIssueTypeId) {
        this.agileIssueTypeId = agileIssueTypeId;
    }

    public Long getAgileStatusId() {
        return agileStatusId;
    }

    public void setAgileStatusId(Long agileStatusId) {
        this.agileStatusId = agileStatusId;
    }

    public Long getTestStatusId() {
        return testStatusId;
    }

    public void setTestStatusId(Long testStatusId) {
        this.testStatusId = testStatusId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
