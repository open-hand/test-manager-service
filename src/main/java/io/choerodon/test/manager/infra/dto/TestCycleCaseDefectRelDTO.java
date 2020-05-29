package io.choerodon.test.manager.infra.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@VersionAudit
@ModifyAudit
@Table(name = "test_cycle_case_defect_rel")
public class TestCycleCaseDefectRelDTO extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;
    private String defectType;
    private Long defectLinkId;
    private Long issueId;
    private Long projectId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
