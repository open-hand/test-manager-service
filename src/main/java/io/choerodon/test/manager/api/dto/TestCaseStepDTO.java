package io.choerodon.test.manager.api.dto;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public class TestCaseStepDTO {
    private Long stepId;

    private String rank;

    private Long issueId;

    private String testStep;

    private String testData;

    private String expectedResult;

    private Long objectVersionNumber;

    private String lastRank;

    private String nextRank;

    private List<TestCycleCaseAttachmentRelDTO> attachments;

    public Long getStepId() {
        return stepId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getLastRank() {
        return lastRank;
    }

    public void setLastRank(String lastRank) {
        this.lastRank = lastRank;
    }

    public String getNextRank() {
        return nextRank;
    }

    public void setNextRank(String nextRank) {
        this.nextRank = nextRank;
    }

    public List<TestCycleCaseAttachmentRelDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<TestCycleCaseAttachmentRelDO> attachments) {
        this.attachments = ConvertHelper.convertList(attachments, TestCycleCaseAttachmentRelDTO.class);
    }
}
