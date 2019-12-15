//package io.choerodon.test.manager.api.controller.v1
//
//import feign.Target
//import io.choerodon.test.manager.api.vo.devops.ApplicationRepDTO
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.app.service.JsonImportService
//import io.choerodon.test.manager.app.service.TestCaseService
//import io.choerodon.test.manager.app.service.impl.JsonImportServiceImpl
//import io.choerodon.test.manager.infra.dto.TestAppInstanceDTO
//import io.choerodon.test.manager.infra.feign.ApplicationFeignClient
//import io.choerodon.test.manager.infra.mapper.TestAppInstanceMapper
//import org.mockito.InjectMocks
//import org.mockito.Mockito
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.web.client.TestRestTemplate
//import org.springframework.context.annotation.Import
//import org.springframework.core.io.FileSystemResource
//import org.springframework.http.HttpEntity
//import org.springframework.mock.web.MockMultipartFile
//import org.springframework.test.util.AopTestUtils
//import org.springframework.test.util.ReflectionTestUtils
//import org.springframework.util.LinkedMultiValueMap
//import org.springframework.util.MultiValueMap
//import org.springframework.web.multipart.MultipartFile
//import spock.lang.Specification
//import spock.lang.Stepwise
//
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
//
///**
// User: wangxiang
// Date: 2019/8/30
// */
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//@Stepwise
//class TestAutomationControllerSpec extends Specification {
//
//    @Autowired
//    TestRestTemplate restTemplate
//
//    @Autowired
//    TestAppInstanceMapper testAppInstanceMapper
//
//    @Autowired
//    JsonImportService jsonImportService
//
//    @Autowired
//    ApplicationFeignClient applicationFeignClient
//
//
//    def "ImportMochaReport"() {
//        given: '导入自动化测试报告【mocha】'
//
//        FileSystemResource resource = new FileSystemResource(new File("D:\\2019 研发培训\\04-软件\\02-后端\\apache-maven-3.5.4-bin.tar.gz"))
//        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>()
//        param.add("file", resource)
//        HttpEntity<MockMultipartFile> httpEntity = new HttpEntity<MultipartFile>(param, null)
//
//        TestAppInstanceDTO instanceDTO = new TestAppInstanceDTO()
//        instanceDTO.setId(1L)
//        instanceDTO.setProjectId(1L)
//        instanceDTO.setAppId(1L)
//        instanceDTO.setAppVersionId(1L)
//        instanceDTO.setLogId(1L)
//        instanceDTO.setAppVersionId(1L)
//        instanceDTO.setCreatedBy(1L)
//
//        testAppInstanceMapper.insert(instanceDTO)
//        def all = testAppInstanceMapper.selectAll()
//
//        Long aLong = 1L
//
//        when:
//        def result = restTemplate.postForEntity("/v1/automation/import/report/mocha?releaseName=att-1-1-1",
//                httpEntity,
//                Long
//        )
//        then:
//        1 * jsonImportService.importMochaReport(_, _) >> aLong
//        result.statusCode.is2xxSuccessful()
//    }
//
//    def "ImportTestNgReport"() {
//        given: '导入自动化测试报告【testng】'
//        FileSystemResource resource = new FileSystemResource(new File("D:\\2019 研发培训\\04-软件\\02-后端\\apache-maven-3.5.4-bin.tar.gz"))
//        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>()
//        param.add("file", resource)
//        HttpEntity<MockMultipartFile> httpEntity = new HttpEntity<MultipartFile>(param, null)
//        Long aLong = 1L
//        when:
//        def result = restTemplate.postForEntity("/v1/automation/import/report/testng?releaseName=att-1-1-1",
//                httpEntity,
//                Long
//        )
//        then:
//        1 * jsonImportService.importTestNgReport(_, _) >> aLong
//        result.statusCode.is2xxSuccessful()
//    }
//}
