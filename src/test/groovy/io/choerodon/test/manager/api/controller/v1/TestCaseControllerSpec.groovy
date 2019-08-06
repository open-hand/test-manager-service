//package io.choerodon.test.manager.api.controller.v1
//
//
//import io.choerodon.agile.api.vo.*
//import com.github.pagehelper.PageInfo
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.app.service.ExcelService
//import io.choerodon.test.manager.app.service.ExcelServiceHandler
//import io.choerodon.test.manager.app.service.TestCaseService
//import io.choerodon.test.manager.app.service.UserService
//
//import org.apache.poi.ss.usermodel.Workbook
//import org.assertj.core.util.Lists
//import org.assertj.core.util.Maps
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.web.client.TestRestTemplate
//import org.springframework.context.annotation.Import
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.mock.web.MockHttpServletRequest
//import org.springframework.mock.web.MockHttpServletResponse
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
//    TestRestTemplate restTemplate;
//
//    @Autowired
//    ExcelService excelService
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
//    IExcelImportService iExcelImportService
//
//    void setup() {
//        excelServiceHandler = Mock(ExcelServiceHandler)
//        testCaseController.setExcelServiceHandler(excelServiceHandler)
//    }
//
//
//    def "DownLoadByProject"() {
//        when:
//        testCaseController.downLoadByProject(1L, new MockHttpServletRequest(), new MockHttpServletResponse(),1L)
//
//        then:
//        1 * excelServiceHandler.exportCaseByProject(_, _, _,_)
//    }
//
//    def "DownLoadByVersion"() {
//        when:
//        testCaseController.downLoadByVersion(1L, 1L, new MockHttpServletRequest(), new MockHttpServletResponse(),1L)
//
//        then:
//        1 * excelServiceHandler.exportCaseByVersion(_, _, _, _,_)
//    }
//
//    def "DownLoadByFolder"() {
//        when:
//        testCaseController.downLoadByFolder(1L, 1L, new MockHttpServletRequest(), new MockHttpServletResponse(),1L)
//
//        then:
//        1 * excelServiceHandler.exportCaseByFolder(_, _, _, _,_)
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
//        1 * testCaseService.queryLookupValueByCode(_, _) >> lookupTypeWithValuesDTO
//        1 * userService.list(_, _, _, _) >> new ResponseEntity<PageInfo<UserDTO>>(pageInfo, HttpStatus.OK)
//        1 * testCaseService.getVersionInfo(_) >> versionInfo
//        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS
//    }
//
//    def "downloadImportTemplate"() {
//        when:
//        Workbook importTemp = iExcelImportService.buildImportTemp()
//        then:
//        File file = File.createTempFile("import_temp", ".xlsx")
//        importTemp.write(file.newOutputStream())
//        file.delete()
//    }
//}
