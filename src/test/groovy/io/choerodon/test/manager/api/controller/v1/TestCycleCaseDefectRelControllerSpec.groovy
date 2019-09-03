package io.choerodon.test.manager.api.controller.v1

import io.choerodon.agile.api.vo.IssueCreateDTO
import io.choerodon.agile.api.vo.IssueDTO
import io.choerodon.agile.api.vo.IssueInfoDTO
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.vo.IssueInfosVO
import io.choerodon.test.manager.api.vo.TestCycleCaseDefectRelVO
import io.choerodon.test.manager.api.vo.TestCycleCaseVO
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService
import io.choerodon.test.manager.app.service.impl.TestCycleCaseDefectRelServiceImpl
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO
import io.choerodon.test.manager.infra.dto.TestCycleCaseDefectRelDTO
import io.choerodon.test.manager.infra.enums.TestCycleCaseDefectCode
import io.choerodon.test.manager.infra.feign.ApplicationFeignClient
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient
import io.choerodon.test.manager.infra.mapper.TestCycleCaseDefectRelMapper
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper
import io.choerodon.test.manager.infra.mapper.TestStatusMapper
import javafx.beans.binding.When
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import javax.annotation.Resource


import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by 842767365@qq.com on 8/22/18.
 */

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCycleCaseDefectRelControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    TestCaseService caseService

    @Autowired
    TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper

    @Autowired
    ModelMapper modelMapper

    @Autowired
    TestCaseService testCaseService

    @Resource
    TestCaseFeignClient testCaseFeignClient

    @Autowired
    TestCycleCaseMapper testCycleCaseMapper

    @Autowired
    TestStatusMapper testStatusMapper

    @Autowired
//    @Qualifier("testCycleCaseDefectRelService")
    TestCycleCaseDefectRelService testCycleCaseDefectRelService

    @Shared
    Long defectId

    @Shared
    Long project_Id = 1L

    @Shared
    String issue_Num

//    void setup() {
//        testCycleCaseDefectRelService = new TestCycleCaseDefectRelServiceImpl(testCaseFeignClient: testCaseFeignClient)
//
//    }


    def "Insert"() {
        given: '增加缺陷'
        IssueInfosVO issueInfosVO = new IssueInfosVO()
        issueInfosVO.setIssueId(1L)
        issueInfosVO.setProjectId(1L)
        issueInfosVO.setIssueName("name")
        issueInfosVO.setIssueNum("name1")
        issueInfosVO.setStatusId(1L)

        IssueDTO issueDTO = new IssueDTO()
        issueDTO.setIssueId(1L)
        issueDTO.setIssueNum("name1")
        issueDTO.setProjectId(1L)
        issueDTO.setStatusId(1L)
        issueDTO.setIssueNum("name")

        List<TestCycleCaseDefectRelVO> testCycleCaseDefectRelVOList = new ArrayList<TestCycleCaseDefectRelVO>()
        TestCycleCaseDefectRelVO caseDefectRelVO1 = new TestCycleCaseDefectRelVO()
        caseDefectRelVO1.setId(1L)
        caseDefectRelVO1.setProjectId(1L)
        caseDefectRelVO1.setIssueId(99L)
        caseDefectRelVO1.setDefectType(TestCycleCaseDefectCode.CYCLE_CASE)
        caseDefectRelVO1.setDefectLinkId(999L)
        caseDefectRelVO1.setIssueInfosVO(issueInfosVO)

        testCycleCaseDefectRelVOList.add(caseDefectRelVO1)
        defectId = caseDefectRelVO1.getId()
        issue_Num = issueDTO.getIssueNum()
        HttpEntity<List<TestCycleCaseDefectRelVO>> httpEntity = new HttpEntity<List<TestCycleCaseDefectRelVO>>(testCycleCaseDefectRelVOList, null)
        when:
        def result = restTemplate.postForEntity("/v1/projects/{project_id}/defect?organizationId=1",
                httpEntity,
                List,
                project_Id
        )

        then:
        1 * testCaseService.queryIssue(_, _, _) >> new ResponseEntity<>(issueDTO, HttpStatus.CREATED)
        result.statusCode.is2xxSuccessful()
        testCycleCaseDefectRelVOList.get(0).getId() == result.getBody().get(0).getAt("id")
    }

//    def "RemoveDefect"() {
//        given:
//        IssueDTO mockResult = new IssueDTO(issueNum: "name1")
////        TestCycleCaseDefectRelService serviceAOP = AopTestUtils.getTargetObject(testCycleCaseDefectRelService)
//        when:
//        restTemplate.delete("/v1/projects/{project_id}/defect/delete/{defectId}?organizationId=1", 144L, defectId)
//        then:
//        1 * caseService.queryIssue(_, _, _) >> new ResponseEntity<>(mockResult, HttpStatus.CREATED);
//    }
    def "RemoveAttachment"() {
        given: '删除缺陷'
        IssueDTO issueDTO = new IssueDTO()
        issueDTO.setIssueId(1L)
        issueDTO.setIssueNum("name1")
        issueDTO.setProjectId(1L)
        issueDTO.setStatusId(1L)
        issueDTO.setIssueNum("name")

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/defect/delete/{defectId}?organizationId=1",
                HttpMethod.DELETE,
                null,
                ResponseEntity.class,
                project_Id,
                defectId
        )
        then:
        1 * testCaseService.queryIssue(_, _, _) >> new ResponseEntity<>(issueDTO, HttpStatus.CREATED)
        entity.getStatusCode().is2xxSuccessful()

    }

    def "CreateIssueAndLinkDefect"() {
        given: '创建一个缺陷并且关联到对应case或者step'
        IssueDTO issueDTO = new IssueDTO()
        issueDTO.setIssueId(1L)
        issueDTO.setIssueNum("name1")
        issueDTO.setProjectId(1L)
        issueDTO.setStatusId(1L)
        issueDTO.setIssueNum("name")

        IssueCreateDTO issueCreateDTO = new IssueCreateDTO()
        issueCreateDTO.setProjectId(1L)

        HttpEntity<IssueCreateDTO> httpEntity = new HttpEntity<IssueCreateDTO>(issueCreateDTO, null)

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/defect/createIssueAndDefect/{defectType}/{id}?applyType=CYCLE_CASE&organizationId=1",
                HttpMethod.POST,
                httpEntity,
                TestCycleCaseDefectRelVO.class,
                project_Id,
                TestCycleCaseDefectCode.CASE_STEP,
                defectId
        )
        then:
        1 * testCaseService.createTest(_, _, _) >> issueDTO
        1 * testCaseService.queryIssue(_, _, _) >> new ResponseEntity<>(issueDTO, HttpStatus.CREATED)
        entity.statusCode.is2xxSuccessful()

    }

    def "FixDefectData"() {
        given: '修改缺陷的projectId'
        Map<Long, IssueInfosVO> issueInfosVOMap = new HashMap<Long, IssueInfosVO>()
        IssueInfosVO issueInfosVO = new IssueInfosVO()
        issueInfosVO.setIssueId(1L)
        issueInfosVO.setProjectId(1L)
        issueInfosVO.setIssueName("name")
        issueInfosVO.setIssueNum("name1")
        issueInfosVO.setStatusId(1L)
        issueInfosVOMap.put(issueInfosVO.getIssueId(), issueInfosVO)

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/defect/fix?organizationId=1",
                HttpMethod.PUT,
                null,
                ResponseEntity.class,
                project_Id
        )
        then:
        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueInfosVOMap

        entity.statusCode.is2xxSuccessful()

    }

//    def "QueryByBug"() {
//        given: '根据缺陷issueId查询测试步骤'
//        List<IssueInfoDTO> issueInfoDTOList = new ArrayList<IssueInfoDTO>()
//
//        IssueInfoDTO issueInfoDTO = new IssueInfoDTO()
//        issueInfoDTO.setIssueId(1L)
//        issueInfoDTO.setIssueNum("name1")
//        issueInfoDTO.setSummary("summary")
//        issueInfoDTOList.add(issueInfoDTO)
//
//        TestCycleCaseDefectRelDTO dto = new TestCycleCaseDefectRelDTO()
//        dto.setId(8L)
//        dto.setProjectId(1L)
//        dto.setIssueId(1L)
//        dto.setDefectType(TestCycleCaseDefectCode.CYCLE_CASE)
//        dto.setDefectLinkId(1L)
//        testCycleCaseDefectRelMapper.insert(dto)
//
//        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO()
//        testCycleCaseDTO.setId(1L)
//        testCycleCaseDTO.setProjectId(1L)
//        testCycleCaseDTO.setIssueId(1L)
//        testCycleCaseDTO.setExecuteId(1L)
//        testCycleCaseDTO.setExecutionStatus(1L)
//        testCycleCaseMapper.insert(testCycleCaseDTO)
//
//        def bug = testCycleCaseDefectRelMapper.queryByBug(1L, 1L)
//        def mockito = new Mockito()
//        List<TestCycleCaseVO> list = new ArrayList<>()
//        //TestCycleCaseDefectRelService testCycleCaseDefectRelService = Mock()
//        Mockito.when(testCycleCaseDefectRelService.queryByBug(Matchers.anyLong(), Matchers.anyLong())).thenReturn(list)
//        when:
//        def entity = restTemplate.exchange("/v1/projects/{project_id}/defect/query_by_bug?bugId=1",
//                HttpMethod.GET,
//                null,
//                List.class,
//                project_Id
//        )
//        then:
//        //1 * testCaseFeignClient.listByIssueIds(_, _) >> new ResponseEntity<List<IssueInfoDTO>>(issueInfoDTOList, HttpStatus.OK)
//        //1 * testCycleCaseDefectRelService.queryByBug(_, _) >> list
//        entity.statusCode.is2xxSuccessful()
//
//    }
}
