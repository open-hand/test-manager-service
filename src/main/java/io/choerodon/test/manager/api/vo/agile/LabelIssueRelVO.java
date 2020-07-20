package io.choerodon.test.manager.api.vo.agile;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.test.manager.infra.util.StringUtil;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 敏捷开发Issue标签关联
 *
 * @author dinghuang123@gmail.com
 * @since 2018-05-14 21:31:22
 */
public class LabelIssueRelVO implements Serializable {

    @ApiModelProperty(value = "问题主键id")
    @Encrypt
    private Long issueId;

    @ApiModelProperty(value = "标签id")
    @Encrypt
    private Long labelId;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "标签名称")
    private String labelName;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }

}