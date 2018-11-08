package io.choerodon.test.manager.api.controller.v1

import io.choerodon.agile.api.dto.*
import io.choerodon.core.domain.Page
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.IssueInfosDTO
import io.choerodon.test.manager.app.service.ExcelImportService
import io.choerodon.test.manager.app.service.ExcelService
import io.choerodon.test.manager.app.service.FileService
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.domain.service.IExcelImportService
import io.choerodon.test.manager.infra.feign.FileFeignClient
import io.reactivex.netty.protocol.http.server.HttpServerRequest
import org.apache.poi.ss.usermodel.Workbook
import org.assertj.core.util.Lists
import org.assertj.core.util.Maps
import org.springframework.aop.framework.AdvisedSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification
import spock.lang.Stepwise

import java.lang.reflect.Field

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCaseControllerTest extends Specification {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    TestCaseController testCaseController

    private ExcelService excelService

    @Autowired
    TestCaseService testCaseService

    @Autowired
    UserService userService

    @Autowired
    IExcelImportService iExcelImportService

    void setup() {
        excelService = Mock(ExcelService)
        testCaseController.setExcelService(excelService)
    }


    def "DownLoadByProject"() {
        when:
        testCaseController.downLoadByProject(1L, new MockHttpServletRequest(), new MockHttpServletResponse(),1L)

        then:
        1 * excelService.exportCaseByProject(_, _, _,_)
    }

    def "DownLoadByVersion"() {
        when:
        testCaseController.downLoadByVersion(1L, 1L, new MockHttpServletRequest(), new MockHttpServletResponse(),1L)

        then:
        1 * excelService.exportCaseByVersion(_, _, _, _,_)
    }

    def "DownLoadByFolder"() {
        when:
        testCaseController.downLoadByFolder(1L, 1L, new MockHttpServletRequest(), new MockHttpServletResponse(),1L)

        then:
        1 * excelService.exportCaseByFolder(_, _, _, _,_)
    }

    def "DownLoadTemplate"() {
        when:
        restTemplate.getForEntity("/v1/projects/{project_id}/case/download/excel/template", null, 1L)

        then:
        1 * excelService.exportCaseTemplate(_, _, _)
    }

    def "downloadImportTemplate"() {
        //
        when:
        Workbook importTemp = iExcelImportService.buildImportTemp()
        then:
        File tempFile = new File("/tmp/import_temp.xlsx")
        if (tempFile.exists()) {
            tempFile.delete()
        }
        importTemp.write(tempFile.newOutputStream())
    }
}
