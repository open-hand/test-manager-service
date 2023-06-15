package io.choerodon.test.manager.api.vo;

import java.util.List;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.dto.TestCaseStepDTO;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhaotianxin
 * @since 2019/12/4
 */
public class CaseCompareVO {
    @ApiModelProperty(value = "用例id")
    private Long caseId;

    @ApiModelProperty(value = "测试用例")
    private TestCaseVO testCase;

    @ApiModelProperty(value = "用例步骤")
    private List<TestCaseStepDTO> caseStep;

    @ApiModelProperty(value = "用例关联附件")
    private List<TestCaseAttachmentDTO> caseAttach;

    public TestCaseVO getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCaseVO testCase) {
        this.testCase = testCase;
    }

    public List<TestCaseStepDTO> getCaseStep() {
        return caseStep;
    }

    public void setCaseStep(List<TestCaseStepDTO> caseStep) {
        this.caseStep = caseStep;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public List<TestCaseAttachmentDTO> getCaseAttach() {

        return caseAttach;
    }

    public void setCaseAttach(List<TestCaseAttachmentDTO> caseAttach) {
        this.caseAttach = caseAttach;
    }
}
