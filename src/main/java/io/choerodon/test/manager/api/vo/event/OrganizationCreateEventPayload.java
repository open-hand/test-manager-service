package io.choerodon.test.manager.api.vo.event;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/18
 */
public class OrganizationCreateEventPayload {

    @ApiModelProperty(value = "组织id")
    private Long id;
    @ApiModelProperty(value = "组织名称")
    private String name;
    @ApiModelProperty(value = "组织编码")
    private String code;
    @ApiModelProperty(value = "用户id")
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "OrganizationCreateEventPayload{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", userId=" + userId +
                '}';
    }

}
