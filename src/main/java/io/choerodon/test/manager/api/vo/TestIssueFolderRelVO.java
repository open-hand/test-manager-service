package io.choerodon.test.manager.api.vo;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public class TestIssueFolderRelVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "文件夹id")
    private Long folderId;

    @ApiModelProperty(value = "版本id")
    private Long versionId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "issueID")
    private Long issueId;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "issue信息DTO")
    private IssueInfosVO issueInfosVO;

    @ApiModelProperty(value = "文件夹名")
    private String folderName;

    @ApiModelProperty(value = "测试步骤DTO")
    private List<TestCaseStepVO> testCaseStepVOS;

    @ApiModelProperty(value = "导出的报错信息")
    private String errorInfo;

    public TestIssueFolderRelVO() {
    }

    public TestIssueFolderRelVO(Long folderId, Long versionId, Long projectId, Long issueId, Long objectVersionNumber) {
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

    public List<TestCaseStepVO> getTestCaseStepVOS() {
        return testCaseStepVOS;
    }

    public void setTestCaseStepVOS(List<TestCaseStepVO> testCaseStepVOS) {
        this.testCaseStepVOS = testCaseStepVOS;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public IssueInfosVO getIssueInfosVO() {
        return issueInfosVO;
    }

    public void setIssueInfosVO(IssueInfosVO issueInfosVO) {
        this.issueInfosVO = issueInfosVO;
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
