package io.choerodon.test.manager.infra.util;

import java.util.function.Function;
import java.util.function.Supplier;

import io.choerodon.core.exception.CommonException;

public class DBValidateUtil {

    public static <T> void executeAndvalidateUpdateNum(Function<T, Integer> function, T param, int expectResult, String errorMessage) {
        if (function.apply(param).intValue() != expectResult) {
            throw new CommonException(errorMessage);
        }
    }

    public static void executeAndvalidateUpdateNum(Supplier function, int expectResult, String errorMessage) {
        if ((int) function.get() != expectResult) {
            throw new CommonException(errorMessage);
        }
    }

    private DBValidateUtil() {
    }
}
