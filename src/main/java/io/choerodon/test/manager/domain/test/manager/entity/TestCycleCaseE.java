package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.repository.TestCycleCaseRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDefectRelDO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

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

    private String executionStatus;

    private Long assignedTo;

    private String comment;

    private String lastRank;

    private String nextRank;

    List<TestCycleCaseAttachmentRelE> caseAttachment;

    private Long objectVersionNumber;

    private Long lastUpdateBy;

    private Date lastUpdateDate;

    private List<TestCycleCaseStepE> testCycleCaseStepES;

    private List<TestCycleCaseDefectRelE> defects;


    @Autowired
    private TestCycleCaseRepository testCycleCaseRepository;

    public Page<TestCycleCaseE> querySelf(PageRequest pageRequest) {
        return testCycleCaseRepository.query(this, pageRequest);
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
        return testCycleCaseRepository.insert(this);
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

    public String getExecutionStatus() {
        return executionStatus;
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

    public void setExecutionStatus(String executionStatus) {
        this.executionStatus = executionStatus;
    }

    public void setAssignedTo(Long assignedTo) {
        if (assignedTo == null) {
            this.assignedTo = new Long(0);
        } else {
            this.assignedTo = assignedTo;
        }
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

    public List<TestCycleCaseStepE> getTestCycleCaseStepES() {
        return testCycleCaseStepES;
    }

    public void setTestCycleCaseStepES(List<TestCycleCaseStepE> testCycleCaseStepES) {
        this.testCycleCaseStepES = testCycleCaseStepES;
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
        return defects;
    }

    public void setDefects(List<TestCycleCaseDefectRelE> defects) {
        this.defects = ConvertHelper.convertList(defects, TestCycleCaseDefectRelE.class);
    }

    public Long getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(Long lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
