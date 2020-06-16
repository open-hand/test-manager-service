package io.choerodon.test.manager.api.vo;

import java.util.Date;

import io.choerodon.test.manager.infra.constant.EncryptKeyConstants;
import io.swagger.annotations.ApiModelProperty;

import io.choerodon.test.manager.api.vo.agile.UserDO;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */

public class TestCycleCaseHistoryVO {

    @ApiModelProperty(value = "测试执行ID")
    @Encrypt(/**EncryptKeyConstants.TEST_CYCLE_CASE_HISTORY**/)
    private Long executeId;

    @ApiModelProperty(value = "旧值")
    private String oldValue;

    @ApiModelProperty(value = "新值")
    private String newValue;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "最后更新人ID")
    private Long lastUpdatedBy;

    @ApiModelProperty(value = "最后更新日期")
    private Date lastUpdateDate;

    @ApiModelProperty(value = "更新人详情")
    private UserDO user;

    @ApiModelProperty(value = "改动的字段")
    private String field;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Long getExecuteId() {
        return executeId;
    }

    public void setExecuteId(Long executeId) {
        this.executeId = executeId;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public UserDO getUser() {
        return user;
    }

    public void setUser(UserDO user) {
        this.user = user;
    }
}
