package io.choerodon.test.manager.api.dto;

import java.util.List;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public class TestIssueFolderRelDTO {
    private Long id;

    private Long folderId;

    private Long versionId;

    private Long projectId;

    private Long issueId;

    private Long objectVersionNumber;

    private IssueInfosDTO issueInfosDTO;

    private String folderName;

    private List<TestCaseStepDTO> testCaseStepDTOS;

    private String errorInfo;

    public TestIssueFolderRelDTO() {
    }

    public TestIssueFolderRelDTO(Long folderId, Long versionId, Long projectId, Long issueId, Long objectVersionNumber) {
        this.folderId = folderId;
        this.versionId = versionId;
        this.projectId = projectId;
        this.issueId = issueId;
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public List<TestCaseStepDTO> getTestCaseStepDTOS() {
        return testCaseStepDTOS;
    }

    public void setTestCaseStepDTOS(List<TestCaseStepDTO> testCaseStepDTOS) {
        this.testCaseStepDTOS = testCaseStepDTOS;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public IssueInfosDTO getIssueInfosDTO() {
        return issueInfosDTO;
    }

    public void setIssueInfosDTO(IssueInfosDTO issueInfosDTO) {
        this.issueInfosDTO = issueInfosDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
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

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
