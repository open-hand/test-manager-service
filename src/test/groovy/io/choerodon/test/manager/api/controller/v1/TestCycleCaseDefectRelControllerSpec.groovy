package io.choerodon.test.manager.api.controller.v1

import io.choerodon.agile.api.dto.IssueDTO
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient
import org.assertj.core.util.Lists
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.util.AopTestUtils
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by 842767365@qq.com on 8/22/18.
 */

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCycleCaseDefectRelControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    TestCaseService caseService;

    @Shared
    Long defectId

    def "Insert"() {
        given:
        IssueDTO mockResult=new IssueDTO(issueNum: "name1")
        TestCycleCaseDefectRelDTO defect = new TestCycleCaseDefectRelDTO(issueId: 99L, defectType: TestCycleCaseDefectRelE.CASE_STEP, defectLinkId: 999L)
        when:
        def result = restTemplate.postForEntity("/v1/projects/{project_id}/defect", Lists.newArrayList(defect),List,144)
        defectId = result.getBody().get(0).getAt("id")
        then:
        1*caseService.queryIssue(_,_)>>new ResponseEntity<>(mockResult, HttpStatus.CREATED);
        result.getBody().get(0).getAt("id") != null
        result.statusCode.is2xxSuccessful()
    }

    def "RemoveDefect"() {
        given:
        IssueDTO mockResult=new IssueDTO(issueNum: "name1")
//        TestCycleCaseDefectRelService serviceAOP = AopTestUtils.getTargetObject(testCycleCaseDefectRelService)
        when:
        restTemplate.delete("/v1/projects/{project_id}/defect/delete/{defectId}",144L,defectId)
        then:
        1*caseService.queryIssue(_,_)>>new ResponseEntity<>(mockResult, HttpStatus.CREATED);
    }
}
