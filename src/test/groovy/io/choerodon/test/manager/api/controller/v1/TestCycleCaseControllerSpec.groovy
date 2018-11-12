package io.choerodon.test.manager.api.controller.v1

import io.choerodon.agile.api.dto.IssueListDTO
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
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO
import io.choerodon.test.manager.api.dto.TestCycleDTO
import io.choerodon.test.manager.api.dto.TestStatusDTO
import io.choerodon.test.manager.app.service.ExcelService
import io.choerodon.test.manager.app.service.ExcelServiceHandler
import io.choerodon.test.manager.app.service.FileService
import io.choerodon.test.manager.app.service.NotifyService
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.TestCaseStepService
import io.choerodon.test.manager.app.service.TestCycleCaseService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.app.service.impl.TestCycleCaseServiceImpl
import io.choerodon.test.manager.domain.service.ITestCycleService
import io.choerodon.test.manager.domain.service.ITestFileLoadHistoryService
import io.choerodon.test.manager.domain.service.impl.ITestCycleServiceImpl
import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE
import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory
import io.choerodon.test.manager.domain.test.manager.factory.TestFileLoadHistoryEFactory
import io.choerodon.test.manager.infra.dataobject.TestStatusDO
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper
import io.choerodon.test.manager.infra.mapper.TestStatusMapper
import io.reactivex.netty.protocol.http.server.HttpServerRequest
import org.apache.commons.lang.StringUtils
import org.assertj.core.util.Lists
import org.assertj.core.util.Maps
import org.assertj.core.util.Sets
import org.springframework.aop.framework.AdvisedSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import javax.swing.undo.AbstractUndoableEdit
import java.lang.reflect.Field

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
    TestStatusDO statusDO = new TestStatusDO()

    @Autowired
    TestStatusMapper testStatusMapper

    @Shared
    List<TestCycleCaseDTO> caseDTO = new ArrayList<>();

    @Shared
    List<Long> cycleIds = new ArrayList<>()

    @Autowired
    TestCycleCaseMapper testCycleCaseMapper

    @Autowired
    TestCaseStepService caseStepService;

    @Autowired
    TestCycleCaseController testCycleCaseController

    private ExcelServiceHandler excelServiceHandler

    @Autowired
    ExcelService relExcelService

    @Autowired
    FileService fileService

    @Autowired
    ITestFileLoadHistoryService historyService

    @Autowired
    FileService fileFeignClient

    @Autowired
    NotifyService notifyService

    @Shared
    Object target

    def "initEnv"() {
        given:
        TestCycleDTO testCycleDTO1 = new TestCycleDTO()
        testCycleDTO1.setFromDate(new Date())
        testCycleDTO1.setToDate(new Date())
        testCycleDTO1.setCycleName("testCycleCaseInsert")
        testCycleDTO1.setFolderId(11111L)
        testCycleDTO1.setVersionId(11111L)
        testCycleDTO1.setType(TestCycleE.CYCLE)
        testCycleDTO1.setObjectVersionNumber(1L)


        statusDO.setProjectId(new Long(142))
        statusDO.setStatusName("未执行")
        statusDO.setStatusColor("yellow")
        statusDO.setStatusType("CYCLE_CASE")


        TestCaseStepDTO stepDTO1 = new TestCaseStepDTO(issueId: 98L, testStep: "11");
        ConvertHelper.convert(stepDTO1, TestCaseStepE.class).addSelf()

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle', testCycleDTO1, TestCycleDTO, 142L)
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

        when:
        TestCycleDTO testCycleDTO2 = new TestCycleDTO()
        testCycleDTO2.setCycleName("childCycle")
        testCycleDTO2.setFolderId(11111L)
        testCycleDTO2.setVersionId(11111L)
        testCycleDTO2.setType(TestCycleE.FOLDER)
        testCycleDTO2.setParentCycleId(cycleIds.get(0))
        testCycleDTO2.setObjectVersionNumber(1L)
        def entity1 = restTemplate.postForEntity('/v1/projects/{project_id}/cycle', testCycleDTO2, TestCycleDTO, 142L)
        then:
        entity1.statusCode.is2xxSuccessful()
    }

    def "InsertOneCase"() {
        given:
        TestCycleCaseDTO dto = new TestCycleCaseDTO(cycleId: cycleIds.get(0), issueId: 98L, assignedTo: 10L)
        TestCycleCaseDTO dto2 = new TestCycleCaseDTO(cycleId: cycleIds.get(0), issueId: 97L, assignedTo: 10L, comment: "comment1")
        TestCycleCaseDTO dto3 = new TestCycleCaseDTO(issueId: 96L, cycleId: cycleIds.get(0))

        when:
        def result = restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/insert", dto, TestCycleCaseDTO, 142)
        then:
        result.body.executeId != null
        and:
        caseDTO.add(result.body)

        when:
        result = restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/insert", dto2, TestCycleCaseDTO, 142)
        then:
        result.body.executeId != null
        and:
        caseDTO.add(result.body)

        when:
        result = restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/insert", dto3, TestCycleCaseDTO, 142)
        then:
        result.body.executeId != null
        and:
        caseDTO.add(result.body)
    }

    def "QueryOne"() {

        when:
        def result = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/query/one/{executeId}?organizationId=1", TestCycleCaseDTO, 142, caseDTO.get(0).executeId)
        then:
        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> Maps.newHashMap(98L, new IssueInfosDTO())
        1 * userService.populateTestCycleCaseDTO(_)
        and:
        result.body.cycleId == cycleIds.get(0)
    }

    def "QueryByIssuse"() {
        when:
        def result = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/query/issue/{issueId}?organizationId=1", List, 142, 98)
        then:
        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> new HashMap<>()
        1 * userService.query(_) >> new HashMap<>()
        1 * testCaseService.getVersionInfo(_) >> Maps.newHashMap(11111L, new ProductVersionDTO())
        and:
        result.body.size() == 1
        when:
        restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/query/issue/{issueId}?organizationId=1", List, 142, -1)
        then:
        0 * testCaseService.getIssueInfoMap(_, _, _, _)
        0 * userService.query(_)
        0 * testCaseService.getVersionInfo(_)
        when:
        restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/query/issue/{issueId}?organizationId=1", List, 142, 96L)
        then:
        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> new HashMap<>()
        0 * userService.query(_)
        1 * testCaseService.getVersionInfo(_) >> Maps.newHashMap(11111L, new ProductVersionDTO())
    }

    def "QueryByCycle"() {
        given:
        TestCycleCaseDTO searchDto = new TestCycleCaseDTO(cycleId: cycleIds.get(0))
        when:
        def result = restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/query/cycleId?page={page}&size={size}&organizationId=1", searchDto, Page.class, 142, 0, 10)
        then:
        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> new HashMap<>()
        1 * userService.query(_) >> new HashMap<>()
        and:
        result.body.size() == 3

        when:
        result = restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/query/cycleId?page={page}&size={size}&organizationId=1", searchDto, Page.class, 142, 0, 1)
        then:
        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> new HashMap<>()
        1 * userService.query(_) >> new HashMap<>()
        and:
        result.body.size() == 1
    }

    def "validateReturn"() {
        given:
        TestCaseService client = Mock(TestCaseService)
        TestCycleCaseService service = new TestCycleCaseServiceImpl(testCaseService: client)
        when:
        service.populateCycleCaseWithDefect(new ArrayList<TestCycleCaseDTO>(), 144L, 1)
        then:
        0 * client.getIssueInfoMap(_, _, _, _)
        when:
        service.populateVersionBuild(144, null)
        then:
        1 * client.getVersionInfo(_) >> new HashMap<>()
    }

    def "UpdateOneCase"() {
        given:
        TestCycleCaseDTO searchDto = caseDTO.get(1);
        searchDto.setRank(searchDto.rank)
        searchDto.setAssignedTo(4L)
        searchDto.setComment("111")
        searchDto.setExecutionStatus(3L)
        searchDto.setObjectVersionNumber(1L)
        Map userMap = Maps.newHashMap(4L, new UserDO(loginName: "login", realName: "real"))
        userMap.put(10L, new UserDO(loginName: "login", realName: "real"))
        when:
        def result1 = restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/update", searchDto, TestCycleCaseDTO, 142)
        then:
        1 * userService.query(_) >> userMap
        1 * userService.populateTestCycleCaseDTO(_)
        result1.body.rank != caseDTO.get(0).rank
    }

    def "UpdateOneCase1"() {
        given:
        TestCycleCaseDTO searchDto = caseDTO.get(1);
        searchDto.setRank(searchDto.rank)
        searchDto.setExecutionStatus(1L)
        searchDto.setObjectVersionNumber(1L)
        searchDto.setComment("comment1")
        searchDto.setAssignedTo(10L)
        when:
        restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/update", searchDto, TestCycleCaseDTO, 142)
        then:
        0 * userService.query(_)
        1 * userService.populateTestCycleCaseDTO(_)
    }

    def "UpdateOneCase2"() {
        given:
        TestCycleCaseDTO searchDto = caseDTO.get(1);
        searchDto.setRank(searchDto.rank)
        searchDto.setExecutionStatus(1L)
        searchDto.setObjectVersionNumber(2L)
        searchDto.setComment(null)
        searchDto.setAssignedTo(0L)
        Map userMap = Maps.newHashMap(4L, new UserDO(loginName: "login", realName: "real"))
        userMap.put(10L, new UserDO(loginName: "login", realName: "real"))
        when:
        restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/update", searchDto, TestCycleCaseDTO, 142)
        then:
        1 * userService.query(_) >> userMap
        1 * userService.populateTestCycleCaseDTO(_)
    }

    def "UpdateOneCase3"() {
        given:
        TestCycleCaseDTO searchDto = caseDTO.get(2);
        searchDto.setRank(searchDto.rank)
        searchDto.setExecutionStatus(1L)
        searchDto.setObjectVersionNumber(1L)
        searchDto.setComment("comment1")
        searchDto.setAssignedTo(10L)
        Map userMap = Maps.newHashMap(4L, new UserDO(loginName: "login", realName: "real"))
        userMap.put(10L, new UserDO(loginName: "login", realName: "real"))
        when:
        restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/update", searchDto, TestCycleCaseDTO, 142)
        then:
        1 * userService.query(_) >> userMap
        1 * userService.populateTestCycleCaseDTO(_)
    }

    def "QueryByCycleWithFilterArgs"() {
        given:
        TestCycleCaseDTO searchDto = new TestCycleCaseDTO(cycleId: cycleIds.get(0))

        when:
        def result = restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/query/filtered/{cycleId}?page={page}&size={size}", searchDto, Page.class, 142, 1, 0, 10)
        then:
        1 * userService.query(_) >> new HashMap<>()
        and:
        result.body.size() == 3
    }

    def "exportExcle"() {
        given:
        excelServiceHandler = Mock(ExcelServiceHandler)
        testCycleCaseController.setExcelServiceHandler(excelServiceHandler)

        when:
        testCycleCaseController.downLoad(1L, 1L, new MockHttpServletRequest(), new MockHttpServletResponse(), 1L)

        then:
        1 * excelServiceHandler.exportCycleCaseInOneCycle(_, _, _, _, _)
    }

    //覆盖excelService中的方法
    def "ExportCycleCaseInOneCycleByTransaction"() {
        given:
        //将被spring代理的对象取出来
        Field h = relExcelService.getClass().getDeclaredField("CGLIB\$CALLBACK_0")
        h.setAccessible(true)
        Object dynamicAdvisedInterceptor = h.get((Object) relExcelService)
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised")
        advised.setAccessible(true)
        target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget()

        MockHttpServletRequest request = new MockHttpServletRequest()
        request.addHeader("User-Agent", "Chrome")

        Map issueMaps = Maps.newHashMap(98L, new IssueInfosDTO(issueName: "issueName", issueNum: 98L, summary: "CylceCaseExcel测试",
                assigneeName: "CylceCaseExcel测试人", statusName: "CylceCaseExcel测试状态"));
        issueMaps.put(97L, new IssueInfosDTO(issueName: "issueName1", issueNum: 97L, summary: "CylceCaseExcel测试",
                assigneeName: "CylceCaseExcel测试人", statusName: "CylceCaseExcel测试状态"))

        ProjectDTO projectDTO = new ProjectDTO(name: "CaseExcel测试项目")
        //循环
        TestFileLoadHistoryE historyE = TestFileLoadHistoryEFactory.create()
        historyE.setProjectId(1L)
        historyE.setActionType(TestFileLoadHistoryE.Action.DOWNLOAD_ISSUE)
        historyE.setStatus(TestFileLoadHistoryE.Status.FAILURE)
        historyE.setLinkedId(caseDTO.get(0).getCycleId())
        historyE.setSourceType(TestFileLoadHistoryE.Source.CYCLE)
        TestFileLoadHistoryE resHistoryE = historyService.insertOne(historyE)

        when:
        target.exportFailCaseByTransaction(55555L, resHistoryE.getId(), 1L)

        then:
        1 * testCaseService.getProjectInfo(_) >> projectDTO
        1 * fileFeignClient.uploadFile(_, _, _) >> new ResponseEntity<String>(new String(), HttpStatus.OK)
        2 * notifyService.postWebSocket(_, _, _)

        when:
        target.exportCycleCaseInOneCycleByTransaction(caseDTO.get(0).getCycleId(), 142, request, new MockHttpServletResponse(), 1L, 1L)

        then:
        1 * testCaseService.getVersionInfo(_) >> Maps.newHashMap(11111L, new ProductVersionDTO(name: "versionName"))
        2 * userService.query(_) >> Maps.newHashMap(10L, new UserDO(realName: "real", loginName: "login"))
        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueMaps
        1 * testCaseService.getProjectInfo(_) >> new ProjectDTO(name: "project1")
        1 * fileService.uploadFile(_, _, _) >> new ResponseEntity<String>(new String(), HttpStatus.OK)
        when:
        target.exportCycleCaseInOneCycleByTransaction(caseDTO.get(2).getCycleId(), 142, request, new MockHttpServletResponse(), 1L, 1L)

        then:
        1 * testCaseService.getVersionInfo(_) >> Maps.newHashMap(11111L, new ProductVersionDTO(name: "versionName"))
        2 * userService.query(_) >> Maps.newHashMap(10L, new UserDO(realName: "real", loginName: "login"))
        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueMaps
        1 * testCaseService.getProjectInfo(_) >> new ProjectDTO(name: "project1")
        1 * fileService.uploadFile(_, _, _) >> new ResponseEntity<String>(new String(), HttpStatus.OK)
    }


    def "QuerySubStep"() {
        when:
        ResponseEntity<Page<TestCycleCaseStepDTO>> page = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/step/query/{cycleCaseId}?organizationId=1", Page.class, 142, caseDTO.get(0).getExecuteId())
        then:
        page.getBody().size() == 1
        TestCycleCaseStepDTO dto = page.getBody().get(0)
        dto.setComment("111")
        expect:
        restTemplate.put("/v1/projects/{project_id}/cycle/case/step", Lists.newArrayList(dto), 142)


    }


    def "delete"() {
        expect:
        restTemplate.delete("/v1/projects/{project_id}/cycle/case?cycleCaseId={cycleCaseId}", 142, caseDTO.get(0).getExecuteId())
    }


}
