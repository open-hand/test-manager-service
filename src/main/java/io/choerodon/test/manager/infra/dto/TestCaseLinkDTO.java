package io.choerodon.test.manager.infra.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author zhaotianxin
 * @since 2019/11/14
 */
@Table(name = "test_case_link")
public class TestCaseLinkDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long linkId;
    private Long linkCaseId;
    private Long issueId;
    private Long linkTypeId;
    private Long projectId;

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public Long getLinkCaseId() {
        return linkCaseId;
    }

    public void setLinkCaseId(Long linkCaseId) {
        this.linkCaseId = linkCaseId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getLinkTypeId() {
        return linkTypeId;
    }

    public void setLinkTypeId(Long linkTypeId) {
        this.linkTypeId = linkTypeId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
