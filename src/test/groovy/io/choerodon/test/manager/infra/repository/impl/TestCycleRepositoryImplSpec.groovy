package io.choerodon.test.manager.infra.repository.impl

import com.github.pagehelper.PageInfo
import io.choerodon.core.exception.CommonException
import io.choerodon.base.domain.PageRequest
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE
import io.choerodon.test.manager.infra.mapper.TestCycleMapper
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import java.util.stream.Stream

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by 842767365@qq.com on 7/26/18.
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
        given:
        TestCycleE cycleE = new TestCycleE(cycleId: new Long(1), versionId: 12L, cycleName: "name")
        cycleE.projectId=1L
        when:
        repository.insert(cycleE)
        then:
        1 * mapper.validateCycle(_) >> 0L
        1*mapper.insert(_)>>1
        when:
        1 * mapper.validateCycle(_) >> 0L
        repository.insert(cycleE)
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
        1*mapper.select(_)>>new ArrayList<>()
        when:
        repository.queryOne()
        then:
        1*mapper.selectOne(_)
    }

    def "QueryBar"() {
        when:
        repository.queryBar(1L,[1,2,3]as Long[],null)
        then:
        1*mapper.query(_,_,_)>>new ArrayList<>()
        when:
        repository.queryBar(1L,new Long[4],null)
        then:
        0*mapper.query(_,_,_)
        when:
        repository.queryBar(1L,null,null)
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
