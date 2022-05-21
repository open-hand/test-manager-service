package io.choerodon.test.manager.api.vo.agile;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/11.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueInfoDTO {

	@ApiModelProperty(value = "issue id")
	private Long issueId;

	@ApiModelProperty(value = "工作项编号")
	private String issueNum;

	@ApiModelProperty(value = "概要")
	private String summary;

	public Long getIssueId() {
		return issueId;
	}

	public void setIssueId(Long issueId) {
		this.issueId = issueId;
	}

	public String getIssueNum() {
		return issueNum;
	}

	public void setIssueNum(String issueNum) {
		this.issueNum = issueNum;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getSummary() {
		return summary;
	}
}
