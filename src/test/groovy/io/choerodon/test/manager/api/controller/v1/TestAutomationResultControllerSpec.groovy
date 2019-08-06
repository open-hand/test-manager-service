package io.choerodon.test.manager.api.controller.v1

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.TestAutomationResultDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestAutomationResultControllerSpec extends Specification {

    @Autowired
    private TestRestTemplate restTemplate

    @Shared
    private List<Long> automationResultIds = []

    def "createAutomationResult"() {
        given:
        TestAutomationResultDTO resultDTO = new TestAutomationResultDTO(result: "测试报告内容", objectVersionNumber: 1L)
        when: "增加一份新的测试报告"
        def response = restTemplate.exchange("/v1/projects/{project_id}/automation/result/change",
                HttpMethod.PUT,
                new HttpEntity<>(resultDTO),
                TestAutomationResultDTO,
                144L
        )
        then:
        response.statusCode == HttpStatus.CREATED
        with(response.body) {
            result == resultDTO.result
            objectVersionNumber == resultDTO.objectVersionNumber
        }
        automationResultIds << response.body.id
    }

    def "updateAutomationResult"() {
        given:
        TestAutomationResultDTO resultDTO = new TestAutomationResultDTO(id: automationResultIds[0], result: "测试报告内容1", objectVersionNumber: 1L)
        when: "更新测试报告内容"
        def response = restTemplate.exchange("/v1/projects/{project_id}/automation/result/change",
                HttpMethod.PUT,
                new HttpEntity<>(resultDTO),
                TestAutomationResultDTO,
                144L
        )
        then:
        response.statusCode == HttpStatus.CREATED
        with(response.body) {
            id == resultDTO.id
            result == resultDTO.result
            objectVersionNumber == resultDTO.objectVersionNumber + 1
        }
    }

    def "queryAutomationResult"() {
        when: "查询指定id的测试报告"
        def response = restTemplate.exchange("/v1/projects/{project_id}/automation/result/query/{id}",
                HttpMethod.GET,
                null,
                Map,
                144L, automationResultIds[0]
        )
        then:
        response.statusCode == HttpStatus.OK
        response.body.get("json") == "测试报告内容1"
    }

    def "removeAutomationResult"() {
        given:
        TestAutomationResultDTO resultDTO = new TestAutomationResultDTO(id: automationResultIds[0])
        when: "删除新增的测试报告"
        def response = restTemplate.exchange("/v1/projects/{project_id}/automation/result/remove",
                HttpMethod.DELETE,
                new HttpEntity<>(resultDTO),
                Void,
                144L
        )
        then:
        response.statusCode == HttpStatus.CREATED
    }
}
