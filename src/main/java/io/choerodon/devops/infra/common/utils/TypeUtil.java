package io.choerodon.devops.infra.common.utils;

/**
 * Created by younger on 2018/3/29.
 */
public class TypeUtil {

    private TypeUtil() {
    }

    /**
     * obj转long类型
     */

    public static Long objToLong(Object obj) {
        if (obj == null) {
            return null;
        }
        return Long.valueOf(String.valueOf(obj));
    }

}
