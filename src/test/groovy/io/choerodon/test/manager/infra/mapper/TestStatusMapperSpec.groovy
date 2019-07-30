package io.choerodon.test.manager.infra.mapper

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO
import io.choerodon.test.manager.infra.dto.TestCycleCaseStepDTO
import io.choerodon.test.manager.infra.dto.TestStatusDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by 842767365@qq.com on 7/24/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestStatusMapperSpec extends Specification {

    @Autowired
    TestStatusMapper mapper

    @Autowired
    TestCycleCaseMapper caseMapper;
    @Autowired
    TestCycleCaseStepMapper caseStepMapper;

    def "QueryAllUnderProject"() {
        given:
        TestStatusDTO statusDO = new TestStatusDTO()
        statusDO.setProjectId(new Long(1))
        statusDO.setStatusName("未执行")
        statusDO.setStatusColor("yellow")
        statusDO.setStatusType("CYCLE_CASE")

        TestStatusDTO statusDO1 = new TestStatusDTO()
        statusDO1.setProjectId(new Long(1))
        statusDO1.setStatusName("未执行")
        statusDO1.setStatusColor("yellow")
        statusDO1.setStatusType("CASE_STEP")

        when:
        def beforeSize = mapper.selectAll().size()
        mapper.insert(statusDO)
        mapper.insert(statusDO1)
        def afterSize = mapper.selectAll().size()
        then:
        afterSize == beforeSize + 2
    }

    def "IfDeleteCycleCaseAllow"() {
        given:
        TestCycleCaseDTO caseDO = new TestCycleCaseDTO()
        caseDO.setCycleId(new Long(9999))
        caseDO.setExecutionStatus(new Long(36))
        caseDO.setIssueId(new Long(999))
        caseDO.setRank("0|c00000:")
        caseMapper.insert(caseDO)
        when:
        def result1 = mapper.ifDeleteCycleCaseAllow(new Long(36))
        then:
        result1 == 1
        when:
        caseMapper.deleteByPrimaryKey(caseDO.getExecuteId())
        def result2 = mapper.ifDeleteCycleCaseAllow(new Long(36))
        then:
        result2 == 0
    }

    def "IfDeleteCaseStepAllow"() {
        given:
        TestCycleCaseStepDTO stepDO = new TestCycleCaseStepDTO()
        stepDO.setExecuteId(new Long(9999))
        stepDO.setStepStatus(new Long(9))
        stepDO.setStepId(new Long(999))
        caseStepMapper.insert(stepDO)
        when:
        def result1 = mapper.ifDeleteCaseStepAllow(new Long(9))
        then:
        result1 == 1
        when:
        caseStepMapper.deleteByPrimaryKey(stepDO.getExecuteStepId())
        def result2 = mapper.ifDeleteCaseStepAllow(new Long(9))
        then:
        result2 == 0
    }

}
