package io.choerodon.test.manager.infra.util;

import java.util.ArrayList;
import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import io.choerodon.web.util.PageableHelper;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;

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

    public static PageInfo buildPageInfoWithPageInfoList(PageInfo pageInfo, List list) {
        Page page = new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize());
        page.setTotal(pageInfo.getTotal());
        page.addAll(list);

        return page.toPageInfo();
    }
    /**
     * 装配Page对象
     *
     * @param all         包含所有内容的列表
     * @param Pageable 分页参数
     * @return PageInfo
     */
    public static <T> PageInfo<T> createPageFromList(List<T> all, Pageable Pageable) {
        PageInfo<T> result = new PageInfo<>();
        boolean queryAll = Pageable.getPageNumber() == 0 || Pageable.getPageSize() == 0;
        result.setPageSize(queryAll ? all.size() : Pageable.getPageSize());
        result.setPageNum(Pageable.getPageNumber());
        result.setTotal(all.size());
        result.setPages(queryAll ? 1 : (int) (Math.ceil(all.size() / (Pageable.getPageSize() * 1.0))));
        int fromIndex = Pageable.getPageSize() * (Pageable.getPageNumber() - 1);
        int size;
        if (all.size() >= fromIndex) {
            if (all.size() <= fromIndex + Pageable.getPageSize()) {
                size = all.size() - fromIndex;
            } else {
                size = Pageable.getPageSize();
            }
            result.setSize(queryAll ? all.size() : size);
            result.setList(queryAll ? all : all.subList(fromIndex, fromIndex + result.getSize()));
        } else {
            size = 0;
            result.setSize(queryAll ? all.size() : size);
            result.setList(new ArrayList<>());
        }
        return result;
    }
}
