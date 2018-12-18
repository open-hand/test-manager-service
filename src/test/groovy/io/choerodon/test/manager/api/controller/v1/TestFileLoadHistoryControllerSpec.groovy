//package io.choerodon.test.manager.api.controller.v1
//
//
//import io.choerodon.agile.api.dto.ProductVersionDTO
//import io.choerodon.agile.api.dto.ProjectDTO
//import io.choerodon.core.convertor.ConvertHelper
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.app.service.TestCaseService
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE
//import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE
//import io.choerodon.test.manager.domain.test.manager.factory.TestIssueFolderEFactory
//import io.choerodon.test.manager.infra.dataobject.TestCycleDO
//import io.choerodon.test.manager.infra.dataobject.TestFileLoadHistoryDO
//import io.choerodon.test.manager.infra.mapper.TestCycleMapper
//import io.choerodon.test.manager.infra.mapper.TestFileLoadHistoryMapper
//import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper
//import org.assertj.core.util.Maps
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
//
///**
// * Created by zongw.lee@gmail.com
// */
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//@Stepwise
//class TestFileLoadHistoryControllerSpec extends Specification {
//
//    @Autowired
//    TestCaseService testCaseService
//
//    @Autowired
//    TestRestTemplate restTemplate
//
//    @Autowired
//    TestFileLoadHistoryMapper historyMapper
//
//    @Autowired
//    TestCycleMapper testCycleMapper
//
//    @Autowired
//    TestIssueFolderMapper testIssueFolderMapper
//
//    @Shared
//    def projectId = 22222L
//    @Shared
//    def versionId = 22222L
//
//    @Shared
//    TestCycleDO resCycleDO
//    @Shared
//    TestIssueFolderE resFolderE
//    @Shared
//    TestFileLoadHistoryDO projectHistory
//    @Shared
//    TestFileLoadHistoryDO versionHistory
//    @Shared
//    TestFileLoadHistoryDO folderHistory
//    @Shared
//    TestFileLoadHistoryDO cycleHistory
//
//
//    def "QueryIssues"() {
//        given:
//        TestCycleDO cycleDO = new TestCycleDO(cycleName: "fileHistory测试")
//        testCycleMapper.insert(cycleDO)
//        resCycleDO = testCycleMapper.selectOne(cycleDO)
//
//        TestIssueFolderE folderE = TestIssueFolderEFactory.create();
//        folderE.setName("fileHistory测试")
//        folderE.setProjectId(projectId)
//        folderE.setVersionId(versionId)
//        folderE.setType("cycle")
//
//        resFolderE = folderE.addSelf();
//        projectHistory = new TestFileLoadHistoryDO(projectId: projectId, actionType: 2L, sourceType: 1L, linkedId: projectId)
//        versionHistory = new TestFileLoadHistoryDO(projectId: projectId, actionType: 2L, sourceType: 2L, linkedId: versionId)
//        folderHistory = new TestFileLoadHistoryDO(projectId: projectId, actionType: 2L, sourceType: 4L, linkedId: resFolderE.getFolderId())
//        cycleHistory = new TestFileLoadHistoryDO(projectId: projectId, actionType: 3L, sourceType: 3L, linkedId: resCycleDO.getCycleId())
//        historyMapper.insert(projectHistory)
//        historyMapper.insert(versionHistory)
//        historyMapper.insert(folderHistory)
//        historyMapper.insert(cycleHistory)
//        Map<Long, ProductVersionDTO> versionsMap = Maps.newHashMap(versionId, new ProductVersionDTO(projectId: projectId, versionId: versionId, name: "fileHistory测试版本"))
//
//        when:
//        def entities = restTemplate.getForEntity("/v1/projects/{project_id}/test/fileload/history/issue", List, projectId)
//
//        then:
//        1 * testCaseService.getProjectInfo(_) >> new ProjectDTO(id: projectId, name: "fileHistory测试项目")
//        1 * testCaseService.getVersionInfo(_) >> versionsMap
//
//        and:
//        entities.getStatusCode().is2xxSuccessful()
//        entities.getBody().size() >= 3
//    }
//
//    def "QueryCycle"() {
//        when:
//        def entities = restTemplate.getForEntity("/v1/projects/{project_id}/test/fileload/history/cycle", List, projectId)
//
//        then:
//        entities.getStatusCode().is2xxSuccessful()
//        entities.getBody().size() == 1
//
//        and: "清理数据"
//        resFolderE.deleteSelf()
//        testCycleMapper.delete(resCycleDO)
//        historyMapper.delete(projectHistory)
//        historyMapper.delete(versionHistory)
//        historyMapper.delete(cycleHistory)
//        historyMapper.delete(folderHistory)
//    }
//}
