package io.choerodon.test.manager.api.dto;

/**
 * Created by zongw.lee@gmail.com on 31/10/2018
 */
public class ExcelReadMeOptionDTO {
    //Key为字段名，Boolean为是否必填
    private String filed;

    private Boolean required;

    public String getFiled() {
        return filed;
    }

    public void setFiled(String filed) {
        this.filed = filed;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public ExcelReadMeOptionDTO(String filed, Boolean required) {
        this.filed = filed;
        this.required = required;
    }
}
