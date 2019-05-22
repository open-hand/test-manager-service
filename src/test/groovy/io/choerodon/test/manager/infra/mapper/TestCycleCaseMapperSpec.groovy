package io.choerodon.test.manager.infra.mapper

import io.choerodon.core.exception.CommonException
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.domain.repository.TestCycleRepository
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDO
import io.choerodon.test.manager.infra.dataobject.TestCycleDO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
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
    TestRestTemplate restTemplate;

    @Autowired
    TestCaseService testCaseService

    @Autowired
    UserService userService

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
        cycleDO1.setProjectId(11L)
        cycleDO2.setCycleName("循环2")
        cycleDO2.setVersionId(new Long(88))
        cycleDO2.setType("cycle")
        cycleDO2.setProjectId(11L)
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


    def "CountCaseNotPlain"() {
        when:
        def result=restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/countCaseNotPlain",Long.class,143)
        then:
        1*testCaseService.getVersionIds(_)>>[88L,78L]
        result.body==2L
    }

    def "CountCaseSum"() {
        when:
        def result=restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/countCaseSum",Long.class,143)
        then:
        1*testCaseService.getVersionIds(_)>>[88L,78L]
        result.body==3L
    }




}
