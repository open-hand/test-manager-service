package io.choerodon.test.manager.api.vo.agile;


import io.choerodon.test.manager.infra.util.StringUtil;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 敏捷开发Issue链接
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:50:34
 */
public class IssueLinkDTO {

    @Encrypt
	private Long issueId;

    @Encrypt
	private Long linkTypeId;

    @Encrypt
	private Long linkedIssueId;

	private String linkTypeName;

	private String ward;

	private String issueNum;

	private String summary;

	private String typeCode;

    @Encrypt
	private Long linkId;

	private IssueTypeVO issueTypeVO;

	private StatusVO statusVO;

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