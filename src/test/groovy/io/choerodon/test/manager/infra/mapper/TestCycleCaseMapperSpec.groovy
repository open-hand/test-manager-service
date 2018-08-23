package io.choerodon.test.manager.infra.mapper

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.domain.repository.TestCycleRepository
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDO
import io.choerodon.test.manager.infra.dataobject.TestCycleDO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by 842767365@qq.com on 7/25/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCycleCaseMapperSpec extends Specification {

    @Autowired
    TestCycleCaseMapper mapper

    @Autowired
    TestCycleRepository cycleMapper
    @Shared
    TestCycleE cycleDO1 = new TestCycleE()
    @Shared
    TestCycleE cycleDO2 = new TestCycleE()
    @Shared
    TestCycleCaseDO caseDO = new TestCycleCaseDO()
    @Shared
    TestCycleCaseDO caseDO1 = new TestCycleCaseDO()
    @Shared
    TestCycleCaseDO caseDO2 = new TestCycleCaseDO()

    def "initEnv"() {
        given:
        cycleDO1.setCycleName("循环1")
        cycleDO1.setVersionId(new Long(88))
        cycleDO1.setType("cycle")
        cycleDO2.setCycleName("循环2")
        cycleDO2.setVersionId(new Long(88))
        cycleDO2.setType("cycle")
        cycleDO1 = cycleMapper.insert(cycleDO1)
        cycleDO2 = cycleMapper.insert(cycleDO2)

        caseDO.setCycleId(cycleDO1.getCycleId())
        caseDO.setExecutionStatus(new Long(1))
        caseDO.setIssueId(new Long(9999))
        caseDO.setRank("0|c00000:")

        caseDO1.setCycleId(cycleDO1.getCycleId())
        caseDO1.setExecutionStatus(new Long(2))
        caseDO1.setIssueId(new Long(9989))
        caseDO1.setRank("0|c00004:")

        caseDO2.setCycleId(cycleDO2.getCycleId())
        caseDO2.setExecutionStatus(new Long(2))
        caseDO2.setIssueId(new Long(9999))
        caseDO2.setRank("0|c00000:")
        mapper.insert(caseDO)
        mapper.insert(caseDO1)
        mapper.insert(caseDO2)
    }

    def "QueryWithAttachAndDefect"() {
    }

    def "Filter"() {
    }

    def "QueryByIssue"() {
    }

    def "QueryCycleCaseForReporter"() {
    }

    def "CountCaseNotRun"() {
    }

    def "CountCaseNotPlain"() {
        expect:
        mapper.countCaseNotPlain(param)==result
        where:
        param                                                    ||          result
        [cycleDO1.getCycleId()] as Long[]                        || 2
        [cycleDO2.getCycleId()] as Long[]                        || 1
        [cycleDO1.getCycleId(), cycleDO2.getCycleId()] as Long[] || 2

    }

    def "CountCaseSum"() {
        expect:
        mapper.countCaseSum(param).longValue()==result
        where:
        param                             ||          result
        [cycleDO1.getCycleId()] as Long[] || 2
        [cycleDO2.getCycleId()] as Long[] || 1
    }

    def "ValidateCycleCaseInCycle"() {
        given:
        TestCycleCaseDO dao=new TestCycleCaseDO(
                cycleId: cycleDO1.getCycleId(),
                issueId: params
        )

        expect:
        mapper.validateCycleCaseInCycle(dao)==result
        where:
        params ||  result
        9999   || 1
        9899   || 0
    }

    def "GetLastedRank"() {
        expect:
        mapper.getLastedRank(cycleId)==result
        where:
        cycleId               ||  result
        cycleDO1.getCycleId() || "0|c00004:"
        cycleDO2.getCycleId() || "0|c00000:"
    }

    def "deleteEnv"(){
        expect:
        cycleMapper.delete(cycleDO1)
        cycleMapper.delete(cycleDO1)
        mapper.deleteByPrimaryKey(caseDO.getExecuteId())==1
        mapper.deleteByPrimaryKey(caseDO1.getExecuteId())==1
        mapper.deleteByPrimaryKey(caseDO2.getExecuteId())==1

    }
}
