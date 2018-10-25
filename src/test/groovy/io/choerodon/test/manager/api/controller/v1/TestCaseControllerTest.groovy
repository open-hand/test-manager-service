package io.choerodon.test.manager.api.controller.v1

import io.choerodon.agile.api.dto.IssueStatusDTO
import io.choerodon.agile.api.dto.LookupTypeWithValuesDTO
import io.choerodon.agile.api.dto.LookupValueDTO
import io.choerodon.agile.api.dto.ProductVersionDTO
import io.choerodon.agile.api.dto.ProjectDTO
import io.choerodon.agile.api.dto.UserDTO
import io.choerodon.core.domain.Page
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.IssueInfosDTO
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.UserService
import org.assertj.core.util.Lists
import org.assertj.core.util.Maps
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCaseControllerTest extends Specification {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    TestCaseService testCaseService

    @Autowired
    UserService userService

//    def "CreateFormsFromIssueToDefect"() {
//    }
//
//    def "CreateFormsFromIssueToDefectByIssueId"() {
//    }
//
//    def "CreateFormDefectFromIssueById"() {
//    }
//
//    def "CreateFormDefectFromIssue"() {
//    }

    def "DownLoadByProject"() {
        given:
        Long[] versionIds = new Long[3];
        versionIds[0] = 1L
        ProjectDTO projectDTO = new ProjectDTO(name: "CaseExcel测试项目")
        List<LookupValueDTO> lookupValueDTOS = Lists.newArrayList(new LookupValueDTO())
        LookupTypeWithValuesDTO lookupTypeWithValuesDTO = new LookupTypeWithValuesDTO(lookupValues: lookupValueDTOS)
        List<UserDTO> userDTOS = Lists.newArrayList(new UserDTO(loginName: "1", realName: "test",id: 1L))
        Page page = new Page()
        page.setContent(userDTOS)
        ProductVersionDTO productVersionDTO = new ProductVersionDTO();
        productVersionDTO.setName("CaseExcel测试版本")
        Map<Long, ProductVersionDTO> versionInfo = Maps.newHashMap(1L, productVersionDTO)
        List<IssueStatusDTO> issueStatusDTOS = Lists.newArrayList(new IssueStatusDTO())

        when:
        restTemplate.getForEntity("/v1/projects/{project_id}/case/download/excel", null,1L)

        then:
        1 * testCaseService.getVersionIds(_) >> versionIds
        1 * testCaseService.getProjectInfo(_) >> projectDTO
        0 * testCaseService.getIssueInfoMap(_, _, _) >> new HashMap()
        1 * testCaseService.queryLookupValueByCode(_, _) >> lookupTypeWithValuesDTO
        1 * userService.list(_, _, _, _) >> new ResponseEntity<Page>(page, HttpStatus.OK)
        1 * testCaseService.getVersionInfo(_) >> versionInfo
        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS
    }

    def "DownLoadByVersion"() {
        given:
        Long[] versionIds = new Long[3];
        versionIds[0] = 1L
        ProjectDTO projectDTO = new ProjectDTO(name: "CaseExcel测试项目")
        List<LookupValueDTO> lookupValueDTOS = Lists.newArrayList(new LookupValueDTO())
        LookupTypeWithValuesDTO lookupTypeWithValuesDTO = new LookupTypeWithValuesDTO(lookupValues: lookupValueDTOS)
        List<UserDTO> userDTOS = Lists.newArrayList(new UserDTO(loginName: "1", realName: "test",id: 1L))
        Page page = new Page()
        page.setContent(userDTOS)
        ProductVersionDTO productVersionDTO = new ProductVersionDTO();
        productVersionDTO.setName("CaseExcel测试版本")
        Map<Long, ProductVersionDTO> versionInfo = Maps.newHashMap(1L, productVersionDTO)
        List<IssueStatusDTO> issueStatusDTOS = Lists.newArrayList(new IssueStatusDTO())

        when:
        restTemplate.getForEntity("/v1/projects/{project_id}/case/download/excel/version?versionId={versionId}", null, 1L,1L)

        then:
        1 * testCaseService.getProjectInfo(_) >> projectDTO
        0 * testCaseService.getIssueInfoMap(_, _, _) >> new HashMap()
        1 * testCaseService.queryLookupValueByCode(_, _) >> lookupTypeWithValuesDTO
        1 * userService.list(_, _, _, _) >> new ResponseEntity<Page>(page, HttpStatus.OK)
        1 * testCaseService.getVersionInfo(_) >> versionInfo
        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS
    }

    def "DownLoadByFolder"() {
        given:
        Long[] versionIds = new Long[3];
        versionIds[0] = 1L
        ProjectDTO projectDTO = new ProjectDTO(name: "CaseExcel测试项目")
        List<LookupValueDTO> lookupValueDTOS = Lists.newArrayList(new LookupValueDTO())
        LookupTypeWithValuesDTO lookupTypeWithValuesDTO = new LookupTypeWithValuesDTO(lookupValues: lookupValueDTOS)
        List<UserDTO> userDTOS = Lists.newArrayList(new UserDTO(loginName: "1", realName: "test",id: 1L))
        Page page = new Page()
        page.setContent(userDTOS)
        ProductVersionDTO productVersionDTO = new ProductVersionDTO();
        productVersionDTO.setName("CaseExcel测试版本")
        Map<Long, ProductVersionDTO> versionInfo = Maps.newHashMap(1L, productVersionDTO)
        List<IssueStatusDTO> issueStatusDTOS = Lists.newArrayList(new IssueStatusDTO())

        when:
        restTemplate.getForEntity("/v1/projects/{project_id}/case/download/excel/folder?folderId={folderId}", null, 1L,1L)

        then:
        1 * testCaseService.getProjectInfo(_) >> projectDTO
        0 * testCaseService.getIssueInfoMap(_, _, _) >> new HashMap()
        1 * testCaseService.queryLookupValueByCode(_, _) >> lookupTypeWithValuesDTO
        1 * userService.list(_, _, _, _) >> new ResponseEntity<Page>(page, HttpStatus.OK)
        1 * testCaseService.getVersionInfo(_) >> versionInfo
        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS
    }

    def "DownLoadTemplate"() {
        given:
        Long[] versionIds = new Long[3];
        versionIds[0] = 1L
        versionIds[1] = 2L
        versionIds[2] = 3L
        ProjectDTO projectDTO = new ProjectDTO(name: "CaseExcel测试项目")
        List<LookupValueDTO> lookupValueDTOS = Lists.newArrayList(new LookupValueDTO())
        LookupTypeWithValuesDTO lookupTypeWithValuesDTO = new LookupTypeWithValuesDTO(lookupValues: lookupValueDTOS)
        List<UserDTO> userDTOS = Lists.newArrayList(new UserDTO(loginName: "1", realName: "test",id: 1L))
        Page page = new Page()
        page.setContent(userDTOS)
        ProductVersionDTO productVersionDTO = new ProductVersionDTO();
        productVersionDTO.setName("CaseExcel测试版本")
        Map<Long, ProductVersionDTO> versionInfo = Maps.newHashMap(1L, productVersionDTO)
        List<IssueStatusDTO> issueStatusDTOS = Lists.newArrayList(new IssueStatusDTO())

        when:
        restTemplate.getForEntity("/v1/projects/{project_id}/case/download/excel/template", null,1L)

        then:
        1 * testCaseService.getProjectInfo(_) >> projectDTO
        1 * testCaseService.queryLookupValueByCode(_, _) >> lookupTypeWithValuesDTO
        1 * userService.list(_, _, _, _) >> new ResponseEntity<Page>(page, HttpStatus.OK)
        1 * testCaseService.getVersionInfo(_) >> versionInfo
        1 * testCaseService.listStatusByProjectId(_) >> issueStatusDTOS
    }
}
