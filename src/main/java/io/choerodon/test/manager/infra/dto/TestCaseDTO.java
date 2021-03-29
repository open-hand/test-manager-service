package io.choerodon.test.manager.infra.dto;

import java.math.BigDecimal;
import javax.persistence.*;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author zhaotianxin
 * @since 2019/11/14
 */
@VersionAudit
@ModifyAudit
@Table(name = "test_case")
public class TestCaseDTO extends AuditDomain {

    public static final String FIELD_FOLDER_ID = "folderId";
    public static final String FIELD_CASE_ID = "caseId";

    @Id
    @GeneratedValue
    private Long caseId;
    private String caseNum;
    private String summary;
    private String description;
    @Column(name = "`rank`")
    private String rank;
    private Long folderId;
    private Long versionNum;
    private Long projectId;
    private Long priorityId;
    @Transient
    private String priorityName;
    @Transient
    private BigDecimal sequence;
    @Transient
    private String priorityColour;

    private String customNum;

    public String getPriorityColour() {
        return priorityColour;
    }

    public void setPriorityColour(String priorityColour) {
        this.priorityColour = priorityColour;
    }

    public BigDecimal getSequence() {
        return sequence;
    }

    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getCaseNum() {
        return caseNum;
    }

    public void setCaseNum(String caseNum) {
        this.caseNum = caseNum;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(Long versionNum) {
        this.versionNum = versionNum;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;

    }

    public String getCustomNum() {
        return customNum;
    }

    public void setCustomNum(String customNum) {
        this.customNum = customNum;
    }
}

