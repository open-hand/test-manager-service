package io.choerodon.test.manager.api.vo;


import io.swagger.annotations.ApiModelProperty;

/**
 * @author superlee
 * @since 2021-10-26
 */
public class IssuePersonalSortVO {
    @ApiModelProperty(value = "属性")
    private String property;
    @ApiModelProperty(value = "排序规则")
    private String direction;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
