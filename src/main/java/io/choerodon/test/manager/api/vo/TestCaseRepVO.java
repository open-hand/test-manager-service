package io.choerodon.test.manager.api.vo;

import java.util.Date;
import java.util.List;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.dto.TestCaseLabelDTO;
import io.choerodon.test.manager.infra.dto.UserMessageDTO;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhaotianxin
 * @since 2019/11/15
 */
public class TestCaseRepVO {
    @ApiModelProperty(value = "用例Id")
    private Long caseId;

    @ApiModelProperty(value = "用例编号")
    private String caseNum;

    @ApiModelProperty(value = "概要")
    private String summary;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "项目Id")
    private Long projectId;

    @ApiModelProperty(value = "文件夹Id")
    private Long folderId;

    @ApiModelProperty(value = "创建人")
    private UserMessageDTO createUser;

    @ApiModelProperty(value = "最后修改人")
    private UserMessageDTO lastUpdateUser;

    @ApiModelProperty(value = "创建时间")
    private Date creationDate;

    @ApiModelProperty(value = "修改时间")
    private Date lastUpdateDate;

    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "标签")
    private List<TestCaseLabelDTO> labels;

    @ApiModelProperty(value = "附件")
    private List<TestCaseAttachmentDTO> attachment;

    @ApiModelProperty(value = "文件夹名称")
    private String folderName;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public UserMessageDTO getCreateUser() {
        return createUser;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<TestCaseLabelDTO> getLabels() {
        return labels;
    }

    public void setLabels(List<TestCaseLabelDTO> labels) {
        this.labels = labels;
    }

    public List<TestCaseAttachmentDTO> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<TestCaseAttachmentDTO> attachment) {
        this.attachment = attachment;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
