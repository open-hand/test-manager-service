package io.choerodon.test.manager.domain.test.manager.event;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/17
 */
public class IssuePayload {

    private Long issueId;

    private Long projectId;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

}
