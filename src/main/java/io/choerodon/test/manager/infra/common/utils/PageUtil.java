package io.choerodon.test.manager.infra.common.utils;

import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import io.choerodon.base.domain.Sort;

/**
 * Created by WangZhe@choerodon.io on 2019-06-13.
 * Email: ettwz@hotmail.com
 */
public class PageUtil {
    public static String sortToSql(Sort sort) {
        if (sort == null) {
            return "";
        } else {
            return sort.toSql();
        }
    }
}
