package io.choerodon.test.manager.app.service.impl

import io.choerodon.agile.api.dto.*
import io.choerodon.core.domain.Page
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.IssueInfosDTO
import io.choerodon.test.manager.app.service.ExcelService
import io.choerodon.test.manager.app.service.FileService
import io.choerodon.test.manager.app.service.NotifyService
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.infra.dataobject.TestIssueFolderDO
import io.choerodon.test.manager.infra.dataobject.TestIssueFolderRelDO
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper
import io.choerodon.test.manager.infra.mapper.TestIssueFolderRelMapper
import org.assertj.core.util.Lists
import org.assertj.core.util.Maps
import org.springframework.aop.framework.AdvisedSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.lang.reflect.Field

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class ExcelServiceImplSpec extends Specification {

    @Autowired
    TestCaseService testCaseService

    @Autowired
    UserService userService

    @Autowired
    FileService fileFeignClient

    @Autowired
    ExcelService excelService

    @Autowired
    NotifyService notifyService

    @Autowired
    TestIssueFolderMapper testIssueFolderMapper

    @Autowired
    TestIssueFolderRelMapper testIssueFolderRelMapper

    @Shared
    Long[] versionIds = new Long[1]
    @Shared
    ProjectDTO projectDTO
    @Shared
    LookupTypeWithValuesDTO lookupTypeWithValuesDTO
    @Shared
    List<UserDTO> userDTOS
    @Shared
    Page page
    @Shared
    Map<Long, ProductVersionDTO> versionInfo
    @Shared
    List<IssueStatusDTO> issueStatusDTOS
    @Shared
    MockHttpServletRequest request
    @Shared
    Object target
    @Shared
    TestIssueFolderDO resFolderDO
    @Shared
    TestIssueFolderRelDO resFolderRelDO
    @Shared
    def projectId = 55555L
    @Shared
    def versionId = 55555L
    @Shared
    Long[] issuesId = new Long[2]
    @Shared
    Map<Long, IssueInfosDTO> issueInfosDTOMap

    void setupSpec() {
        issuesId[0] = 55555L
        issuesId[1] = 55556L
        issueInfosDTOMap = Maps.newHashMap(issuesId[0], new IssueInfosDTO(issueId: issuesId[0], issueNum: 1L, summary: "CaseExcel测试",
                priorityDTO: new PriorityDTO(id:1L,name: "CaseExcel测试"), assigneeName: "CaseExcel测试人", statusName: "CaseExcel测试状态"))
        versionIds[0] = versionId
        projectDTO = new ProjectDTO(name: "CaseExcel测试项目")
        List<LookupValueDTO> lookupValueDTOS = Lists.newArrayList(new LookupValueDTO())
        lookupTypeWithValuesDTO = new LookupTypeWithValuesDTO(lookupValues: lookupValueDTOS)
        userDTOS = Lists.newArrayList(new UserDTO(loginName: "1", realName: "test", id: 1L))
        page = new Page()
        page.setContent(userDTOS)
        ProductVersionDTO productVersionDTO = new ProductVersionDTO();
        productVersionDTO.setName("CaseExcel测试版本")
        versionInfo = Maps.newHashMap(versionId, productVersionDTO)
        issueStatusDTOS = Lists.newArrayList(new IssueStatusDTO())
        request = new MockHttpServletRequest()
        request.addHeader("User-Agent", "Chrome")
    }

    def "exportCaseProjectByTransaction"() {
        given:
        //将被spring代理的对象取出来
        Field h = excelService.getClass().getDeclaredField("CGLIB\$CALLBACK_0")
        h.setAccessible(true)
        Object dynamicAdvisedInterceptor = h.get((Object) excelService)
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised")
        advised.setAccessible(true)
        target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget()

        TestIssueFolderDO testIssueFolderDO = new TestIssueFolderDO(name: "caseExcel导出测试", versionId: versionId, projectId: projectId)
        testIssueFolderMapper.insert(testIssueFolderDO)
        resFolderDO = testIssueFolderMapper.selectOne(testIssueFolderDO)

        TestIssueFolderRelDO testIssueFolderRelDO = new TestIssueFolderRelDO(issueId: issuesId[0], projectId: projectId, versionId: versionId, folderId: resFolderDO.getFolderId())
        testIssueFolderRelMapper.insert(testIssueFolderRelDO)
        resFolderRelDO = testIssueFolderRelMapper.selectOne(testIssueFolderRelDO)

        when:
        target.exportCaseProjectByTransaction(projectId, request, new MockHttpServletResponse(), 1L,1L)

        then:
        1 * testCaseService.getProjectInfo(_) >> projectDTO
        1 * testCaseService.getVersionIds(_) >> versionIds
        1 * testCaseService.getIssueInfoMap(_, _, _,_) >> issueInfosDTOMap
        1 * testCaseService.queryLookupValueByCode(_, _) >> lookupTypeWithValuesDTO
        1 * userService.list(_, _, _, _) >> new ResponseEntity<Page>(page, HttpStatus.OK)
        1 * testCaseService.getVersionInfo(_) >> versionInfo
        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS
        1 * fileFeignClient.uploadFile(_, _, _) >> new ResponseEntity<String>(new String(), HttpStatus.OK)
        (4.._) * notifyService.postWebSocket(_, _, _)
    }

    def "exportCaseVersionByTransaction"() {
        when:
        target.exportCaseVersionByTransaction(projectId, versionId, request, new MockHttpServletResponse(), 1L,1L)

        then:
        1 * testCaseService.getProjectInfo(_) >> projectDTO
        1 * testCaseService.getIssueInfoMap(_, _, _,_) >> issueInfosDTOMap
        1 * testCaseService.queryLookupValueByCode(_, _) >> lookupTypeWithValuesDTO
        1 * userService.list(_, _, _, _) >> new ResponseEntity<Page>(page, HttpStatus.OK)
        2 * testCaseService.getVersionInfo(_) >> versionInfo
        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS
        1 * fileFeignClient.uploadFile(_, _, _) >> new ResponseEntity<String>(new String(), HttpStatus.OK)
        (4.._) * notifyService.postWebSocket(_, _, _)
    }

    def "exportCaseFolderByTransaction"() {
        when:
        target.exportCaseFolderByTransaction(projectId, resFolderDO.getFolderId(), request, new MockHttpServletResponse(), 1L,1L)

        then:
        1 * testCaseService.getProjectInfo(_) >> projectDTO
        1 * testCaseService.getIssueInfoMap(_, _, _,_) >> issueInfosDTOMap
        1 * testCaseService.queryLookupValueByCode(_, _) >> lookupTypeWithValuesDTO
        1 * userService.list(_, _, _, _) >> new ResponseEntity<Page>(page, HttpStatus.OK)
        1 * testCaseService.getVersionInfo(_) >> versionInfo
        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS
        1 * fileFeignClient.uploadFile(_, _, _) >> new ResponseEntity<String>(new String(), HttpStatus.OK)
        (4.._) * notifyService.postWebSocket(_, _, _)
    }

    def "ExportCaseTemplate"() {
        given:
        request.addHeader("User-Agent", "Firefox")

        when:
        excelService.exportCaseTemplate(1L, request, new MockHttpServletResponse())

        then:
        1 * testCaseService.getProjectInfo(_) >> projectDTO
        1 * testCaseService.getVersionIds(_) >> versionIds
        1 * testCaseService.queryLookupValueByCode(_, _) >> lookupTypeWithValuesDTO
        1 * userService.list(_, _, _, _) >> new ResponseEntity<Page>(page, HttpStatus.OK)
        1 * testCaseService.getVersionInfo(_) >> versionInfo
        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS

        and: "清理数据"
        testIssueFolderRelMapper.delete(resFolderRelDO)
        testIssueFolderMapper.delete(resFolderDO)
    }
}