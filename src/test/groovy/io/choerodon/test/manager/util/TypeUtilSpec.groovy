package io.choerodon.test.manager.util

import io.choerodon.devops.infra.common.utils.TypeUtil
import spock.lang.Specification

class TypeUtilSpec extends Specification {

    def "ObjToLong"() {
        when:
        Long res= TypeUtil.objToLong(null)
        then:
        res==null
        when:
        Long res1= TypeUtil.objToLong('1')
        then:
        res1==1L

    }

}
