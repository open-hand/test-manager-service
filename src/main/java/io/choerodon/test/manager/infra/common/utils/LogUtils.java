package io.choerodon.test.manager.infra.common.utils;

import org.apache.commons.logging.Log;

public class LogUtils {

    private LogUtils() {
    }

    public static void debugLog(Log log, String msg) {
        if (log.isDebugEnabled()) {
            log.debug(msg);
        }
    }

    public static void errorLog(Log log, Object msg) {
        log.error(msg);
    }

    public static void infoLog(Log log,String msg){
        log.info(msg);
    }
}
