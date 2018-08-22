package io.choerodon.test.manager.app.service.impl

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory
import org.assertj.core.util.Lists
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by jialongZuo@hand-china.com on 8/22/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class TestCycleCaseDefectRelServiceImplSpec extends Specification {

    TestCycleCaseDefectRelService service
    TestCaseService client

    void setup() {
        client = Mock(TestCaseService)
        service = new TestCycleCaseDefectRelServiceImpl(testCaseService: client)
    }

    def "PopulateDefectInfo"() {
        given:
        TestCycleCaseDefectRelDTO defect1 = new TestCycleCaseDefectRelDTO(issueId: 1)
        TestCycleCaseDefectRelDTO defect2 = new TestCycleCaseDefectRelDTO(issueId: 2)
        TestCycleCaseDefectRelDTO defect3 = new TestCycleCaseDefectRelDTO(issueId: 3)

        when:
        service.populateDefectInfo(new ArrayList<>(), 1L)
        then:
        0 * client.getIssueInfoMap(_, _, false)
        when:
        service.populateDefectInfo(Lists.newArrayList(defect1, defect2, defect3), 1L)
        then:
        1 * client.getIssueInfoMap(_, _, false) >> new HashMap<>()
    }

    def "PopulateCycleCaseDefectInfo"() {
        given:
        TestCycleCaseDefectRelE defect1 = new TestCycleCaseDefectRelE()
        defect1.setIssueId(1L)
        defect1.setDefectType(TestCycleCaseDefectRelE.CYCLE_CASE)
        defect1.setDefectName("name1")
        defect1.setDefectLinkId(111L)
        TestCycleCaseDTO dto = new TestCycleCaseDTO()
        List list = new ArrayList();
        list.add(defect1)
        dto.setDefects(list)
        when:
        service.populateCycleCaseDefectInfo(Lists.newArrayList(dto), 1L)
        then:
        1 * client.getIssueInfoMap(_, _, false) >> new HashMap<>()
    }

    def "PopulateCaseStepDefectInfo"() {
        given:
        TestCycleCaseDefectRelE defect1 = new TestCycleCaseDefectRelE()
        defect1.setIssueId(1L)
        defect1.setDefectType(TestCycleCaseDefectRelE.CASE_STEP)
        defect1.setDefectName("name1")
        defect1.setDefectLinkId(111L)
        TestCycleCaseStepDTO dto = new TestCycleCaseStepDTO()
        List list = new ArrayList();
        list.add(defect1)
        dto.setDefects(list)
        when:
        service.populateCaseStepDefectInfo(Lists.newArrayList(dto), 1L)
        then:
        1 * client.getIssueInfoMap(_, _, false) >> new HashMap<>()
    }

}
