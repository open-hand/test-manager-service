package io.choerodon.test.manager.api.vo.agile;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.test.manager.infra.util.StringUtil;

/**
 * @author dinghuang123@gmail.com
 * @since 2019/1/22
 */
public class IssueLinkTypeSearchDTO {

    @ApiModelProperty(value = "搜索内容，传字符串集合")
    private List<String> contents;

    @ApiModelProperty(value = "链接名称")
    private String linkName;

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
