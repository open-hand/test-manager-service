package io.choerodon.test.manager.api.vo;

/**
 * @author zhaotianxin
 * @since 2019/12/3
 */
public class CaseChangeVO {

    private TestCycleCaseVO  testCycleCase;

    private TestCaseInfoVO testCase;

    public TestCycleCaseVO getTestCycleCase() {
        return testCycleCase;
    }

    public void setTestCycleCase(TestCycleCaseVO testCycleCase) {
        this.testCycleCase = testCycleCase;
    }

    public TestCaseInfoVO getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCaseInfoVO testCase) {
        this.testCase = testCase;
    }
}
