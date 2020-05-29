package io.choerodon.test.manager.infra.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author: 25499
 * @date: 2019/11/20 13:41
 * @description:
 */
@VersionAudit
@ModifyAudit
@Table(name = "test_case_label")
public class TestCaseLabelDTO extends AuditDomain {
    @Id
    @GeneratedValue
    private Long labelId;
    private String labelName;
    private Long projectId;

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
