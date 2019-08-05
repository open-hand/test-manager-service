//package io.choerodon.test.manager.app.service.impl
//
//import com.github.pagehelper.PageInfo
//import io.choerodon.agile.api.vo.*
//import io.choerodon.core.exception.CommonException
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.api.vo.IssueInfosVO
//import io.choerodon.test.manager.app.service.ExcelService
//import io.choerodon.test.manager.app.service.FileService
//import io.choerodon.test.manager.app.service.NotifyService
//import io.choerodon.test.manager.app.service.TestCaseService
//import io.choerodon.test.manager.app.service.UserService
//import io.choerodon.test.manager.domain.service.ITestFileLoadHistoryService
//import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE
//import io.choerodon.test.manager.domain.test.manager.factory.TestFileLoadHistoryEFactory
//import io.choerodon.test.manager.infra.vo.TestIssueFolderDTO
//import io.choerodon.test.manager.infra.vo.TestIssueFolderRelDTO
//import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper
//import io.choerodon.test.manager.infra.mapper.TestIssueFolderRelMapper
//import org.assertj.core.util.Lists
//import org.assertj.core.util.Maps
//import org.springframework.aop.framework.AdvisedSupport
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.context.annotation.Import
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.mock.web.MockHttpServletRequest
//import org.springframework.mock.web.MockHttpServletResponse
//import spock.lang.Shared
//import spock.lang.Specification
//import spock.lang.Stepwise
//
//import java.lang.reflect.Field
//
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
//
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//@Stepwise
//class ExcelServiceImplSpec extends Specification {
//
//    @Autowired
//    TestCaseService testCaseService
//
//    @Autowired
//    UserService userService
//
//    @Autowired
//    FileService fileService
//
//    @Autowired
//    ExcelService excelService
//
//    @Autowired
//    NotifyService notifyService
//
//    @Autowired
//    TestIssueFolderMapper testIssueFolderMapper
//
//    @Autowired
//    TestIssueFolderRelMapper testIssueFolderRelMapper
//
//    @Autowired
//    ITestFileLoadHistoryService historyService
//
//    @Shared
//    Long[] versionIds = new Long[1]
//    @Shared
//    ProjectDTO projectDTO
//    @Shared
//    LookupTypeWithValuesDTO lookupTypeWithValuesDTO
//    @Shared
//    List<UserDTO> userDTOS
//    @Shared
//    PageInfo page
//    @Shared
//    Map<Long, ProductVersionDTO> versionInfo
//    @Shared
//    List<IssueStatusDTO> issueStatusDTOS
//    @Shared
//    MockHttpServletRequest request
//    @Shared
//    Object target
//    @Shared
//    TestIssueFolderDTO resFolderDO
//    @Shared
//    TestIssueFolderRelDTO resFolderRelDO
//    @Shared
//    def projectId = 55555L
//    @Shared
//    def versionId = 55555L
//    @Shared
//    Long[] issuesId = new Long[2]
//    @Shared
//    Map<Long, IssueInfosVO> issueInfosDTOMap
//
//    void setupSpec() {
//        issuesId[0] = 55555L
//        issuesId[1] = 55556L
//        issueInfosDTOMap = Maps.newHashMap(issuesId[0], new IssueInfosVO(issueId: issuesId[0], issueNum: 1L, summary: "CaseExcel测试",
//                priorityVO: new PriorityVO(id: 1L, name: "CaseExcel测试"), assigneeName: "CaseExcel测试人", statusName: "CaseExcel测试状态"))
//        versionIds[0] = versionId
//        projectDTO = new ProjectDTO(name: "CaseExcel测试项目")
//        List<LookupValueDTO> lookupValueDTOS = Lists.newArrayList(new LookupValueDTO())
//        lookupTypeWithValuesDTO = new LookupTypeWithValuesDTO(lookupValues: lookupValueDTOS)
//        userDTOS = Lists.newArrayList(new UserDTO(loginName: "1", realName: "test", id: 1L))
//        page = new PageInfo<UserDTO>()
//        page.setList(userDTOS)
//        ProductVersionDTO productVersionDTO = new ProductVersionDTO();
//        productVersionDTO.setName("CaseExcel测试版本")
//        versionInfo = Maps.newHashMap(versionId, productVersionDTO)
//        issueStatusDTOS = Lists.newArrayList(new IssueStatusDTO())
//        request = new MockHttpServletRequest()
//        request.addHeader("User-Agent", "Chrome")
//    }
//
//    def "exportCaseProjectByTransaction"() {
//        given:
//        //将被spring代理的对象取出来
//        Field h = excelService.getClass().getDeclaredField("CGLIB\$CALLBACK_0")
//        h.setAccessible(true)
//        Object dynamicAdvisedInterceptor = h.get((Object) excelService)
//        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised")
//        advised.setAccessible(true)
//        target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget()
//
//        TestIssueFolderDTO testIssueFolderDO = new TestIssueFolderDTO(name: "caseExcel导出测试", versionId: versionId, projectId: projectId)
//        testIssueFolderMapper.insert(testIssueFolderDO)
//        resFolderDO = testIssueFolderMapper.selectOne(testIssueFolderDO)
//
//        TestIssueFolderRelDTO testIssueFolderRelDO = new TestIssueFolderRelDTO(issueId: issuesId[0], projectId: projectId, versionId: versionId, folderId: resFolderDO.getFolderId())
//        testIssueFolderRelMapper.insert(testIssueFolderRelDO)
//        resFolderRelDO = testIssueFolderRelMapper.selectOne(testIssueFolderRelDO)
//
//        when:
//        target.exportCaseProjectByTransaction(projectId, request, new MockHttpServletResponse(), 1L, 1L)
//
//        then:
//        1 * testCaseService.getProjectInfo(_) >> projectDTO
//        1 * testCaseService.getVersionIds(_) >> versionIds
//        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueInfosDTOMap
//        1 * testCaseService.queryLookupValueByCode(_, _) >> lookupTypeWithValuesDTO
//        1 * userService.list(_, _, _, _) >> new ResponseEntity<PageInfo<UserDTO>>(page, HttpStatus.OK)
//        1 * testCaseService.getVersionInfo(_) >> versionInfo
//        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS
//        1 * fileService.uploadFile(_, _, _) >> new ResponseEntity<String>("http://minio.staging.saas.hand-china.com/test/file_5bf86f5c8e384b66b64a51689a81d831_.xlsx", HttpStatus.OK)
//        (4.._) * notifyService.postWebSocket(_, _, _)
//    }
//
//    def "exportCaseVersionByTransaction"() {
//        when:
//        target.exportCaseVersionByTransaction(projectId, versionId, request, new MockHttpServletResponse(), 1L, 1L)
//
//        then:
//        1 * testCaseService.getProjectInfo(_) >> projectDTO
//        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueInfosDTOMap
//        1 * testCaseService.queryLookupValueByCode(_, _) >> lookupTypeWithValuesDTO
//        1 * userService.list(_, _, _, _) >> new ResponseEntity<PageInfo>(page, HttpStatus.OK)
//        2 * testCaseService.getVersionInfo(_) >> versionInfo
//        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS
//        1 * fileService.uploadFile(_, _, _) >> new ResponseEntity<String>(new String(), HttpStatus.OK)
//        (4.._) * notifyService.postWebSocket(_, _, _)
//
//        when:"fileFeign异常情况"
//        def exception = target.exportCaseVersionByTransaction(projectId, versionId, request, new MockHttpServletResponse(), 1L, 1L)
//
//        then:
//        1 * testCaseService.getProjectInfo(_) >> projectDTO
//        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueInfosDTOMap
//        1 * testCaseService.queryLookupValueByCode(_, _) >> lookupTypeWithValuesDTO
//        1 * userService.list(_, _, _, _) >> new ResponseEntity<PageInfo>(page, HttpStatus.OK)
//        2 * testCaseService.getVersionInfo(_) >> versionInfo
//        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS
//        1 * fileService.uploadFile(_, _, _) >> new CommonException("error.file.upload")
//        (4.._) * notifyService.postWebSocket(_, _, _)
//    }
//
//    def "exportCaseFolderByTransaction"() {
//        given:
//        testIssueFolderRelMapper.delete(resFolderRelDO)
//
//        when:
//        target.exportCaseFolderByTransaction(projectId, resFolderDO.getFolderId(), request, new MockHttpServletResponse(), 1L, 1L)
//
//        then:
//        1 * testCaseService.getProjectInfo(_) >> projectDTO
//        1 * testCaseService.queryLookupValueByCode(_, _) >> lookupTypeWithValuesDTO
//        1 * userService.list(_, _, _, _) >> new ResponseEntity<PageInfo>(page, HttpStatus.OK)
//        1 * testCaseService.getVersionInfo(_) >> versionInfo
//        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS
//        1 * fileService.uploadFile(_, _, _) >> new ResponseEntity<String>("error.file.upload", HttpStatus.INTERNAL_SERVER_ERROR)
//        (4.._) * notifyService.postWebSocket(_, _, _)
//    }
//
//    def "exportFailCaseByTransaction"() {
//        given:
//        TestFileLoadHistoryE historyE = TestFileLoadHistoryEFactory.create()
//        //项目
//        historyE.setProjectId(1L)
//        historyE.setActionType(TestFileLoadHistoryE.Action.DOWNLOAD_ISSUE)
//        historyE.setSourceType(TestFileLoadHistoryE.Source.PROJECT)
//        historyE.setStatus(TestFileLoadHistoryEnums.Status.FAILURE)
//        historyE.setLinkedId(1L)
//        TestFileLoadHistoryE projectHistoryE = historyService.insertOne(historyE)
//        //版本
//        historyE.setSourceType(TestFileLoadHistoryE.Source.VERSION)
//        TestFileLoadHistoryE versionHistoryE = historyService.insertOne(historyE)
//        //文件夹
//        historyE.setSourceType(TestFileLoadHistoryE.Source.FOLDER)
//        historyE.setLinkedId(resFolderDO.getFolderId())
//        TestFileLoadHistoryE folderHistoryE = historyService.insertOne(historyE)
//
//        when:
//        target.exportFailCaseByTransaction(projectId, projectHistoryE.getId(), 1L)
//
//        then:
//        1 * testCaseService.getProjectInfo(_) >> projectDTO
//        1 * fileService.uploadFile(_, _, _) >> new ResponseEntity<String>(new String(), HttpStatus.OK)
//        2 * notifyService.postWebSocket(_, _, _)
//
//        when:
//        target.exportFailCaseByTransaction(projectId, versionHistoryE.getId(), 1L)
//
//        then:
//        1 * testCaseService.getProjectInfo(_) >> projectDTO
//        1 * testCaseService.getVersionInfo(_) >> versionInfo
//        1 * fileService.uploadFile(_, _, _) >> new ResponseEntity<String>(new String(), HttpStatus.INTERNAL_SERVER_ERROR)
//        1 * notifyService.postWebSocket(_, _, _)
//
//        when:
//        target.exportFailCaseByTransaction(projectId, folderHistoryE.getId(), 1L)
//
//        then:
//        1 * testCaseService.getProjectInfo(_) >> projectDTO
//        1 * testCaseService.getVersionInfo(_) >> versionInfo
//        1 * fileService.uploadFile(_, _, _) >> new ResponseEntity<String>(new String(), HttpStatus.OK)
//        2 * notifyService.postWebSocket(_, _, _)
//    }
//
//    def "ExportCaseTemplate"() {
//        given:
//        request.removeAttribute("User-Agent")
//        request.addHeader("User-Agent", "Firefox")
//
//        when:
//        excelService.exportCaseTemplate(1L, request, new MockHttpServletResponse())
//
//        then:
//        1 * testCaseService.getProjectInfo(_) >> projectDTO
//        1 * testCaseService.getVersionIds(_) >> versionIds
//        1 * testCaseService.queryLookupValueByCode(_, _) >> lookupTypeWithValuesDTO
//        1 * userService.list(_, _, _, _) >> new ResponseEntity<PageInfo>(page, HttpStatus.OK)
//        1 * testCaseService.getVersionInfo(_) >> versionInfo
//        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS
//
//        and: "清理数据"
//        testIssueFolderMapper.delete(resFolderDO)
//    }
//}