package io.choerodon.test.manager.api.vo.agile;

import io.choerodon.test.manager.infra.constant.EncryptKeyConstants;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/6/15
 */
public class ProjectInfoVO {

    @ApiModelProperty(value = "主键id")
    @Encrypt(/**EncryptKeyConstants.TEST_PROJECT_INFO**/)
    private Long infoId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "项目code")
    private String projectCode;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    public Long getInfoId() {
        return infoId;
    }

    public void setInfoId(Long infoId) {
        this.infoId = infoId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
