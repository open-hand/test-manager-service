package io.choerodon.test.manager.infra.repository.impl

import io.choerodon.core.exception.CommonException
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE
import io.choerodon.test.manager.infra.dataobject.TestStatusDO
import io.choerodon.test.manager.infra.mapper.TestStatusMapper
import org.assertj.core.util.Lists
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by 842767365@qq.com on 7/26/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class TestStatusRepositoryImpSpec extends Specification {

    def "QueryAllUnderProject"() {
        given:
        TestStatusE status = new TestStatusE(statusId: 1,statusType: "CASE_STEP",statusColor: "red",statusName: "red")
        TestStatusMapper mapper=Mock(TestStatusMapper)
        TestStatusRepositoryImpl repository=new TestStatusRepositoryImpl(testStatusMapper:mapper)

        when:
        TestStatusE statusE=repository.queryAllUnderProject(status).get(0)
        then:
        1*mapper.queryAllUnderProject(_)>> Lists.newArrayList(new TestStatusDO(statusName: "name1"))
        statusE.getStatusName().equals("name1")
    }

    def "QueryOne"() {
        given:
        TestStatusMapper mapper=Mock(TestStatusMapper)
        TestStatusRepositoryImpl repository=new TestStatusRepositoryImpl(testStatusMapper:mapper)
        Long param=new Long(1)

        when:
        TestStatusE statusE =repository.queryOne(param)
        then:
        1 * mapper.selectByPrimaryKey(_)>>new TestStatusDO(statusId: 1,statusType: "CASE_STEP",statusColor: "red",statusName: "red")
        statusE.getStatusType()=="CASE_STEP"
        when:
        repository.queryOne(null)
        then:
        thrown(IllegalArgumentException)
    }

    def "Insert"() {
        given:
        TestStatusE status = new TestStatusE(statusType: "CASE_STEP",statusColor: "red",statusName: "red")
        TestStatusMapper mapper=Mock(TestStatusMapper)
        TestStatusRepositoryImpl repository=new TestStatusRepositoryImpl(testStatusMapper:mapper)
        when:
        repository.insert(status)
        then:
        1 * mapper.insert(_)>>1
        when:
        repository.insert(status)
        then:
        1 * mapper.insert(_)>>0
        thrown(CommonException)
        when:
        repository.insert(null)
        then:
        thrown(CommonException)
    }

    def "Delete"() {
        given:
        TestStatusE status = new TestStatusE(statusId: 1,statusType: "CASE_STEP",statusColor: "red",statusName: "red")
        TestStatusMapper mapper=Mock(TestStatusMapper)
        TestStatusRepositoryImpl repository=new TestStatusRepositoryImpl(testStatusMapper:mapper)
        when:
        repository.delete(status)
        then:
        1*mapper.delete(_)
        when:
        repository.delete(null)
        then:
        thrown(IllegalArgumentException)
    }

    def "Update"() {
        given:
        TestStatusE status = new TestStatusE(statusId: 1,statusType: "CASE_STEP",statusColor: "red",statusName: "red")
        TestStatusMapper mapper=Mock(TestStatusMapper)
        TestStatusRepositoryImpl repository=new TestStatusRepositoryImpl(testStatusMapper:mapper)

        when:
        repository.update(status)
        then:
        1 * mapper.updateByPrimaryKey(_)>>1
        1 * mapper.selectByPrimaryKey(_)
        when:
        repository.update(status)
        then:
        1 * mapper.updateByPrimaryKey(_)>>0
        thrown(CommonException)
        when:
        repository.update(null)
        then:
        thrown(IllegalArgumentException)
    }

    def "ValidateDeleteCycleCaseAllow"() {
        given:
        TestStatusMapper mapper=Mock(TestStatusMapper)
        TestStatusRepositoryImpl repository=new TestStatusRepositoryImpl(testStatusMapper:mapper)
        when:
        repository.validateDeleteCycleCaseAllow(1)
        then:
        1*mapper.ifDeleteCycleCaseAllow(_)>>0
        when:
        repository.validateDeleteCycleCaseAllow(1)
        then:
        1*mapper.ifDeleteCycleCaseAllow(_)>>1
        thrown(CommonException)
    }

    def "ValidateDeleteCaseStepAllow"() {
        given:
        TestStatusMapper mapper=Mock(TestStatusMapper)
        TestStatusRepositoryImpl repository=new TestStatusRepositoryImpl(testStatusMapper:mapper)
        when:
        repository.validateDeleteCaseStepAllow(1)
        then:
        1*mapper.ifDeleteCaseStepAllow(_)>>0
        when:
        repository.validateDeleteCaseStepAllow(1)
        then:
        1*mapper.ifDeleteCaseStepAllow(_)>>1
        thrown(CommonException)
    }

    def "GetDefaultStatus"() {
        given:
        TestStatusMapper mapper=Mock(TestStatusMapper)
        TestStatusRepositoryImpl repository=new TestStatusRepositoryImpl(testStatusMapper:mapper)
        when:
        repository.getDefaultStatus("1")
        then:
        1*mapper.getDefaultStatus(_)>>1
        when:
        repository.getDefaultStatus(null)
        then:
        thrown(IllegalArgumentException)
    }
}
