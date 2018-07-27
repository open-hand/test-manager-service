package io.choerodon.test.manager.infra.repository.impl

import io.choerodon.agile.infra.common.utils.RankUtil
import io.choerodon.agile.infra.common.utils.arilerank.AgileRank
import io.choerodon.core.exception.CommonException
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.domain.repository.TestCycleCaseStepRepository
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseStepDO
import io.choerodon.test.manager.infra.mapper.TestCycleCaseStepMapper
import org.assertj.core.util.Lists
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by jialongZuo@hand-china.com on 7/26/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class TestCycleCaseStepRepositoryImplSpec extends Specification {

    TestCycleCaseStepMapper mapper
    TestCycleCaseStepRepository repository

    void setup() {
        mapper=Mock(TestCycleCaseStepMapper)
        repository=new TestCycleCaseStepRepositoryImpl(testCycleCaseStepMapper:mapper)
    }


    def "Insert"() {
        when:
        repository.insert(new TestCycleCaseStepE())
        then:
        1*mapper.insert(_)>>1
        when:
        repository.insert(new TestCycleCaseStepE())
        then:
        1*mapper.insert(_)>>0
        thrown(CommonException)
        when:
        repository.insert(null)
        then:
        thrown(IllegalArgumentException)
    }

    def "Delete"() {
        when:
        repository.delete(new TestCycleCaseStepE())
        then:
        1*mapper.delete(_)
        when:
        repository.delete(null)
        then:
        thrown(IllegalArgumentException)
    }

    def "Update"() {
        when:
        repository.update(new TestCycleCaseStepE(executeStepId: 1))
        then:
        1*mapper.updateByPrimaryKeySelective(_)>>1
        1*mapper.selectByPrimaryKey(_)
        when:
        repository.update(new TestCycleCaseStepE(executeStepId: 1))
        then:
        1*mapper.updateByPrimaryKeySelective(_)>>0
        thrown(CommonException)
        when:
        repository.update(null)
        then:
        thrown(IllegalArgumentException)

    }

    def "Query"() {
        when:
        repository.query(new TestCycleCaseStepE(executeId: 1),new PageRequest(1,5))
        then:
        1*mapper.queryWithTestCaseStep(_,_,_)>> Lists.newArrayList(new TestCycleCaseStepDO())
        1*mapper.queryWithTestCaseStep_count(_)>>3L
        when:
        repository.query(new TestCycleCaseStepE(executeId: 1),new PageRequest(1,5))
        then:
        1*mapper.queryWithTestCaseStep(_,_,_)>> Lists.newArrayList()
        0*mapper.queryWithTestCaseStep_count(_)
    }


    def "QueryOne"() {
        when:
        repository.query(null)
        then:
        1*mapper.select(_)
        when:
        repository.queryOne(null)
        then:
        1*mapper.selectOne(_)
    }

    def "te"(){
        expect:
        RankUtil.between()
    }

    def "QueryCycleCaseForReporter"() {
        when:
        repository.queryCycleCaseForReporter(null)
        then:
        thrown(IllegalArgumentException)
        when:
        repository.queryCycleCaseForReporter([1,2,3] as Long[])
        then:
        1*mapper.queryCycleCaseForReporter(_)
    }
}
