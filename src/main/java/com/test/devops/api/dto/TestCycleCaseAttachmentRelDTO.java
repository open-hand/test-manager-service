package com.test.devops.api.dto;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.Table;
import java.sql.Blob;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */

public class TestCycleCaseAttachmentRelDTO {
	private String attachmentType;
	private Long attachmentId;
	private String attachmentName;
	private Blob attachment;

	public String getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}

	public Long getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	public Blob getAttachment() {
		return attachment;
	}

	public void setAttachment(Blob attachment) {
		this.attachment = attachment;
	}
}
