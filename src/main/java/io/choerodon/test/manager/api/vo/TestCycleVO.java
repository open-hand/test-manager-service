package io.choerodon.test.manager.api.vo;

import io.choerodon.test.manager.api.vo.agile.UserDO;
import io.choerodon.test.manager.infra.constant.EncryptKeyConstants;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public class TestCycleVO {

    @ApiModelProperty(value = "主键，循环ID")
    @Encrypt(/**EncryptKeyConstants.TEST_ISSUE_FOLDER**/)
    private Long cycleId;

    @ApiModelProperty(value = "父循环ID")
    @Encrypt(/**EncryptKeyConstants.TEST_ISSUE_FOLDER**/)
    private Long parentCycleId;

    @ApiModelProperty(value = "循环名")
    private String cycleName;

    @ApiModelProperty(value = "版本ID")
    private Long versionId;

    @ApiModelProperty(value = "版本名称")
    private String versionName;

    @ApiModelProperty(value = "版本状态名")
    private String versionStatusName;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "构建号")
    private String build;

    @ApiModelProperty(value = "创建人详细信息")
    private UserDO createdUser;

    @ApiModelProperty(value = "创建人ID")
    private Long createdBy;

    @ApiModelProperty(value = "环境")
    private String environment;

    @ApiModelProperty(value = "起始日期")
    private Date fromDate;

    @ApiModelProperty(value = "结束日期")
    private Date toDate;

    @ApiModelProperty(value = "类型：循环(cycle)还是阶段(folder)")
    private String type;

    @ApiModelProperty(value = "进度条list")
    private List<Object> cycleCaseList;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "关联的用例文件夹id")
    private Long folderId;

    @ApiModelProperty(value = "当前rank")
    private String rank;

    @ApiModelProperty(value = "前一个的rank值")
    private String lastRank;

    @ApiModelProperty(value = "后一个的rank值")
    private String nextRank;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "计划Id")
    @Encrypt(/**EncryptKeyConstants.TEST_ISSUE_FOLDER**/)
    private Long planId;

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
        return cycleCaseList;
    }

    public void setCycleCaseWithBarList(List<Object> cycleCaseList) {
        this.cycleCaseList = cycleCaseList;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<Object> getCycleCaseList() {
        return cycleCaseList;
    }

    public void setCycleCaseList(List<Object> cycleCaseList) {
        this.cycleCaseList = cycleCaseList;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }
}
