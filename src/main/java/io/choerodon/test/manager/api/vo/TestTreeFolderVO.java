package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

/**
 * @author: 25499
 * @date: 2019/11/14 13:02
 * @description:
 */
public class TestTreeFolderVO {
    @ApiModelProperty(value = "测试文件夹id")
    @Encrypt
    private Long id;
    @Encrypt
    @ApiModelProperty(value = "子文件夹id集合")
    private List<Long> children;
    @ApiModelProperty(value = "是否有子文件夹")
    private Boolean hasChildren;
    @ApiModelProperty(value = "是否扩展")
    private Boolean isExpanded;
    @ApiModelProperty(value = "子文件夹是否加载中")
    private Boolean isChildrenLoading;
    @ApiModelProperty(value = "文件夹信息")
    private TestIssueFolderVO issueFolderVO;
    @ApiModelProperty(value = "是否有用例")
    private Boolean hasCase;
    @ApiModelProperty(value = "是否是第一层级")
    private Boolean topLevel;
    @ApiModelProperty(value = "用例数量")
    private Long caseCount;
    @Encrypt
    @ApiModelProperty(value = "计划id")
    private Long  planId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getChildren() {
        return children;
    }

    public void setChildren(List<Long> children) {
        this.children = children;
    }

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Boolean getExpanded() {
        return isExpanded;
    }

    public void setExpanded(Boolean expanded) {
        isExpanded = expanded;
    }

    public Boolean getChildrenLoading() {
        return isChildrenLoading;
    }

    public void setChildrenLoading(Boolean childrenLoading) {
        isChildrenLoading = childrenLoading;
    }

    public TestIssueFolderVO getIssueFolderVO() {
        return issueFolderVO;
    }

    public void setIssueFolderVO(TestIssueFolderVO issueFolderVO) {

        this.issueFolderVO = issueFolderVO;
    }

    public Boolean getHasCase() {
        return hasCase;
    }

    public void setHasCase(Boolean hasCase) {
        this.hasCase = hasCase;
    }

    public Boolean getTopLevel() {
        return topLevel;
    }

    public void setTopLevel(Boolean topLevel) {
        this.topLevel = topLevel;
    }


    public Long getCaseCount() {
        return caseCount;
    }

    public void setCaseCount(Long caseCount) {
        this.caseCount = caseCount;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    @Override
    public String toString() {
        return "TestTreeFolderVO{" +
                "id=" + id +
                ", children=" + children +
                ", hasChildren=" + hasChildren +
                ", isExpanded=" + isExpanded +
                ", isChildrenLoading=" + isChildrenLoading +
                ", issueFolderVO=" + issueFolderVO +
                ", hasCase=" + hasCase +
                '}';
    }

}
