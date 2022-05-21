package io.choerodon.test.manager.infra.dto;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@VersionAudit
@ModifyAudit
@Table(name = "test_cycle")
public class TestCycleDTO extends AuditDomain {
    @Id
    @GeneratedValue
    @ApiModelProperty(value = "计划文件夹id")
    private Long cycleId;

    @ApiModelProperty(value = "父文件夹id")
    private Long parentCycleId;

    @ApiModelProperty(value = "文件夹id")
    private String cycleName;

    @ApiModelProperty(value = "版本id")
    private Long versionId;

    @ApiModelProperty(value = "描述id")
    private String description;

    @ApiModelProperty(value = "build")
    private String build;

    @ApiModelProperty(value = "环境")
    private String environment;

    @ApiModelProperty(value = "开始时间")
    private Date fromDate;

    @ApiModelProperty(value = "结束时间")
    private Date toDate;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "文件夹id")
    private Long folderId;

    @ApiModelProperty(value = "创建人")
    private Long createdBy;

    @ApiModelProperty(value = "更新人")
    private Long lastUpdatedBy;

    @ApiModelProperty(value = "rank")
    private String rank;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "计划id")
    private Long planId;

    @ApiModelProperty(value = "循环用例")
    @Transient
    private List<Map<Long, Object>> cycleCaseList;

    @ApiModelProperty(value = "用例计数")
    @Transient
    private Long caseCount;
    @ApiModelProperty(value = "原计划文件夹id")
    @Transient
    private Long oldCycleId;

    public Long getCaseCount() {
        return caseCount;
    }

    public void setCaseCount(Long caseCount) {
        this.caseCount = caseCount;
    }

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

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getOldCycleId() {
        return oldCycleId;
    }

    public void setOldCycleId(Long oldCycleId) {
        this.oldCycleId = oldCycleId;
    }
}
