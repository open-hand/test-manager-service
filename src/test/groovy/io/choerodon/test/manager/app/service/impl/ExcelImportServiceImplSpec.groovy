package io.choerodon.test.manager.app.service.impl

import com.google.common.collect.Lists
import io.choerodon.agile.api.dto.IssueDTO
import io.choerodon.agile.api.dto.IssueTypeDTO
import io.choerodon.agile.api.dto.PriorityDTO
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.ExcelImportService
import io.choerodon.test.manager.app.service.FileService
import io.choerodon.test.manager.app.service.NotifyService
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.TestFileLoadHistoryService
import io.choerodon.test.manager.domain.service.IExcelImportService
import io.choerodon.test.manager.domain.service.ITestFileLoadHistoryService
import io.choerodon.test.manager.domain.service.impl.IExcelImportServiceImpl
import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE
import io.choerodon.test.manager.infra.common.utils.ExcelUtil
import io.choerodon.test.manager.infra.common.utils.SpringUtil
import io.choerodon.test.manager.infra.dataobject.TestFileLoadHistoryDO
import io.choerodon.test.manager.infra.feign.IssueFeignClient
import io.choerodon.test.manager.infra.mapper.TestFileLoadHistoryMapper
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.util.AopTestUtils
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import javax.servlet.http.HttpServletResponse

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ExcelImportServiceImplSpec extends Specification {

    @Autowired
    private ExcelImportService excelImportService

    @Autowired
    private TestFileLoadHistoryService testFileLoadHistoryService

    @Autowired
    private TestFileLoadHistoryMapper loadHistoryMapper

    @Autowired
    private ITestFileLoadHistoryService iTestFileLoadHistoryService

    @Shared
    private CustomUserDetails userDetails

    @Autowired
    TestFileLoadHistoryMapper historyMapper

    @Autowired
    NotifyService notifyService

    @Autowired
    TestCaseService testCaseService;

    @Autowired
    FileService fileService

    def setupSpec() {
        userDetails = new CustomUserDetails("test", "12345678", Collections.emptyList())
        userDetails.setUserId(0L)
    }

    def "downloadImportTemp"() {
        given:
        HttpServletResponse response = new MockHttpServletResponse()
        MockHttpServletRequest request = new MockHttpServletRequest()
        when:
        excelImportService.downloadImportTemp(request,response)
        then:
        with(response) {
            status == HttpStatus.OK.value()
            contentType == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            characterEncoding == "utf-8"
        }
    }

    def "queryLatestImportIssueHistory"() {
        given:
        TestFileLoadHistoryE testFileLoadHistoryE = SpringUtil.getApplicationContext().getBean(TestFileLoadHistoryE)

        TestFileLoadHistoryDO historyDO = new TestFileLoadHistoryDO(projectId: 144L, actionType: 1L, sourceType: 1L, linkedId: 144L,createdBy: 0L)
        historyMapper.insert(historyDO)
        TestFileLoadHistoryDO resHistoryDO = historyMapper.selectByPrimaryKey(historyDO.getId())


        when:
        testFileLoadHistoryE.setCreatedBy(resHistoryDO.getCreatedBy())

        testFileLoadHistoryE = iTestFileLoadHistoryService.queryLatestImportIssueHistory(testFileLoadHistoryE)
        then:
        with(testFileLoadHistoryE) {
            id == resHistoryDO.getId()
            projectId == 144
            actionType == 1
            sourceType == 1
            status == 0
        }
    }

    def "importIssueByExcel1"(){
        given:
        HSSFWorkbook workbook=new HSSFWorkbook()
        workbook.createSheet("测试用例")
        ExcelImportService service=AopTestUtils.getTargetObject(excelImportService);
        when:
        service.importIssueByExcel(4, 144, 4L, 1L, workbook)
        then:
        1*notifyService.postWebSocket(_,_,_)
    }


    def "importIssueByExcel2"(){
        given:
        HSSFWorkbook workbook=new HSSFWorkbook()
        Sheet sheet=workbook.createSheet("测试用例")
        ExcelUtil.createRow(sheet,0,null)
        Row row=ExcelUtil.createRow(sheet,1,null)
        Row row2=ExcelUtil.createRow(sheet,2,null)
        ExcelUtil.createCell(row,0, ExcelUtil.CellType.TEXT,"概要2")
        ExcelUtil.createCell(row2,2, ExcelUtil.CellType.TEXT,"step")
        ExcelImportService service=AopTestUtils.getTargetObject(excelImportService);
        IssueFeignClient issueFeignClient = Mock(IssueFeignClient)
        ((ExcelImportServiceImpl) service).setIssueFeignClient(issueFeignClient)
        when:
        service.importIssueByExcel(4, 144, 4L, 1L, workbook)
        then:
        3*notifyService.postWebSocket(_,_,_)
        1*testCaseService.createTest(_,_,_)>>new IssueDTO(issueId: 199L)
        1*issueFeignClient.queryIssueType(_,_,_)>> new ResponseEntity([new IssueTypeDTO(id: 18L, typeCode: "issue_test")], HttpStatus.OK)
        1*issueFeignClient.queryPriorityId(_,_)>> new ResponseEntity([new PriorityDTO(id: 8L, default: true)], HttpStatus.OK)
    }

    def "importIssueByExcel3"(){
        given:
        HSSFWorkbook workbook=new HSSFWorkbook()
        Sheet sheet=workbook.createSheet("测试用例")
        ExcelUtil.createRow(sheet,0,null)
        Row row=ExcelUtil.createRow(sheet,1,null)
        Row row2=ExcelUtil.createRow(sheet,2,null)
        ExcelUtil.createCell(row,0, ExcelUtil.CellType.TEXT,"概要2")
        ExcelUtil.createCell(row2,2, ExcelUtil.CellType.TEXT,"step")
        ExcelImportService service=AopTestUtils.getTargetObject(excelImportService)
        IssueFeignClient issueFeignClient = Mock(IssueFeignClient)
        ((ExcelImportServiceImpl) service).setIssueFeignClient(issueFeignClient)
        when:
        service.importIssueByExcel(4, 144, 4L, 1L, workbook)
        then:
        3*notifyService.postWebSocket(_,_,_)
        1*testCaseService.createTest(_,_,_)>>null
        1*fileService.uploadFile(_,_,_)>>new ResponseEntity("url",HttpStatus.OK)
        1*issueFeignClient.queryIssueType(_,_,_)>> new ResponseEntity([new IssueTypeDTO(id: 18L, typeCode: "issue_test")], HttpStatus.OK)
        1*issueFeignClient.queryPriorityId(_,_)>> new ResponseEntity([new PriorityDTO(id: 8L, default: true)], HttpStatus.OK)
    }

    def "importIssueByExcel4"(){
        given:
        HSSFWorkbook workbook=new HSSFWorkbook()
        Sheet sheet=workbook.createSheet("测试用例")
        ExcelUtil.createRow(sheet,0,null)
        Row row=ExcelUtil.createRow(sheet,1,null)
        Row row2=ExcelUtil.createRow(sheet,2,null)
        ExcelUtil.createCell(row,0, ExcelUtil.CellType.TEXT,"概要2")
        ExcelUtil.createCell(row2,2, ExcelUtil.CellType.TEXT,"step")
        ExcelImportService service=AopTestUtils.getTargetObject(excelImportService)
        IssueFeignClient issueFeignClient = Mock(IssueFeignClient)
        ((ExcelImportServiceImpl) service).setIssueFeignClient(issueFeignClient)
        when:
        service.importIssueByExcel(4, 144, 4L, 1L, workbook)
        then:
        3*notifyService.postWebSocket(_,_,_)
        1*testCaseService.createTest(_,_,_)>>null
        1*fileService.uploadFile(_,_,_)>>new ResponseEntity("url",HttpStatus.GATEWAY_TIMEOUT)
        1*issueFeignClient.queryIssueType(_,_,_)>> new ResponseEntity([new IssueTypeDTO(id: 18L, typeCode: "issue_test")], HttpStatus.OK)
        1*issueFeignClient.queryPriorityId(_,_)>> new ResponseEntity([new PriorityDTO(id: 8L, default: true)], HttpStatus.OK)
    }

    def "importExcel5"(){
        given:
        IExcelImportServiceImpl iExcelImportService=new IExcelImportServiceImpl();
        HSSFWorkbook workbook=new HSSFWorkbook()
        Row row=ExcelUtil.createRow(workbook.createSheet(),0,null)
        when:
        def re=iExcelImportService.processIssueHeaderRow(row, 4, 144L, 2L, 1L)
        then:
        re==null
    }

}
