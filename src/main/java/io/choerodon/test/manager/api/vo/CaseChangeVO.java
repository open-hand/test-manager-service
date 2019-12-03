package io.choerodon.test.manager.api.vo;

/**
 * @author zhaotianxin
 * @since 2019/12/3
 */
public class CaseChangeVO {

    private TestCycleCaseVO testCycleCaseVO;

    private TestCaseInfoVO testCaseInfoVO;

    public TestCycleCaseVO getTestCycleCaseVO() {
        return testCycleCaseVO;
    }

    public void setTestCycleCaseVO(TestCycleCaseVO testCycleCaseVO) {
        this.testCycleCaseVO = testCycleCaseVO;
    }

    public TestCaseInfoVO getTestCaseInfoVO() {
        return testCaseInfoVO;
    }

    public void setTestCaseInfoVO(TestCaseInfoVO testCaseInfoVO) {
        this.testCaseInfoVO = testCaseInfoVO;
    }
}
