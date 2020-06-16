package io.choerodon.test.manager.api.vo;

import io.choerodon.test.manager.infra.constant.EncryptKeyConstants;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author zhaotianxin
 * @since 2019/12/5
 */
public class CaseCompareRepVO {
    @ApiModelProperty("用例Id")
    @Encrypt(EncryptKeyConstants.TEST_CYCLE_CASE)
    private Long caseId;

    @ApiModelProperty("测试执行Id")
    private Long executeId;

    @ApiModelProperty("是否测试信息改变")
    private Boolean changeCase;

    @ApiModelProperty("是否测试步骤改变")
    private Boolean changeStep;

    @ApiModelProperty("是否附件改变")
    private Boolean changeAttach;

    @ApiModelProperty("是否同步到用例库")
    private Boolean syncToCase;

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Long getExecuteId() {
        return executeId;
    }

    public void setExecuteId(Long executeId) {
        this.executeId = executeId;
    }

    public Boolean getChangeCase() {
        return changeCase;
    }

    public void setChangeCase(Boolean changeCase) {
        this.changeCase = changeCase;
    }

    public Boolean getChangeStep() {
        return changeStep;
    }

    public void setChangeStep(Boolean changeStep) {
        this.changeStep = changeStep;
    }

    public Boolean getChangeAttach() {
        return changeAttach;
    }

    public void setChangeAttach(Boolean changeAttach) {
        this.changeAttach = changeAttach;
    }

    public Boolean getSyncToCase() {
        return syncToCase;
    }

    public void setSyncToCase(Boolean syncToCase) {
        this.syncToCase = syncToCase;
    }
}
