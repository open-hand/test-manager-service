package io.choerodon.test.manager.app.service.impl

import io.choerodon.agile.api.dto.PriorityDTO
import io.choerodon.agile.api.dto.ProjectDTO
import io.choerodon.core.exception.CommonException
import io.choerodon.devops.api.dto.ApplicationRepDTO
import io.choerodon.devops.api.dto.ApplicationVersionRepDTO
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.JsonImportService
import io.choerodon.test.manager.domain.service.IExcelImportService
import io.choerodon.test.manager.domain.service.IJsonImportService
import io.choerodon.test.manager.domain.service.impl.IExcelImportServiceImpl
import io.choerodon.test.manager.domain.service.impl.IJsonImportServiceImpl
import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceE
import io.choerodon.test.manager.infra.common.utils.FileUtil
import io.choerodon.test.manager.infra.feign.ApplicationFeignClient
import io.choerodon.test.manager.infra.feign.IssueFeignClient
import io.choerodon.test.manager.infra.feign.ProjectFeignClient
import io.choerodon.test.manager.infra.mapper.TestAppInstanceMapper
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

    @Shared
    private String report = new String(FileUtil.unTarGzToMemory(new ClassPathResource("mochawesome.json.tar.gz")
            .file.newInputStream()).get(0), StandardCharsets.UTF_8)

    @Shared
    private List<TestAppInstanceE> instances = []

//    def "importMochaReport"() {
//        given:
//        TestAppInstanceE instanceE = new TestAppInstanceE(
//                code: "mocha-test",
//                projectVersionId: 233L,
//                projectId: 144L,
//                createdBy: 8956L,
//                lastUpdatedBy: 8956L
//        )
//        instances << instanceE
//        instanceMapper.insert(instanceE)
//        ProjectFeignClient projectFeignClient = Mock()
//        ApplicationFeignClient applicationFeignClient = Mock()
//        iJsonImportService.setProjectFeignClient(projectFeignClient)
//        iJsonImportService.setApplicationFeignClient(applicationFeignClient)
//        IssueFeignClient issueFeignClient = Mock()
//        iExcelImportService.setIssueFeignClient(issueFeignClient)
//
//        when: "app instance 不存在"
//        jsonImportService.importMochaReport("att-662-582-1000000", report)
//        then:
//        CommonException commonException = thrown()
//        commonException.message == "app instance 不存在"
//
//        when: "app version id 不存在"
//        jsonImportService.importMochaReport("att-662-582000000-1", report)
//        then:
//        1 * projectFeignClient.query(instanceE.projectId) >>
//                new ResponseEntity<>(new ProjectDTO(organizationId: 1L), HttpStatus.OK)
//        1 * applicationFeignClient.getAppversion(instanceE.projectId, _ as Long) >>
//                new ResponseEntity<>(new ApplicationVersionRepDTO(version: "版本号"), HttpStatus.OK)
//        1 * applicationFeignClient.queryByAppId(instanceE.projectId, _ as Long) >>
//                new ResponseEntity<>(new ApplicationRepDTO(name: "应用名称"), HttpStatus.OK)
//        _ * issueFeignClient.queryPriorityId(instanceE.projectId, _ as Long) >>
//                new ResponseEntity<>([new PriorityDTO(id: 8L, isDefault: true)], HttpStatus.OK)
//        commonException = thrown()
//        commonException.message == "error.get.app.version.name"
//    }
}
