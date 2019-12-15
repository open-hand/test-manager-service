//package io.choerodon.test.manager.infra.mapper
//
//
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO
//import io.choerodon.test.manager.infra.dto.TestCycleDTO
//import org.springframework.beans.BeanUtils
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.context.annotation.Import
//import spock.lang.Shared
//import spock.lang.Specification
//import spock.lang.Stepwise
//
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
//
///**
// * Created by 842767365@qq.com on 7/25/18.
// */
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//@Stepwise
//class TestCycleMapperSpec extends Specification {
//
//    @Autowired
//    TestCycleMapper mapper
//
//    @Autowired
//    TestCycleCaseMapper caseMapper
//    @Shared
//    TestCycleDTO cycleDO1=new TestCycleDTO()
//    @Shared
//    TestCycleDTO cycleDO2=new TestCycleDTO()
//    @Shared
//    TestCycleDTO cycleDO3=new TestCycleDTO()
//    @Shared
//    TestCycleDTO cycleDO4=new TestCycleDTO()
//    @Shared
//    TestCycleCaseDTO caseDO = new TestCycleCaseDTO()
//    @Shared
//    TestCycleCaseDTO caseDO1 = new TestCycleCaseDTO()
//    @Shared
//    TestCycleCaseDTO caseDO2 = new TestCycleCaseDTO()
//
//    def "initEnv"(){
//        given:
//        cycleDO1.setCycleName("循环1")
//        cycleDO1.setVersionId(new Long(929))
//        cycleDO1.projectId = 1L
//        cycleDO1.setType("cycle")
//        mapper.insert(cycleDO1)
//        cycleDO2.setCycleName("循环2")
//        cycleDO2.setVersionId(new Long(929))
//        cycleDO2.projectId = 1L
//        cycleDO2.setType("cycle")
//        mapper.insert(cycleDO2)
//        cycleDO3.setType("folder")
//        cycleDO3.setCycleName("文件夹1-1")
//        cycleDO3.setVersionId(new Long(929))
//        cycleDO3.projectId = 1L
//        cycleDO3.setParentCycleId(cycleDO1.getCycleId())
//        mapper.insert(cycleDO3)
//        cycleDO4.setCycleName("文件夹1-2")
//        cycleDO4.setType("folder")
//        cycleDO4.setVersionId(new Long(929))
//        cycleDO4.projectId = 1L
//        cycleDO4.setParentCycleId(cycleDO1.getCycleId())
//        mapper.insert(cycleDO4)
//
//        caseDO.setCycleId(cycleDO1.getCycleId())
//        caseDO.setExecutionStatus(new Long(1))
//        caseDO.setIssueId(new Long(999))
//        caseDO.projectId = 1L
//        caseDO.setRank("0|c00000:")
//
//        caseDO1.setCycleId(cycleDO1.getCycleId())
//        caseDO1.setExecutionStatus(new Long(2))
//        caseDO1.setIssueId(new Long(998))
//        caseDO1.projectId = 1L
//        caseDO1.setRank("0|c00004:")
//
//        caseDO2.setCycleId(cycleDO2.getCycleId())
//        caseDO2.setExecutionStatus(new Long(2))
//        caseDO2.setIssueId(new Long(999))
//        caseDO2.projectId = 1L
//        caseDO2.setRank("0|c00000:")
//        caseMapper.insert(caseDO)
//        caseMapper.insert(caseDO1)
//        caseMapper.insert(caseDO2)
//    }
//
//    def "Query"() {
//        given:
//
//        Long[] versions=new Long[1]
//        versions[0] = new Long(929)
//        when:
//        List<TestCycleDTO> result=mapper.query(1L,versions,null)
//        then:
//        result.size()==4
//
//        expect:
//        count==result.get(id).getCycleCaseList().size()
//        where:
//        id  |   count
//        0   |   2
//        1   |   1
//        2   |   0
//        3   |   0
//    }
//
//    def "SelectCyclesInVersions"() {
//        expect:
//        result==mapper.selectCyclesInVersions(param).size()
//        where:
//        param                     |     result
//        [new Long(929)] as Long[] | 4
//    }
//
//    def "validateCycle"(){
//        given:
//        TestCycleDTO cycleDO2 = new TestCycleDTO()
//        BeanUtils.copyProperties(cycleDO1, cycleDO2, "cycleId")
//
//        expect:
//        mapper.validateCycle(cycleDO2) == 1
//    }
//
//    def "deleteEnv"(){
//        expect:
//        mapper.deleteByPrimaryKey(cycleDO1.getCycleId())==1
//        mapper.deleteByPrimaryKey(cycleDO2.getCycleId())==1
//        mapper.deleteByPrimaryKey(cycleDO3.getCycleId())==1
//        mapper.deleteByPrimaryKey(cycleDO4.getCycleId())==1
//        caseMapper.deleteByPrimaryKey(caseDO.getExecuteId())==1
//        caseMapper.deleteByPrimaryKey(caseDO1.getExecuteId())==1
//        caseMapper.deleteByPrimaryKey(caseDO2.getExecuteId())==1
//    }
//}
