//package io.choerodon.test.manager.api.controller.v1
//
//
//import io.choerodon.agile.api.vo.*
//import com.github.pagehelper.PageInfo
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.api.vo.IssueInfosVO
//import io.choerodon.test.manager.api.vo.agile.IssueLinkDTO
//import io.choerodon.test.manager.api.vo.agile.IssueStatusDTO
//import io.choerodon.test.manager.api.vo.agile.LookupTypeWithValuesDTO
//import io.choerodon.test.manager.api.vo.agile.LookupValueDTO
//import io.choerodon.test.manager.api.vo.agile.ProductVersionDTO
//import io.choerodon.test.manager.api.vo.agile.ProjectDTO
//import io.choerodon.test.manager.api.vo.agile.SearchDTO
//import io.choerodon.test.manager.api.vo.agile.UserDTO
//import io.choerodon.test.manager.app.service.ExcelImportService
//import io.choerodon.test.manager.app.service.ExcelService
//import io.choerodon.test.manager.app.service.ExcelServiceHandler
//import io.choerodon.test.manager.app.service.FileService
//import io.choerodon.test.manager.app.service.NotifyService
//import io.choerodon.test.manager.app.service.TestCaseService
//import io.choerodon.test.manager.app.service.UserService
//import io.choerodon.test.manager.app.service.impl.ExcelImportServiceImpl
//import io.choerodon.test.manager.infra.dto.TestCycleCaseDefectRelDTO
//import io.choerodon.test.manager.infra.enums.TestCycleCaseDefectCode
//import io.choerodon.test.manager.infra.feign.IssueFeignClient
//import io.choerodon.test.manager.infra.mapper.TestCycleCaseDefectRelMapper
//import org.apache.poi.ss.usermodel.Workbook
//import org.assertj.core.util.Lists
//import org.assertj.core.util.Maps
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.web.client.TestRestTemplate
//import org.springframework.context.annotation.Import
//import org.springframework.core.io.FileSystemResource
//import org.springframework.http.HttpEntity
//import org.springframework.http.HttpMethod
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.mock.web.MockHttpServletRequest
//import org.springframework.mock.web.MockHttpServletResponse
//import org.springframework.mock.web.MockMultipartFile
//import org.springframework.util.LinkedMultiValueMap
//import org.springframework.util.MultiValueMap
//import org.springframework.web.multipart.MultipartFile
//import spock.lang.Shared
//import spock.lang.Specification
//import spock.lang.Stepwise
//
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
//
///**
// * Created by zongw.lee@gmail.com
// */
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//@Stepwise
//class TestCaseControllerSpec extends Specification {
//    @Autowired
//    TestRestTemplate restTemplate
//
////    @Autowired
////    ExcelService excelService
//
//    @Autowired
//    TestCaseController testCaseController
//
//    private ExcelServiceHandler excelServiceHandler
//
//    @Autowired
//    TestCaseService testCaseService
//
//    @Autowired
//    UserService userService
//
//    @Autowired
//    NotifyService notifyService
//
//    @Autowired
//    FileService fileService
//
//    @Autowired
//    TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper
//
//    @Autowired
////    IExcelImportService iExcelImportService
//    ExcelImportService excelImportService
//
//    ExcelImportServiceImpl excelImportService1
//    IssueFeignClient issueFeignClient
//
//    @Shared
//    Long project_id = 1L
//
//    void setup() {
//        excelServiceHandler = Mock(ExcelServiceHandler)
//        testCaseController.setExcelServiceHandler(excelServiceHandler)
//
///*
//        excelImportService = Mock(ExcelImportService)
//        excelImportService new ExcelImportServiceImpl(issueFeignClient:issueFeignClient)
//*/
////        issueFeignClient = Mock(IssueFeignClient)
////        excelImportService = new ExcelImportServiceImpl(issueFeignClient: issueFeignClient)
//
//    }
//
//
//    def "DownLoadByProject"() {
//        when:
//        testCaseController.downLoadByProject(1L, new MockHttpServletRequest(), new MockHttpServletResponse(), 1L)
//
//        then:
//        1 * excelServiceHandler.exportCaseByProject(_, _, _, _)
//    }
//
//    def "DownLoadByVersion"() {
//        when:
//        testCaseController.downLoadByVersion(1L, 1L, new MockHttpServletRequest(), new MockHttpServletResponse(), 1L)
//
//        then:
//        1 * excelServiceHandler.exportCaseByVersion(_, _, _, _, _)
//    }
//
//    def "DownLoadByFolder"() {
//        when:
//        testCaseController.downLoadByFolder(1L, 1L, new MockHttpServletRequest(), new MockHttpServletResponse(), 1L)
//
//        then:
//        1 * excelServiceHandler.exportCaseByFolder(_, _, _, _, _)
//    }
//
//    def "DownLoadTemplate"() {
//        given:
//        Long[] issuesId = new Long[2]
//        issuesId[0] = 55555L
//        issuesId[1] = 55556L
//        Long[] versionIds = new Long[1]
//        versionIds[0] = 55555L
//        ProjectDTO projectDTO = new ProjectDTO(name: "CaseExcel测试项目")
//        List<LookupValueDTO> lookupValueDTOS = Lists.newArrayList(new LookupValueDTO())
//        LookupTypeWithValuesDTO lookupTypeWithValuesDTO = new LookupTypeWithValuesDTO(lookupValues: lookupValueDTOS)
//        List<UserDTO> userDTOS = Lists.newArrayList(new UserDTO(loginName: "1", realName: "test", id: 1L))
//        PageInfo pageInfo = new PageInfo<UserDTO>()
//        pageInfo.setList(userDTOS)
//        ProductVersionDTO productVersionDTO = new ProductVersionDTO()
//        productVersionDTO.setName("CaseExcel测试版本")
//        Map<Long, ProductVersionDTO> versionInfo = Maps.newHashMap(55555L, productVersionDTO)
//        List<IssueStatusDTO> issueStatusDTOS = Lists.newArrayList(new IssueStatusDTO())
//        MockHttpServletRequest request = new MockHttpServletRequest()
//        request.addHeader("User-Agent", "Chrome")
//
//        when:
//        restTemplate.getForEntity("/v1/projects/{project_id}/case/download/excel/template", null, 1L)
//
//        then:
//        1 * testCaseService.getProjectInfo(_) >> projectDTO
//        1 * testCaseService.getVersionIds(_) >> versionIds
//        1 * testCaseService.queryLookupValueByCode(_) >> lookupTypeWithValuesDTO
//        1 * userService.list(_, _, _, _) >> new ResponseEntity<PageInfo<UserDTO>>(pageInfo, HttpStatus.OK)
//        1 * testCaseService.getVersionInfo(_) >> versionInfo
//        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS
//    }
//
//    /* def "downloadImportTemplate"() {
//         when:
//         Workbook importTemp = excelImportService.buildImportTemp(1,1)
//         then:
//         File file = File.createTempFile("import_temp", ".xlsx")
//         importTemp.write(file.newOutputStream())
//         file.delete()
//     }*/
//
//    def "CreateFormsFromIssueToDefect"() {
//        given: '生成报表从issue到缺陷'
//        SearchDTO searchDTO = new SearchDTO()
//        HttpEntity<SearchDTO> httpEntity = new HttpEntity<SearchDTO>(searchDTO, null)
//        Map<Long, IssueInfosVO> issueInfosVOMap = new HashMap<Long, IssueInfosVO>()
//        IssueInfosVO issueInfosVO = new IssueInfosVO()
//        issueInfosVO.setIssueId(1L)
//        issueInfosVO.setProjectId(1L)
//        issueInfosVO.setIssueName("name")
//        issueInfosVO.setIssueNum("name1")
//        issueInfosVO.setStatusId(1L)
//        issueInfosVOMap.put(issueInfosVO.getIssueId(), issueInfosVO)
//
//        List<IssueLinkDTO> issueLinkDTOList = new ArrayList<>()
//        IssueLinkDTO issueLinkDTO = new IssueLinkDTO()
//        issueLinkDTO.setIssueId(1L)
//
//        when:
//        def res = restTemplate.exchange("/v1/projects/{project_id}/case/get/reporter/from/issue?organizationId=1",
//                HttpMethod.POST,
//                httpEntity,
//                PageInfo.class,
//                project_id)
//
//        then:
//        1 * testCaseService.getIssueInfoMapAndPopulatePageInfo(_, _, _, _, _) >> issueInfosVOMap
//        1 * testCaseService.getLinkIssueFromIssueToTest(_, _) >> issueLinkDTOList
//        res.statusCode.is2xxSuccessful()
//
//    }
//
//    def "CreateFormsFromIssueToDefectByIssueId"() {
//        given: '通过IssueId生成issue到缺陷的报表'
//        Long[] issueIds = new Long[2]
//        issueIds[0] = 1L
//        issueIds[1] = 2L
//        HttpEntity<Long[]> httpEntity = new HttpEntity<Long[]>(issueIds, null)
//
//        Map<Long, IssueInfosVO> issueInfosVOMap = new HashMap<Long, IssueInfosVO>()
//        IssueInfosVO issueInfosVO = new IssueInfosVO()
//        issueInfosVO.setIssueId(1L)
//        issueInfosVO.setProjectId(1L)
//        issueInfosVO.setIssueName("name")
//        issueInfosVO.setIssueNum("name1")
//        issueInfosVO.setStatusId(1L)
//        issueInfosVOMap.put(issueInfosVO.getIssueId(), issueInfosVO)
//
//        List<IssueLinkDTO> issueLinkDTOList = new ArrayList<>()
//        IssueLinkDTO issueLinkDTO = new IssueLinkDTO()
//        issueLinkDTO.setIssueId(1L)
//        when:
//        def res = restTemplate.exchange("/v1/projects/{project_id}/case/get/reporter/from/issue/by/issueId?organizationId=1",
//                HttpMethod.POST,
//                httpEntity,
//                List.class,
//                project_id)
//
//        then:
//        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueInfosVOMap
//        1 * testCaseService.getLinkIssueFromIssueToTest(_, _) >> issueLinkDTOList
//        res.statusCode.is2xxSuccessful()
//
//    }
//
//    def "CreateFormDefectFromIssueById"() {
//        given: '通过缺陷Id生成报表从缺陷到issue'
//        Long[] issueIds = new Long[2]
//        issueIds[0] = 1L
//        issueIds[1] = 2L
//        HttpEntity<Long[]> httpEntity = new HttpEntity<Long[]>(issueIds, null)
//
//        Map<Long, IssueInfosVO> issueInfosVOMap = new HashMap<Long, IssueInfosVO>()
//        IssueInfosVO issueInfosVO = new IssueInfosVO()
//        issueInfosVO.setIssueId(1L)
//        issueInfosVO.setProjectId(1L)
//        issueInfosVO.setIssueName("name")
//        issueInfosVO.setIssueNum("name1")
//        issueInfosVO.setStatusId(1L)
//        issueInfosVOMap.put(issueInfosVO.getIssueId(), issueInfosVO)
//
//
//        when:
//        def res = restTemplate.exchange("/v1/projects/{project_id}/case/get/reporter/from/defect/by/issueId?organizationId=1",
//                HttpMethod.POST,
//                httpEntity,
//                List.class,
//                project_id)
//
//        then:
//        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueInfosVOMap
//        res.statusCode.is2xxSuccessful()
//
//        and:
//
//        TestCycleCaseDefectRelDTO dto = new TestCycleCaseDefectRelDTO()
//        dto.setId(1L)
//        dto.setProjectId(1L)
//        dto.setIssueId(1L)
//        dto.setDefectType(TestCycleCaseDefectCode.CYCLE_CASE)
//        dto.setDefectLinkId(1L)
//        testCycleCaseDefectRelMapper.insert(dto)
//
//        def all = testCycleCaseDefectRelMapper.selectAll()
//
//        List<IssueLinkDTO> issueLinkDTOList = new ArrayList<>()
//        IssueLinkDTO issueLinkDTO = new IssueLinkDTO()
//        issueLinkDTO.setIssueId(1L)
//
//
//        when:
//        def res2 = restTemplate.exchange("/v1/projects/{project_id}/case/get/reporter/from/defect/by/issueId?organizationId=1",
//                HttpMethod.POST,
//                httpEntity,
//                List.class,
//                project_id)
//        then:
//        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueInfosVOMap
//        // 1 * testCaseService.getLinkIssueFromIssueToTest(_, _) >> issueLinkDTOList
//        1 * testCaseService.getLinkIssueFromTestToIssue(_, _) >> issueLinkDTOList
//
//        res2.statusCode.is2xxSuccessful()
//
//    }
//
//    def "CreateFormDefectFromIssue"() {
//        given: '生成报表从缺陷到issue'
//        SearchDTO searchDTO = new SearchDTO()
//        HttpEntity<SearchDTO> httpEntity = new HttpEntity<>(searchDTO, null)
//
//        Long[] allFilteredIssues = new Long[2]
//        allFilteredIssues[0] = 1L
//        allFilteredIssues[1] = 2L
//
//        Map<Long, IssueInfosVO> issueInfosVOMap = new HashMap<Long, IssueInfosVO>()
//        IssueInfosVO issueInfosVO = new IssueInfosVO()
//        issueInfosVO.setIssueId(1L)
//        issueInfosVO.setProjectId(1L)
//        issueInfosVO.setIssueName("name")
//        issueInfosVO.setIssueNum("name1")
//        issueInfosVO.setStatusId(1L)
//        issueInfosVOMap.put(issueInfosVO.getIssueId(), issueInfosVO)
//
//        when:
//        def res = restTemplate.exchange("/v1/projects/{project_id}/case/get/reporter/from/defect?organizationId=1",
//                HttpMethod.POST,
//                httpEntity,
//                PageInfo.class,
//                project_id)
//
//        then:
//        1 * testCaseService.queryIssueIdsByOptions(_, _) >> allFilteredIssues
//        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueInfosVOMap
//        res.statusCode.is2xxSuccessful()
//
//    }
//
//    def "DownExcelFail"() {
//        given: '导出之前失败过的excel'
//        ProjectDTO projectDTO = new ProjectDTO()
//        projectDTO.setId(project_id)
//        projectDTO.setName("project")
//
//        ProductVersionDTO productVersionDTO = new ProductVersionDTO()
//        productVersionDTO.setName("CaseExcel测试版本")
//        Map<Long, ProductVersionDTO> versionInfo = Maps.newHashMap(1L, productVersionDTO)
////        List<IssueStatusDTO> issueStatusDTOS = Lists.newArrayList(new IssueStatusDTO())
//        when:
//        def res = restTemplate.exchange("/v1/projects/{project_id}/case/download/excel/fail?historyId=1",
//                HttpMethod.GET,
//                null,
//                ResponseEntity.class,
//                project_id)
//        then:
//        1 * excelServiceHandler.exportFailCase(_, _)
////        1 * testCaseService.getProjectInfo(_) >> projectDTO
////        1 * testCaseService.getVersionInfo() >> versionInfo
////        2 * notifyService.postWebSocket(_, _, _)
////        1 * fileService.uploadFile(_, _, _) >> new ResponseEntity<>(HttpStatus.OK)
//
//        res.statusCode.is2xxSuccessful()
//
//
//    }
//
//    def "DownloadImportTemplate"() {
//        given: '生成excel导入模板'
//        when:
//        def res = restTemplate.exchange("/v1/projects/{project_id}/case/download/excel/import_template?organizationId=1",
//                HttpMethod.GET,
//                null,
//                ResponseEntity.class,
//                project_id)
//        then:
//        1 * excelImportService.downloadImportTemp(_, _, _, _)
//        res.statusCode.is2xxSuccessful()
//
//    }
//
//    def "ImportIssues"() {
//        given: '从excel导入模板导入issue以及测试步骤'
//        FileSystemResource resource = new FileSystemResource(new File("D:\\test2.xlsx"))
//        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>()
//        param.add("file", resource)
//        HttpEntity<MultipartFile> httpEntity = new HttpEntity<MultipartFile>(param, null)
//
//        when:
//        def res = restTemplate.exchange("/v1/projects/{project_id}/case/import/testCase?versionId=1&organizationId=1",
//                HttpMethod.POST,
//                httpEntity,
//                ResponseEntity.class,
//                project_id)
//        then:
//       // 1 * excelImportService.importIssueByExcel(_, _, _, _)
//        //1 * testCaseService.batchDeleteIssues(_, _)
//        res.statusCode.is2xxSuccessful()
//    }
//}
