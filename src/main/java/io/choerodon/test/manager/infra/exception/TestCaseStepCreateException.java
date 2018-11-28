package io.choerodon.test.manager.infra.exception;

import io.choerodon.core.exception.CommonException;

public class TestCaseStepCreateException extends CommonException {

    private static final String ERROR_CREATE_TEST_CASE_STEP = "error.create.test.case.step";

    public TestCaseStepCreateException(Object... parameters) {
        super(ERROR_CREATE_TEST_CASE_STEP, parameters);
    }

    public TestCaseStepCreateException(Throwable cause, Object... parameters) {
        super(ERROR_CREATE_TEST_CASE_STEP, cause, parameters);
    }

    public TestCaseStepCreateException(Throwable cause) {
        super(ERROR_CREATE_TEST_CASE_STEP, cause);
    }

}
