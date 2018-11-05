package io.choerodon.test.manager.app.service.impl


import io.choerodon.agile.api.dto.IssueComponentDetailDTO
import io.choerodon.agile.api.dto.IssueListDTO
import io.choerodon.agile.api.dto.ProjectDTO
import io.choerodon.agile.api.dto.SearchDTO
import io.choerodon.agile.api.dto.StatusMapDTO
import io.choerodon.core.domain.Page
import io.choerodon.core.domain.PageInfo
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.mybatis.pagehelper.domain.Sort
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.infra.feign.ProductionVersionClient
import io.choerodon.test.manager.infra.feign.ProjectFeignClient
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient
import org.assertj.core.util.Lists
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by jialongZuo@hand-china.com on 8/24/18.
 */

class TestCaseServiceImplSpec extends Specification {

    TestCaseServiceImpl service
    ProjectFeignClient projectFeignClient
    TestCaseFeignClient testCaseFeignClient
    ProductionVersionClient productionVersionClient

    def setup(){
        testCaseFeignClient=Mock(TestCaseFeignClient)
        productionVersionClient=Mock(ProductionVersionClient)
        projectFeignClient=Mock(ProjectFeignClient)
        service=new TestCaseServiceImpl(testCaseFeignClient:testCaseFeignClient,productionVersionClient:productionVersionClient,projectFeignClient:projectFeignClient)

    }

    def "ListIssueWithoutSub"() {
        when:
        service.listIssueWithoutSub(1L,new SearchDTO(),new PageRequest(sort: new Sort("id")),1L)
        then:
        1*testCaseFeignClient.listIssueWithoutSubToTestComponent(_,_,_,_,_,_)>>new ResponseEntity<>(new Page(),HttpStatus.OK)
    }

    def "ListIssueWithoutSubDetail"() {
        when:
        service.listIssueWithoutSubDetail(1L,new SearchDTO(),new PageRequest(sort: new Sort("id")),1L)
        then:
        1*testCaseFeignClient.listIssueWithoutSubDetail(_,_,_,_,_,_)
    }

    def "QueryIssue"() {
        when:
        service.queryIssue(1,2,1L)
        then:
        1*testCaseFeignClient.queryIssue(_,_,_)
    }

    def "GetIssueInfoMap"() {
        when:
        service.getIssueInfoMap(1L,new SearchDTO(),new PageRequest(sort: new Sort("id")),1L)
        then:
        1*testCaseFeignClient.listIssueWithoutSubToTestComponent(_,_,_,_,_,_)>>new ResponseEntity<>(new Page(),HttpStatus.OK)

    }

    def "GetIssueInfoMapAndPopulatePageInfo"() {
        when:
        service.getIssueInfoMapAndPopulatePageInfo(1L,new SearchDTO(),new PageRequest(sort: new Sort("id")),new Page(),1L)
        then:
        1*testCaseFeignClient.listIssueWithLinkedIssues(_,_,_,_,_,_)>>
                new ResponseEntity<>(new Page(Lists.newArrayList(new IssueListDTO(issueId:1L,statusMapDTO: new StatusMapDTO(code: "code"))),new PageInfo(0,1,false),1),HttpStatus.OK)
    }

    def "GetIssueInfoMap1"() {
        when:
        service.getIssueInfoMap(1L,new SearchDTO(),true)
        then:
        1*testCaseFeignClient.listIssueWithoutSubDetail(_,_,_,_,_,_)>>new ResponseEntity<>(new Page(Lists.newArrayList(new IssueComponentDetailDTO(issueId:1L)),new PageInfo(0,1,false),1),HttpStatus.OK)
        when:
        service.getIssueInfoMap(1L,new SearchDTO(),false)
        then:
        1*testCaseFeignClient.listIssueWithoutSubToTestComponent(_,_,_,_,_,_)>>new ResponseEntity<>(new Page(Lists.newArrayList(new IssueListDTO(issueId:1L)),new PageInfo(0,1,false),1),HttpStatus.OK)

    }

    def "GetIssueInfoMap2"() {
        when:
        service.getIssueInfoMap(1L,[1,2] as Long[],true)
        then:
        1*testCaseFeignClient.listIssueWithoutSubDetail(_,_,_,_,_,_)>>new ResponseEntity<>(new Page(Lists.newArrayList(new IssueComponentDetailDTO(issueId:1L)),new PageInfo(0,1,false),1),HttpStatus.OK)
        when:
        service.getIssueInfoMap(1L,[] as Long[],true)
        then:
        0*testCaseFeignClient.listIssueWithoutSubDetail(_,_,_,_,_,_)
        0*testCaseFeignClient.listIssueWithoutSubDetail(_,_,_,_,_,_)

    }

    def "GetIssueInfoMap3"() {
        when:
        service.getIssueInfoMap(1L,[1,2] as Long[],new PageRequest(sort: new Sort("id")))
        then:
        1*testCaseFeignClient.listIssueWithoutSubDetail(_,_,_,_,_,_)>>new ResponseEntity<>(new Page(Lists.newArrayList(new IssueListDTO(issueId:1L)),new PageInfo(0,1,false),1),HttpStatus.OK)
        when:
        service.getIssueInfoMap(1L,[] as Long[],new PageRequest())
        then:
        0*testCaseFeignClient.listIssueWithoutSubDetail(_,_,_,_,_,_)
        0*testCaseFeignClient.listIssueWithoutSubDetail(_,_,_,_,_,_)
    }

    def "ListIssueLinkByIssueId"() {
        when:
        service.listIssueLinkByIssueId(1L,new ArrayList<Long>())
        then:
        0*testCaseFeignClient.listIssueLinkByBatch(_,_)
        when:
        service.listIssueLinkByIssueId(1L,Lists.newArrayList(1L))
        then:
        1*testCaseFeignClient.listIssueLinkByBatch(_,_)>>new ResponseEntity<>(new ArrayList(),HttpStatus.OK)

    }

    def "GetLinkIssueFromIssueToTest"() {
        when:
        service.getLinkIssueFromIssueToTest(1L,Lists.newArrayList(1L))
        then:
        1*testCaseFeignClient.listIssueLinkByBatch(_,_)>>new ResponseEntity<>(new ArrayList(),HttpStatus.OK)

    }

    def "GetLinkIssueFromTestToIssue"() {
        when:
        service.getLinkIssueFromTestToIssue(1L,Lists.newArrayList(1L))
        then:
        1*testCaseFeignClient.listIssueLinkByBatch(_,_)>>new ResponseEntity<>(new ArrayList(),HttpStatus.OK)
    }

    def "GetVersionInfo"() {
        when:
        service.getVersionInfo(1)
        then:
        1*productionVersionClient.listByProjectId(_)>>new ResponseEntity<>(new ArrayList(),HttpStatus.OK)
    }

    def "GetProjectInfo"() {
        when:
        service.getProjectInfo(1)
        then:
        1*projectFeignClient.query(_)>>new ResponseEntity<>(new ProjectDTO(),HttpStatus.OK)
    }
}
