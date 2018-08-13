package io.choerodon.test.manager.domain.service.impl

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.domain.repository.TestStatusRepository
import io.choerodon.test.manager.domain.service.ITestStatusService
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE
import io.choerodon.test.manager.infra.repository.impl.TestStatusRepositoryImpl
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by 842767365@qq.com on 7/27/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ITestStatusServiceImplSpec extends Specification {

    ITestStatusService service
    TestStatusRepository repository
    TestStatusE statusE

    void setup() {
        repository=Mock(TestStatusRepository)
        statusE=new TestStatusE(testStatusRepository:repository)
        service=new ITestStatusServiceImpl(statusRepository:repository)
    }

    def "Query"() {
        when:
        service.query(statusE)
        then:
        1*repository.queryAllUnderProject(_)
    }

    def "Insert"() {
        when:
        service.insert(statusE)
        then:
        1*repository.insert(_)
    }

    def "Delete"() {
        when:
        service.delete(statusE)
        then:
        1*repository.queryOne(_)>>new TestStatusE(statusType:TestStatusE.STATUS_TYPE_CASE,statusId: 1)
        1*repository.validateDeleteCycleCaseAllow(_)
        0*repository.validateDeleteCaseStepAllow(_)
        1*repository.delete(_)
        when:
        service.delete(statusE)
        then:
        1*repository.queryOne(_)>>new TestStatusE(statusType:TestStatusE.STATUS_TYPE_CASE_STEP,statusId: 1)
        0*repository.validateDeleteCycleCaseAllow(_)
        1*repository.validateDeleteCaseStepAllow(_)
        1*repository.delete(_)
    }

    def "Update"() {
        when:
        service.update(statusE)
        then:
        1*repository.update(_)
    }

    def "GetDefaultStatusId"() {
        when:
        service.getDefaultStatusId(TestStatusE.STATUS_TYPE_CASE)
        then:
        1*repository.getDefaultStatus(_)
    }
}
