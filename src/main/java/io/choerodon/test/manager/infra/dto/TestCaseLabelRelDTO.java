package io.choerodon.test.manager.infra.dto;

import javax.persistence.Table;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author: 25499
 * @date: 2019/11/20 13:43
 * @description:
 */
@Table(name = "test_case_label_rel")
public class TestCaseLabelRelDTO extends BaseDTO {
    private Long caseId;
    private Long labelId;
    private Long projectId;

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
