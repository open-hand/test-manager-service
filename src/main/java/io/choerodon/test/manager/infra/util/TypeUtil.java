package io.choerodon.test.manager.infra.util;

import java.util.List;

public class TypeUtil {
    private TypeUtil() {
    }

    public static Long[] longsToArray(List<Long> values) {
        Long[] result = new Long[values.size()];
        int i = 0;
        for (Long l : values)
            result[i++] = l;
        return result;
    }

    public static Long objToLong(Object obj) {
        if (obj == null) {
            return null;
        }
        return Long.valueOf(String.valueOf(obj));
    }
}
