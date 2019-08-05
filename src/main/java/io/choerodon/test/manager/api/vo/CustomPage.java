package io.choerodon.test.manager.api.vo;

import java.util.List;

import com.github.pagehelper.PageInfo;

public class CustomPage<E> extends PageInfo<E> {
    private Long[] allIdValues;

    public CustomPage(List<E> content, Long[] allNumber) {
        super(content);
        this.setTotal(allNumber.length);
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
