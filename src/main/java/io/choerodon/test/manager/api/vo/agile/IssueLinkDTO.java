package io.choerodon.test.manager.api.vo.agile;


import io.choerodon.test.manager.infra.util.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 敏捷开发Issue链接
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:50:34
 */
public class IssueLinkDTO {

    @ApiModelProperty(value = "issue id")
    @Encrypt
    private Long issueId;

    @ApiModelProperty(value = "关联类型")
    @Encrypt
    private Long linkTypeId;

    @ApiModelProperty(value = "关联的工作项id")
    @Encrypt
    private Long linkedIssueId;

    @ApiModelProperty(value = "关联类型名称")
    private String linkTypeName;

    @ApiModelProperty(value = "ward")
    private String ward;

    @ApiModelProperty(value = "工作项编号")
    private String issueNum;

    @ApiModelProperty(value = "概要")
    private String summary;

    @ApiModelProperty(value = "类型编码")
    private String typeCode;

    @ApiModelProperty(value = "关联id")
    @Encrypt
    private Long linkId;

    @ApiModelProperty(value = "工作项类型")
    private IssueTypeVO issueTypeVO;

    @ApiModelProperty(value = "状态")
    private StatusVO statusVO;

    @ApiModelProperty(value = "优先级")
    private PriorityVO priorityVO;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getLinkTypeId() {
        return linkTypeId;
    }

    public void setLinkTypeId(Long linkTypeId) {
        this.linkTypeId = linkTypeId;
    }

    public Long getLinkedIssueId() {
        return linkedIssueId;
    }

    public void setLinkedIssueId(Long linkedIssueId) {
        this.linkedIssueId = linkedIssueId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLinkTypeName() {
        return linkTypeName;
    }

    public void setLinkTypeName(String linkTypeName) {
        this.linkTypeName = linkTypeName;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(String issueNum) {
        this.issueNum = issueNum;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public IssueTypeVO getIssueTypeVO() {
        return issueTypeVO;
    }

    public void setIssueTypeVO(IssueTypeVO issueTypeVO) {
        this.issueTypeVO = issueTypeVO;
    }

    public StatusVO getStatusMapDTO() {
        return statusVO;
    }

    public void setStatusMapDTO(StatusVO statusVO) {
        this.statusVO = statusVO;
    }

    public PriorityVO getPriorityVO() {
        return priorityVO;
    }

    public void setPriorityVO(PriorityVO priorityVO) {
        this.priorityVO = priorityVO;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}