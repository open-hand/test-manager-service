package io.choerodon.test.manager.infra.dto;

import java.util.Date;

import javax.persistence.*;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.test.manager.infra.enums.TestAutomationHistoryEnums;
@VersionAudit
@ModifyAudit
@Table(name = "test_automation_history")
public class TestAutomationHistoryDTO extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;

    private String framework;

    private Long testStatus;

    private Long instanceId;

    private Long projectId;
    private String cycleIds;
    private Long resultId;

    private Long lastUpdatedBy;

    private Long objectVersionNumber;

    private Date lastUpdateDate;

    private Date creationDate;

    @Override
    public String toString() {
        return "TestAutomationHistoryDTO{" +
                "id=" + id +
                ", framework='" + framework + '\'' +
                ", testStatus=" + testStatus +
                ", instanceId=" + instanceId +
                ", projectId=" + projectId +
                ", cycleIds='" + cycleIds + '\'' +
                ", resultId=" + resultId +
                ", lastUpdatedBy=" + lastUpdatedBy +
                ", objectVersionNumber=" + objectVersionNumber +
                ", lastUpdateDate=" + lastUpdateDate +
                ", creationDate=" + creationDate +
                ", testAppInstanceDTO=" + testAppInstanceDTO +
                '}';
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    @Override
    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @Override
    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    @Override
    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Transient
    private TestAppInstanceDTO testAppInstanceDTO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getCycleIds() {
        return cycleIds;
    }

    public void setCycleIds(String cycleIds) {
        this.cycleIds = cycleIds;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(TestAutomationHistoryEnums.Status status) {
        this.testStatus = status.getStatus();
    }

    public void setTestStatus(Long testStatus) {
        this.testStatus = testStatus;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public TestAppInstanceDTO getTestAppInstanceDTO() {
        return testAppInstanceDTO;
    }

    public void setTestAppInstanceDTO(TestAppInstanceDTO testAppInstanceDTO) {
        this.testAppInstanceDTO = testAppInstanceDTO;
    }

}
