package io.choerodon.test.manager.api.vo;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.swagger.annotations.ApiModelProperty;

public class CustomPage<E> extends Page<E> {
    @ApiModelProperty(value = "allIdValues")
    private Long[] allIdValues;

    public CustomPage(List<E> content, Long[] allNumber) {
        this.setTotalElements(allNumber.length);
        allIdValues = allNumber;
    }

    public Long[] getAllIdValues() {
        return allIdValues;
    }

    public void setAllIdValues(Long[] allIdValues) {
        this.allIdValues = allIdValues;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
