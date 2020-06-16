package io.choerodon.test.manager.api.vo;

import io.choerodon.test.manager.infra.constant.EncryptKeyConstants;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */

public class TestCycleCaseAttachmentRelVO {

    @ApiModelProperty(value = "主键id")
    @Encrypt(EncryptKeyConstants.TEST_CYCLE_CASE_ATTACH_REL)
    private Long id;

    @ApiModelProperty(value = "附件类型：测试执行附件，步骤附件")
    private String attachmentType;

    @ApiModelProperty(value = "附件关联对象id")
    private Long attachmentLinkId;

    @ApiModelProperty(value = "附件名")
    private String attachmentName;

    @ApiModelProperty(value = "附件minioURL")
    private String url;

    @ApiModelProperty(value = "描述")
    private String comment;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
