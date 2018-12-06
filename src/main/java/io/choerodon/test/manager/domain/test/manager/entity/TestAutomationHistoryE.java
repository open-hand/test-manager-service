package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "test_automation_history")
@ModifyAudit
@VersionAudit
public class TestAutomationHistoryE extends AuditDomain {

    public enum Status {
        NONEXECUTION(0L), COMPLETE(1L), PARTIALEXECUTION(2L);
        private Long testStatus;

        public Long getStatus() {
            return testStatus;
        }

        Status(Long status) {
            this.testStatus = status;
        }
    }

    @Id
    @GeneratedValue
    private Long id;

    private String framework;

    private Long testStatus;

    private Long instanceId;

    private Long projectId;
    private Long cycleId;
    private Long resultId;

    private Long lastUpdatedBy;

    private Long objectVersionNumber;

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
    private TestAppInstanceE testAppInstanceE;

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

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
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

    public void setTestStatus(Status status) {
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

    public TestAppInstanceE getTestAppInstanceE() {
        return testAppInstanceE;
    }

    public void setTestAppInstanceE(TestAppInstanceE testAppInstanceE) {
        this.testAppInstanceE = testAppInstanceE;
    }

}
