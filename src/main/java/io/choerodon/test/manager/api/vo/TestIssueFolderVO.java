package io.choerodon.test.manager.api.vo;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public class TestIssueFolderVO {

    @ApiModelProperty(value = "文件夹ID")
    @Encrypt
    private Long folderId;

    @ApiModelProperty(value = "父级文件夹ID,无父级目录id传0")
    @Encrypt
    private Long parentId;

    private Boolean rootNode;

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

    private String initStatus;

    private String rank;

    private String lastRank;

    private String nextRank;

    private Date fromDate;

    private Date toDate;

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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "TestIssueFolderVO{" +
                "folderId=" + folderId +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", versionId=" + versionId +
                ", projectId=" + projectId +
                ", type='" + type + '\'' +
                ", objectVersionNumber=" + objectVersionNumber +
                '}';
    }

    public String getInitStatus() {

        return initStatus;
    }

    public String getLastRank() {
        return lastRank;
    }

    public void setLastRank(String lastRank) {
        this.lastRank = lastRank;
    }

    public String getNextRank() {
        return nextRank;
    }

    public void setNextRank(String nextRank) {
        this.nextRank = nextRank;
    }

    public void setInitStatus(String initStatus) {
        this.initStatus = initStatus;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Boolean getRootNode() {
        return rootNode;
    }

    public void setRootNode(Boolean rootNode) {
        this.rootNode = rootNode;
    }
}
