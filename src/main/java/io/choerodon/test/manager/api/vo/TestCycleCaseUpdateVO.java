package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

/**
 * @author: 25499
 * @date: 2019/11/29 9:05
 * @description:
 */
public class TestCycleCaseUpdateVO {
    @Encrypt
    private Long executeId;

    private String summary;

    private String description;

    private Long objectVersionNumber;

    @ApiModelProperty(value = "优先级id")
    @Encrypt
    private Long priorityId;

    private PriorityVO priorityVO;

    private List<TestCycleCaseAttachmentRelVO> cycleCaseAttachmentRelVOList;

    private List<TestCycleCaseStepUpdateVO> testCycleCaseStepUpdateVOS;

    private Boolean caseHasExist;

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public PriorityVO getPriorityVO() {
        return priorityVO;
    }

    public void setPriorityVO(PriorityVO priorityVO) {
        this.priorityVO = priorityVO;
    }

    public Long getExecuteId() {
        return executeId;
    }

    public void setExecuteId(Long executeId) {
        this.executeId = executeId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TestCycleCaseAttachmentRelVO> getCycleCaseAttachmentRelVOList() {
        return cycleCaseAttachmentRelVOList;
    }

    public void setCycleCaseAttachmentRelVOList(List<TestCycleCaseAttachmentRelVO> cycleCaseAttachmentRelVOList) {
        this.cycleCaseAttachmentRelVOList = cycleCaseAttachmentRelVOList;
    }

    public List<TestCycleCaseStepUpdateVO> getTestCycleCaseStepUpdateVOS() {
        return testCycleCaseStepUpdateVOS;
    }

    public void setTestCycleCaseStepUpdateVOS(List<TestCycleCaseStepUpdateVO> testCycleCaseStepUpdateVOS) {
        this.testCycleCaseStepUpdateVOS = testCycleCaseStepUpdateVOS;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Boolean getCaseHasExist() {
        return caseHasExist;
    }

    public void setCaseHasExist(Boolean caseHasExist) {
        this.caseHasExist = caseHasExist;
    }
}
