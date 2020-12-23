package io.choerodon.test.manager.api.vo.agile;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

public class IssueLinkCreateVO {

    @ApiModelProperty(value = "问题链接类型id")
    @Encrypt
    private Long linkTypeId;

    @ApiModelProperty(value = "被链接的问题id")
    @Encrypt
    private Long linkedIssueId;

    @ApiModelProperty(value = "链接问题id")
    @Encrypt
    private Long issueId;

    @ApiModelProperty(value = "正向或反向")
    private Boolean in;

    public Boolean getIn() {
        return in;
    }

    public void setIn(Boolean in) {
        this.in = in;
    }

    public Long getLinkTypeId() {
        return linkTypeId;
    }

    public void setLinkTypeId(Long linkTypeId) {
        this.linkTypeId = linkTypeId;
    }

    public Long getLinkedIssueId() {
        return linkedIssueId;
    }

    public void setLinkedIssueId(Long linkedIssueId) {
        this.linkedIssueId = linkedIssueId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }
}