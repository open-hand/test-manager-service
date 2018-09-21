package io.choerodon.test.manager.api.controller.v1

import io.choerodon.agile.api.dto.IssueCommonDTO
import io.choerodon.agile.api.dto.SearchDTO
import io.choerodon.core.domain.Page
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.IssueInfosDTO
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO
import io.choerodon.test.manager.api.dto.TestCycleDTO
import io.choerodon.test.manager.api.dto.TestStatusDTO
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory
import io.choerodon.test.manager.infra.dataobject.TestStatusDO
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper
import io.choerodon.test.manager.infra.mapper.TestStatusMapper
import org.apache.commons.lang.StringUtils
import org.assertj.core.util.Lists
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import javax.swing.undo.AbstractUndoableEdit

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCycleCaseControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    TestCaseService testCaseService

    @Autowired
    UserService userService

    @Shared
    TestStatusDO statusDO=new TestStatusDO()

    @Autowired
    TestStatusMapper testStatusMapper

    @Shared
    List<TestCycleCaseDTO> caseDTO=new ArrayList<>();

    @Shared
    List<Long> cycleIds = new ArrayList<>()

    @Autowired
    TestCycleCaseMapper testCycleCaseMapper

    def setup() {
        given:
        TestCycleDTO testCycleDTO1 = new TestCycleDTO()
        testCycleDTO1.setCycleName("testCycleCaseInsert")
        testCycleDTO1.setFolderId(11111L)
        testCycleDTO1.setVersionId(11111L)
        testCycleDTO1.setType(TestCycleE.FOLDER)
        testCycleDTO1.setObjectVersionNumber(1L)

        given:
        statusDO.setProjectId(new Long(0))
        statusDO.setStatusName("未执行")
        statusDO.setStatusColor("yellow")
        statusDO.setStatusType("CYCLE_CASE")

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle', testCycleDTO1,TestCycleDTO, 143L)
        then:
        entity.statusCode.is2xxSuccessful()
        and:
        entity.body != null
        cycleIds.add(entity.body.cycleId)
        entity.body.folderId == 11L

        when: '向插入status的接口发请求'
        entity = restTemplate.postForEntity('/v1/projects/{project_id}/status', statusDO, TestStatusDTO, 0L)
        then:
        entity.statusCode.is2xxSuccessful()

        and:
        entity.body != null
        StringUtils.equals(entity.getBody().statusName, "未执行")
    }

    def "InsertOneCase"() {
        given:
        TestCycleCaseDTO dto=new TestCycleCaseDTO(cycleId:cycleIds.get(0),issueId: 98L,assignedTo:10L)
        TestCycleCaseDTO dto2=new TestCycleCaseDTO(cycleId:cycleIds.get(0),issueId: 97L,assignedTo:10L)

        when:
        def result=restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/insert",dto,TestCycleCaseDTO,143)
        then:
        result.body.executeId!=null
        and:
        caseDTO.add(result.body)

        when:
        result=restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/insert",dto2,TestCycleCaseDTO,143)
        then:
        result.body.executeId!=null
        and:
        caseDTO.add(result.body)
    }

    def "QueryOne"() {

        when:
        def result=restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/query/one/{executeId}",TestCycleCaseDTO,143,caseDTO.get(0).executeId)
        then:
        1*userService.query(_)>>new HashMap();
        and:
        result.body.cycleId==cycleIds.get(0)
    }

    def "QueryByIssuse"() {
        when:
        def result=restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/query/issue/{issueId}",List,143,98)
        then:
        1*testCaseService.getIssueInfoMap(_,_,_)>>new HashMap<>()
        1*userService.query(_)>>new HashMap<>()
        1*testCaseService.getVersionInfo(_)>>new HashMap<>()
        and:
        result.body.size()==1
    }
    def "QueryByCycle"() {
        given:
        TestCycleCaseDTO searchDto=new TestCycleCaseDTO(cycleId: cycleIds.get(0))
        when:
        def result=restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/query/cycleId?page={page}&size={size}",searchDto, Page.class,143,0,10)
        then:
        1*testCaseService.getIssueInfoMap(_,_,_)>>new HashMap<>()
        1*userService.query(_)>>new HashMap<>()
        and:
        result.body.size() == 2

        when:
        result=restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/query/cycleId?page={page}&size={size}",searchDto, Page.class,143,0,1)
        then:
        1*testCaseService.getIssueInfoMap(_,_,_)>>new HashMap<>()
        1*userService.query(_)>>new HashMap<>()
        and:
        result.body.size() == 1
    }

    def "UpdateOneCase"() {
        given:
        TestCycleCaseDTO searchDto=caseDTO.get(1);
        searchDto.setLastRank(searchDto.rank)
        searchDto.setObjectVersionNumber(1L)
        when:
        def result1= restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/update",searchDto, TestCycleCaseDTO,143)
        then:
        1*userService.query(_)>>new HashMap<>()
        and:
        result1.body.rank!=caseDTO.get(0).rank
    }

    def "QueryByCycleWithFilterArgs"() {
        given:
        TestCycleCaseDTO searchDto=new TestCycleCaseDTO(cycleId: cycleIds.get(0))

        when:
        def result=restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/query/filtered/{cycleId}?page={page}&size={size}",searchDto, Page.class,143,1,0,10)
        then:
        1*userService.query(_)>>new HashMap<>()
        and:
        result.body.size()==2
    }

    def "CreateFilteredCycleCaseInCycle"() {
        given:
        Long fromCycle=caseDTO.get(0).cycleId
        TestCycleCaseE caseE=TestCycleCaseEFactory.create();
        caseE.setCycleId(990)
        TestCycleCaseDefectRelE defectRelE= TestCycleCaseDefectRelEFactory.create();
        defectRelE.setIssueId(98L)
        defectRelE.setDefectType(TestCycleCaseDefectRelE.CYCLE_CASE)
        defectRelE.setDefectLinkId(fromCycle)
        defectRelE.addSelf();
        Page page=new Page()
        page.setContent(Lists.newArrayList(new IssueCommonDTO(issueId: 98L,issueNum: "issueNum1")))
        Map map = new HashMap()
        map.put(98L,new IssueInfosDTO(issueId: 98L,issueNum: "issueNum1"))
        when:
        restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/insert/case/filter/{fromCycleId}/to/{toCycleId}/assigneeTo/{assignee}",new SearchDTO(), Boolean,143,fromCycle,990,56)
        then:
       1*testCaseService.listIssueWithoutSub(_,_,_)>>new ResponseEntity<Page>(page,HttpStatus.OK)
       1*testCaseService.getIssueInfoMap(_,_,_)>>map
        and:
        caseE.queryOne().assignedTo==56
    }

    def "delete"(){

        expect:
        restTemplate.delete("/v1/projects/{project_id}/cycle/case?cycleCaseId={cycleCaseId}",143,caseDTO.get(0).getExecuteId())
    }

    def cleanup(){
        testStatusMapper.delete(statusDO)
    }
}
