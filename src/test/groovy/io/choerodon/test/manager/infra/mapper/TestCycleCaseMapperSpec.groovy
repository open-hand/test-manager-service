package io.choerodon.test.manager.infra.mapper

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by jialongZuo@hand-china.com on 7/25/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class TestCycleCaseMapperSpec extends Specification {

    @Autowired
    TestCycleCaseMapper mapper

    def setup() {
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
        caseDO2.setExecutionStatus(new Long(3))
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
    }

    def "CountCaseSum"() {
    }

    def "ValidateCycleCaseInCycle"() {
    }

    def "GetLastedRank"() {
        given:
        Long param1=new Long(1)
        Long param2=new Long(2)
        expect:
        mapper.getLastedRank(cycleId)==result
        where:
        cycleId || result
        param1  ||  "0|c00004:"
        param2  ||  "0|c00000:"
    }
}
