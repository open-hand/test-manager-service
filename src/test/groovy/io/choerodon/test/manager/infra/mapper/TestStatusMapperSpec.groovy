package io.choerodon.test.manager.infra.mapper

import io.choerodon.test.manager.IntegrationTestConfiguration
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
@Stepwise
class TestStatusMapperSpec extends Specification {

    @Autowired
    TestStatusMapper mapper;

    def "QueryAllUnderProject"() {
        given:
        TestStatusDO statusDO=new TestStatusDO()
        statusDO.setProjectId(new Long(1))
        statusDO.setStatusName("name")
        statusDO.setStatusColor("yellow")
        statusDO.setStatusType("CYCLE_CASE")
        TestStatusDO statusDO1=new TestStatusDO()

        when:
        def result1=mapper.queryAllUnderProject(statusDO1).size()

        mapper.insert()
        def result2=mapper.queryAllUnderProject(statusDO)
        then:
        result1!=0


    }

    def "IfDeleteCycleCaseAllow"() {
    }

    def "IfDeleteCaseStepAllow"() {
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
