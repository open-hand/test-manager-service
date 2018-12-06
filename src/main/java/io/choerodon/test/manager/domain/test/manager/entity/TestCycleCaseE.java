package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.repository.TestCycleCaseRepository;
import io.choerodon.test.manager.infra.common.utils.SpringUtil;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDO;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDefectRelDO;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseStepDO;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
@Scope("prototype")
public class TestCycleCaseE {
    private Long executeId;

    private Long cycleId;

    private Long issueId;

    private String rank;

    private Long executionStatus;

    private String executionStatusName;

    private Long assignedTo;

    private String comment;

    private String lastRank;

    private String nextRank;

    List<TestCycleCaseAttachmentRelE> caseAttachment;

    List<TestCycleCaseDefectRelE> caseDefect;

    List<TestCycleCaseDefectRelE>subStepDefects;

    List<TestCaseStepE> testCaseSteps;

    private Long objectVersionNumber;

    private Long lastUpdatedBy;

    private Date lastUpdateDate;

    private String cycleName;

    private String folderName;

	private Long versionId;

    private Long lastExecuteId;

    private Long nextExecuteId;

    private Long createdBy;

	List<TestCycleCaseStepE> cycleCaseStep;

    @Autowired
    private TestCycleCaseRepository testCycleCaseRepository;

    public static List<TestCycleCaseE> createCycleCases(List<TestCycleCaseE> testCycleCases) {
        TestCycleCaseE currentCycleCase = testCycleCases.get(0);
        currentCycleCase.setRank(RankUtil.Operation.INSERT.getRank(currentCycleCase.getLastedRank(currentCycleCase.getCycleId()), null));
        TestCycleCaseE prevCycleCase = currentCycleCase;

        for (int i = 1; i < testCycleCases.size(); i++) {
            currentCycleCase = testCycleCases.get(i);
            currentCycleCase.setRank(RankUtil.Operation.INSERT.getRank(prevCycleCase.rank, null));
            prevCycleCase = currentCycleCase;
        }

        TestCycleCaseRepository repository = SpringUtil.getApplicationContext().getBean(TestCycleCaseRepository.class);
        return repository.batchInsert(testCycleCases);
    }

    public List<TestCycleCaseE> queryByIssue(Long versionId) {
        return testCycleCaseRepository.queryByIssue(versionId);
    }

    public List<TestCycleCaseE> filter(Map map) {
        map.put("cycleId", this.cycleId);
        return testCycleCaseRepository.filter(map);
    }

	public String getLastedRank(Long cycleId) {
        return testCycleCaseRepository.getLastedRank(cycleId);
	}


    public TestCycleCaseE createOneCase() {
        setRank(RankUtil.Operation.INSERT.getRank(lastRank, nextRank));
        return addSelf();
    }

    public TestCycleCaseE changeOneCase() {
        if (!StringUtils.isEmpty(lastRank) || !StringUtils.isEmpty(nextRank)) {
            setRank(RankUtil.Operation.UPDATE.getRank(lastRank, nextRank));
        }
        return updateSelf();
    }

    public TestCycleCaseE queryOne() {
        return testCycleCaseRepository.queryOne(this);
    }

    public List<TestCycleCaseE> querySelf() {
        return testCycleCaseRepository.query(this);
    }

    public TestCycleCaseE addSelf() {
        TestCycleCaseDO testCycleCase = new TestCycleCaseDO();
        testCycleCase.setCycleId(cycleId);
        testCycleCase.setIssueId(issueId);
        testCycleCaseRepository.validateCycleCaseInCycle(testCycleCase);

        return testCycleCaseRepository.insert(this);
    }

    public TestCycleCaseE getCloneCase(String rank,Long newCycleId,Long defaultStatus){
        setExecuteId(null);
        setRank(rank);
        setCycleId(newCycleId);
        setExecutionStatus(defaultStatus);
        setObjectVersionNumber(null);
        return this;
    }

    public TestCycleCaseE updateSelf() {
        return testCycleCaseRepository.update(this);
    }

    public void deleteSelf() {
        testCycleCaseRepository.delete(this);
    }

    public Long getExecuteId() {
        return executeId;
    }

    public Long getCycleId() {
        return cycleId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public String getComment() {
        return comment;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }


    public void setExecuteId(Long executeId) {
        this.executeId = executeId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Long getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(Long executionStatus) {
        this.executionStatus = executionStatus;
    }

    public void setAssignedTo(Long assignedTo) {
            this.assignedTo = assignedTo;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public TestCycleCaseRepository getTestCycleCaseRepository() {
        return testCycleCaseRepository;
    }

    public void setTestCycleCaseRepository(TestCycleCaseRepository testCycleCaseRepository) {
        this.testCycleCaseRepository = testCycleCaseRepository;
    }

    public List<TestCycleCaseDefectRelE> getSubStepDefects() {
        return subStepDefects;
    }

    public void setSubStepDefects(List<TestCycleCaseDefectRelDO> subStepDefects) {
        this.subStepDefects = ConvertHelper.convertList(subStepDefects, TestCycleCaseDefectRelE.class);
    }

    public String getExecutionStatusName() {
        return executionStatusName;
    }

    public void setExecutionStatusName(String executionStatusName) {
        this.executionStatusName = executionStatusName;
    }

    public String getLastRank() {
        return lastRank;
    }

    public void setLastRank(String lastRank) {
        this.lastRank = lastRank;
    }

    public String getNextRank() {
        return nextRank;
    }

    public void setNextRank(String nextRank) {
        this.nextRank = nextRank;
    }

    public List<TestCycleCaseAttachmentRelE> getCaseAttachment() {
        return caseAttachment;
    }

    public void setCaseAttachment(List<TestCycleCaseAttachmentRelDO> caseAttachment) {
        this.caseAttachment = ConvertHelper.convertList(caseAttachment, TestCycleCaseAttachmentRelE.class);
    }

    public List<TestCycleCaseDefectRelE> getDefects() {
        return caseDefect;
    }

    public void setDefects(List<TestCycleCaseDefectRelDO> defects) {
        this.caseDefect = ConvertHelper.convertList(defects, TestCycleCaseDefectRelE.class);
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

	public Long getVersionId() {
		return versionId;
	}

	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	public List<TestCycleCaseStepE> getCycleCaseStep() {
		return cycleCaseStep;
	}

    public Long getLastExecuteId() {
        return lastExecuteId;
    }

    public void setLastExecuteId(Long lastExecuteId) {
        this.lastExecuteId = lastExecuteId;
    }

    public Long getNextExecuteId() {
        return nextExecuteId;
    }

    public void setNextExecuteId(Long nextExecuteId) {
        this.nextExecuteId = nextExecuteId;
    }

    public List<TestCaseStepE> getTestCaseSteps() {
        return testCaseSteps;
    }

    public void setTestCaseSteps(List<TestCaseStepE> testCaseSteps) {
        this.testCaseSteps = testCaseSteps;
    }

    public void setCycleCaseStep(List<TestCycleCaseStepDO> cycleCaseStep) {
        this.cycleCaseStep = ConvertHelper.convertList(cycleCaseStep, TestCycleCaseStepE.class);
    }

    public void setCycleCaseStepEs(List<TestCycleCaseStepE> cycleCaseStep) {
        this.cycleCaseStep = cycleCaseStep;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isPassed() {
        return "通过".equals(executionStatusName);
    }
}
