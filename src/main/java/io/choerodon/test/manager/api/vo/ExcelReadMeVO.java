package io.choerodon.test.manager.api.vo;

/**
 * Created by zongw.lee@gmail.com on 31/10/2018
 */
public class ExcelReadMeVO {
    private String header = "字段是否为必填项";

    private String operation = "请至下一页，填写信息";

    private String bodyHeader = "是否必填/字段";

    private String necessary = "必填项";

    private String optional = "选填项";

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getBodyHeader() {
        return bodyHeader;
    }

    public void setBodyHeader(String bodyHeader) {
        this.bodyHeader = bodyHeader;
    }

    public String getNecessary() {
        return necessary;
    }

    public void setNecessary(String necessary) {
        this.necessary = necessary;
    }

    public String getOptional() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }
}
