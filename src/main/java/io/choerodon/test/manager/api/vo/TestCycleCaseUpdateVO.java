package io.choerodon.test.manager.api.vo;

import java.util.List;

/**
 * @author: 25499
 * @date: 2019/11/29 9:05
 * @description:
 */
public class TestCycleCaseUpdateVO {
    private Long executeId;

    private Long cycleId;

    private Long caseId;

    private String rank;

    private Long projectId;

    private String summary;

    private String description;

    private List<TestCycleCaseAttachmentRelVO> cycleCaseAttachmentRelVOList;

    private List<TestCycleCaseStepVO> testCycleCaseStepVOList;

    public Long getExecuteId() {
        return executeId;
    }

    public void setExecuteId(Long executeId) {
        this.executeId = executeId;
    }

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public List<TestCycleCaseStepVO> getTestCycleCaseStepVOList() {
        return testCycleCaseStepVOList;
    }

    public void setTestCycleCaseStepVOList(List<TestCycleCaseStepVO> testCycleCaseStepVOList) {
        this.testCycleCaseStepVOList = testCycleCaseStepVOList;
    }
}
