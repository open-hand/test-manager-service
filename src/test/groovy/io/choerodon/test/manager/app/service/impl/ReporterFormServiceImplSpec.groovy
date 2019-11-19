package io.choerodon.test.manager.app.service.impl

import com.github.pagehelper.PageInfo
import io.choerodon.agile.api.vo.IssueLinkDTO
import io.choerodon.agile.api.vo.SearchDTO
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.vo.IssueInfosVO
import io.choerodon.test.manager.api.vo.ReporterFormVO
import io.choerodon.test.manager.app.service.ReporterFormService
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.TestCycleCaseService
import io.choerodon.test.manager.infra.mapper.TestCycleCaseDefectRelMapper
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper
import io.choerodon.test.manager.infra.mapper.TestCycleCaseStepMapper
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 User: wangxiang
 Date: 2019/9/5
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ReporterFormServiceImplSpec extends Specification {

    @Autowired
    ReporterFormService reporterFormService

    @Autowired
    TestCaseService testCaseService

    @Autowired
    TestCycleCaseService testCycleCaseService

    @Autowired
    TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper

    @Autowired
    TestCycleCaseMapper testCycleCaseMapper

    @Autowired
    TestCycleCaseStepMapper testCycleCaseStepMapper

    @Autowired
    ModelMapper modelMapper

    @Shared
    Long projectId = 1L

    @Shared
    Long organizationId = 1L


    def "CreateFromIssueToDefect"() {

        Map<Long, IssueInfosVO> issueInfosVOMap = new HashMap<Long, IssueInfosVO>()
        IssueInfosVO issueInfosVO = new IssueInfosVO()
        issueInfosVO.setIssueId(1L)
        issueInfosVO.setProjectId(1L)
        issueInfosVO.setIssueName("name")
        issueInfosVO.setIssueNum("name1")
        issueInfosVO.setStatusId(1L)
        issueInfosVOMap.put(issueInfosVO.getIssueId(), issueInfosVO)

        List<IssueLinkDTO> issueLinkDTOList = new ArrayList<>()
        IssueLinkDTO issueLinkDTO = new IssueLinkDTO()
        issueLinkDTO.setIssueId(1L)

        SearchDTO searchDTO = new SearchDTO()
        PageRequest pageRequest = new PageRequest(1, 10)

        when:
        PageInfo<ReporterFormVO> list = reporterFormService.createFromIssueToDefect(projectId, searchDTO, pageRequest, organizationId)

        then:
        1 * testCaseService.getIssueInfoMapAndPopulatePageInfo(_, _, _, _, _) >> issueInfosVOMap
        1 * testCaseService.getLinkIssueFromIssueToTest(_, _) >> issueLinkDTOList
        noExceptionThrown()
    }

    def "CreateFromIssueToDefect1"() {
        given:
        Long[] issueIds = new Long[1]
        issueIds[0] = 1L

        Map<Long, IssueInfosVO> issueInfosVOMap = new HashMap<Long, IssueInfosVO>()
        IssueInfosVO issueInfosVO = new IssueInfosVO()
        issueInfosVO.setIssueId(1L)
        issueInfosVO.setProjectId(1L)
        issueInfosVO.setIssueName("name")
        issueInfosVO.setIssueNum("name1")
        issueInfosVO.setStatusId(1L)
        issueInfosVOMap.put(issueInfosVO.getIssueId(), issueInfosVO)

        List<IssueLinkDTO> issueLinkDTOList = new ArrayList<>()
        IssueLinkDTO issueLinkDTO = new IssueLinkDTO()
        issueLinkDTO.setIssueId(1L)

        when:
        reporterFormService.createFromIssueToDefect(projectId, issueIds, organizationId)
        then:
        1 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueInfosVOMap
        1 * testCaseService.getLinkIssueFromIssueToTest(_, _) >> issueLinkDTOList
        noExceptionThrown()
    }

    def "CreateFormDefectFromIssue"() {
        given:
        Long[] issueIds = new Long[1]
        issueIds[0] = 1L

        Map<Long, IssueInfosVO> issueInfosVOMap = new HashMap<Long, IssueInfosVO>()
        IssueInfosVO issueInfosVO = new IssueInfosVO()
        issueInfosVO.setIssueId(1L)
        issueInfosVO.setProjectId(1L)
        issueInfosVO.setIssueName("name")
        issueInfosVO.setIssueNum("name1")
        issueInfosVO.setStatusId(1L)
        issueInfosVOMap.put(issueInfosVO.getIssueId(), issueInfosVO)

        List<IssueLinkDTO> issueLinkDTOList = new ArrayList<>()
        IssueLinkDTO issueLinkDTO = new IssueLinkDTO()
        issueLinkDTO.setIssueId(1L)

        when:
        reporterFormService.createFormDefectFromIssue(projectId, issueIds, organizationId)

        then:
        2 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueInfosVOMap
        1 * testCaseService.getLinkIssueFromTestToIssue(_, _) >> issueLinkDTOList
        noExceptionThrown()

    }

    def "CreateFormDefectFromIssue1"() {
        given:

        Long[] issueIds = new Long[1]
        issueIds[0] = 1L

        Map<Long, IssueInfosVO> issueInfosVOMap = new HashMap<Long, IssueInfosVO>()
        IssueInfosVO issueInfosVO = new IssueInfosVO()
        issueInfosVO.setIssueId(1L)
        issueInfosVO.setProjectId(1L)
        issueInfosVO.setIssueName("name")
        issueInfosVO.setIssueNum("name1")
        issueInfosVO.setStatusId(1L)
        issueInfosVOMap.put(issueInfosVO.getIssueId(), issueInfosVO)

        List<IssueLinkDTO> issueLinkDTOList = new ArrayList<>()
        IssueLinkDTO issueLinkDTO = new IssueLinkDTO()
        issueLinkDTO.setIssueId(1L)

        SearchDTO searchDTO = new SearchDTO()
        PageRequest pageRequest = new PageRequest(1, 10)

        when:
        reporterFormService.createFormDefectFromIssue(projectId, searchDTO, pageRequest, organizationId)

        then:
        1 * testCaseService.queryIssueIdsByOptions(_, _) >> issueIds
        2 * testCaseService.getIssueInfoMap(_, _, _, _) >> issueInfosVOMap
        1 * testCaseService.getLinkIssueFromTestToIssue(_, _) >> issueLinkDTOList
        noExceptionThrown()
    }
}
