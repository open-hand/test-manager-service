package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhaotianxin
 * @date 2021-05-07 14:11
 */
public class ListLayoutVO {
    @Encrypt
    @ApiModelProperty(value = "id")
    private Long id;

    @NotNull(message = "error.layout.applyType.null")
    @ApiModelProperty(value = "应用类型")
    private String applyType;

    @Encrypt
    @ApiModelProperty(value = "用户id")
    private Long userId;
    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "组织id")
    private Long organizationId;
    @ApiModelProperty(value = "列布局")
    private List<ListLayoutColumnRelVO> listLayoutColumnRelVOS;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<ListLayoutColumnRelVO> getListLayoutColumnRelVOS() {
        return listLayoutColumnRelVOS;
    }

    public void setListLayoutColumnRelVOS(List<ListLayoutColumnRelVO> listLayoutColumnRelVOS) {
        this.listLayoutColumnRelVOS = listLayoutColumnRelVOS;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
