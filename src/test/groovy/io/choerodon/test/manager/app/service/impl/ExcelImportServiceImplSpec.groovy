package io.choerodon.test.manager.app.service.impl


import io.choerodon.agile.api.vo.IssueDTO
import io.choerodon.agile.api.vo.IssueTypeVO
import io.choerodon.agile.api.vo.PriorityVO
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.*

import io.choerodon.test.manager.infra.util.ExcelUtil

import io.choerodon.test.manager.infra.feign.IssueFeignClient
import io.choerodon.test.manager.infra.mapper.TestFileLoadHistoryMapper
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.util.AopTestUtils
import spock.lang.Shared
import spock.lang.Specification
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ExcelImportServiceImplSpec extends Specification {

//    @Autowired
//    private ExcelImportService excelImportService
    @Autowired
    ExcelImportService excelImportService

    @Autowired
    ExcelService excelService

    @Autowired
    private TestFileLoadHistoryService testFileLoadHistoryService

    @Autowired
    private TestFileLoadHistoryMapper loadHistoryMapper

//    @Autowired
//    private ITestFileLoadHistoryService iTestFileLoadHistoryService

    @Shared
    private CustomUserDetails userDetails

    @Autowired
    TestFileLoadHistoryMapper historyMapper

    @Autowired
    NotifyService notifyService

    @Autowired
    TestCaseService testCaseService

    @Autowired
    FileService fileService

    @Shared
    Long organizationId = 1L

    @Shared
    Long projectId = 1L

    def setupSpec() {
        userDetails = new CustomUserDetails("test", "12345678", Collections.emptyList())
        userDetails.setUserId(0L)

    }

    def "downloadImportTemp"() {
        given:
        HttpServletResponse response = new MockHttpServletResponse()
        HttpServletRequest request = new MockHttpServletRequest()

        when:
        excelImportService.downloadImportTemp(request, response, organizationId, projectId)
        then:
        1 * excelImportService.downloadImportTemp(_, _, _, _)
        noExceptionThrown()
    }

//    def "queryLatestImportIssueHistory"() {
//        given:
//        TestFileLoadHistoryE testFileLoadHistoryE = SpringUtil.getApplicationContext().getBean(TestFileLoadHistoryE)
//
//        TestFileLoadHistoryDTO historyDO = new TestFileLoadHistoryDTO(projectId: 144L, actionType: 1L, sourceType: 1L, linkedId: 144L, createdBy: 0L)
//        historyMapper.insert(historyDO)
//        TestFileLoadHistoryDTO resHistoryDO = historyMapper.selectByPrimaryKey(historyDO.getId())
//
//
//        when:
//        testFileLoadHistoryE.setCreatedBy(resHistoryDO.getCreatedBy())
//        testFileLoadHistoryE.setActionType(TestFileLoadHistoryE.Action.UPLOAD_ISSUE)
//
//        testFileLoadHistoryE = iTestFileLoadHistoryService.queryLatestHistory(testFileLoadHistoryE)
//        then:
//        with(testFileLoadHistoryE) {
//            id == resHistoryDO.getId()
//            projectId == 144
//            actionType == 1
//            sourceType == 1
//            status == 0
//        }
//    }
//
    def "importIssueByExcel1"() {
        given:
        HSSFWorkbook workbook = new HSSFWorkbook()
        workbook.createSheet("测试用例")
        ExcelImportService service = AopTestUtils.getTargetObject(excelImportService)
        when:
        service.importIssueByExcel(4, 144, 4L, 1L, workbook)
        then:
        0 * notifyService.postWebSocket(_, _, _)
        noExceptionThrown()
    }


//    def "importIssueByExcel2"() {
//        given:
//        HSSFWorkbook workbook = new HSSFWorkbook()
//        Sheet sheet = workbook.createSheet("测试用例")
//        ExcelUtil.createRow(sheet, 0, null)
//        Row row = ExcelUtil.createRow(sheet, 1, null)
//        Row row2 = ExcelUtil.createRow(sheet, 2, null)
//        ExcelUtil.createCell(row, 0, ExcelUtil.CellType.TEXT, "概要2")
//        ExcelUtil.createCell(row2, 2, ExcelUtil.CellType.TEXT, "step")
//        ExcelImportService service = AopTestUtils.getTargetObject(excelImportService);
//        IssueFeignClient issueFeignClient = Mock(IssueFeignClient)
////        ((ExcelImportServiceImpl) service).setIssueFeignClient(issueFeignClient)
//        when:
//        service.importIssueByExcel(4, 144, 4L, 1L, workbook)
//        then:
//        3 * notifyService.postWebSocket(_, _, _)
//        1 * testCaseService.createTest(_, _, _) >> new IssueDTO(issueId: 199L)
//        1 * issueFeignClient.queryIssueType(_, _, _) >> new ResponseEntity([new IssueTypeVO(typeCode: "issue_test", id: 18L), new IssueTypeVO(typeCode: "issue_auto_test", id: 19L)], HttpStatus.OK)
//        1 * issueFeignClient.queryDefaultPriority(_, _) >> new ResponseEntity(new PriorityVO(id: 8L, default: true), HttpStatus.OK)
//    }
//
//    def "importIssueByExcel3"() {
//        given:
//        HSSFWorkbook workbook = new HSSFWorkbook()
//        Sheet sheet = workbook.createSheet("测试用例")
//        ExcelUtil.createRow(sheet, 0, null)
//        Row row = ExcelUtil.createRow(sheet, 1, null)
//        Row row2 = ExcelUtil.createRow(sheet, 2, null)
//        ExcelUtil.createCell(row, 0, ExcelUtil.CellType.TEXT, "概要2")
//        ExcelUtil.createCell(row2, 2, ExcelUtil.CellType.TEXT, "step")
//        ExcelImportService service = AopTestUtils.getTargetObject(excelImportService)
//        IssueFeignClient issueFeignClient = Mock(IssueFeignClient)
//        ((ExcelImportServiceImpl) service).setIssueFeignClient(issueFeignClient)
//        when:
//        service.importIssueByExcel(4, 144, 4L, 1L, workbook)
//        then:
//        3 * notifyService.postWebSocket(_, _, _)
//        1 * testCaseService.createTest(_, _, _) >> null
//        1 * fileService.uploadFile(_, _, _) >> new ResponseEntity("url", HttpStatus.OK)
////        1*issueFeignClient.queryIssueType(_,_,_)>> new ResponseEntity([new IssueTypeVO(id: 18L, typeCode: "issue_test")], HttpStatus.OK)
////        1*issueFeignClient.queryDefaultPriority(_,_)>> new ResponseEntity(new PriorityVO(id: 8L, default: true), HttpStatus.OK)
//    }
//
//    def "importIssueByExcel4"() {
//        given:
//        HSSFWorkbook workbook = new HSSFWorkbook()
//        Sheet sheet = workbook.createSheet("测试用例")
//        ExcelUtil.createRow(sheet, 0, null)
//        Row row = ExcelUtil.createRow(sheet, 1, null)
//        Row row2 = ExcelUtil.createRow(sheet, 2, null)
//        ExcelUtil.createCell(row, 0, ExcelUtil.CellType.TEXT, "概要2")
//        ExcelUtil.createCell(row2, 2, ExcelUtil.CellType.TEXT, "step")
//        ExcelImportService service = AopTestUtils.getTargetObject(excelImportService)
//        IssueFeignClient issueFeignClient = Mock(IssueFeignClient)
//        ((ExcelImportServiceImpl) service).setIssueFeignClient(issueFeignClient)
//        when:
//        service.importIssueByExcel(4, 144, 4L, 1L, workbook)
//        then:
//        3 * notifyService.postWebSocket(_, _, _)
//        1 * testCaseService.createTest(_, _, _) >> null
//        1 * fileService.uploadFile(_, _, _) >> new ResponseEntity("url", HttpStatus.GATEWAY_TIMEOUT)
////        1*issueFeignClient.queryIssueType(_,_,_)>> new ResponseEntity([new IssueTypeVO(id: 18L, typeCode: "issue_test")], HttpStatus.OK)
////        1*issueFeignClient.queryDefaultPriority(_,_)>> new ResponseEntity(new PriorityVO(id: 8L, default: true), HttpStatus.OK)
//    }

//    def "importExcel5"() {
//        given:
//        IExcelImportServiceImpl iExcelImportService = new IExcelImportServiceImpl();
//        HSSFWorkbook workbook = new HSSFWorkbook()
//        Row row = ExcelUtil.createRow(workbook.createSheet(), 0, null)
//        when:
//        def re = iExcelImportService.processIssueHeaderRow(row, 4, 144L, 2L, 1L)
//        then:
//        re == null
//    }

}
