package io.choerodon.test.manager.api.vo;

/**
 * @author superlee
 * @since 2022-06-01
 */
public class DailyStatisticVO {

    private Long projectId;

    private Integer testCaseCount;

    private Integer testStepCount;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getTestCaseCount() {
        return testCaseCount;
    }

    public void setTestCaseCount(Integer testCaseCount) {
        this.testCaseCount = testCaseCount;
    }

    public Integer getTestStepCount() {
        return testStepCount;
    }

    public void setTestStepCount(Integer testStepCount) {
        this.testStepCount = testStepCount;
    }
}
