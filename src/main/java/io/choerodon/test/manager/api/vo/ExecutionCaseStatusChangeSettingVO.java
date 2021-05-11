package io.choerodon.test.manager.api.vo;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author zhaotianxin
 * @date 2021-05-10 16:53
 */
public class ExecutionCaseStatusChangeSettingVO {
    @Encrypt
    private Long id;

    @Encrypt
    private Long agileIssueTypeId;

    @Encrypt
    private Long agileStatusId;

    @Encrypt
    private Long testStatusId;

    private TestStatusVO testStatusVO;

    private Long projectId;

    private Long organizationId;

    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgileIssueTypeId() {
        return agileIssueTypeId;
    }

    public void setAgileIssueTypeId(Long agileIssueTypeId) {
        this.agileIssueTypeId = agileIssueTypeId;
    }

    public Long getAgileStatusId() {
        return agileStatusId;
    }

    public void setAgileStatusId(Long agileStatusId) {
        this.agileStatusId = agileStatusId;
    }

    public Long getTestStatusId() {
        return testStatusId;
    }

    public void setTestStatusId(Long testStatusId) {
        this.testStatusId = testStatusId;
    }

    public TestStatusVO getTestStatusVO() {
        return testStatusVO;
    }

    public void setTestStatusVO(TestStatusVO testStatusVO) {
        this.testStatusVO = testStatusVO;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
