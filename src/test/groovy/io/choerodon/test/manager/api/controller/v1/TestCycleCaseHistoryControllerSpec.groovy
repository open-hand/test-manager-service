//package io.choerodon.test.manager.api.controller.v1
//
//
//import com.github.pagehelper.PageInfo
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.api.vo.TestCycleCaseHistoryVO
//import io.choerodon.test.manager.app.service.TestCycleCaseHistoryService
//import io.choerodon.test.manager.app.service.UserService
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.web.client.TestRestTemplate
//import org.springframework.context.annotation.Import
//import spock.lang.Specification
//import spock.lang.Stepwise
//
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
//
///**
// * Created by jialongZuo@hand-china.com on 8/24/18.
// */
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//@Stepwise
//class TestCycleCaseHistoryControllerSpec extends Specification {
//    @Autowired
//    TestCycleCaseHistoryService service
//
//    @Autowired
//    TestRestTemplate restTemplate;
//
//    @Autowired
//    UserService userService;
//
//    def "Insert"() {
//        given:
//        TestCycleCaseHistoryVO historyDTO = new TestCycleCaseHistoryVO(oldValue: "old", newValue: "new", field: TestCycleCaseHistoryE.FIELD_STATUS, executeId: 1L,lastUpdatedBy: 1L)
//        expect:
//        service.insert(historyDTO)
//    }
//
//    def "Query"() {
//        when:
//        def result=restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/history/{cycleCaseId}?page={page}&size={size}", PageInfo,144,1,0,5)
//        then:
//        1 * userService.populateUsersInHistory(_)
//        result.statusCode.is2xxSuccessful()
//    }
//}
