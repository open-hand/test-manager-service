package io.choerodon.test.manager.api.vo;

import java.util.List;

import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.dto.TestCaseLabelDTO;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author zhaotianxin
 * @since 2019/11/14
 */
public class TestCaseVO {
    @ApiModelProperty(value = "用例Id")
    private Long caseId;
    @ApiModelProperty(value = "用例编号")
    private String caseNum;
    @ApiModelProperty(value = "概要")
    private String summary;
    @ApiModelProperty(value = "用例详情")
    private String description;
    @ApiModelProperty(value = "rank")
    private String rank;
    @ApiModelProperty(value = "文件夹Id")
    @Encrypt
    private Long folderId;
    @ApiModelProperty(value = "版本Id")
    private Long versionId;
    @ApiModelProperty(value = "项目Id")
    private Long projectId;
    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "用例步骤")
    private List<TestCaseStepVO> caseStepVOS;
    @ApiModelProperty(value = "标签")
    private List<TestCaseLabelDTO> labels;
    @ApiModelProperty(value = "测试用例关联的附件")
    private List<TestCaseAttachmentDTO> attachment;
    @ApiModelProperty(value = "文件夹名字")
    private String folderName;
    @ApiModelProperty(value = "优先级")
    @Encrypt
    private Long priorityId;
    @ApiModelProperty(value = "优先级")
    private PriorityVO priorityVO;

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public PriorityVO getPriorityVO() {
        return priorityVO;
    }

    public void setPriorityVO(PriorityVO priorityVO) {
        this.priorityVO = priorityVO;
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

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<TestCaseStepVO> getCaseStepVOS() {
        return caseStepVOS;
    }

    public void setCaseStepVOS(List<TestCaseStepVO> caseStepVOS) {
        this.caseStepVOS = caseStepVOS;
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
