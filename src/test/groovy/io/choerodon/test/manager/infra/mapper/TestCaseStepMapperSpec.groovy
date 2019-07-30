//package io.choerodon.test.manager.infra.mapper
//
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE
//import io.choerodon.test.manager.infra.vo.TestCaseStepDTO
//import io.choerodon.test.manager.infra.vo.TestCycleCaseAttachmentRelDTO
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.context.annotation.Import
//import spock.lang.Specification
//import spock.lang.Stepwise
//
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
//
///**
// * Created by 842767365@qq.com on 7/27/18.
// */
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//@Stepwise
//class TestCaseStepMapperSpec extends Specification {
//    @Autowired
//    TestCaseStepMapper mapper
//
//    @Autowired
//    TestCycleCaseAttachmentRelMapper attchMapper
//
//    def "initEnv"(){
//        given:
//        TestCaseStepDTO stepDO=new TestCaseStepDTO(issueId: 111,testStep: "T1",rank:"0|c00000:")
//        TestCaseStepDTO stepDO1=new TestCaseStepDTO(issueId: 111,testStep: "T2",rank:"0|c00004:")
//        TestCaseStepDTO stepDO2=new TestCaseStepDTO(issueId: 111,testStep: "T3",rank:"0|c00008:")
//        mapper.insert(stepDO)
//        mapper.insert(stepDO2)
//        mapper.insert(stepDO1)
//        TestCycleCaseAttachmentRelDTO attach=new TestCycleCaseAttachmentRelDTO()
//        attach.setUrl("URL")
//        attach.setAttachmentLinkId(1)
//        attach.setAttachmentName("att1")
//        attach.setAttachmentType(TestCycleCaseAttachmentRelE.ATTACHMENT_CASE_STEP)
//        attchMapper.insert(attach)
//    }
//    def "Query"() {
//        when:
//        List<TestCaseStepDTO> result=mapper.query(new TestCaseStepDTO(issueId: 111))
//        then:
//        result.size()==3
//    }
//
//    def "GetLastedRank"() {
//        expect:
//        mapper.getLastedRank(param)==result
//        where:
//        param   ||  result
//        111       ||  "0|c00008:"
//        99999   ||  null
//    }
//
//}
