package io.choerodon.test.manager.api.controller.v1

import io.choerodon.agile.api.dto.IssueCommonDTO
import io.choerodon.agile.api.dto.ProductVersionDTO
import io.choerodon.agile.api.dto.ProjectDTO
import io.choerodon.agile.api.dto.SearchDTO
import io.choerodon.agile.api.dto.UserDO
import io.choerodon.core.convertor.ConvertHelper
import io.choerodon.core.domain.Page
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.IssueInfosDTO
import io.choerodon.test.manager.api.dto.TestCaseStepDTO
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO
import io.choerodon.test.manager.api.dto.TestCycleDTO
import io.choerodon.test.manager.api.dto.TestStatusDTO
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.TestCaseStepService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE
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
import org.assertj.core.util.Maps
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
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

    @Autowired
    TestCaseStepService caseStepService;

    def "initEnv"() {
        given:
        TestCycleDTO testCycleDTO1 = new TestCycleDTO()
        testCycleDTO1.setCycleName("testCycleCaseInsert")
        testCycleDTO1.setFolderId(11111L)
        testCycleDTO1.setVersionId(11111L)
        testCycleDTO1.setType(TestCycleE.FOLDER)
        testCycleDTO1.setObjectVersionNumber(1L)


        statusDO.setProjectId(new Long(142))
        statusDO.setStatusName("未执行")
        statusDO.setStatusColor("yellow")
        statusDO.setStatusType("CYCLE_CASE")


        TestCaseStepDTO stepDTO1=new TestCaseStepDTO(issueId:98L,testStep:"11");
        ConvertHelper.convert(stepDTO1, TestCaseStepE.class).addSelf()

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle', testCycleDTO1,TestCycleDTO, 142L)
        then:
        entity.statusCode.is2xxSuccessful()
        and:
        entity.body != null
        cycleIds.add(entity.body.cycleId)
        entity.body.folderId == 11111L

        when: '向插入status的接口发请求'
        entity = restTemplate.postForEntity('/v1/projects/{project_id}/status', statusDO, TestStatusDTO, 142)
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
        def result=restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/insert",dto,TestCycleCaseDTO,142)
        then:
        result.body.executeId!=null
        and:
        caseDTO.add(result.body)

        when:
        result=restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/insert",dto2,TestCycleCaseDTO,142)
        then:
        result.body.executeId!=null
        and:
        caseDTO.add(result.body)
    }

    def "QueryOne"() {

        when:
        def result=restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/query/one/{executeId}",TestCycleCaseDTO,142,caseDTO.get(0).executeId)
        then:
        1*userService.query(_)>>new HashMap();
        and:
        result.body.cycleId==cycleIds.get(0)
    }

    def "QueryByIssuse"() {
        when:
        def result=restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/query/issue/{issueId}",List,142,98)
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
        def result=restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/query/cycleId?page={page}&size={size}",searchDto, Page.class,142,0,10)
        then:
        1*testCaseService.getIssueInfoMap(_,_,_)>>new HashMap<>()
        1*userService.query(_)>>new HashMap<>()
        and:
        result.body.size() == 2

        when:
        result=restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/query/cycleId?page={page}&size={size}",searchDto, Page.class,142,0,1)
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
        searchDto.setAssignedTo(4L)
        searchDto.setComment("111")
        searchDto.setExecutionStatus(3L)
        searchDto.setObjectVersionNumber(1L)
        Map userMap=Maps.newHashMap(4L,new UserDO(loginName: "login",realName: "real"))
        userMap.put(10L,new UserDO(loginName: "login",realName: "real"))
        when:
        def result1= restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/update",searchDto, TestCycleCaseDTO,142)
        then:
        2*userService.query(_)>>userMap
        and:
        result1.body.rank!=caseDTO.get(0).rank
    }

    def "QueryByCycleWithFilterArgs"() {
        given:
        TestCycleCaseDTO searchDto=new TestCycleCaseDTO(cycleId: cycleIds.get(0))

        when:
        def result=restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/query/filtered/{cycleId}?page={page}&size={size}",searchDto, Page.class,142,1,0,10)
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
        restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/insert/case/filter/{fromCycleId}/to/{toCycleId}/assigneeTo/{assignee}",new SearchDTO(), Boolean,142,fromCycle,990,56)
        then:
       1*testCaseService.listIssueWithoutSub(_,_,_)>>new ResponseEntity<Page>(page,HttpStatus.OK)
       1*testCaseService.getIssueInfoMap(_,_,_)>>map
        and:
        caseE.queryOne().assignedTo==56
    }


    def "exportExcle"(){
        given:
        Map issueMaps=Maps.newHashMap(98L,new IssueInfosDTO(issueName: "issueName",issueNum: 98L));
        issueMaps.put(97L,new IssueInfosDTO(issueName: "issueName1",issueNum: 97L))
        when:
        restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/download/excel/{cycleId}",null,142,caseDTO.get(0).getCycleId())
        then:
        1*testCaseService.getVersionInfo(_)>> Maps.newHashMap(11111L, new ProductVersionDTO(name: "versionName"))
        2*userService.query(_)>>Maps.newHashMap(10L,new UserDO(realName: "real",loginName: "login"))
        1*testCaseService.getIssueInfoMap(_,_,_)>>issueMaps
        1*testCaseService.getProjectInfo(_)>>new ProjectDTO(name: "project1")
    }


    def "QuerySubStep"() {
        when:
        ResponseEntity<Page<TestCycleCaseStepDTO>> page=restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/step/query/{cycleCaseId}",Page.class,142,caseDTO.get(0).getExecuteId())
        then:
        page.getBody().size()==1
        TestCycleCaseStepDTO dto=page.getBody().get(0)
        dto.setComment("111")
        expect:
        restTemplate.put("/v1/projects/{project_id}/cycle/case/step",Lists.newArrayList(dto),142)


    }


    def "delete"(){

        expect:
        restTemplate.delete("/v1/projects/{project_id}/cycle/case?cycleCaseId={cycleCaseId}",142,caseDTO.get(0).getExecuteId())
    }

}
