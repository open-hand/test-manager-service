package io.choerodon.test.manager.app.service.impl

import io.choerodon.agile.api.dto.IssueCreateDTO
import io.choerodon.agile.api.dto.IssueDTO
import io.choerodon.agile.api.dto.IssueTypeDTO
import io.choerodon.agile.api.dto.PriorityDTO
import io.choerodon.agile.api.dto.ProjectDTO
import io.choerodon.core.exception.CommonException
import io.choerodon.devops.api.dto.ApplicationRepDTO
import io.choerodon.devops.api.dto.ApplicationVersionRepDTO
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.domain.service.impl.IExcelImportServiceImpl
import io.choerodon.test.manager.domain.service.impl.IJsonImportServiceImpl
import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceE
import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationHistoryE
import io.choerodon.test.manager.infra.common.utils.FileUtil
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDO
import io.choerodon.test.manager.infra.dataobject.TestIssueFolderRelDO
import io.choerodon.test.manager.infra.feign.ApplicationFeignClient
import io.choerodon.test.manager.infra.feign.IssueFeignClient
import io.choerodon.test.manager.infra.feign.ProjectFeignClient
import io.choerodon.test.manager.infra.mapper.TestAppInstanceMapper
import io.choerodon.test.manager.infra.mapper.TestAutomationHistoryMapper
import io.choerodon.test.manager.infra.mapper.TestAutomationResultMapper
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper
import io.choerodon.test.manager.infra.mapper.TestIssueFolderRelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletionException

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class JsonImportServiceImplSpec extends Specification {

    @Autowired
    private JsonImportServiceImpl jsonImportService

    @Autowired
    private IJsonImportServiceImpl iJsonImportService

    @Autowired
    private TestAppInstanceMapper instanceMapper

    @Autowired
    private IExcelImportServiceImpl iExcelImportService

    @Autowired
    private TestIssueFolderRelMapper issueFolderRelMapper

    @Autowired
    private TestAutomationResultMapper automationResultMapper

    @Autowired
    private TestAutomationHistoryMapper automationHistoryMapper

    @Autowired
    private TestCycleCaseMapper cycleCaseMapper

    @Shared
    private String report = new String(FileUtil.unTarGzToMemory(new ClassPathResource("mochawesome.json.tar.gz")
            .file.newInputStream()).get(0), StandardCharsets.UTF_8)

    @Shared
    private List<TestAppInstanceE> instances = []

    private List<TestAutomationHistoryE> automationHistories = []

    def "importMochaReport"() {
        given:
        TestAppInstanceE instanceE = new TestAppInstanceE(
                code: "mocha-test",
                projectVersionId: 233L,
                projectId: 144L,
                appId: 662,
                appVersionId: 582L
        )
        instances << instanceE
        instanceMapper.insert(instanceE)

        TestAutomationHistoryE automationHistory = new TestAutomationHistoryE(instanceId: 5L)
        automationHistories << automationHistory
        automationHistoryMapper.insert(automationHistory)

        ProjectFeignClient projectFeignClient = Mock() {
            _ * query(instanceE.projectId) >>
                    new ResponseEntity<>(new ProjectDTO(organizationId: 1L), HttpStatus.OK)
        }
        ApplicationFeignClient applicationFeignClient = Mock()

        iJsonImportService.setProjectFeignClient(projectFeignClient)
        iJsonImportService.setApplicationFeignClient(applicationFeignClient)

        IssueFeignClient issueFeignClient = Mock() {
            _ * queryPriorityId(instanceE.projectId, _ as Long) >>
                    new ResponseEntity<>([new PriorityDTO(id: 8L, isDefault: true)], HttpStatus.OK)
            _ * queryIssueType(instanceE.projectId, "test", _ as Long) >>
                    new ResponseEntity<>([new IssueTypeDTO(typeCode: "issue_test", id: 18L)], HttpStatus.OK)
        }

        iExcelImportService.setIssueFeignClient(issueFeignClient)
        def issueDTOs = []
        for (long i in 1000001L..1000009L) {
            issueDTOs << new IssueDTO(issueId: i)
        }
        TestCaseService testCaseService = Mock() {
            _ * createTest(_ as IssueCreateDTO, instanceE.projectId, "test") >>> issueDTOs
        }

        iExcelImportService.setTestCaseService(testCaseService)

        when: "app instance 不存在"
        jsonImportService.importMochaReport("att-662-582-5000000", report)
        then:
        CommonException commonException = thrown()
        commonException.message == "app instance 不存在"

        when: "app version id 不存在"
        jsonImportService.importMochaReport("att-662-582000000-5", report)
        then:
        1 * applicationFeignClient.getAppversion(instanceE.projectId, _ as List<Long>) >>
                new ResponseEntity<>([new ApplicationVersionRepDTO(version: "测试版本号")], HttpStatus.INTERNAL_SERVER_ERROR)
        1 * applicationFeignClient.queryByAppId(instanceE.projectId, _ as Long) >>
                new ResponseEntity<>(new ApplicationRepDTO(name: "应用名称"), HttpStatus.OK)
        CompletionException completionException = thrown()
        completionException.cause.message == "error.get.app.version.name"

        when: "app id 不存在"
        jsonImportService.importMochaReport("att-662000000-582-5", report)
        then:
        1 * applicationFeignClient.getAppversion(instanceE.projectId, _ as List<Long>) >>
                new ResponseEntity<>([new ApplicationVersionRepDTO(version: "测试版本号")], HttpStatus.OK)
        1 * applicationFeignClient.queryByAppId(instanceE.projectId, _ as Long) >>
                new ResponseEntity<>(new ApplicationRepDTO(name: "应用名称"), HttpStatus.INTERNAL_SERVER_ERROR)
        completionException = thrown()
        completionException.cause.message == "error.get.app.name"

        when:
        Long reportId = jsonImportService.importMochaReport("att-662-582-5", report)
        for (long i in 1000000L..1000009L) {
            issueFolderRelMapper.delete(new TestIssueFolderRelDO(issueId: i))
            cycleCaseMapper.delete(new TestCycleCaseDO(issueId: i))
        }
        automationResultMapper.deleteByPrimaryKey(reportId)
        then:
        1 * applicationFeignClient.getAppversion(instanceE.projectId, _ as List<Long>) >>
                new ResponseEntity<>([new ApplicationVersionRepDTO(version: "测试版本号")], HttpStatus.OK)
        1 * applicationFeignClient.queryByAppId(instanceE.projectId, _ as Long) >>
                new ResponseEntity<>(new ApplicationRepDTO(name: "应用名称"), HttpStatus.OK)
        reportId == 2L
    }
}
