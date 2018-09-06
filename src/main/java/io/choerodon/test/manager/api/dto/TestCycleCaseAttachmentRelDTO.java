package io.choerodon.test.manager.api.dto;


/**
 * Created by 842767365@qq.com on 6/11/18.
 */

public class TestCycleCaseAttachmentRelDTO {

    private Long id;
    private String attachmentType;
    private Long attachmentLinkId;
    private String attachmentName;
    private String url;
    private String comment;
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
