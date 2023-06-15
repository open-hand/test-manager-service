package io.choerodon.test.manager.api.vo;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.test.manager.infra.dto.TestCycleCaseAttachmentRelDTO;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public class TestCaseStepVO {

    @ApiModelProperty(value = "步骤id")
    @Encrypt
    private Long stepId;

    @ApiModelProperty(value = "排序值")
    private String rank;

    @ApiModelProperty(value = "用例issueID")
    @Encrypt
    private Long issueId;

    @ApiModelProperty(value = "测试步骤")
    private String testStep;

    @ApiModelProperty(value = "测试数据")
    private String testData;

    @ApiModelProperty(value = "预期结果")
    private String expectedResult;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "上一位排序值")
    private String lastRank;

    @ApiModelProperty(value = "后一位排序值")
    private String nextRank;

    @ApiModelProperty(value = "issue详情DTO")
    private IssueInfosVO issueInfosVO;

    @ApiModelProperty(value = "附件DTOList")
    private List<TestCycleCaseAttachmentRelDTO> attachments;

    public IssueInfosVO getIssueInfosVO() {
        return issueInfosVO;
    }

    public void setIssueInfosVO(IssueInfosVO issueInfosVO) {
        this.issueInfosVO = issueInfosVO;
    }

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

    public void setAttachments(List<TestCycleCaseAttachmentRelDTO> attachments) {
        this.attachments = attachments;
    }
}
