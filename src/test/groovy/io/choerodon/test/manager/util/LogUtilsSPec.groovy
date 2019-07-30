package io.choerodon.test.manager.util

import io.choerodon.test.manager.infra.util.LogUtils
import org.apache.commons.logging.Log
import spock.lang.Specification

class LogUtilsSPec extends Specification {


    def "ErrorLog"() {
        Log log=Mock(Log);
        when:
        LogUtils.errorLog(log,'log')
        then:
        1*log.error(_)
    }

    def "WarnLog"() {
        Log log=Mock(Log);
        when:
        LogUtils.warnLog(log,'log',new Exception())
        then:
        1*log.warn(_,_)
    }
}
