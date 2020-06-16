package io.choerodon.test.manager.api.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.choerodon.test.manager.infra.constant.EncryptKeyConstants;
import io.swagger.annotations.ApiModelProperty;

import io.choerodon.test.manager.api.vo.agile.IssueLinkDTO;
import io.choerodon.test.manager.api.vo.agile.SearchDTO;
import io.choerodon.test.manager.api.vo.agile.UserDO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDefectRelDTO;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public class TestCycleCaseVO {

    @ApiModelProperty(value = "测试执行ID")
    @Encrypt(/**EncryptKeyConstants.TEST_CYCLE_CASE**/)
    private Long executeId;

    @ApiModelProperty(value = "循环ID")
    private Long cycleId;

    @ApiModelProperty(value = "用例issueID")
    private Long issueId;

    @ApiModelProperty(value = "用例issue名称")
    private String summary;

    @ApiModelProperty(value = "排序值")
    private String rank;

    @ApiModelProperty(value = "执行状态")
    @Encrypt(/**EncryptKeyConstants.TEST_STATUS**/)
    private Long executionStatus;

    @ApiModelProperty(value = "执行状态名")
    private String executionStatusName;

    @ApiModelProperty(value = "状态颜色")
    private String statusColor;

    @ApiModelProperty(value = "指派人")
    private Long assignedTo;

    @ApiModelProperty(value = "描述")
    private String comment;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "上一位排序值")
    private String lastRank;

    @ApiModelProperty(value = "后一位排序值")
    private String nextRank;

    @ApiModelProperty(value = "指派人详情")
    private UserDO assigneeUser;

    @ApiModelProperty(value = "最后更新人详情")
    private UserDO lastUpdateUser;

    @ApiModelProperty(value = "版本ID")
    private Long versionId;

    @ApiModelProperty(value = "最后更新人ID")
    private Long lastUpdatedBy;

    @ApiModelProperty(value = "最后更新日期")
    private Date lastUpdateDate;

    @ApiModelProperty(value = "循环名")
    private String cycleName;

    @ApiModelProperty(value = "issue详情DTO")
    private IssueInfosVO issueInfosVO;

    @ApiModelProperty(value = "用例文件夹名")
    private String folderName;

    @ApiModelProperty(value = "版本名")
    private String versionName;

    @ApiModelProperty(value = "上一位执行ID")
    private Long lastExecuteId;

    @ApiModelProperty(value = "后一位执行ID")
    @Encrypt
    private Long nextExecuteId;

    private String description;

    @ApiModelProperty(value = "执行附件")
    private List<TestCycleCaseAttachmentRelVO> caseAttachment;

    @ApiModelProperty(value = "执行缺陷")
    private List<TestCycleCaseDefectRelVO> caseDefect = new ArrayList<>();

    @ApiModelProperty(value = "步骤缺陷")
    private List<TestCycleCaseDefectRelVO> subStepDefects = new ArrayList<>();

    @ApiModelProperty(value = "测试步骤DTOList")
    List<TestCycleCaseStepVO> cycleCaseStep;

    @ApiModelProperty(value = "关联issueDTOList")
    private List<IssueLinkDTO> issueLinkDTOS;

    @ApiModelProperty(value = "searchDTO")
    private SearchDTO searchDTO;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "所属计划Id")
    private Long planId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<TestCycleCaseDefectRelVO> getSubStepDefects() {
        return subStepDefects;
    }

    public List<IssueLinkDTO> getIssueLinkDTOS() {
        return issueLinkDTOS;
    }

    public void setIssueLinkDTOS(List<IssueLinkDTO> issueLinkDTOS) {
        this.issueLinkDTOS = issueLinkDTOS;
    }

    public void addIssueLinkDTOS(IssueLinkDTO issueLinkDTO) {
        if (this.issueLinkDTOS == null) {
            this.issueLinkDTOS = new ArrayList<>();
        }
        this.issueLinkDTOS.add(issueLinkDTO);
    }

    public SearchDTO getSearchDTO() {
        return searchDTO;
    }

    public void setSearchDTO(SearchDTO searchDTO) {
        this.searchDTO = searchDTO;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setSubStepDefects(List<TestCycleCaseDefectRelDTO> subStepDefects) {
        this.subStepDefects = ConvertHelper.convertList(subStepDefects, TestCycleCaseDefectRelVO.class);
    }

    public String getExecutionStatusName() {
        return executionStatusName;
    }

    public void setExecutionStatusName(String executionStatusName) {
        this.executionStatusName = executionStatusName;
    }

    public Long getExecuteId() {
        return executeId;
    }

    public void setExecuteId(Long executeId) {
        this.executeId = executeId;
    }

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(Long executionStatus) {
        this.executionStatus = executionStatus;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public IssueInfosVO getIssueInfosVO() {
        return issueInfosVO;
    }

    public void setIssueInfosVO(IssueInfosVO issueInfosVO) {
        this.issueInfosVO = issueInfosVO;
    }

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }

    public UserDO getAssigneeUser() {
        return assigneeUser;
    }

    public void setAssigneeUser(UserDO assigneeUser) {
        this.assigneeUser = assigneeUser;
    }

    public UserDO getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(UserDO lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public String getComment() {
        return comment;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
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

    public List<TestCycleCaseAttachmentRelVO> getCaseAttachment() {
        return caseAttachment;
    }

    public void setCaseAttachment(List<TestCycleCaseAttachmentRelVO> caseAttachment) {
        this.caseAttachment = caseAttachment;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public List<TestCycleCaseStepVO> getCycleCaseStep() {
        return cycleCaseStep;
    }

    public Long getLastExecuteId() {
        return lastExecuteId;
    }

    public void setLastExecuteId(Long lastExecuteId) {
        this.lastExecuteId = lastExecuteId;
    }

    public Long getNextExecuteId() {
        return nextExecuteId;
    }

    public void setNextExecuteId(Long nextExecuteId) {
        this.nextExecuteId = nextExecuteId;
    }


    public List<TestCycleCaseDefectRelVO> getDefects() {
        return caseDefect;
    }

    public void setDefects(List<TestCycleCaseDefectRelVO> caseDefect) {
        this.caseDefect = caseDefect;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public List<TestCycleCaseDefectRelVO> getCaseDefect() {
        return caseDefect;
    }

    public void setCaseDefect(List<TestCycleCaseDefectRelVO> caseDefect) {
        this.caseDefect = caseDefect;
    }

    public void setCycleCaseStep(List<TestCycleCaseStepVO> cycleCaseStep) {
        this.cycleCaseStep = cycleCaseStep;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }
}
