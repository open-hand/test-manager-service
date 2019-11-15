package io.choerodon.test.manager.infra.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author zhaotianxin
 * @since 2019/11/14
 */
@Table(name = "test_project_info")
public class TestProjectInfoDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long infoId;
    private Long projectId;
    private String projectCode;
    private Long caseMaxNum;

    public Long getInfoId() {
        return infoId;
    }

    public void setInfoId(Long infoId) {
        this.infoId = infoId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public Long getCaseMaxNum() {
        return caseMaxNum;
    }

    public void setCaseMaxNum(Long caseMaxNum) {
        this.caseMaxNum = caseMaxNum;
    }
}
