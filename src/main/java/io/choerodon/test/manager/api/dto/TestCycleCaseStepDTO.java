package io.choerodon.test.manager.api.dto;

import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDefectRelDO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */

public class TestCycleCaseStepDTO {
    private Long executeStepId;
    private Long executeId;
    private Long stepId;
    private String comment;
    private Long objectVersionNumber;

	private Long stepStatus;

    private String testStep;

    private String testData;

    private String expectedResult;

    private List<IssueLinkDTO> issueLinkDTOS;

    private List<TestCycleCaseAttachmentRelDTO> stepAttachment;

    private Long issueId;

    private List<TestCycleCaseDefectRelDTO> defects;

    private IssueInfosDTO issueInfosDTO;

    public IssueInfosDTO getIssueInfosDTO() {
        return issueInfosDTO;
    }

    public void setIssueInfosDTO(IssueInfosDTO issueInfosDTO) {
        this.issueInfosDTO = issueInfosDTO;
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

    public Long getExecuteStepId() {
        return executeStepId;
    }

    public void setExecuteStepId(Long executeStepId) {
        this.executeStepId = executeStepId;
    }

    public Long getExecuteId() {
        return executeId;
    }

    public List<IssueLinkDTO> getIssueLinkDTOS() {
        return issueLinkDTOS;
    }

    public void setIssueLinkDTOS(List<IssueLinkDTO> issueLinkDTOS) {
        this.issueLinkDTOS = issueLinkDTOS;
    }
    public void addIssueLinkDTOS(IssueLinkDTO issueLinkDTO){
        if(this.issueLinkDTOS==null){
            this.issueLinkDTOS=new ArrayList<>();
            }
        this.issueLinkDTOS.add(issueLinkDTO);
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

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }


    public List<TestCycleCaseAttachmentRelDTO> getStepAttachment() {
        return stepAttachment;
    }

    public void setStepAttachment(List<TestCycleCaseAttachmentRelE> stepAttachment) {
        this.stepAttachment = ConvertHelper.convertList(stepAttachment, TestCycleCaseAttachmentRelDTO.class);
    }

    public List<TestCycleCaseDefectRelDTO> getDefects() {
        return defects;
    }

    public void setDefects(List<TestCycleCaseDefectRelE> defects) {
        this.defects = ConvertHelper.convertList(defects, TestCycleCaseDefectRelDTO.class);
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getStepStatus() {
		return stepStatus;
	}

	public void setStepStatus(Long stepStatus) {
		this.stepStatus = stepStatus;
	}
}
