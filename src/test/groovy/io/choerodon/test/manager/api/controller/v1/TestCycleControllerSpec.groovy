package io.choerodon.test.manager.api.controller.v1

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.api.dto.ProductVersionDTO
import io.choerodon.agile.api.dto.ProductVersionPageDTO
import io.choerodon.agile.api.dto.UserDO
import io.choerodon.core.domain.Page
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.TestCycleDTO
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE
import io.choerodon.test.manager.infra.dataobject.TestCaseStepDO
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDO
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseStepDO
import io.choerodon.test.manager.infra.dataobject.TestCycleDO
import io.choerodon.test.manager.infra.dataobject.TestIssueFolderRelDO
import io.choerodon.test.manager.infra.feign.UserFeignClient
import io.choerodon.test.manager.infra.mapper.TestCaseStepMapper
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper
import io.choerodon.test.manager.infra.mapper.TestCycleCaseStepMapper
import io.choerodon.test.manager.infra.mapper.TestCycleMapper
import io.choerodon.test.manager.infra.mapper.TestIssueFolderRelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCycleControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    TestCaseService testCaseService

    @Autowired
    UserService userService

    @Autowired
    TestCycleMapper testCycleMapper

    @Autowired
    TestIssueFolderRelMapper testIssueFolderRelMapper

    @Autowired
    TestCycleCaseMapper testCycleCaseMapper

    @Autowired
    TestCaseStepMapper testCaseStepMapper

    @Shared
    def projectId = 1L
    @Shared
    def versionId = 1L
    @Shared
    List<TestCycleDTO> testCycleDTOS = new ArrayList<>()

    @Shared
    List cycleIds = new ArrayList()

    @Shared
    TestIssueFolderRelDO testIssueFolderRelDO = new TestIssueFolderRelDO()
    @Shared
    TestIssueFolderRelDO testIssueFolderRelDO2 = new TestIssueFolderRelDO()
    @Shared
    TestCycleCaseDO testCycleCaseDO = new TestCycleCaseDO()
    @Shared
    TestIssueFolderRelDO insertFolderRel = new TestIssueFolderRelDO()
    @Shared
    TestCaseStepDO testCaseStepDO1 = new TestCaseStepDO()
    @Shared
    TestCaseStepDO testCaseStepDO2 = new TestCaseStepDO()


    def "Insert"() {
        given:
        TestCycleDTO testCycleDTO1 = new TestCycleDTO()
        testCycleDTO1.setCycleName("testCycleInsert")
        testCycleDTO1.setVersionId(versionId)
        testCycleDTO1.setType(TestCycleE.CYCLE)
        testCycleDTO1.setObjectVersionNumber(1L)

        testCycleDTOS.add(testCycleDTO1)

        TestCycleDTO testCycleDTO2 = new TestCycleDTO()
        testCycleDTO2.setCycleName("testFolderInsert")
        testCycleDTO2.setFolderId(11L)
        testCycleDTO2.setVersionId(versionId)
        testCycleDTO2.setType(TestCycleE.FOLDER)
        testCycleDTO2.setObjectVersionNumber(1L)

        insertFolderRel.setProjectId(projectId)
        insertFolderRel.setVersionId(versionId)
        insertFolderRel.setFolderId(11L)
        insertFolderRel.setIssueId(44444444L)
        testIssueFolderRelMapper.insert(insertFolderRel)

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle', testCycleDTOS.get(0), TestCycleDTO, projectId)
        then:
        entity.statusCode.is2xxSuccessful()
        and:
        entity.body != null
        entity.body.folderId == null
        entity.body.type == TestCycleE.CYCLE
        and:
        testCycleDTOS.get(0).setCycleId(entity.getBody().getCycleId())
        and:
        testCycleDTO2.setParentCycleId(testCycleDTOS.get(0).getCycleId())
        testCycleDTOS.add(testCycleDTO2)

        when:
        entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle', testCycleDTOS.get(1), TestCycleDTO, projectId)
        then:
        entity.statusCode.is2xxSuccessful()
        and:
        entity.body != null
        entity.body.folderId == 11L
        entity.body.type == TestCycleE.FOLDER
        and:
        testCycleDTOS.get(1).setCycleId(entity.getBody().getCycleId())

    }

    def "Update"() {
        given:
        testCycleDTOS.get(0).setCycleName("testCycleUpdate")
        testCycleDTOS.get(1).setCycleName("testFolderUpdate")
        testCycleDTOS.get(1).setParentCycleId(testCycleDTOS.get(0).getCycleId())

        TestCycleDO cycleDO = new TestCycleDO()
        cycleDO.setCycleName("testTemp")
        cycleDO.setType("temp")
        cycleDO.setVersionId(versionId)
        testCycleMapper.insert(cycleDO)
        TestCycleDO resCycleDO = testCycleMapper.selectOne(cycleDO)

        when:
        HttpEntity<TestCycleDTO> requestEntity = new HttpEntity<TestCycleDTO>(testCycleDTOS.get(0), null)
        def entity = restTemplate.exchange('/v1/projects/{project_id}/cycle',
                HttpMethod.PUT, requestEntity, TestCycleDTO, projectId)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.getCycleName() == "testCycleUpdate"
        entity.body.type == TestCycleE.CYCLE

        when:
        requestEntity = new HttpEntity<TestCycleDTO>(testCycleDTOS.get(1), null)
        entity = restTemplate.exchange('/v1/projects/{project_id}/cycle',
                HttpMethod.PUT, requestEntity, TestCycleDTO, projectId)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.getCycleName() == "testFolderUpdate"
        entity.body.type == TestCycleE.FOLDER

        when:'覆盖temp1.getType().equals(TestCycleE.TEMP)的情况'
        requestEntity = new HttpEntity<TestCycleDTO>(resCycleDO, null)
        entity = restTemplate.exchange('/v1/projects/{project_id}/cycle',
                HttpMethod.PUT, requestEntity, TestCycleDTO, projectId)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.getCycleName() == "testTemp"
        entity.body.type == "temp"
        and:'清理值'
        testCycleMapper.delete(cycleDO)
    }

    def "QueryOne"() {
        when:
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/cycle/query/one/{cycleId}', TestCycleDTO, projectId, testCycleDTOS.get(0).getCycleId())
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.cycleName == "testCycleUpdate"
    }

    def "GetTestCycle"() {
        given:
        ProductVersionDTO productVersionDTO = new ProductVersionDTO()
        productVersionDTO.setVersionId(versionId)
        productVersionDTO.setStatusName("testCycle")
        productVersionDTO.setName("testCycle")

        ProductVersionDTO productVersionDTO2 = new ProductVersionDTO()
        productVersionDTO2.setVersionId(22222222L)
        productVersionDTO2.setName("testCycle2")
        productVersionDTO2.setStatusName("testCycle2")

        Map map = new HashMap()
        map.put(1L, productVersionDTO)
        map.put(2L, productVersionDTO2)

        Map userMap = new HashMap()
        userMap.put(20645L, new UserDO())
        userMap.put(20645L, new UserDO())

        when:
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/query", JSONObject.class, projectId, 20645L)
        then:
        1 * testCaseService.getVersionInfo(_) >> map
        1 * userService.query(_) >> userMap
        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject jsonObject = entity.body

        expect:
        !jsonObject.isEmpty()

        when:
        entity = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/query", JSONObject.class, projectId, 20645L)
        then:
        1 * testCaseService.getVersionInfo(_) >> new HashMap<>()
        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject jsonObject2 = entity.body

        expect:
        jsonObject2.isEmpty()
    }

    def "getTestCycleCaseCountInVersion()"(){
        when:
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/count/color/in/version/{versionId}", JSONArray.class, projectId, versionId)
        then:
        entity.statusCode.is2xxSuccessful()

    }

    def "GetTestCycleVersion"() {
        given:
        Map<String, Object> searchParamMap = new HashMap<>()
        searchParamMap.put("cycleName", "发布11")

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/query/version', searchParamMap, Page.class, 12L)
        then: '返回值'
        1 * testCaseService.getTestCycleVersionInfo(_, _) >> new ResponseEntity<Page<ProductVersionPageDTO>>(HttpStatus.OK)
    }

    def "CloneCycle"() {
        given:
        TestCycleDTO testCycleDTO = new TestCycleDTO()
        testCycleDTO.setVersionId(99L)
        testCycleDTO.setCycleName("cloneCycleTest")

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/clone/folder/{cycleId}', testCycleDTO, TestCycleDTO, testCycleDTOS.get(0).getCycleId(), projectId)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.versionId == 99L
        entity.body.cycleName == "cloneCycleTest"

        and: '设置值'
        testCycleDTOS.add(entity.body)
    }

    def "CloneFolder"() {
        given:
        TestCycleDTO testCycleDTO = new TestCycleDTO()
        testCycleDTO.setCycleName("cloneCycleFolderTest")

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/clone/folder/{cycleId}', testCycleDTO, TestCycleDTO, testCycleDTOS.get(1).getCycleId(), projectId)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.cycleName == "cloneCycleFolderTest"

        and: '设置值'
        testCycleDTOS.add(entity.body)
    }

    def "GetFolderByCycleId"() {
        given:
        TestCycleDTO testCycleDTO = new TestCycleDTO()
        testCycleDTO.setCycleName("cloneCycleFolderTest")

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/query/folder/cycleId/{cycleId}', null, List, projectId, testCycleDTOS.get(0).getCycleId())
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.size() == 1
        List<TestCycleDTO> list = entity.body
        list.get(0).cycleName == "testFolderUpdate"
    }

    def "SynchroFolder"() {
        given:
        testIssueFolderRelDO.setIssueId(888L)
        testIssueFolderRelDO.setProjectId(1L)
        testIssueFolderRelDO.setVersionId(1L)
        testIssueFolderRelDO.setFolderId(testCycleDTOS.get(1).getFolderId())

        testIssueFolderRelDO2.setIssueId(889L)
        testIssueFolderRelDO2.setProjectId(1L)
        testIssueFolderRelDO2.setVersionId(1L)
        testIssueFolderRelDO2.setFolderId(testCycleDTOS.get(1).getFolderId())

        testIssueFolderRelMapper.insert(testIssueFolderRelDO)
        testIssueFolderRelMapper.insert(testIssueFolderRelDO2)

        testCycleCaseDO.setIssueId(888L)
        testCycleCaseDO.setCycleId(testCycleDTOS.get(1).getCycleId())
        testCycleCaseDO.setRank("0|c04564:")
        testCycleCaseDO.setExecutionStatus(1L)
        testCycleCaseMapper.insert(testCycleCaseDO)
        TestCycleCaseDO resCycleCase = testCycleCaseMapper.selectOne(testCycleCaseDO)

        testCaseStepDO1.setIssueId(889L)
        testCaseStepDO1.setRank("0|c04564:")
        testCaseStepMapper.insert(testCaseStepDO1)
        testCaseStepDO2.setIssueId(888L)
        testCaseStepDO1.setRank("0|c04564:")
        testCaseStepMapper.insert(testCaseStepDO2)


        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/synchro/folder/{folderId}/in/{cycleId}', null, null, projectId, testCycleDTOS.get(1).getFolderId(), testCycleDTOS.get(1).getCycleId())
        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        when:
        entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/synchro/folder/{folderId}/in/{cycleId}', null, null, projectId, testCycleDTOS.get(1).getFolderId(), 1111111L)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        when: '覆盖syncCycleCaseStep的caseSteps为空的情况'
        entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/synchro/folder/{folderId}/in/{cycleId}', null, null, projectId, 1111111L, 1111111L)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
    }

    def "synchroFolderInCycle"() {
        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/synchro/folder/all/in/cycle/{cycleId}', null, null, projectId, testCycleDTOS.get(0).getCycleId())
        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        when:
        entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/synchro/folder/all/in/cycle/{cycleId}', null, null, projectId, testCycleDTOS.get(1).getCycleId())
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
    }

    def "SynchroFolderInVersion"() {
        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/synchro/folder/all/in/version/{versionId}', null, null, projectId, testCycleDTOS.get(1).getVersionId())
        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        when:
        entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/synchro/folder/all/in/version/{versionId}', null, null, projectId, 1111111111L)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()

        and:
        cycleIds.add(testCycleDTOS.get(0).getCycleId())
        cycleIds.add(testCycleDTOS.get(1).getCycleId())
        cycleIds.add(testCycleDTOS.get(2).getCycleId())
        cycleIds.add(testCycleDTOS.get(3).getCycleId())
    }


    def "getCycleInVersion"() {
        when:
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/cycle/get/cycles/all/in/version/{versionId}', List, projectId, versionId)
        then:
        entity.statusCode.is2xxSuccessful()
    }

    def "Delete"() {
        given: '清理数据'
        testCycleCaseMapper.delete(testCycleCaseDO)
        testIssueFolderRelMapper.delete(testIssueFolderRelDO)
        testIssueFolderRelMapper.delete(testIssueFolderRelDO2)
        testIssueFolderRelMapper.delete(insertFolderRel)
        testCaseStepMapper.delete(testCaseStepDO1)
        testCaseStepMapper.delete(testCaseStepDO2)

        when: '执行方法'
        restTemplate.delete('/v1/projects/{project_id}/cycle/delete/{cycleId}', projectId, cycleId)

        then: '返回值'
        def result = testCycleMapper.selectByPrimaryKey(cycleId as Long)

        expect: '期望值'
        result == null

        where: '判断cycle是否删除'
        cycleId << cycleIds

    }



}
