package io.choerodon.test.manager.api.controller.v1

import io.choerodon.core.exception.CommonException
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.TestStatusDTO
import io.choerodon.test.manager.app.service.TestStatusService
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestStatusControllerSpec extends Specification {
    @Autowired
    TestStatusService testStatusService;

    def "Query"() {
        given:
        TestStatusDTO status = new TestStatusDTO()
        status.setStatusId(52l)
        status.setProjectId(0l)
        status.setStatusType(TestStatusE.STATUS_TYPE_CASE)
        when:
        List<TestStatusDTO> result = testStatusService.query(status)
        then:
        result.size() == 3
    }

    def "Insert"() {
        given:
        TestStatusDTO status = new TestStatusDTO()
        status.setDescription("突发情况")
        status.setStatusName("突发")
        status.setStatusColor("rgba(0,191,165,21)")
        status.setStatusType(TestStatusE.STATUS_TYPE_CASE)
        status.setProjectId(1l)
        when:
        TestStatusDTO status1 = testStatusService.insert(status)
        then:
        status1 !=null
        StringUtils.equals(status1.getStatusName(),"突发")
        when:
        testStatusService.insert(new TestStatusDTO(statusId:1L) )
        then:
        thrown(CommonException)

    }


    def "Update"() {
        given:
        TestStatusDTO statusNew=new TestStatusDTO();
        statusNew.setStatusId(7L)
        statusNew.setStatusType(TestStatusE.STATUS_TYPE_CASE)
        statusNew.setDescription("未通过1")
        statusNew.setStatusName("未通过")
        statusNew.setStatusColor("rgba(0,191,165,31)")
        statusNew.setObjectVersionNumber(1L)
        when:
        TestStatusDTO status2 = testStatusService.update(statusNew)
        then:
        StringUtils.equals(status2.getStatusName(),"未通过")
        when:
        testStatusService.update(new TestStatusDTO(statusId: 999L,statusName: "aa") )
        then:
        thrown(CommonException)
    }

    def "Delete"() {
        TestStatusDTO statusNew = new TestStatusDTO()
        statusNew.setStatusType(TestStatusE.STATUS_TYPE_CASE_STEP)
        statusNew.setDescription("未通过2")
        statusNew.setStatusName("未通过2")
        statusNew.setStatusColor("rgba(0,191,165,36)")

        expect:
        testStatusService.delete(testStatusService.insert(statusNew))
    }
}
