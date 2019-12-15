//package io.choerodon.test.manager.app.service.impl
//
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.app.service.TestAppInstanceLogService
//import io.choerodon.test.manager.infra.mapper.TestAppInstanceLogMapper
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.context.annotation.Import
//import spock.lang.Specification
//
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
//
///**
// User: wangxiang
// Date: 2019/9/5
// */
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//class TestAppInstanceLogServiceImplSpec extends Specification {
//
//    @Autowired
//    TestAppInstanceLogService testAppInstanceLogService
//
//    @Autowired
//    TestAppInstanceLogMapper mapper
//
//    def "QueryLog"() {
//
//        given:
//        Long logId = 1L
//
//        when:
//        String log = testAppInstanceLogService.queryLog(logId)
//
//        then:
//        log != null
//        noExceptionThrown()
//    }
//}
