package io.choerodon.test.manager.api.vo.agile;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.infra.util.StringUtil;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Transient;
import java.util.List;
import java.util.Map;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/5/31
 */
public class SearchDTO {

    /**
     * 输入查询参数
     */
    @ApiModelProperty(value = "输入查询参数")
    private Map<String, Object> searchArgs;

    /**
     * 过滤查询参数
     */
    @ApiModelProperty(value = "过滤查询参数")
    private Map<String, Object> advancedSearchArgs;

    /**
     * 关联查询参数
     */
    @ApiModelProperty(value = "关联查询参数")
    private Map<String, Object> otherArgs;

    @ApiModelProperty(value = "执行状态")
    @Transient
    private Long[] executionStatus;

    @ApiModelProperty(value = "缺陷状态")
    @Transient
    private String[] defectStatus;

    @ApiModelProperty(value = "输入查询参数")
    private String content;

    @ApiModelProperty(value = "页码")
    private int page;

    @ApiModelProperty(value = "大小")
    private int size;

    @ApiModelProperty(value = "排序")
    private Sort sort;

    @ApiModelProperty(value = "应用类型")
    private List<String> applyTypes;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    /**
     * issueNum+summary模糊搜索
     */
    private List<String> contents;

    public Map<String, Object> getSearchArgs() {
        return searchArgs;
    }

    public void setSearchArgs(Map<String, Object> searchArgs) {
        this.searchArgs = searchArgs;
    }

    public Map<String, Object> getAdvancedSearchArgs() {
        return advancedSearchArgs;
    }

    public void setAdvancedSearchArgs(Map<String, Object> advancedSearchArgs) {
        this.advancedSearchArgs = advancedSearchArgs;
    }

    public Map<String, Object> getOtherArgs() {
        return otherArgs;
    }

    public void setOtherArgs(Map<String, Object> otherArgs) {
        this.otherArgs = otherArgs;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public Long[] getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(Long[] executionStatus) {
        this.executionStatus = executionStatus;
    }

    public String[] getDefectStatus() {
        return defectStatus;
    }

    public void setDefectStatus(String[] defectStatus) {
        this.defectStatus = defectStatus;
    }

    public List<String> getApplyTypes() {
        return applyTypes;
    }

    public void setApplyTypes(List<String> applyTypes) {
        this.applyTypes = applyTypes;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
