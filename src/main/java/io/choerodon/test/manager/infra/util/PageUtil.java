package io.choerodon.test.manager.infra.util;

import java.util.ArrayList;
import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.core.domain.PageInfo;
import io.choerodon.core.utils.PageableHelper;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * Created by WangZhe@choerodon.io on 2019-06-13.
 * Email: ettwz@hotmail.com
 */
public class PageUtil {
    public static String sortToSql(Sort sort) {
        if (sort == null) {
            return "";
        } else {
            return PageableHelper.getSortSql(sort);
        }
    }

    public static <T> Page<T> buildPageInfoWithPageInfoList(Page<?> pageInfo, List<T> list) {
        Page<T> page = new Page<>();
        page.setNumber(pageInfo.getNumber());
        page.setSize(pageInfo.getSize());
        page.setNumberOfElements(pageInfo.getNumberOfElements());
        page.setTotalElements(pageInfo.getTotalElements());
        page.setTotalPages(pageInfo.getTotalPages());
        page.setContent(list);
        return page;
    }
    /**
     * 装配Page对象
     *
     * @param all         包含所有内容的列表
     * @param pageRequest 分页参数
     * @return Page
     */
    public static <T> Page<T> createPageFromList(List<T> all, PageRequest pageRequest) {
        Page<T> result = new Page<>();
        boolean queryAll = pageRequest.getPage() < 0 || pageRequest.getSize() == 0;
        result.setSize(queryAll ? all.size() : pageRequest.getSize());
        result.setNumber(pageRequest.getPage());
        result.setTotalElements(all.size());
        result.setTotalPages(queryAll ? (all.size() == 0 ? 0 : 1) : ((int) (Math.ceil(all.size() / (pageRequest.getSize() * 1.0)))));
        int fromIndex = pageRequest.getSize() * pageRequest.getPage();
        int size;
        if (all.size() >= fromIndex) {
            if (all.size() <= fromIndex + pageRequest.getSize()) {
                size = all.size() - fromIndex;
            } else {
                size = pageRequest.getSize();
            }
            result.setContent(queryAll ? all : all.subList(fromIndex, fromIndex + size));
        } else {
            size = 0;
            result.setSize(queryAll ? all.size() : size);
            result.setContent(new ArrayList<>());
        }
        return result;
    }

    public static Page empty(PageRequest pageRequest) {
        PageInfo pageInfo = new PageInfo(pageRequest.getPage(), pageRequest.getSize());
        return new Page(new ArrayList(), pageInfo, 0);
    }
}
