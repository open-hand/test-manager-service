package io.choerodon.test.manager.infra.exception;

import io.choerodon.core.exception.CommonException;

public class FeignReceiveException extends CommonException {

    public FeignReceiveException(String code, Object... parameters) {
        super(code, parameters);
    }

    public FeignReceiveException(String code, Throwable cause, Object... parameters) {
        super(code, cause, parameters);
    }

    public FeignReceiveException(String code, Throwable cause) {
        super(code, cause);
    }

    public FeignReceiveException(Throwable cause, Object... parameters) {
        super(cause, parameters);
    }
}
