//package io.choerodon.test.manager.app.service.impl
//
//import io.choerodon.agile.api.vo.StatusVO
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.api.vo.IssueInfosVO
//import io.choerodon.test.manager.api.vo.TestCycleCaseDefectRelVO
//import io.choerodon.test.manager.api.vo.TestCycleCaseStepVO
//import io.choerodon.test.manager.api.vo.TestCycleCaseVO
//import io.choerodon.test.manager.app.service.TestCaseService
//import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE
//import org.assertj.core.util.Lists
//import org.assertj.core.util.Maps
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.context.annotation.Import
//import spock.lang.Specification
//
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
//
///**
// * Created by 842767365@qq.com on 8/22/18.
// */
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//class TestCycleCaseDefectRelServiceImplSpec extends Specification {
//
//    TestCycleCaseDefectRelService service
//    TestCaseService client
//
//    void setup() {
//        client = Mock(TestCaseService)
//        service = new TestCycleCaseDefectRelServiceImpl(testCaseService: client)
//    }
//
//    def "PopulateDefectInfo"() {
//        given:
//        TestCycleCaseDefectRelVO defect1 = new TestCycleCaseDefectRelVO(issueId: 1)
//        TestCycleCaseDefectRelVO defect2 = new TestCycleCaseDefectRelVO(issueId: 2)
//        TestCycleCaseDefectRelVO defect3 = new TestCycleCaseDefectRelVO(issueId: 3)
//
//        when:
//        service.populateDefectInfo(new ArrayList<>(), 1L,1L)
//        then:
//        0 * client.getIssueInfoMap(_, _, false,_)
//        when:
//        service.populateDefectInfo(Lists.newArrayList(defect1, defect2, defect3), 1L,1L)
//        then:
//        1 * client.getIssueInfoMap(_, _, false,_) >> new HashMap<>()
//    }
//
//    def "PopulateCycleCaseDefectInfo"() {
//        given:
//        TestCycleCaseDefectRelE defect1 = new TestCycleCaseDefectRelE()
//        defect1.setIssueId(1L)
//        defect1.setDefectType(TestCycleCaseDefectRelE.CYCLE_CASE)
//        defect1.setDefectName("name1")
//        defect1.setDefectLinkId(111L)
//        TestCycleCaseVO vo = new TestCycleCaseVO()
//        List list = new ArrayList();
//        list.add(defect1)
//        vo.setDefects(list)
//        when:
//        service.populateCycleCaseDefectInfo(Lists.newArrayList(vo), 1L,1L)
//        then:
//        1 * client.getIssueInfoMap(_, _, false,1L) >> new HashMap<>()
//    }
//
//    def "PopulateCaseStepDefectInfo"() {
//        given:
//        TestCycleCaseDefectRelE defect1 = new TestCycleCaseDefectRelE()
//        defect1.setIssueId(1L)
//        defect1.setDefectType(TestCycleCaseDefectRelE.CASE_STEP)
//        defect1.setDefectName("name1")
//        defect1.setDefectLinkId(111L)
//        TestCycleCaseStepVO vo = new TestCycleCaseStepVO()
//        List list = new ArrayList();
//        list.add(defect1)
//        vo.setDefects(list)
//        when:
//        service.populateCaseStepDefectInfo(Lists.newArrayList(vo), 1L,1L)
//        then:
//        1 * client.getIssueInfoMap(_, _, false,_) >> Maps.newHashMap (1L,new IssueInfosVO(statusVO: new StatusVO(code: "code")))
//    }
//
//    def "populateDefectAndIssue"(){
//        given:
//        TestCycleCaseVO vo=new TestCycleCaseVO(issueId: 1)
//        when:
//        service.populateDefectAndIssue(vo,144L,1L)
//        then:
//        1*client.getIssueInfoMap(_,_,_,_)>> org.assertj.core.util.Maps.newHashMap(98L,new IssueInfosVO(statusVO: new StatusVO(code: "code")))
//        when:
//        vo.setDefects(Lists.newArrayList(new TestCycleCaseDefectRelE(issueId: 1L)))
//        service.populateDefectAndIssue(vo,144L,1L)
//        then:
//        1*client.getIssueInfoMap(_,_,_,_)>> Maps.newHashMap(1L,new IssueInfosVO(statusVO: new StatusVO(code: "code")))
//
//    }
//}
