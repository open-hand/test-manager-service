package io.choerodon.test.manager.api.controller.v1

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.TestAppInstanceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestAppInstanceControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    TestAppInstanceService instanceService


    def "QueryValues"() {
    }

    def "Deploy"() {
    }

    def "DeployBySchedule"() {
    }
}
