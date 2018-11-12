package io.choerodon.test.manager.app.service.impl

import io.choerodon.core.convertor.ConvertHelper
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.core.oauth.DetailsHelper
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.ExcelImportService
import io.choerodon.test.manager.app.service.TestFileLoadHistoryService
import io.choerodon.test.manager.domain.service.ITestFileLoadHistoryService
import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE
import io.choerodon.test.manager.infra.common.utils.SpringUtil
import io.choerodon.test.manager.infra.dataobject.TestFileLoadHistoryDO
import io.choerodon.test.manager.infra.mapper.TestFileLoadHistoryMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import javax.servlet.http.HttpServletResponse

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
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

    def setupSpec() {
        userDetails = new CustomUserDetails("test", "12345678", Collections.emptyList())
        userDetails.setUserId(0L)
    }

    def "downloadImportTemp"() {
        given:
        HttpServletResponse response = new MockHttpServletResponse()
        when:
        excelImportService.downloadImportTemp(response)
        then:
        with(response) {
            status == HttpStatus.OK.value()
            contentType == "application/vnd.ms-excel"
            characterEncoding == "UTF-8"
        }
    }

    def "queryLatestImportIssueHistory"() {
        given:
        TestFileLoadHistoryE testFileLoadHistoryE = SpringUtil.getApplicationContext().getBean(TestFileLoadHistoryE)
        testFileLoadHistoryE.setCreatedBy(userDetails.userId)
        when:
        testFileLoadHistoryE = iTestFileLoadHistoryService.queryLatestImportIssueHistory(testFileLoadHistoryE)
        then:
        with(testFileLoadHistoryE) {
            id == 40
            projectId == 0
            actionType == 1
            sourceType == 0
            status == 1
            createdBy == 0
        }
    }

}
