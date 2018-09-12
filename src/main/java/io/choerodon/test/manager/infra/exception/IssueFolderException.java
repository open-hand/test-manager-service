package io.choerodon.test.manager.infra.exception;

import io.choerodon.core.exception.CommonException;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public class IssueFolderException extends CommonException {

    public static final String ERROR_FOLDER_TYPE="illegal.folder.type";

    public static final String ERROR_INSERT="error.insert.param";

    public static final String ERROR_UPDATE="error.update.param";

    public IssueFolderException(String code, Object... parameters) {
        super(code, parameters);
    }

    public IssueFolderException(String code, Throwable cause, Object... parameters) {
        super(code, cause, parameters);
    }

    public IssueFolderException(String code, Throwable cause) {
        super(code, cause);
    }

    public IssueFolderException(Throwable cause, Object... parameters) {
        super(cause, parameters);
    }
}
