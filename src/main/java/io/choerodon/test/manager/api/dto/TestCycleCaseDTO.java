package io.choerodon.test.manager.api.dto;

import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public class TestCycleCaseDTO {

    @ApiModelProperty(value = "测试执行ID")
    private Long executeId;

    @ApiModelProperty(value = "循环ID")
    private Long cycleId;

    @ApiModelProperty(value = "用例issueID")
    private Long issueId;

    @ApiModelProperty(value = "排序值")
    private String rank;

    @ApiModelProperty(value = "执行状态")
    private Long executionStatus;

    @ApiModelProperty(value = "执行状态名")
    private String executionStatusName;

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
    private IssueInfosDTO issueInfosDTO;

    @ApiModelProperty(value = "用例文件夹名")
    private String folderName;

    @ApiModelProperty(value = "版本名")
    private String versionName;

    @ApiModelProperty(value = "上一位执行ID")
    private Long lastExecuteId;

    @ApiModelProperty(value = "后一位执行ID")
    private Long nextExecuteId;

    @ApiModelProperty(value = "执行附件")
    private List<TestCycleCaseAttachmentRelDTO> caseAttachment;

    @ApiModelProperty(value = "执行缺陷")
    private List<TestCycleCaseDefectRelDTO> caseDefect = new ArrayList<>();

    @ApiModelProperty(value = "步骤缺陷")
    private List<TestCycleCaseDefectRelDTO> subStepDefects = new ArrayList<>();

    @ApiModelProperty(value = "测试步骤DTOList")
    List<TestCycleCaseStepDTO> cycleCaseStep;

    @ApiModelProperty(value = "关联issueDTOList")
    private List<IssueLinkDTO> issueLinkDTOS;

    @ApiModelProperty(value = "searchDTO")
    private SearchDTO searchDTO;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<TestCycleCaseDefectRelDTO> getSubStepDefects() {
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

    public void setSubStepDefects(List<TestCycleCaseDefectRelE> subStepDefects) {
        this.subStepDefects = ConvertHelper.convertList(subStepDefects, TestCycleCaseDefectRelDTO.class);
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

    public IssueInfosDTO getIssueInfosDTO() {
        return issueInfosDTO;
    }

    public void setIssueInfosDTO(IssueInfosDTO issueInfosDTO) {
        this.issueInfosDTO = issueInfosDTO;
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

    public List<TestCycleCaseAttachmentRelDTO> getCaseAttachment() {
        return caseAttachment;
    }

    public void setCaseAttachment(List<TestCycleCaseAttachmentRelE> caseAttachment) {
        this.caseAttachment = ConvertHelper.convertList(caseAttachment, TestCycleCaseAttachmentRelDTO.class);
    }

    public List<TestCycleCaseDefectRelDTO> getDefects() {
        return caseDefect;
    }

    public void setDefects(List<TestCycleCaseDefectRelE> defects) {
        this.caseDefect = ConvertHelper.convertList(defects, TestCycleCaseDefectRelDTO.class);
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

    public List<TestCycleCaseStepDTO> getCycleCaseStep() {
        return cycleCaseStep;
    }

    public void setCycleCaseStep(List<TestCycleCaseStepE> cycleCaseStep) {
        this.cycleCaseStep = ConvertHelper.convertList(cycleCaseStep, TestCycleCaseStepDTO.class);
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
}
