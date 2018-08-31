package io.choerodon.test.manager.infra.dataobject;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
@ModifyAudit
@VersionAudit
@Table(name = "test_issue_folder")
public class TestIssueFolderDO extends AuditDomain {

    @Id
    @GeneratedValue
    private Long folderId;

    private String name;

    private Long versionId;

    private Long projectId;

    private String type;

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

    @Override
    public String toString() {
        return "TestIssueFolderDO{" +
                "folderId=" + folderId +
                ", name='" + name + '\'' +
                ", versionId=" + versionId +
                ", projectId=" + projectId +
                ", type='" + type + '\'' +
                '}';
    }
}
