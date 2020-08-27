package io.choerodon.test.manager.api.vo;

import java.util.Date;
import java.util.List;
import io.choerodon.test.manager.api.vo.agile.IssueInfoDTO;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.dto.TestCaseStepDTO;
import io.choerodon.test.manager.infra.dto.UserMessageDTO;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author zhaotianxin
 * @since 2019/11/14
 */
public class TestCaseInfoVO {

    @ApiModelProperty(value = "用例Id")
    @Encrypt
    private Long caseId;

    @ApiModelProperty(value = "用例编号")
    private String caseNum;

    @ApiModelProperty(value = "概要")
    private String summary;

    @ApiModelProperty(value = "用例详情")
    private String description;

    @ApiModelProperty(value = "rank")
    private String rank;

    @ApiModelProperty(value = "文件夹")
    private String folder;

    @ApiModelProperty(value = "版本Id")
    private Long versionNum;

    @ApiModelProperty(value = "项目Id")
    private Long projectId;

    @ApiModelProperty(value = "创建人")
    private UserMessageDTO createUser;

    @ApiModelProperty(value = "最后修改人")
    private UserMessageDTO lastUpdateUser;

    @ApiModelProperty(value = "创建时间")
    private Date creationDate;

    @ApiModelProperty(value = "修改时间")
    private Date lastUpdateDate;

    @ApiModelProperty(value = "用例关联的问题链接")
    private List<IssueInfoDTO> issuesInfos;

    @ApiModelProperty(value = "用例关联的标签Id")
    private List<Long> lableIds;

    @ApiModelProperty(value = "测试用例关联的附件信息")
    private List<TestCaseAttachmentDTO> attachment;

    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "测试步骤")
    private List<TestCaseStepDTO> testCaseStepS;

    @ApiModelProperty(value = "优先级id")
    @Encrypt
    private Long priorityId;

    @ApiModelProperty(value = "优先级")
    private PriorityVO priorityVO;

    public PriorityVO getPriorityVO() {
        return priorityVO;
    }

    public void setPriorityVO(PriorityVO priorityVO) {
        this.priorityVO = priorityVO;
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

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
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

    public UserMessageDTO getCreateUser() {
        return createUser;
    }

    public void setCreateUser(UserMessageDTO createUser) {
        this.createUser = createUser;
    }

    public UserMessageDTO getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(UserMessageDTO lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<IssueInfoDTO> getIssuesInfos() {
        return issuesInfos;
    }

    public void setIssuesInfos(List<IssueInfoDTO> issuesInfos) {
        this.issuesInfos = issuesInfos;
    }

    public List<Long> getLableIds() {
        return lableIds;
    }

    public void setLableIds(List<Long> lableIds) {
        this.lableIds = lableIds;
    }

    public List<TestCaseAttachmentDTO> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<TestCaseAttachmentDTO> attachment) {
        this.attachment = attachment;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<TestCaseStepDTO> getTestCaseStepS() {

        return testCaseStepS;
    }

    public void setTestCaseStepS(List<TestCaseStepDTO> testCaseStepS) {
        this.testCaseStepS = testCaseStepS;
    }
}
