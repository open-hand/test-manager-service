package io.choerodon.test.manager.infra.dataobject;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Table(name = "test_cycle")
public class TestCycleDO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cycleId;

    private Long parentCycleId;

    private String cycleName;

    private Long versionId;

    private String description;

    private String build;

    private String environment;

    private Date fromDate;

    private Date toDate;

    private String type;

    private Long folderId;

    private Long createdBy;

    private Long lastUpdatedBy;

    private String rank;

    private Long projectId;

    @Transient
    private List<Map<Long, Object>> cycleCaseList;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public Long getParentCycleId() {
        return parentCycleId;
    }

    public void setParentCycleId(Long parentCycleId) {
        this.parentCycleId = parentCycleId;
    }

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Map<Long, Object>> getCycleCaseList() {
        return cycleCaseList;
    }

    public void setCycleCaseList(List<Map<Long, Object>> cycleCaseList) {
        this.cycleCaseList = cycleCaseList;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Override
    public Long getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    @Override
    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}
