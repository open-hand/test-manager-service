package io.choerodon.test.manager.api.dto;

import java.util.Date;
import java.util.List;

import io.choerodon.agile.api.dto.UserDO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public class TestCycleDTO {

    private Long cycleId;

    private Long parentCycleId;

    private String cycleName;

    private Long versionId;

    private String versionName;

    private String versionStatusName;

    private String description;

    private String build;

    private UserDO createdUser;

    private Long createdBy;

    private String environment;

    private Date fromDate;

    private Date toDate;

    private String type;

    private List<Object> cycleCaseWithBarList;

    private Long objectVersionNumber;

    private Long folderId;

    private String rank;

    private String lastRank;

    private String nextRank;

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<Object> getCycleCaseWithBarList() {
        return cycleCaseWithBarList;
    }

    public void setCycleCaseWithBarList(List<Object> cycleCaseWithBarList) {
        this.cycleCaseWithBarList = cycleCaseWithBarList;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionStatusName() {
        return versionStatusName;
    }

    public void setVersionStatusName(String versionStatusName) {
        this.versionStatusName = versionStatusName;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public UserDO getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(UserDO createdUser) {
        this.createdUser = createdUser;
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

    public String getLastRank() {
        return lastRank;
    }

    public void setLastRank(String lastRank) {
        this.lastRank = lastRank;
    }

    public String getNextRank() {
        return nextRank;
    }

    public void setNextRank(String nextRank) {
        this.nextRank = nextRank;
    }
}
