package io.choerodon.test.manager.api.vo;

import java.util.List;

/**
 * @author: 25499
 * @date: 2019/11/29 9:05
 * @description:
 */
public class TestCycleCaseUpdateVO {
    private Long executeId;

    private String summary;

    private String description;

    private Long objectVersionNumber;


    private List<TestCycleCaseAttachmentRelVO> cycleCaseAttachmentRelVOList;

    private List<TestCycleCaseStepUpdateVO> testCycleCaseStepUpdateVOS;

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
}
