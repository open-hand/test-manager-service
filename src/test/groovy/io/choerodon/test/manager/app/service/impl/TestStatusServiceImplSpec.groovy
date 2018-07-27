package io.choerodon.test.manager.app.service.impl

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.TestStatusDTO
import io.choerodon.test.manager.app.service.TestStatusService
import io.choerodon.test.manager.domain.repository.TestStatusRepository
import io.choerodon.test.manager.domain.service.ITestStatusService
import io.choerodon.test.manager.domain.service.impl.ITestStatusServiceImpl
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE
import org.assertj.core.util.Lists
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by jialongZuo@hand-china.com on 7/27/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class TestStatusServiceImplSpec extends Specification {

    TestStatusService service
    ITestStatusService iService

    void setup() {
        iService=Mock(ITestStatusService)
        service=new TestStatusServiceImpl(iTestStatusService:iService)
    }

    def "Query"() {
        given:
        TestStatusDTO statusDTO=new TestStatusDTO(statusId: 1,statusColor: "red")
        when:
        def result= service.query(statusDTO)
        then:
        1*iService.query(_)>> Lists.newArrayList(new TestStatusE(statusId: 1),new TestStatusE(statusId: 1,statusColor: "red"))
        and:
        result.size()==2
    }

    def "Insert"() {
        given:
        TestStatusDTO statusDTO=new TestStatusDTO(statusColor: "red")
        when:
        def result= service.insert(statusDTO)
        then:
        1*iService.insert(_)>> new TestStatusE(statusId: 1,statusColor: "red")
        and:
        result.getStatusColor()=="red"
    }

    def "Delete"() {
        given:
        TestStatusDTO statusDTO=new TestStatusDTO(statusId: 1,statusColor: "red")
        when:
        service.delete(statusDTO)
        then:
        1*iService.delete(_)
    }

    def "Update"() {
        given:
        TestStatusDTO statusDTO=new TestStatusDTO(statusId: 1,statusColor: "red")
        when:
        def result= service.update(statusDTO)
        then:
        1*iService.update(_)>> new TestStatusE(statusId: 1,statusColor: "red")
        and:
        result.getStatusColor()=="red"
    }
}
