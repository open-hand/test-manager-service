package io.choerodon.test.manager.infra.dto;

public class TestIssueFolderProDTO {
    private Long folderId;

    private String name;

    private Long versionId;

    private Long projectId;

    private String type;

    private Long objectVersionNumber;

    private Boolean newFolder;

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Boolean getNewFolder() {
        return newFolder;
    }

    public void setNewFolder(Boolean newFolder) {
        this.newFolder = newFolder;
    }
}
