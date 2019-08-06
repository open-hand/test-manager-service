package io.choerodon.test.manager.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by zongw.lee@gmail.com on 31/10/2018
 */
public class ExcelReadMeOptionDTO {

    @ApiModelProperty(value = "字段名称")
    private String filed;

    @ApiModelProperty(value = "是否为必输")
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
