package io.choerodon.test.manager.domain.test.manager.entity;

import java.util.Date;

import javax.persistence.*;

import io.choerodon.mybatis.entity.BaseDTO;

@Table(name = "test_automation_history")
public class TestAutomationHistoryE extends BaseDTO {

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
