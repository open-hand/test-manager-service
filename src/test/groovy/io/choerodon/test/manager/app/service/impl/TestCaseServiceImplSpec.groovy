//package io.choerodon.test.manager.app.service.impl
//
//import com.github.pagehelper.Page
//import com.github.pagehelper.PageInfo
//import io.choerodon.agile.api.vo.*
//import io.choerodon.test.manager.api.vo.agile.IssueComponentDetailVO
//import io.choerodon.test.manager.api.vo.agile.IssueListTestVO
//import io.choerodon.test.manager.api.vo.agile.IssueListTestWithSprintVersionDTO
//import io.choerodon.test.manager.api.vo.agile.ProjectDTO
//import io.choerodon.test.manager.api.vo.agile.SearchDTO
//import io.choerodon.test.manager.api.vo.agile.StatusVO
//import io.choerodon.test.manager.infra.feign.BaseFeignClient
//import io.choerodon.test.manager.infra.feign.ProductionVersionClient
//import io.choerodon.test.manager.infra.feign.TestCaseFeignClient
//import org.assertj.core.util.Lists
//import org.springframework.data.domain.PageRequest
//import org.springframework.data.domain.Sort
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import spock.lang.Specification
//
///**
// * Created by jialongZuo@hand-china.com on 8/24/18.
// */
//
//class TestCaseServiceImplSpec extends Specification {
//
//    TestCaseServiceImpl service
//    BaseFeignClient baseFeignClient
//    TestCaseFeignClient testCaseFeignClient
//    ProductionVersionClient productionVersionClient
////    ApplicationFeignClient applicationFeignClient
//
//    def setup() {
//        testCaseFeignClient = Mock(TestCaseFeignClient)
//        productionVersionClient = Mock(ProductionVersionClient)
//        baseFeignClient = Mock(BaseFeignClient)
////        applicationFeignClient = Mock(ApplicationFeignClient)
//        service = new TestCaseServiceImpl(
//                testCaseFeignClient: testCaseFeignClient,
//                productionVersionClient: productionVersionClient,
//                baseFeignClient: baseFeignClient
////                ApplicationFeignClient: ApplicationFeignClient
//        )
//
//    }
//
//    def "ListIssueWithoutSub"() {
//        Sort sort = new Sort("id")
//        when:
//        service.listIssueWithoutSub(1L, new SearchDTO(), new PageRequest(1, 10, sort), 1L)
//        then:
//        1 * testCaseFeignClient.listIssueWithoutSubToTestComponent(_, _, _, _, _, _) >> new ResponseEntity<>(new PageInfo(), HttpStatus.OK)
//    }
//
//    def "ListIssueWithoutSubDetail"() {
//        Sort sort = new Sort("id")
//        when:
//        service.listIssueWithoutSubDetail(1L, new SearchDTO(), new PageRequest(1, 10, sort), 1L)
//        then:
//        1 * testCaseFeignClient.listIssueWithoutSubDetail(_, _, _, _, _, _)
//    }
//
//    def "QueryIssue"() {
//        when:
//        service.queryIssue(1, 2, 1L)
//        then:
//        1 * testCaseFeignClient.queryIssue(_, _, _)
//    }
//
//    def "GetIssueInfoMapAndPopulatePageInfo"() {
//        Sort sort = new Sort("id")
//
//        when:
//        service.getIssueInfoMapAndPopulatePageInfo(1L, new SearchDTO(), new PageRequest(1, 10, sort), new Page(), 1L)
//        then:
//        1 * testCaseFeignClient.listIssueWithLinkedIssues(_, _, _, _, _, _) >>
//                new ResponseEntity<>(new PageInfo(Lists.newArrayList(new IssueListTestWithSprintVersionDTO(issueId: 1L, statusVO: new StatusVO(code: "code")))), HttpStatus.OK)
//    }
//
//    def "GetIssueInfoMap1"() {
//        when:
//        service.getIssueInfoMap(1L, new SearchDTO(), true, 1L)
//        then:
//        1 * testCaseFeignClient.listIssueWithoutSubDetail(_, _, _, _, _, _) >> new ResponseEntity<>(new PageInfo(Lists.newArrayList(new IssueComponentDetailVO(issueId: 1L, statusVO: new StatusVO(code: "code")))), HttpStatus.OK)
//        when:
//        service.getIssueInfoMap(1L, new SearchDTO(), false, 1L)
//        then:
//        1 * testCaseFeignClient.listIssueWithoutSubToTestComponent(_, _, _, _, _, _) >> new ResponseEntity<>(new PageInfo(Lists.newArrayList(new IssueListTestVO(issueId: 1L, statusVO: new StatusVO(code: "code")))), HttpStatus.OK)
//
//    }
//
//    def "GetIssueInfoMap2"() {
//        when:
//        service.getIssueInfoMap(1L, [1, 2] as Long[], true, 1L)
//        then:
//        1 * testCaseFeignClient.listIssueWithoutSubDetail(_, _, _, _, _, _) >> new ResponseEntity<>(new PageInfo(Lists.newArrayList(new IssueComponentDetailVO(issueId: 1L, statusVO: new StatusVO(code: "code")))), HttpStatus.OK)
//        when:
//        service.getIssueInfoMap(1L, [] as Long[], true, 1L)
//        then:
//        0 * testCaseFeignClient.listIssueWithoutSubDetail(_, _, _, _, _, _)
//        0 * testCaseFeignClient.listIssueWithoutSubToTestComponent(_, _, _, _, _, _)
//
//    }
//
//    def "GetIssueInfoMap3"() {
//        when:
//        service.getIssueInfoMap(1L, [1, 2] as Long[], new PageRequest(1, 10, new Sort("id")), 1L)
//        then:
//        1 * testCaseFeignClient.listIssueWithoutSubToTestComponent(_, _, _, _, _, _) >> new ResponseEntity<>(new PageInfo(Lists.newArrayList(new IssueListTestVO(issueId: 1L, statusVO: new StatusVO(code: "code")))), HttpStatus.OK)
//        when:
//        service.getIssueInfoMap(1L, [] as Long[], new PageRequest(1, 10, new Sort("id")), 1L)
//        then:
//        0 * testCaseFeignClient.listIssueWithoutSubDetail(_, _, _, _, _, _)
//        0 * testCaseFeignClient.listIssueWithoutSubToTestComponent(_, _, _, _, _, _)
//    }
//
//    def "ListIssueLinkByIssueId"() {
//        when:
//        service.listIssueLinkByIssueId(1L, new ArrayList<Long>())
//        then:
//        0 * testCaseFeignClient.listIssueLinkByBatch(_, _)
//        when:
//        service.listIssueLinkByIssueId(1L, Lists.newArrayList(1L))
//        then:
//        1 * testCaseFeignClient.listIssueLinkByBatch(_, _) >> new ResponseEntity<>(new ArrayList(), HttpStatus.OK)
//
//    }
//
//    def "GetLinkIssueFromIssueToTest"() {
//        when:
//        service.getLinkIssueFromIssueToTest(1L, Lists.newArrayList(1L))
//        then:
//        1 * testCaseFeignClient.listIssueLinkByBatch(_, _) >> new ResponseEntity<>(new ArrayList(), HttpStatus.OK)
//
//    }
//
//    def "GetLinkIssueFromTestToIssue"() {
//        when:
//        service.getLinkIssueFromTestToIssue(1L, Lists.newArrayList(1L))
//        then:
//        1 * testCaseFeignClient.listIssueLinkByBatch(_, _) >> new ResponseEntity<>(new ArrayList(), HttpStatus.OK)
//    }
//
//    def "GetVersionInfo"() {
//        when:
//        service.getVersionInfo(1)
//        then:
//        1 * productionVersionClient.listByProjectId(_) >> new ResponseEntity<>(new ArrayList(), HttpStatus.OK)
//    }
//
//    def "GetProjectInfo"() {
//        when:
//        service.getProjectInfo(1)
//        then:
//        1 * baseFeignClient.queryProject(_) >> new ResponseEntity<>(new ProjectDTO(), HttpStatus.OK)
//    }
//}
