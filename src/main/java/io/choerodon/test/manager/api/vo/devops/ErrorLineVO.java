package io.choerodon.test.manager.api.vo.devops;

import io.swagger.annotations.ApiModelProperty;

public class ErrorLineVO {

    @ApiModelProperty(value = "行编号")
    private Long lineNumber;
    @ApiModelProperty(value = "错误信息")
    private String errorMsg;

    public Long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Long lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
