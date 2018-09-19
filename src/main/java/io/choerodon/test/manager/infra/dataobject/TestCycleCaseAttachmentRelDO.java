package io.choerodon.test.manager.infra.dataobject;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@ModifyAudit
@VersionAudit
@Table(name = "test_cycle_case_attach_rel")
public class TestCycleCaseAttachmentRelDO extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;
    private String attachmentType;
    private Long attachmentLinkId;
    private String attachmentName;
    private String url;
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public Long getAttachmentLinkId() {
        return attachmentLinkId;
    }

    public void setAttachmentLinkId(Long attachmentLinkId) {
        this.attachmentLinkId = attachmentLinkId;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
