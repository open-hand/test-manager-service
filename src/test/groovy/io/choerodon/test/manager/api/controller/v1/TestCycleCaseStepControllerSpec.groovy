//package io.choerodon.test.manager.api.controller.v1
//
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.api.vo.IssueInfosVO
//import io.choerodon.test.manager.api.vo.TestCycleCaseStepVO
//import io.choerodon.test.manager.app.service.TestCaseService
//import io.choerodon.test.manager.infra.dto.TestCycleCaseStepDTO
//import io.choerodon.test.manager.infra.mapper.TestCycleCaseStepMapper
//import org.modelmapper.ModelMapper
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.web.client.TestRestTemplate
//import org.springframework.context.annotation.Import
//import org.springframework.http.HttpEntity
//import org.springframework.http.HttpMethod
//import spock.lang.Shared
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
//class TestCycleCaseStepControllerSpec extends Specification {
//    @Autowired
//    TestRestTemplate restTemplate
//
//    @Autowired
//    TestCycleCaseStepMapper testCycleCaseStepMapper
//
//    @Autowired
//    ModelMapper modelMapper
//
//    @Autowired
//    TestCaseService testCaseService
//
//    @Shared
//    Long project_Id = 1L
//
//    @Shared
//    Long cycleCaseId = 1L
//
//    def "Update"() {
//        given: '更新一个循环步骤'
//        TestCycleCaseStepVO testCycleCaseStepVO = new TestCycleCaseStepVO()
//        testCycleCaseStepVO.setExecuteId(1L)
//        testCycleCaseStepVO.setIssueId(1L)
//        testCycleCaseStepVO.setCycleId(1L)
//        testCycleCaseStepVO.setExecuteStepId(1L)
//        testCycleCaseStepVO.setCycleName("name")
//        testCycleCaseStepVO.setStepId(1L)
//        testCycleCaseStepVO.setStepStatus(1L)
//        testCycleCaseStepVO.setObjectVersionNumber(1L)
//        TestCycleCaseStepDTO testCycleCaseStepDTO = modelMapper.map(testCycleCaseStepVO, TestCycleCaseStepDTO.class)
//        testCycleCaseStepMapper.insert(testCycleCaseStepDTO)
//
//        List<TestCycleCaseStepVO> list = new ArrayList<TestCycleCaseStepVO>()
//        testCycleCaseStepDTO.setCycleName("re_name")
//        list.add(testCycleCaseStepVO)
//        HttpEntity<List<TestCycleCaseStepVO>> httpEntity = new HttpEntity<List<TestCycleCaseStepVO>>(list, null)
//
//        when:
//        def entity = restTemplate.exchange("/v1/projects/{project_id}/cycle/case/step",
//                HttpMethod.PUT,
//                httpEntity,
//                List,
//                project_Id
//        )
//        then:
//
//        entity.statusCode.is2xxSuccessful()
//        entity.getBody().size() > 0
//
//    }
//
//    def "QuerySubStep"() {
//        given: '查询循环步骤'
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
//        def entity = restTemplate.exchange("/v1/projects/{project_id}/cycle/case/step/query/{cycleCaseId}?organizationId=1",
//                HttpMethod.GET,
//                null,
//                List,
//                project_Id,
//                cycleCaseId
//        )
//        then:
//        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueInfosVOMap
//        entity.statusCode.is2xxSuccessful()
//        entity.getBody().size() > 0
//    }
//}
