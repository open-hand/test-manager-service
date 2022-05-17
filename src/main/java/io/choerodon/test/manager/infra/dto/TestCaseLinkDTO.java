package io.choerodon.test.manager.infra.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author zhaotianxin
 * @since 2019/11/14
 */
@VersionAudit
@ModifyAudit
@Table(name = "test_case_link")
public class TestCaseLinkDTO extends AuditDomain {
    @Id
    @GeneratedValue
    @Encrypt
    @ApiModelProperty(value = "关联id")
    private Long linkId;
    @Encrypt
    @ApiModelProperty(value = "关联的用例id")
    private Long linkCaseId;
    @Encrypt
    @ApiModelProperty(value = "关联的工作项id")
    private Long issueId;
    @Encrypt
    @ApiModelProperty(value = "关联类型id")
    private Long linkTypeId;
    @ApiModelProperty(value = "项目id")
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
