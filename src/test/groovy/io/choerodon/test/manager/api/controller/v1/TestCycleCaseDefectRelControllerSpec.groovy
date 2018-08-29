package io.choerodon.test.manager.api.controller.v1

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.util.AopTestUtils
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by 842767365@qq.com on 8/22/18.
 */

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class TestCycleCaseDefectRelControllerSpec extends Specification {

    @Autowired
    TestCycleCaseDefectRelService testCycleCaseDefectRelService;


    def "Insert"() {
        given:
        TestCycleCaseDefectRelService serviceAOP = AopTestUtils.getTargetObject(testCycleCaseDefectRelService)
        TestCycleCaseDefectRelDTO defect = new TestCycleCaseDefectRelDTO(issueId: 99L, defectType: TestCycleCaseDefectRelE.CASE_STEP, defectLinkId: 999L)
        when:
        def result = serviceAOP.insert(defect, 99L)
        then:
        result.getId() != null
    }

    def "RemoveAttachment"() {
        given:
        TestCycleCaseDefectRelService serviceAOP = AopTestUtils.getTargetObject(testCycleCaseDefectRelService)

        TestCycleCaseDefectRelDTO defect = new TestCycleCaseDefectRelDTO(issueId: 99L, defectType: TestCycleCaseDefectRelE.CASE_STEP, defectLinkId: 299L)
        def result = serviceAOP.insert(defect, 99L)
        TestCycleCaseDefectRelDTO removeDto = new TestCycleCaseDefectRelDTO(id: result.getId())
        expect:
        serviceAOP.delete(removeDto, 11L)
    }
}
