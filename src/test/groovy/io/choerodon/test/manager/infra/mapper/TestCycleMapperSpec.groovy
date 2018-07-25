package io.choerodon.test.manager.infra.mapper

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDO
import io.choerodon.test.manager.infra.dataobject.TestCycleDO
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
class TestCycleMapperSpec extends Specification {

    @Autowired
    TestCycleMapper mapper

    @Autowired
    TestCycleCaseMapper caseMapper

    def setup(){
        TestCycleDO cycleDO1=new TestCycleDO()
        cycleDO1.setCycleName("循环1")
        cycleDO1.setVersionId(new Long(99))
        cycleDO1.setType("cycle")
        mapper.insert(cycleDO1)
        cycleDO1.setCycleName("循环2")
        cycleDO1.setCycleId(null)
        mapper.insert(cycleDO1)
        cycleDO1.setType("folder")
        cycleDO1.setCycleName("文件夹1-1")
        cycleDO1.setCycleId(null)
        cycleDO1.setVersionId(new Long(99))
        cycleDO1.setParentCycleId(new Long(1))
        mapper.insert(cycleDO1)
        cycleDO1.setCycleName("文件夹1-2")
        cycleDO1.setCycleId(null)
        mapper.insert(cycleDO1)

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
        caseMapper.insert(caseDO)
        caseMapper.insert(caseDO1)
        caseMapper.insert(caseDO2)
    }
    def "Query"() {
        given:
        Long[] versions=new Long[1]
        versions[0]=new Long(99)
        when:
        List<TestCycleDO> result=mapper.query(versions)
        then:
        result.size()==4

        expect:
        count==result.get(id).getCycleCaseList().size()
        where:
        id  |   count
        0   |   2
        1   |   1
        3   |   0
    }

    def "SelectCyclesInVersions"() {
        expect:
        result==mapper.selectCyclesInVersions(param).size()
        where:
        param                                |     result
        [new Long(99)] as Long[]       |       4
    }
}
