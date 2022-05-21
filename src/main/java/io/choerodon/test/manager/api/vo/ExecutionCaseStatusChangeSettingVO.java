package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author zhaotianxin
 * @date 2021-05-10 16:53
 */
public class ExecutionCaseStatusChangeSettingVO {

    @ApiModelProperty(value = "id")
    @Encrypt
    private Long id;

    @ApiModelProperty(value = "敏捷工作项状态id")
    @Encrypt
    private Long agileIssueTypeId;

    @ApiModelProperty(value = "工作项状态id")
    @Encrypt
    private Long agileStatusId;

    @ApiModelProperty(value = "测试用例状态id")
    @Encrypt
    private Long testStatusId;

    @ApiModelProperty(value = "测试状态")
    private TestStatusVO testStatusVO;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "组织id")
    private Long organizationId;

    @ApiModelProperty(value = "乐观锁")
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
