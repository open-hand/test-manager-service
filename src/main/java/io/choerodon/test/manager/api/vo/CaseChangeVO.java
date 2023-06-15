package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhaotianxin
 * @since 2019/12/3
 */
public class CaseChangeVO {

    @ApiModelProperty(value = "循环用例")
    private TestCycleCaseVO  testCycleCase;

    @ApiModelProperty(value = "测试用例")
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
