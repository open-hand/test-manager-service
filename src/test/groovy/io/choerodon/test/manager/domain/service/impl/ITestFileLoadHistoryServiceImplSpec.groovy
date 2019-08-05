//package io.choerodon.test.manager.domain.service.impl
//
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.domain.service.ITestFileLoadHistoryService
//import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.web.client.TestRestTemplate
//import org.springframework.context.annotation.Import
//import spock.lang.Shared
//import spock.lang.Specification
//import spock.lang.Stepwise
//
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
//
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//@Stepwise
//class ITestFileLoadHistoryServiceImplSpec extends Specification {
//    @Autowired
//    ITestFileLoadHistoryService service;
//
//    @Shared
//    Long id;
//
//    @Autowired
//    TestRestTemplate restTemplate;
//
//    def "InsertOne"() {
//        given:
//        TestFileLoadHistoryE testFileLoadHistoryE= new TestFileLoadHistoryE();
//        testFileLoadHistoryE.setProjectId(144L)
//        testFileLoadHistoryE.setActionType(TestFileLoadHistoryE.Action.DOWNLOAD_CYCLE)
//        testFileLoadHistoryE.setSourceType(TestFileLoadHistoryE.Source.CYCLE)
//        testFileLoadHistoryE.setLinkedId(666L)
//        testFileLoadHistoryE.setStatus(TestFileLoadHistoryEnums.Status.SUSPENDING)
//        when:
//        TestFileLoadHistoryE result=service.insertOne(testFileLoadHistoryE);
//        then:
//        result.getId()!=null
//        when:
//        result.setStatus(TestFileLoadHistoryEnums.Status.SUCCESS)
//        result.setObjectVersionNumber(1L)
//        result=service.update(result)
//        id=result.getId();
//        then:
//        result.getStatus()==TestFileLoadHistoryEnums.Status.SUCCESS.getTypeValue()
//
//    }
//}
