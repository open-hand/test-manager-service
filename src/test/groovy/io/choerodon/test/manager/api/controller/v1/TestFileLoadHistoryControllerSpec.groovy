package io.choerodon.test.manager.api.controller.v1

import io.choerodon.test.manager.api.vo.agile.ProductVersionDTO
import io.choerodon.test.manager.api.vo.agile.ProjectDTO
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.vo.TestIssuesUploadHistoryVO
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.infra.dto.TestFileLoadHistoryDTO
import io.choerodon.test.manager.infra.mapper.TestFileLoadHistoryMapper
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 User: wangxiang
 Date: 2019/8/30
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestFileLoadHistoryControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    TestFileLoadHistoryMapper testFileLoadHistoryMapper

    @Autowired
    TestCaseService testCaseService

    @Autowired
    TestIssueFolderMapper testIssueFolderMapper

    @Shared
    Long projectId

    def "QueryIssues"() {
        given: '查询issue上传历史'
        TestFileLoadHistoryDTO fileLoadHistoryDTO = new TestFileLoadHistoryDTO()
        fileLoadHistoryDTO.setProjectId(1L)
        fileLoadHistoryDTO.setLinkedId(1L)
        fileLoadHistoryDTO.setActionType(2L)
        fileLoadHistoryDTO.setSourceType(2L)
        fileLoadHistoryDTO.setStatus(1L)
        testFileLoadHistoryMapper.insert(fileLoadHistoryDTO)

        List<TestFileLoadHistoryDTO> testFileLoadHistoryDTOList = testFileLoadHistoryMapper.selectAll()
        projectId = testFileLoadHistoryDTOList.get(0).getProjectId()
        ProjectDTO projectDTO = new ProjectDTO()
        projectDTO.setId(projectId)
        projectDTO.setName("project")

        Map<Long, ProductVersionDTO> map = new HashMap<Long, ProductVersionDTO>()
        ProductVersionDTO dto = new ProductVersionDTO()
        dto.setProjectId(1L)
        dto.setVersionId(1L)
        dto.setName("version")
        map.put(1L, dto)

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/test/fileload/history/issue",
                HttpMethod.GET,
                null,
                List.class,
                projectId,
        )
        then:
        1 * testCaseService.getVersionInfo(_) >> map
        entity.statusCode.is2xxSuccessful()

        and:
        TestFileLoadHistoryDTO historyDTO = new TestFileLoadHistoryDTO()
        historyDTO.setProjectId(1L)
        historyDTO.setLinkedId(1L)
        historyDTO.setActionType(3L)
        historyDTO.setSourceType(1L)
        historyDTO.setStatus(1L)
        testFileLoadHistoryMapper.delete(fileLoadHistoryDTO)
        testFileLoadHistoryMapper.insert(historyDTO)
        when:
        def entity1 = restTemplate.exchange("/v1/projects/{project_id}/test/fileload/history/issue",
                HttpMethod.GET,
                null,
                List.class,
                projectId,
        )

        then:
        1 * testCaseService.getProjectInfo(_) >> projectDTO
        entity1.statusCode.is2xxSuccessful()


    }

    def "QueryLatestLoadHistory"() {
        given: '查询最近一次导入记录'

        TestFileLoadHistoryDTO historyDTO = new TestFileLoadHistoryDTO()
        historyDTO.setProjectId(1L)
        historyDTO.setActionType(1L)
        historyDTO.setSourceType(1L)
        historyDTO.setLinkedId(1L)
        testFileLoadHistoryMapper.insert(historyDTO)

        Map<Long, ProductVersionDTO> map = new HashMap<Long, ProductVersionDTO>()
        ProductVersionDTO dto = new ProductVersionDTO()
        dto.setProjectId(1L)
        dto.setVersionId(1L)
        dto.setName("version")
        map.put(11111L, dto)

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/test/fileload/history/latest",
                HttpMethod.GET,
                null,
                TestIssuesUploadHistoryVO.class,
                projectId,
        )
        then:
        1 * testCaseService.getVersionInfo(_) >> map
        entity.statusCode.is2xxSuccessful()
    }

    def "CancelUpLoad"() {
        given: '将指定导入记录置为取消'
        def all = testFileLoadHistoryMapper.selectAll()

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/test/fileload/history/cancel?historyId=1",
                HttpMethod.PUT,
                null,
                ResponseEntity.class,
                projectId,
        )
        then:
        entity.statusCode.is2xxSuccessful()
    }

    def "QueryCycles"() {
        given: '查询cycle上传历史'
        TestFileLoadHistoryDTO historyDTO = new TestFileLoadHistoryDTO()
        historyDTO.setProjectId(1L)
        historyDTO.setActionType(1L)
        historyDTO.setSourceType(3L)
        historyDTO.setLinkedId(1L)
        testFileLoadHistoryMapper.insert(historyDTO)
        def list = testFileLoadHistoryMapper.selectAll()

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/test/fileload/history/cycle?historyId=1",
                HttpMethod.GET,
                null,
                List,
                projectId,
        )
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.size() > 0
    }
}
