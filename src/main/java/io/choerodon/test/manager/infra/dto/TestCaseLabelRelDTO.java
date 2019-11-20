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
    private Long case_id;
    private Long labelId;
    private Long projectId;

    public Long getCase_id() {
        return case_id;
    }

    public void setCase_id(Long case_id) {
        this.case_id = case_id;
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
