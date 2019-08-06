package io.choerodon.test.manager.api.controller.v1

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.TestCaseStepDTO
import io.choerodon.test.manager.app.service.TestCaseStepService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCaseStepControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    TestCaseStepService caseStepService;

    @Shared
    List<ResponseEntity<TestCaseStepDTO>> stepIds=new ArrayList<>();


    def "ChangeStep"() {
        given:"创建一个将插入StepDTO"
        TestCaseStepDTO step=new TestCaseStepDTO(issueId:99L)
        HttpEntity<TestCaseStepDTO> requestEntity = new HttpEntity<>(step,null)

        when:"调用服务"
        def result = restTemplate.exchange("/v1/projects/{project_id}/case/step/change",
                HttpMethod.PUT,
                requestEntity,
                TestCaseStepDTO.class,
                144)
        then:
        result.body.stepId!=null
    }

    def "Query"() {
        when:
        def result = restTemplate.getForEntity("/v1/projects/{project_id}/case/step/query/{caseId}",List,144,99)
        then:
        result.body.size()==1
        and:
        stepIds.addAll(result.body)
        when:
        def result1 = restTemplate.getForEntity("/v1/projects/{project_id}/case/step/query/{caseId}",List,144,96)
        then:
        result1.body.size()==0
    }
    def "ChangeOneStep"() {
        given:"创建一个将更新StepDTO"
        TestCaseStepDTO step=stepIds.get(0)
        step.setTestData("test data")
        HttpEntity<TestCaseStepDTO> requestEntity = new HttpEntity<>(step,null)

        when:
        def result = restTemplate.exchange("/v1/projects/{project_id}/case/step/change",
                HttpMethod.PUT,
                requestEntity,
                TestCaseStepDTO.class,
                144)
        then:
        result.body.objectVersionNumber==2
    }

    def "Clone"() {
        given:"需要克隆步骤"
        TestCaseStepDTO step=stepIds.get(0)
        when:
        restTemplate.postForEntity("/v1/projects/{project_id}/case/step/clone",step,TestCaseStepDTO,144)
        then:
        def result = restTemplate.getForEntity("/v1/projects/{project_id}/case/step/query/{caseId}",List,144,99)
        expect:
        result.body.size()==2
        when:
        def result1 = restTemplate.postForEntity("/v1/projects/{project_id}/case/step/clone",new TestCaseStepDTO(stepId: 0),TestCaseStepDTO,144)
        then:
        result1.statusCode.value()==200
    }

    def "RemoveStep"() {
        given:
        TestCaseStepDTO step=new TestCaseStepDTO(issueId: 99L)
        HttpEntity<TestCaseStepDTO> requestEntity = new HttpEntity<>(step,null)

        when:
        restTemplate.exchange("/v1/projects/{project_id}/case/step",
                HttpMethod.DELETE,
                requestEntity,
                void,
                144)
        then:
        def result = restTemplate.getForEntity("/v1/projects/{project_id}/case/step/query/{caseId}",List,144,99)
        expect:
        result.body.size()==0
    }


}
