package io.choerodon.test.manager.infra.mapper

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE
import io.choerodon.test.manager.infra.dataobject.TestCaseStepDO
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by jialongZuo@hand-china.com on 7/27/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCaseStepMapperSpec extends Specification {
    @Autowired
    TestCaseStepMapper mapper

    @Autowired
    TestCycleCaseAttachmentRelMapper attchMapper

    def "initEnv"(){
        given:
        TestCaseStepDO stepDO=new TestCaseStepDO(issueId: 1,testStep: "T1",rank:"0|c00000:")
        TestCaseStepDO stepDO1=new TestCaseStepDO(issueId: 1,testStep: "T2",rank:"0|c00004:")
        TestCaseStepDO stepDO2=new TestCaseStepDO(issueId: 1,testStep: "T3",rank:"0|c00008:")
        mapper.insert(stepDO)
        mapper.insert(stepDO2)
        mapper.insert(stepDO1)
        TestCycleCaseAttachmentRelDO attach=new TestCycleCaseAttachmentRelDO()
        attach.setUrl("URL")
        attach.setAttachmentLinkId(1)
        attach.setAttachmentName("att1")
        attach.setAttachmentType(TestCycleCaseAttachmentRelE.ATTACHMENT_CASE_STEP)
        attchMapper.insert(attach)
    }
    def "Query"() {
        when:
        List<TestCaseStepDO> result=mapper.query(new TestCaseStepDO(issueId: 1))
        then:
        result.size()==3
        and:
        result.get(0).getAttachments().size()==1
        result.get(0).getAttachments().get(0).getUrl()=="URL"
    }

    def "GetLastedRank"() {
        expect:
        mapper.getLastedRank(param)==result
        where:
        param   ||  result
        1       ||  "0|c00008:"
        99999   ||  null
    }

}
