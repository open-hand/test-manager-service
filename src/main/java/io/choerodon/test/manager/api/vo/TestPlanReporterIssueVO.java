package io.choerodon.test.manager.api.vo;

import io.choerodon.test.manager.api.vo.agile.StatusVO;
import io.choerodon.test.manager.api.vo.agile.UserDO;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

/**
 * @author superlee
 * @since 2020-12-11
 */
public class TestPlanReporterIssueVO {

    @Encrypt
    private Long issueId;

    private String summary;

    private StatusVO statusMapVO;

    private UserDO assignee;

    private List<TestFolderCycleCaseVO> testFolderCycleCases;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public StatusVO getStatusMapVO() {
        return statusMapVO;
    }

    public void setStatusMapVO(StatusVO statusMapVO) {
        this.statusMapVO = statusMapVO;
    }

    public UserDO getAssignee() {
        return assignee;
    }

    public void setAssignee(UserDO assignee) {
        this.assignee = assignee;
    }

    public List<TestFolderCycleCaseVO> getTestFolderCycleCases() {
        return testFolderCycleCases;
    }

    public void setTestFolderCycleCases(List<TestFolderCycleCaseVO> testFolderCycleCases) {
        this.testFolderCycleCases = testFolderCycleCases;
    }
}
