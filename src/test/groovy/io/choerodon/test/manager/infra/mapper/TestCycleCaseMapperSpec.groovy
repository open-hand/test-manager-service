package io.choerodon.test.manager.infra.mapper

import io.choerodon.test.manager.IntegrationTestConfiguration
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
 * Created by jialongZuo@hand-china.com on 7/25/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCycleCaseMapperSpec extends Specification {

    @Autowired
    TestCycleCaseMapper mapper

    @Autowired
    TestCycleMapper cycleMapper

    def "init"() {
        given:
        TestCycleDO cycleDO1=new TestCycleDO()
        cycleDO1.setCycleName("循环1")
        cycleDO1.setVersionId(new Long(88))
        cycleDO1.setType("cycle")
        cycleMapper.insert(cycleDO1)
        cycleDO1.setCycleName("循环2")
        cycleDO1.setCycleId(null)
        cycleMapper.insert(cycleDO1)

        TestCycleCaseDO caseDO = new TestCycleCaseDO()
        caseDO.setCycleId(new Long(1))
        caseDO.setExecutionStatus(new Long(1))
        caseDO.setIssueId(new Long(999))
        caseDO.setRank("0|c00000:")

        TestCycleCaseDO caseDO1 = new TestCycleCaseDO()
        caseDO1.setCycleId(new Long(1))
        caseDO1.setExecutionStatus(new Long(2))
        caseDO1.setIssueId(new Long(998))
        caseDO1.setRank("0|c00004:")

        TestCycleCaseDO caseDO2 = new TestCycleCaseDO()
        caseDO2.setCycleId(new Long(2))
        caseDO2.setExecutionStatus(new Long(2))
        caseDO2.setIssueId(new Long(999))
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
        param                                             ||          result
        [new Long(1)] as Long[]                     ||          2
        [new Long(2)] as Long[]                     ||          1
        [new Long(1),new Long(2)] as Long[]   ||          2

    }

    def "CountCaseSum"() {
        expect:
        mapper.countCaseSum(param).longValue()==result
        where:
        param                            ||          result
        [new Long(1)] as Long[]    ||          2
        [new Long(2)] as Long[]    ||          1
    }

    def "ValidateCycleCaseInCycle"() {
        given:
        TestCycleCaseDO dao=new TestCycleCaseDO(
                cycleId:new Long(1),
                issueId: params
        )

        expect:
        mapper.validateCycleCaseInCycle(dao)==result
        where:
        params  ||  result
        999     ||  1
        989     ||  0
    }

    def "GetLastedRank"() {
        expect:
        mapper.getLastedRank(cycleId)==result
        where:
        cycleId ||  result
        1       ||  "0|c00004:"
        2       ||  "0|c00000:"
    }
}
