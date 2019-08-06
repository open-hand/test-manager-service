package io.choerodon.test.manager.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */

public class TestCycleCaseDefectRelDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "缺陷关联类型：测试执行，执行步骤")
    private String defectType;

    @ApiModelProperty(value = "缺陷关联对象id")
    private Long defectLinkId;

    @ApiModelProperty(value = "缺陷issueID")
    private Long issueId;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "issue详情DTO")
    private IssueInfosDTO issueInfosDTO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getDefectType() {
        return defectType;
    }

    public void setDefectType(String defectType) {
        this.defectType = defectType;
    }

    public Long getDefectLinkId() {
        return defectLinkId;
    }

    public void setDefectLinkId(Long defectLinkId) {
        this.defectLinkId = defectLinkId;
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

    public IssueInfosDTO getIssueInfosDTO() {
        return issueInfosDTO;
    }

    public void setIssueInfosDTO(IssueInfosDTO issueInfosDTO) {
        this.issueInfosDTO = issueInfosDTO;
    }
}
