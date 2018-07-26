package io.choerodon.test.manager.infra.mapper

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDO
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseStepDO
import io.choerodon.test.manager.infra.dataobject.TestStatusDO
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by jialongZuo@hand-china.com on 7/24/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class TestStatusMapperSpec extends Specification {

    @Autowired
    TestStatusMapper mapper

    @Autowired
    TestCycleCaseMapper caseMapper;
    @Autowired
    TestCycleCaseStepMapper caseStepMapper;

    def "QueryAllUnderProject"() {
        given:
        TestStatusDO statusDO=new TestStatusDO()
        statusDO.setProjectId(new Long(2))
        statusDO.setStatusName("name")
        statusDO.setStatusColor("yellow")
        statusDO.setStatusType("CYCLE_CASE")
        TestStatusDO statusDO1=new TestStatusDO()

        when:
        def result1=mapper.queryAllUnderProject(statusDO1).size()
        mapper.insert(statusDO)
        def result2=mapper.queryAllUnderProject(statusDO).size()
        then:
        result2==result1+1
    }

    def "IfDeleteCycleCaseAllow"() {
        given:
        TestCycleCaseDO caseDO=new TestCycleCaseDO()
        caseDO.setCycleId(new Long(9999))
        caseDO.setExecutionStatus(new Long(3))
        caseDO.setIssueId(new Long(999))
        caseDO.setRank("0|c00000:")
        caseMapper.insert(caseDO)
        when:
        def result1 = mapper.ifDeleteCycleCaseAllow(new Long(3))
        then:
        result1==1
        when:
        caseMapper.deleteByPrimaryKey(caseDO.getExecuteId())
        def result2 = mapper.ifDeleteCycleCaseAllow(new Long(3))
        then:
        result2==0
    }

    def "IfDeleteCaseStepAllow"() {
        given:
        TestCycleCaseStepDO stepDO=new TestCycleCaseStepDO()
        stepDO.setExecuteId(new Long(9999))
        stepDO.setStepStatus(new Long(4))
        stepDO.setStepId(new Long(999))
        caseStepMapper.insert(stepDO)
        when:
        def result1 = mapper.ifDeleteCaseStepAllow(new Long(4))
        then:
        result1==1
        when:
        caseStepMapper.deleteByPrimaryKey(stepDO.getExecuteStepId())
        def result2 = mapper.ifDeleteCaseStepAllow(new Long(4))
        then:
        result2==0
    }

    def "GetDefaultStatus"() {
        expect:
        result== mapper.getDefaultStatus(statusType)
        where:
        statusType      |   result
        "CYCLE_CASE"    |   1
        "CASE_STEP"     |   4
        "任意其他字符"    |   null
    }
}
