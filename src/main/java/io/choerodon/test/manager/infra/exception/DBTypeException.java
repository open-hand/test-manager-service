package io.choerodon.test.manager.infra.exception;

import io.choerodon.core.exception.CommonException;

public class DBTypeException extends CommonException {

    public static final String UNKNOWN_DB_TYPE="unknown.db.type";


    public DBTypeException(String code, Object... parameters) {
        super(code, parameters);
    }

    public DBTypeException(String code, Throwable cause, Object... parameters) {
        super(code, cause, parameters);
    }

    public DBTypeException(String code, Throwable cause) {
        super(code, cause);
    }

    public DBTypeException(Throwable cause, Object... parameters) {
        super(cause, parameters);
    }
}
