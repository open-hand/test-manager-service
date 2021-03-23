package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author huaxin.deng@hand-china.com 2021-03-22 19:58:53
 */
public class WebSocketMeaasgeVO {

    @ApiModelProperty(value = "主键ID")
    @Encrypt
    private Long userId;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "进度")
    private Double rate;

    @ApiModelProperty(value = "错误消息")
    private String error;

    public WebSocketMeaasgeVO(Long userId, String status, Double rate) {
        this.userId = userId;
        this.status = status;
        this.rate = rate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
