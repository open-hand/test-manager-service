package io.choerodon.test.manager.app.service.impl

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.TestCaseStepService
import io.choerodon.test.manager.domain.service.ITestCaseStepService
import io.choerodon.test.manager.domain.service.ITestStatusService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by 842767365@qq.com on 7/27/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class TestCaseStepServiceImplSpec extends Specification {

    TestCaseStepService service;
    ITestCaseStepService stepService
    ITestStatusService statusService
    def setup(){
        given:
        stepService=Mock(ITestCaseStepService)
        statusService=Mock(ITestStatusService)
        service=new TestCaseStepServiceImpl(iTestCaseStepService:stepService,iTestStatusService:statusService)
    }

    def "RemoveStep"() {
        when:
        service.removeStep(null)
        then:
        1*stepService.removeStep(_)
    }

    def "Query"() {
        when:
        service.query(null)
        then:
        1*stepService.query(_)
    }

    def "ChangeStep"() {
    }

    def "BatchInsertStep"() {
    }

    def "Clone"() {
    }
}
