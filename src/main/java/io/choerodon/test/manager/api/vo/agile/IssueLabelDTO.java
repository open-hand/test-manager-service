package io.choerodon.test.manager.api.vo.agile;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 敏捷开发Issue标签
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:04:00
 */
@VersionAudit
@ModifyAudit
@Table(name = "agile_issue_label")
public class IssueLabelDTO extends AuditDomain {

    public IssueLabelDTO() {}

    public IssueLabelDTO(String labelName, Long projectId) {
        this.labelName = labelName;
        this.projectId = projectId;
    }

    /***/
    @Id
    @GeneratedValue
    private Long labelId;

    /**
     * 标签名称
     */
    private String labelName;

    /**
     * 项目id
     */
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