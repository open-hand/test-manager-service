package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public class TestIssueFolderVO {

    @ApiModelProperty(value = "文件夹ID")
    private Long folderId;

    @ApiModelProperty(value = "父级文件夹ID,无父级目录id传0")
    private Long parentId;

    @ApiModelProperty(value = "文件夹名")
    private String name;

    @ApiModelProperty(value = "版本ID")
    private Long versionId;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "文件夹类型")
    private String type;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    public TestIssueFolderVO() {
    }

    public TestIssueFolderVO(Long folderId, String name, Long versionId, Long projectId, String type, Long objectVersionNumber) {
        this.folderId = folderId;
        this.name = name;
        this.versionId = versionId;
        this.projectId = projectId;
        this.type = type;
        this.objectVersionNumber = objectVersionNumber;
    }

    public TestCycleVO transferToCycle() {

        TestCycleVO testCycleVO = new TestCycleVO();

        testCycleVO.setCycleId(this.getFolderId());
        testCycleVO.setCycleName(this.getName());
        testCycleVO.setVersionId(this.getVersionId());
        testCycleVO.setType(this.getType());
        testCycleVO.setObjectVersionNumber(this.getObjectVersionNumber());
        return testCycleVO;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
