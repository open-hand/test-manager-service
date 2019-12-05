package io.choerodon.test.manager.api.vo;

import java.util.List;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.dto.TestCaseStepDTO;

/**
 * @author zhaotianxin
 * @since 2019/12/4
 */
public class CaseCompareVO {
    private Long caseId;

    private TestCaseVO testCase;

    private List<TestCaseStepDTO> caseStep;

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
