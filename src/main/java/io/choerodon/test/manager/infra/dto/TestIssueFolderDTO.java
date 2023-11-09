package io.choerodon.test.manager.infra.dto;

import javax.persistence.*;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
@VersionAudit
@ModifyAudit
@Table(name = "test_issue_folder")
public class TestIssueFolderDTO extends AuditDomain {

    public static final String MESSAGE_COPY_TEST_FOLDER = "COPY_TEST_FOLDER";
    public static final String FIELD_INIT_STATUS = "initStatus";

    public TestIssueFolderDTO(TestIssueFolderDTO source, Long parentId, Long versionId){
        this.name = source.name;
        this.versionId = versionId;
        this.projectId = source.projectId;
        this.type = source.type;
        this.parentId = parentId;
        this.rank = source.rank;
        this.oldFolderId = source.folderId;
    }

    @Id
    @GeneratedValue
    @Encrypt
    @ApiModelProperty(value = "主键id")
    private Long folderId;

    @ApiModelProperty(value = "文件夹名称")
    private String name;

    @ApiModelProperty(value = "版本id")
    private Long versionId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "父级id")
    private Long parentId;

    @ApiModelProperty(value = "rank值")
    private String rank;

    @ApiModelProperty(value = "初始状态")
    private String initStatus;

    @ApiModelProperty(value = "用例计数")
    @Transient
    private Long caseCount;
    @ApiModelProperty(value = "原文件夹id")
    @Transient
    @Encrypt
    private Long oldFolderId;

    public String getInitStatus() {
        return initStatus;
    }

    public void setInitStatus(String initStatus) {
        this.initStatus = initStatus;
    }

    public Long getOldFolderId() {
        return oldFolderId;
    }

    public void setOldFolderId(Long oldFolderId) {
        this.oldFolderId = oldFolderId;
    }

    public Long getCaseCount() {
        return caseCount;
    }

    public void setCaseCount(Long caseCount) {
        this.caseCount = caseCount;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public TestIssueFolderDTO(Long projectId) {
        this.projectId = projectId;
    }

    public TestIssueFolderDTO() {
    }



    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
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

    @Override
    public String toString() {
        return "TestIssueFolderDTO{"
                + "folderId=" + folderId
                + ", name='" + name + '\''
                + ", versionId=" + versionId
                + ", projectId=" + projectId
                + ", parentId=" + parentId
                + ", type='" + type + '\'' + '}';
    }
}
