package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author jiaxu.cui@hand-china.com 2020/9/10 下午8:57
 */
public class TestFileLoadHistoryWebsocketVO {

    @ApiModelProperty(value = "主键ID")
    @Encrypt
    private Long id;

    @ApiModelProperty(value = "状态")
    private Long status;

    @ApiModelProperty(value = "进度")
    private Double rate;

    @ApiModelProperty(value = "错误消息编码")
    private String code;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
