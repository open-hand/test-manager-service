package io.choerodon.test.manager.infra.exception;

import io.choerodon.core.exception.CommonException;

public class IssueCreateException extends CommonException {

    private static final String ERROR_CREATE_ISSUE = "error.create.issue";

    public IssueCreateException(Object... parameters) {
        super(ERROR_CREATE_ISSUE, parameters);
    }

    public IssueCreateException(Throwable cause, Object... parameters) {
        super(ERROR_CREATE_ISSUE, cause, parameters);
    }

    public IssueCreateException(Throwable cause) {
        super(ERROR_CREATE_ISSUE, cause);
    }

}
