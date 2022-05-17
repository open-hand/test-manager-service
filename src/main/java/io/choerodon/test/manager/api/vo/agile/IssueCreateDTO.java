package io.choerodon.test.manager.api.vo.agile;

import io.choerodon.test.manager.infra.dto.TestCaseStepProDTO;
import io.choerodon.test.manager.infra.util.StringUtil;
import io.choerodon.test.manager.infra.dto.TestCaseLinkDTO;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 */
public class IssueCreateDTO {
    @ApiModelProperty(value = "用例id")
    @Encrypt
    private Long caseId;

    @ApiModelProperty(value = "类型编码")
    private String typeCode;

    @ApiModelProperty(value = "概要")
    private String summary;

    @ApiModelProperty(value = "优先级编码")
    private String priorityCode;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "经办人id")
    @Encrypt
    private Long assigneeId;

    @ApiModelProperty(value = "报告人id")
    @Encrypt
    private Long reporterId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "所属史诗id")
    @Encrypt(ignoreValue = "0")
    private Long epicId;
    @ApiModelProperty(value = "冲刺id")
    @Encrypt(ignoreValue = "0")
    private Long sprintId;
    @ApiModelProperty(value = "优先级id")
    @Encrypt
    private Long priorityId;
    @ApiModelProperty(value = "工作项类型id")
    @Encrypt
    private Long issueTypeId;
    @ApiModelProperty(value = "文件夹id")
    @Encrypt
    private Long folderId;

    @ApiModelProperty(value = "关联工作项编号")
    private String relateIssueNums;

    @ApiModelProperty(value = "自定义编号")
    private String customNum;

    @ApiModelProperty(value = "关联版本列表")
    private List<VersionIssueRelVO> versionIssueRelVOList;

    @ApiModelProperty(value = "关联标签列表")
    private List<LabelIssueRelVO> labelIssueRelVOList;

    @ApiModelProperty(value = "关联模块列表")
    private List<ComponentIssueRelVO> componentIssueRelVOList;

    @ApiModelProperty(value = "关联测试用例列表")
    private List<TestCaseLinkDTO> testCaseLinkDTOList;

    @ApiModelProperty(value = "原始预估时间")
    private BigDecimal remainingTime;

    @ApiModelProperty(value = "剩余预估时间")
    private BigDecimal estimateTime;

    @ApiModelProperty(value = "所属史诗名称")
    private String epicName;

    @ApiModelProperty(value = "测试步骤列表")
    private List<TestCaseStepProDTO> testCaseStepProList;

    @ApiModelProperty(value = "工作项关联列表")
    private List<IssueLinkCreateVO> issueLinkCreateVOList;

    @ApiModelProperty(value = "关联工作项id")
    @Encrypt(ignoreValue = "0")
    private Long relateIssueId;

    @ApiModelProperty(value = "主要负责人id")
    @Encrypt
    private Long mainResponsibleId;

    @ApiModelProperty(value = "环境")
    private String environment;

    @ApiModelProperty(value = "预计开始时间")
    private Date estimatedStartTime;

    @ApiModelProperty(value = "预计结束时间")
    private Date estimatedEndTime;

    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "版本编号")
    private Long versionNum;

    @ApiModelProperty(value = "用例编号")
    private String caseNum;

    @ApiModelProperty(value = "文件夹路径")
    private String folderPath;

    public List<IssueLinkCreateVO> getIssueLinkCreateVOList() {
        return issueLinkCreateVOList;
    }

    public void setIssueLinkCreateVOList(List<IssueLinkCreateVO> issueLinkCreateVOList) {
        this.issueLinkCreateVOList = issueLinkCreateVOList;
    }

    public List<TestCaseLinkDTO> getTestCaseLinkDTOList() {
        return testCaseLinkDTOList;
    }

    public void setTestCaseLinkDTOList(List<TestCaseLinkDTO> testCaseLinkDTOList) {
        this.testCaseLinkDTOList = testCaseLinkDTOList;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPriorityCode() {
        return priorityCode;
    }

    public void setPriorityCode(String priorityCode) {
        this.priorityCode = priorityCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public List<VersionIssueRelVO> getVersionIssueRelVOList() {
        return versionIssueRelVOList;
    }

    public void setVersionIssueRelVOList(List<VersionIssueRelVO> versionIssueRelVOList) {
        this.versionIssueRelVOList = versionIssueRelVOList;
    }

    public List<LabelIssueRelVO> getLabelIssueRelVOList() {
        return labelIssueRelVOList;
    }

    public void setLabelIssueRelVOList(List<LabelIssueRelVO> labelIssueRelVOList) {
        this.labelIssueRelVOList = labelIssueRelVOList;
    }

    public List<ComponentIssueRelVO> getComponentIssueRelVOList() {
        return componentIssueRelVOList;
    }

    public void setComponentIssueRelVOList(List<ComponentIssueRelVO> componentIssueRelVOList) {
        this.componentIssueRelVOList = componentIssueRelVOList;
    }

    public BigDecimal getRemainingTime() {
        return remainingTime;
    }

    public BigDecimal getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(BigDecimal estimateTime) {
        this.estimateTime = estimateTime;
    }

    public void setRemainingTime(BigDecimal remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setIssueTypeId(Long issueTypeId) {
        this.issueTypeId = issueTypeId;
    }

    public Long getIssueTypeId() {
        return issueTypeId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getRelateIssueNums() {
        return relateIssueNums;
    }

    public void setRelateIssueNums(String relateIssueNums) {
        this.relateIssueNums = relateIssueNums;
    }

    public String getCustomNum() {
        return customNum;
    }

    public void setCustomNum(String customNum) {
        this.customNum = customNum;
    }

    public List<TestCaseStepProDTO> getTestCaseStepProList() {
        return testCaseStepProList;
    }

    public void setTestCaseStepProList(List<TestCaseStepProDTO> testCaseStepProList) {
        this.testCaseStepProList = testCaseStepProList;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Long getRelateIssueId() {
        return relateIssueId;
    }

    public void setRelateIssueId(Long relateIssueId) {
        this.relateIssueId = relateIssueId;
    }

    public Long getMainResponsibleId() {
        return mainResponsibleId;
    }

    public void setMainResponsibleId(Long mainResponsibleId) {
        this.mainResponsibleId = mainResponsibleId;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Date getEstimatedStartTime() {
        return estimatedStartTime;
    }

    public void setEstimatedStartTime(Date estimatedStartTime) {
        this.estimatedStartTime = estimatedStartTime;
    }

    public Date getEstimatedEndTime() {
        return estimatedEndTime;
    }

    public void setEstimatedEndTime(Date estimatedEndTime) {
        this.estimatedEndTime = estimatedEndTime;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(Long versionNum) {
        this.versionNum = versionNum;
    }

    public String getCaseNum() {
        return caseNum;
    }

    public void setCaseNum(String caseNum) {
        this.caseNum = caseNum;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
}
