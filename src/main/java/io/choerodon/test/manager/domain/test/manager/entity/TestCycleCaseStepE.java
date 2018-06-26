package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.repository.TestCycleCaseStepRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDefectRelDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Transient;
import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@Component
@Scope("prototype")
public class TestCycleCaseStepE {
    private Long executeStepId;

    private Long executeId;

    private Long stepId;

    private String comment;

    private String testStep;

    private String testData;

    private String expectedResult;

    private Long objectVersionNumber;


    private List<TestCycleCaseAttachmentRelE> caseAttachment;


    private List<TestCycleCaseAttachmentRelE> stepAttachment;


    private List<TestCycleCaseDefectRelE> defects;


    @Autowired
    private TestCycleCaseStepRepository testCycleCaseStepRepository;

    public Page<TestCycleCaseStepE> querySelf(PageRequest pageRequest) {
        return testCycleCaseStepRepository.query(this, pageRequest);
    }

    public List<TestCycleCaseStepE> querySelf() {
        return testCycleCaseStepRepository.query(this);
    }

    public TestCycleCaseStepE addSelf() {
        return testCycleCaseStepRepository.insert(this);
    }

    public TestCycleCaseStepE updateSelf() {
        return testCycleCaseStepRepository.update(this);
    }

    public void deleteSelf() {
        testCycleCaseStepRepository.delete(this);
    }

    public Long getExecuteStepId() {
        return executeStepId;
    }

    public Long getExecuteId() {
        return executeId;
    }

    public Long getStepId() {
        return stepId;
    }

    public String getComment() {
        return comment;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setExecuteStepId(Long executeStepId) {
        this.executeStepId = executeStepId;
    }

    public void setExecuteId(Long executeId) {
        this.executeId = executeId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public void setTestCycleCaseStepRepository(TestCycleCaseStepRepository testCycleCaseStepRepository) {
        this.testCycleCaseStepRepository = testCycleCaseStepRepository;
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

    public TestCycleCaseStepRepository getTestCycleCaseStepRepository() {
        return testCycleCaseStepRepository;
    }

    public List<TestCycleCaseAttachmentRelE> getCaseAttachment() {
        return caseAttachment;
    }


    public void setCaseAttachment(List<TestCycleCaseAttachmentRelDO> caseAttachment) {
        this.caseAttachment = ConvertHelper.convertList(caseAttachment, TestCycleCaseAttachmentRelE.class);
    }

    public List<TestCycleCaseAttachmentRelE> getStepAttachment() {
        return stepAttachment;
    }

    public void setStepAttachment(List<TestCycleCaseAttachmentRelDO> stepAttachment) {
        this.stepAttachment = ConvertHelper.convertList(stepAttachment, TestCycleCaseAttachmentRelE.class);
    }

    public List<TestCycleCaseDefectRelE> getDefects() {
        return defects;
    }

    public void setDefects(List<TestCycleCaseDefectRelE> defects) {
        this.defects = defects;
    }

}
