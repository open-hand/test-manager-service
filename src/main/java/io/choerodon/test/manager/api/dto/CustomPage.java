package io.choerodon.test.manager.api.dto;

import io.choerodon.core.domain.Page;

import java.util.List;

public class CustomPage<E> extends Page<E> {
    private Long[] allIdValues;

    public CustomPage(List<E> content,Long[] allNumber){
        this.setContent(content);
        this.setNumberOfElements(allNumber.length);
        allIdValues=allNumber;
    }
    public Long[] getIds() {
        return allIdValues;
    }

    public void setIds(Long[] ids) {
        this.allIdValues = ids;
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
