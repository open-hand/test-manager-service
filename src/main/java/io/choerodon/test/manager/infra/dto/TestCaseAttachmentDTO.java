package io.choerodon.test.manager.infra.dto;

import javax.persistence.Table;

import io.choerodon.mybatis.entity.BaseDTO;

@Table(name = "test_case_attachment")
public class TestCaseAttachmentDTO extends BaseDTO {

    private Long attachmentId;
    private Long caseId;
    private String url;
    private String fileName;
    private Long projectId;

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

}
