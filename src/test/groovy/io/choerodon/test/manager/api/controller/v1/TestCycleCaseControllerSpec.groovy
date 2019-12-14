package io.choerodon.test.manager.api.controller.v1


import io.choerodon.test.manager.api.vo.agile.ProductVersionDTO
import io.choerodon.test.manager.api.vo.agile.ProjectDTO
import io.choerodon.test.manager.api.vo.agile.SearchDTO
import io.choerodon.test.manager.api.vo.agile.UserDO
import io.choerodon.core.convertor.ConvertHelper
import com.github.pagehelper.PageInfo
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.vo.IssueInfosVO
import io.choerodon.test.manager.api.vo.TestCaseStepVO
import io.choerodon.test.manager.api.vo.TestCycleCaseStepVO
import io.choerodon.test.manager.api.vo.TestCycleCaseVO
import io.choerodon.test.manager.api.vo.TestCycleVO
import io.choerodon.test.manager.api.vo.TestFileLoadHistoryVO
import io.choerodon.test.manager.api.vo.TestStatusVO
import io.choerodon.test.manager.app.service.ExcelService
import io.choerodon.test.manager.app.service.ExcelServiceHandler
import io.choerodon.test.manager.app.service.FileService
import io.choerodon.test.manager.app.service.NotifyService
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.TestCaseStepService
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService
import io.choerodon.test.manager.app.service.TestCycleCaseService
import io.choerodon.test.manager.app.service.TestCycleService
import io.choerodon.test.manager.app.service.TestFileLoadHistoryService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.app.service.impl.TestCycleCaseServiceImpl
import io.choerodon.test.manager.infra.dto.TestFileLoadHistoryDTO
import io.choerodon.test.manager.infra.enums.TestCycleType
import io.choerodon.test.manager.infra.enums.TestFileLoadHistoryEnums
import io.choerodon.test.manager.infra.exception.TestCaseStepCreateException
import io.choerodon.test.manager.infra.util.RedisTemplateUtil
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO
import io.choerodon.test.manager.infra.dto.TestStatusDTO
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper
import io.choerodon.test.manager.infra.mapper.TestStatusMapper
import org.apache.commons.lang.StringUtils
import org.assertj.core.util.Lists
import org.assertj.core.util.Maps
import org.modelmapper.ModelMapper
import org.springframework.aop.framework.AdvisedSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.data.redis.support.atomic.RedisAtomicLong
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.ui.ModelMap
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.lang.reflect.Field

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
//@Stepwise
class TestCycleCaseControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    TestCaseService testCaseService

    @Autowired
    UserService userService

    @Shared
    TestStatusDTO statusDO = new TestStatusDTO()

    @Autowired
    TestStatusMapper testStatusMapper

    @Shared
    List<TestCycleCaseVO> caseDTO = new ArrayList<>()

    @Shared
    List<Long> cycleIds = new ArrayList<>()

    @Autowired
    TestCycleCaseMapper testCycleCaseMapper

    @Autowired
    TestCaseStepService caseStepService

    @Autowired
    TestCycleCaseController testCycleCaseController

    private ExcelServiceHandler excelServiceHandler

    @Autowired
    ExcelService relExcelService

    @Autowired
    FileService fileService

    @Autowired
//    ITestFileLoadHistoryService historyService
    TestFileLoadHistoryService historyService
    @Autowired
    FileService fileFeignClient

    @Autowired
    NotifyService notifyService

    @Autowired
    RedisTemplateUtil redisTemplateUtil

    @Autowired
    TestIssueFolderMapper folderMapper

    @Autowired
    TestCycleCaseDefectRelService testCycleCaseDefectRelService

    @Autowired
//    ITestCycleService iTestCycleService
    TestCycleService testCycleService

    @Shared
    Object target

    def "initEnv"() {
        given:
        TestIssueFolderDTO folderDO = new TestIssueFolderDTO(name: "111", projectId: 142L, versionId: 11111L)
        folderMapper.insert(folderDO)

        TestCycleVO testCycleDTO1 = new TestCycleVO()
        testCycleDTO1.setFromDate(new Date())
        testCycleDTO1.setToDate(new Date())
        testCycleDTO1.setCycleName("testCycleCaseInsert")
        testCycleDTO1.setFolderId(folderDO.getFolderId())
        testCycleDTO1.setVersionId(11111L)
        testCycleDTO1.setType(TestCycleType.CYCLE)
        testCycleDTO1.setObjectVersionNumber(1L)


        statusDO.setProjectId(new Long(142))
        statusDO.setStatusName("未执行")
        statusDO.setStatusColor("yellow")
        statusDO.setStatusType("CYCLE_CASE")


        TestCaseStepVO stepDTO1 = new TestCaseStepVO(issueId: 98L, testStep: "11")

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle', testCycleDTO1, TestCycleVO, 142L)
        then:
        entity.statusCode.is2xxSuccessful()
        and:
        entity.body != null
        cycleIds.add(entity.body.cycleId)

        when: '向插入status的接口发请求'
        entity = restTemplate.postForEntity('/v1/projects/{project_id}/status', statusDO, TestStatusVO, 142)
        then:
        entity.statusCode.is2xxSuccessful()

        and:
        entity.body != null
        StringUtils.equals(entity.getBody().statusName, "未执行")

        when:
        TestCycleVO testCycleDTO2 = new TestCycleVO()
        testCycleDTO2.setCycleName("childCycle")
        testCycleDTO2.setFolderId(11111L)
        testCycleDTO2.setVersionId(11111L)
        testCycleDTO2.setFromDate(new Date())
        testCycleDTO2.setToDate(new Date())
        testCycleDTO2.setType(TestCycleType.FOLDER)
        testCycleDTO2.setParentCycleId(cycleIds.get(0))
        testCycleDTO2.setObjectVersionNumber(1L)
        def entity1 = restTemplate.postForEntity('/v1/projects/{project_id}/cycle', testCycleDTO2, TestCycleVO, 142L)
        then:
        entity1.statusCode.is2xxSuccessful()
    }

    def "InsertOneCase"() {
        given:
        TestCycleCaseVO vo = new TestCycleCaseVO(cycleId: cycleIds.get(0), issueId: 98L, assignedTo: 10L)
        TestCycleCaseVO dto2 = new TestCycleCaseVO(cycleId: cycleIds.get(0), issueId: 97L, assignedTo: 10L, comment: "[{'insert':'ffff1'}]")
        TestCycleCaseVO dto3 = new TestCycleCaseVO(issueId: 96L, cycleId: cycleIds.get(0))

        when:
        def result = restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/insert", vo, TestCycleCaseVO, 142)
        then:
        result.body.executeId != null
        and:
        caseDTO.add(result.body)

        when:
        result = restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/insert", dto2, TestCycleCaseVO, 142)
        then:
        result.body.executeId != null
        and:
        caseDTO.add(result.body)

        when:
        result = restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/insert", dto3, TestCycleCaseVO, 142)
        then:
        result.body.executeId != null
        and:
        caseDTO.add(result.body)
    }

    def "QueryOne"() {
        when:
        def result = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/query/one/{executeId}?cycleId=0&organizationId=1", TestCycleCaseVO, 142, caseDTO.get(0).executeId)
        then:
        //0 * testCycleCaseDefectRelService.populateDefectAndIssue(_, _, _)
        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> Maps.newHashMap(98L, new IssueInfosVO())
        1 * userService.populateTestCycleCaseDTO(_)

        and:
        result.body.cycleId == cycleIds.get(0)
    }

    def "QueryByIssuse"() {
        given:
        print("Start QueryByIssuse")
        when:
        def result = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/query/issue/{issueId}?organizationId=1", List, 142L, 98L)
        then:
        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> new HashMap<>()
        0 * userService.query(_) >> new HashMap<>()
        1 * testCaseService.getVersionInfo(_) >> Maps.newHashMap(11111L, new ProductVersionDTO())
        and:
        result.body.size() == 1
        when:
        restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/query/issue/{issueId}?organizationId=1", List, 142L, -1L)
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
        TestCycleCaseVO searchDto = new TestCycleCaseVO(cycleId: cycleIds.get(0), searchDTO: new SearchDTO(content: "test"))
        when:
        def result = restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/query/cycleId?page={page}&size={size}&organizationId=1", searchDto, PageInfo.class, 142, 0, 10)
        then:
        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> new HashMap<>()
        0 * userService.query(_) >> new HashMap<>()
        and:
        result.body.getList().size() == 0

        when:
        result = restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/query/cycleId?page={page}&size={size}&organizationId=1", searchDto, PageInfo.class, 142, 0, 1)
        then:
        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> new HashMap<>()
        0 * userService.query(_) >> new HashMap<>()
        and:
        result.body.getList().size() == 0
    }

    def "validateReturn"() {
        given:
        TestCaseService client = Mock(TestCaseService)
        TestCycleCaseService service = new TestCycleCaseServiceImpl(testCaseService: client)
        when:
        service.populateCycleCaseWithDefect(new ArrayList<TestCycleCaseVO>(), 144L, 1, false)
        then:
        0 * client.getIssueInfoMap(_, _, _, _)
        when:
        service.populateVersionBuild(144, null)
        then:
        1 * client.getVersionInfo(_) >> new HashMap<>()
    }

    def "UpdateOneCase"() {
        given:
        RedisAtomicLong RAL = Mock(RedisAtomicLong)
        redisTemplateUtil.getRedisAtomicLong(_, _) >> RAL
        TestCycleCaseVO searchDto = caseDTO.get(1);
        searchDto.setRank(searchDto.rank)
        searchDto.setAssignedTo(4L)
        searchDto.setComment("[{'insert':'ffff1'}]")
        searchDto.setExecutionStatus(3L)
        searchDto.setObjectVersionNumber(1L)
        Map userMap = Maps.newHashMap(4L, new UserDO(loginName: "login", realName: "real"))
        userMap.put(10L, new UserDO(loginName: "login", realName: "real"))
        when:
        def result1 = restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/update", searchDto, TestCycleCaseVO, 142)
        then:
        1 * userService.query(_) >> userMap
        1 * userService.populateTestCycleCaseDTO(_)
        result1.body.rank != caseDTO.get(0).rank
    }

    def "UpdateOneCase1"() {
        given:
        RedisAtomicLong RAL = Mock(RedisAtomicLong)
        redisTemplateUtil.getRedisAtomicLong(_, _) >> RAL
        TestCycleCaseVO searchDto = caseDTO.get(1);
        searchDto.setRank(searchDto.rank)
        searchDto.setExecutionStatus(1L)
        searchDto.setObjectVersionNumber(2L)
        searchDto.setComment("[{'insert':'ffff1'}]")
        searchDto.setAssignedTo(4L)
        when:
        restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/update", searchDto, TestCycleCaseVO, 142)
        then:
        1 * userService.populateTestCycleCaseDTO(_)
    }

    def "UpdateOneCase2"() {
        given:
        RedisAtomicLong RAL = Mock(RedisAtomicLong)
        redisTemplateUtil.getRedisAtomicLong(_, _) >> RAL
        TestCycleCaseVO searchDto = caseDTO.get(1);
        searchDto.setRank(searchDto.rank)
        searchDto.setExecutionStatus(1L)
        searchDto.setObjectVersionNumber(3L)
        searchDto.setComment(null)
        searchDto.setAssignedTo(0L)
        Map userMap = Maps.newHashMap(4L, new UserDO(loginName: "login", realName: "real"))
        userMap.put(10L, new UserDO(loginName: "login", realName: "real"))
        when:
        restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/update", searchDto, TestCycleCaseVO, 142)
        then:
        1 * userService.populateTestCycleCaseDTO(_)
        1 * userService.query(_) >> userMap
    }

    def "UpdateOneCase3"() {
        given:
        RedisAtomicLong RAL = Mock(RedisAtomicLong)
        redisTemplateUtil.getRedisAtomicLong(_, _) >> RAL
        TestCycleCaseVO searchDto = caseDTO.get(2);
        searchDto.setRank(searchDto.rank)
        searchDto.setExecutionStatus(1L)
        searchDto.setObjectVersionNumber(1L)
        searchDto.setComment("[{'insert':'ffff'}]")
        searchDto.setAssignedTo(10L)
        Map userMap = Maps.newHashMap(4L, new UserDO(loginName: "login", realName: "real"))
        userMap.put(10L, new UserDO(loginName: "login", realName: "real"))
        when:
        restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/update", searchDto, TestCycleCaseVO, 142)
        then:
        1 * userService.query(_) >> userMap
        1 * userService.populateTestCycleCaseDTO(_)
    }

    def "QueryByCycleWithFilterArgs"() {
        given:
        TestCycleCaseVO searchDto = new TestCycleCaseVO(cycleId: cycleIds.get(0))

        when:
        def result = restTemplate.postForEntity("/v1/projects/{project_id}/cycle/case/query/filtered/{cycleId}?page={page}&size={size}", searchDto, PageInfo.class, 142, cycleIds.get(0), 0, 10)
        then:
        1 * userService.query(_) >> new HashMap<>()
        and:
        result.body.getList().size() == 3
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
//    def "ExportCycleCaseInOneCycleByTransaction"() {
//        given:
//        //将被spring代理的对象取出来
//        Field h = relExcelService.getClass().getDeclaredField("CGLIB\$CALLBACK_0")
//        h.setAccessible(true)
//        Object dynamicAdvisedInterceptor = h.get((Object) relExcelService)
//        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised")
//        advised.setAccessible(true)
//        target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget()
//
//        MockHttpServletRequest request = new MockHttpServletRequest()
//        request.addHeader("User-Agent", "Chrome")
//
//        Map issueMaps = Maps.newHashMap(98L, new IssueInfosVO(issueName: "issueName", issueNum: 98L, summary: "CylceCaseExcel测试",
//                assigneeName: "CylceCaseExcel测试人", statusName: "CylceCaseExcel测试状态"));
//        issueMaps.put(97L, new IssueInfosVO(issueName: "issueName1", issueNum: 97L, summary: "CylceCaseExcel测试",
//                assigneeName: "CylceCaseExcel测试人", statusName: "CylceCaseExcel测试状态"))
//
//        ProjectDTO projectDTO = new ProjectDTO(name: "CaseExcel测试项目")
//        //循环
//        TestFileLoadHistoryVO historyE = TestFileLoadHistory.create()
//        historyE.setProjectId(1L)
//        historyE.setActionType(TestFileLoadHistoryEnums.Action.DOWNLOAD_ISSUE)
//        historyE.setStatus(TestFileLoadHistoryEnums.Status.FAILURE)
//        historyE.setLinkedId(caseDTO.get(0).getCycleId())
//        historyE.setSourceType(TestFileLoadHistoryEnums.Source.CYCLE)
//        TestFileLoadHistoryEnums resHistoryE = historyService.insertOne(historyE)
//
//
//        when:
//        target.exportFailCaseByTransaction(55555L, resHistoryE.getId(), 1L)
//
//        then:
//        1 * testCaseService.getProjectInfo(_) >> projectDTO
//        1 * fileFeignClient.uploadFile(_, _, _) >> new ResponseEntity<String>(new String(), HttpStatus.OK)
//        2 * notifyService.postWebSocket(_, _, _)
//
//        when:
//        target.exportCycleCaseInOneCycleByTransaction(caseDTO.get(0).getCycleId(), 142, request, new MockHttpServletResponse(), 1L, 1L)
//
//        then:
//        1 * testCaseService.getVersionInfo(_) >> Maps.newHashMap(11111L, new ProductVersionDTO(name: "versionName"))
//        2 * userService.query(_) >> Maps.newHashMap(10L, new UserDO(realName: "real", loginName: "login"))
//        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueMaps
//        1 * testCaseService.getProjectInfo(_) >> new ProjectDTO(name: "project1")
//        1 * fileService.uploadFile(_, _, _) >> new ResponseEntity<String>(new String(), HttpStatus.OK)
//        when:
//        target.exportCycleCaseInOneCycleByTransaction(caseDTO.get(2).getCycleId(), 142, request, new MockHttpServletResponse(), 1L, 1L)
//
//        then:
//        1 * testCaseService.getVersionInfo(_) >> Maps.newHashMap(11111L, new ProductVersionDTO(name: "versionName"))
//        2 * userService.query(_) >> Maps.newHashMap(10L, new UserDO(realName: "real", loginName: "login"))
//        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueMaps
//        1 * testCaseService.getProjectInfo(_) >> new ProjectDTO(name: "project1")
//        1 * fileService.uploadFile(_, _, _) >> new ResponseEntity<String>(new String(), HttpStatus.OK)
//    }

//    def "QuerySubStep"() {
//
//        when:
//        ResponseEntity<List<TestCycleCaseStepVO>> entity = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/step/query/{cycleCaseId}?organizationId=1", List.class, 142, caseDTO.get(0).getExecuteId())
//        then:
//        entity.getBody().size() == 1
//        TestCycleCaseStepVO vo = entity.getBody().get(0)
//        vo.setComment("111")
//        expect:
//        restTemplate.put("/v1/projects/{project_id}/cycle/case/step", Lists.newArrayList(vo), 142)
//    }


    def "delete"() {
        expect:
        restTemplate.delete("/v1/projects/{project_id}/cycle/case?cycleCaseId={cycleCaseId}", 142, caseDTO.get(0).getExecuteId())
    }


}
