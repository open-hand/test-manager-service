package io.choerodon.test.manager.infra.repository.impl

import io.choerodon.core.domain.Page
import io.choerodon.core.exception.CommonException
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE
import io.choerodon.test.manager.infra.mapper.TestCycleMapper
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import java.util.stream.Stream

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by jialongZuo@hand-china.com on 7/26/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class TestCycleRepositoryImplSpec extends Specification {

    TestCycleMapper mapper
    TestCycleRepositoryImpl repository
    void setup() {
        mapper=Mock(TestCycleMapper)
        repository=new TestCycleRepositoryImpl(cycleMapper:mapper)
    }

    def "Insert"() {
        when:
        repository.insert()
        then:
        1*mapper.insert(_)>>1
        when:
        repository.insert()
        then:
        1*mapper.insert(_)>>0
        thrown(CommonException)
    }

    def "Delete"() {
        when:
        repository.delete()
        then:
        1*mapper.delete(_)
    }

    def "Update"() {
        given:
        TestCycleE cycleE=new TestCycleE(cycleId: new Long(1))
        when:
        repository.update(cycleE)
        then:
        1 * mapper.updateByPrimaryKey(_)>>1
        1 * mapper.selectByPrimaryKey(_)
        when:
        repository.update(cycleE)
        then:
        1 * mapper.updateByPrimaryKey(_)>>0
        thrown(CommonException)
    }

    def "Query"() {
        given:
        TestCycleE cycleE=new TestCycleE(cycleId: new Long(1))
        when:
        repository.query(cycleE)
        then:
        1*mapper.select(_)
        when:
        repository.query(cycleE,new PageRequest())
        then:
        1*mapper.select(_)>>new Page<>()
        when:
        repository.queryOne()
        then:
        1*mapper.selectOne(_)
    }

    def "QueryBar"() {
        when:
        repository.queryBar([1,2,3]as Long[])
        then:
        1*mapper.query(_)>>new ArrayList<>()
        when:
        repository.queryBar(new Long[4])
        then:
        0*mapper.query(_)
        when:
        repository.queryBar(null)
        then:
        thrown(IllegalArgumentException)
    }


    def "SelectCyclesInVersions"() {

        when:
        repository.selectCyclesInVersions([1,2,3]as Long[])
        then:
        1*mapper.selectCyclesInVersions(_)>>new ArrayList<>()
        when:
        repository.selectCyclesInVersions(new Long[4])
        then:
        0*mapper.selectCyclesInVersions(_)
        when:
        repository.selectCyclesInVersions(null)
        then:
        thrown(IllegalArgumentException)
    }
}
