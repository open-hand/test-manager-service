package io.choerodon.test.manager.infra.dto;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Table(name = "test_cycle_case_step")
public class TestCycleCaseStepDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long executeStepId;
    private Long executeId;
    private Long stepId;

    @Column(name = "description")
    private String comment;
    private Long stepStatus;

    private Long createdBy;

    private Long lastUpdatedBy;

    private Date creationDate;

    private Date lastUpdateDate;


    private String testStep;

    private String testData;

    @Column(name = "expect_result")
    private String expectedResult;

    @Transient
    private String cycleName;
    @Transient
    private Long cycleId;

    @Transient
    private Long issueId;

    @Transient
    private String statusName;

    @Transient
    private List<TestCycleCaseAttachmentRelDTO> stepAttachment;

    @Transient
    private List<TestCycleCaseDefectRelDTO> defects;

    public void setId(Long id) {
        executeStepId = id;
    }

    public Long getExecuteStepId() {
        return executeStepId;
    }

    public void setExecuteStepId(Long executeStepId) {
        this.executeStepId = executeStepId;
    }

    public Long getExecuteId() {
        return executeId;
    }

    public void setExecuteId(Long executeId) {
        this.executeId = executeId;
    }

    public Long getStepId() {
        return stepId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }

    public String getComment() {
        return comment;
    }

    public String getDescription() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public String getTestStep() {
        return testStep;
    }

    public void setTestStep(String testStep) {
        this.testStep = testStep;
    }

    public String getTestData() {
        return testData;
    }

    public void setTestData(String testData) {
        this.testData = testData;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public List<TestCycleCaseAttachmentRelDTO> getStepAttachment() {
        return stepAttachment;
    }

    public void setStepAttachment(List<TestCycleCaseAttachmentRelDTO> stepAttachment) {
        this.stepAttachment = stepAttachment;
    }

    public List<TestCycleCaseDefectRelDTO> getDefects() {
        return defects;
    }

    public void setDefects(List<TestCycleCaseDefectRelDTO> defects) {
        this.defects = defects;
    }

    public Long getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(Long stepStatus) {
        this.stepStatus = stepStatus;
    }

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public Long getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    @Override
    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
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

    public boolean isPassed() {
        return "通过".equals(statusName);
    }

}
