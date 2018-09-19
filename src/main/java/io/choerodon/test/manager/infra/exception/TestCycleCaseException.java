package io.choerodon.test.manager.infra.exception;

import io.choerodon.core.exception.CommonException;

public class TestCycleCaseException extends CommonException {
    public static final String ERROR_UN_SUPPORT_DB_TYPE="error.unSupport.db.type";

    public TestCycleCaseException(String code, Object... parameters) {
        super(code, parameters);
    }

    public TestCycleCaseException(String code, Throwable cause, Object... parameters) {
        super(code, cause, parameters);
    }

    public TestCycleCaseException(String code, Throwable cause) {
        super(code, cause);
    }

    public TestCycleCaseException(Throwable cause, Object... parameters) {
        super(cause, parameters);
    }
}
