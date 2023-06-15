package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by zongw.lee@gmail.com on 31/10/2018
 */
public class ExcelReadMeOptionVO {

    @ApiModelProperty(value = "字段名称")
    private String filed;

    @ApiModelProperty(value = "是否为必输")
    private Boolean required;

    @ApiModelProperty(value = "描述")
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExcelReadMeOptionVO(String filed, Boolean required) {
        this.filed = filed;
        this.required = required;
    }

    public ExcelReadMeOptionVO(String filed, Boolean required, String description) {
        this.filed = filed;
        this.required = required;
        this.description = description;
    }
}
