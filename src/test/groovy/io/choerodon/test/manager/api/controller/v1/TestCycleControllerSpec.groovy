package io.choerodon.test.manager.api.controller.v1

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import io.choerodon.test.manager.api.vo.agile.ProductVersionDTO
import io.choerodon.test.manager.api.vo.agile.ProductVersionPageDTO
import io.choerodon.test.manager.api.vo.agile.UserDO
import com.github.pagehelper.PageInfo
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.vo.BatchCloneCycleVO
import io.choerodon.test.manager.api.vo.TestCycleVO
import io.choerodon.test.manager.api.vo.TestFileLoadHistoryVO
import io.choerodon.test.manager.app.service.NotifyService
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.infra.dto.TestCaseStepDTO
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO
import io.choerodon.test.manager.infra.dto.TestCycleDTO
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO
import io.choerodon.test.manager.infra.dto.TestIssueFolderRelDTO
import io.choerodon.test.manager.infra.enums.TestCycleType
import io.choerodon.test.manager.infra.mapper.TestCaseStepMapper
import io.choerodon.test.manager.infra.mapper.TestCycleCaseDefectRelMapper
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper
import io.choerodon.test.manager.infra.mapper.TestCycleMapper
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper
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


/**
 * Created by zongw.lee@gmail.com
 */
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
    TestIssueFolderMapper testIssueFolderMapper

    @Autowired
    TestIssueFolderRelMapper testIssueFolderRelMapper

    @Autowired
    TestCycleCaseMapper testCycleCaseMapper

    @Autowired
    TestCaseStepMapper testCaseStepMapper

    @Autowired
    TestCycleCaseDefectRelMapper defectRelMapper

    @Autowired
    NotifyService notifyService

    @Shared
    def projectId = 1L
    @Shared
    def versionId = 1L
    @Shared
    List<TestCycleVO> testCycleDTOS = new ArrayList<>()

    @Shared
    List cycleIds = new ArrayList()

    @Shared
    TestIssueFolderRelDTO testIssueFolderRelDO = new TestIssueFolderRelDTO()
    @Shared
    TestIssueFolderRelDTO testIssueFolderRelDO2 = new TestIssueFolderRelDTO()
    @Shared
    TestCycleCaseDTO testCycleCaseDO = new TestCycleCaseDTO()
    @Shared
    TestIssueFolderRelDTO insertFolderRel = new TestIssueFolderRelDTO()
    @Shared
    TestCaseStepDTO testCaseStepDO1 = new TestCaseStepDTO()
    @Shared
    TestCaseStepDTO testCaseStepDO2 = new TestCaseStepDTO()
    @Shared
    TestIssueFolderDTO resFolderDO


    def "Insert"() {
        given: '增加测试循环'
        TestCycleVO testCycleDTO1 = new TestCycleVO()
        testCycleDTO1.setCycleName("testCycleInsert")
        testCycleDTO1.setVersionId(versionId)
        testCycleDTO1.setToDate(new Date())
        testCycleDTO1.setFromDate(new Date())
        testCycleDTO1.setType(TestCycleType.CYCLE)
        testCycleDTO1.setObjectVersionNumber(1L)

        TestIssueFolderDTO folderDO = new TestIssueFolderDTO()
        folderDO.setName("cycle测试文件夹")
        folderDO.setProjectId(projectId)
        folderDO.setVersionId(versionId)
        folderDO.setType(TestCycleType.CYCLE)
        testIssueFolderMapper.insert(folderDO)
        resFolderDO = testIssueFolderMapper.selectOne(folderDO)

        testCycleDTOS.add(testCycleDTO1)

        TestCycleVO testCycleDTO2 = new TestCycleVO()
        testCycleDTO2.setCycleName("testFolderInsert")
        testCycleDTO2.setFolderId(resFolderDO.getFolderId())
        testCycleDTO2.setVersionId(versionId)
        testCycleDTO2.setType(TestCycleType.FOLDER)
        testCycleDTO2.setToDate(new Date())
        testCycleDTO2.setFromDate(new Date())
        testCycleDTO2.setObjectVersionNumber(1L)

        insertFolderRel.setProjectId(projectId)
        insertFolderRel.setVersionId(versionId)
        insertFolderRel.setFolderId(resFolderDO.getFolderId())
        insertFolderRel.setIssueId(44444444L)
        testIssueFolderRelMapper.insert(insertFolderRel)

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle', testCycleDTOS.get(0), TestCycleVO, projectId)
        then:
        entity.statusCode.is2xxSuccessful()
        and:
        entity.body != null
        entity.body.folderId == null
        entity.body.type == TestCycleType.CYCLE
        and:
        testCycleDTOS.get(0).setCycleId(entity.getBody().getCycleId())
        and:
        testCycleDTO2.setParentCycleId(testCycleDTOS.get(0).getCycleId())
        testCycleDTOS.add(testCycleDTO2)

        when:
        entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle', testCycleDTOS.get(1), TestCycleVO, projectId)
        then:
        entity.statusCode.is2xxSuccessful()
        and:
        entity.body != null
        entity.body.folderId == resFolderDO.getFolderId()
        entity.body.type == TestCycleType.FOLDER
        and:
        testCycleDTOS.get(1).setCycleId(entity.getBody().getCycleId())

    }

    def "Update"() {
        given: '修改测试循环'
        testCycleDTOS.get(0).setCycleName("testCycleUpdate")
        testCycleDTOS.get(1).setCycleName("testFolderUpdate")
        testCycleDTOS.get(1).setParentCycleId(testCycleDTOS.get(0).getCycleId())

        TestCycleDTO cycleDO = new TestCycleDTO()
        cycleDO.setCycleName("testTemp")
        cycleDO.setType("temp")
        cycleDO.setVersionId(versionId)
        testCycleMapper.insert(cycleDO)
        TestCycleDTO resCycleDO = testCycleMapper.selectOne(cycleDO)

        when:
        HttpEntity<TestCycleVO> requestEntity = new HttpEntity<TestCycleVO>(testCycleDTOS.get(0), null)
        def entity = restTemplate.exchange('/v1/projects/{project_id}/cycle',
                HttpMethod.PUT, requestEntity, TestCycleVO, projectId)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.getCycleName() == "testCycleUpdate"
        entity.body.type == TestCycleType.CYCLE

        when:
        requestEntity = new HttpEntity<TestCycleVO>(testCycleDTOS.get(1), null)
        entity = restTemplate.exchange('/v1/projects/{project_id}/cycle',
                HttpMethod.PUT, requestEntity, TestCycleVO, projectId)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.getCycleName() == "testFolderUpdate"
        entity.body.type == TestCycleType.FOLDER

        when: '覆盖temp1.getType().equals(TestCycleE.TEMP)的情况'
        requestEntity = new HttpEntity<TestCycleVO>(resCycleDO, null)
        entity = restTemplate.exchange('/v1/projects/{project_id}/cycle',
                HttpMethod.PUT, requestEntity, TestCycleVO, projectId)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.getCycleName() == "testTemp"
        entity.body.type == "temp"
//        and:'清理值'
//        testCycleMapper.delete(cycleDO)
    }

//    def "testUpdateDate"() {
//        given:
//        TestCycleServiceImpl service = new TestCycleServiceImpl();
//        Date from = new Date(2018, 12, 18)
//        Date to = new Date(2018, 12, 28)
//
//        expect:
//        result == service.ifSyncNeed(param1, from, to)
//        where:
//        param1              | result
//        TestCycleType.CYCLE | true
////        new  TestCycleType(type: "folder")             |               true
////        new  TestCycleType(type: "cycle",fromDate: new Date(2018,12,20),toDate: new Date(2018,12,20))  |true
////        new  TestCycleType(type: "folder",fromDate: new Date(2018,12,20),toDate: new Date(2018,12,20))  |false
////        new  TestCycleType(type: "cycle",fromDate: new Date(2018,12,10),toDate: new Date(2018,12,30))  |false
////        new  TestCycleType(type: "folder",fromDate: new Date(2018,12,10),toDate: new Date(2018,12,30))  |true
////        new  TestCycleType(type: "folder",fromDate: new Date(2018,12,10),toDate: new Date(2018,12,13))  |true
////        new  TestCycleType(type: "folder",fromDate: new Date(2018,12,29),toDate: new Date(2018,12,30))  |true
////        new  TestCycleType(type: "folder",fromDate: new Date(2018,10,10),toDate: new Date(2018,12,15))  |true
//    }

    def "QueryOne"() {
        given: '查询测试循环'
        when:
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/cycle/query/one/{cycleId}', TestCycleVO, projectId, testCycleDTOS.get(0).getCycleId())
        then:
        entity.statusCode.is2xxSuccessful()
        entity.body.cycleName == "testCycleUpdate"
    }

    def "GetTestCycle"() {
        given: '查询project下的测试循环'
        ProductVersionDTO productVersionDTO = new ProductVersionDTO()
        productVersionDTO.setVersionId(versionId)
        productVersionDTO.setStatusName("testCycle")
        productVersionDTO.setStatusCode("a")
        productVersionDTO.setSequence(1)
        productVersionDTO.setName("testCycle")

        ProductVersionDTO productVersionDTO2 = new ProductVersionDTO()
        productVersionDTO2.setVersionId(22222222L)
        productVersionDTO2.setName("testCycle2")
        productVersionDTO2.setStatusCode("a")
        productVersionDTO2.setSequence(2)
        productVersionDTO2.setStatusName("testCycle2")

        Map map = new HashMap()
        map.put(1L, productVersionDTO)
        map.put(2L, productVersionDTO2)

        Map userMap = new HashMap()
        userMap.put(20645L, new UserDO())
        userMap.put(20645L, new UserDO())

        JSONObject result = new JSONObject()
        result.put("versions", new ArrayList<>())

//        when:
//        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/query", JSONObject.class, projectId, 20645L)
//        then:
//        1 * testCaseService.getVersionInfo(_) >> map
//        then:
//        entity.statusCode.is2xxSuccessful()
//        JSONObject jsonObject = entity.body
//
//        expect:
//        !jsonObject.isEmpty()

        when:
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/query", JSONObject.class, projectId, 20645L)
        then:
        1 * testCaseService.getVersionInfo(_) >> new HashMap<>()
        then:
        entity.statusCode.is2xxSuccessful()
        JSONObject jsonObject2 = entity.body

        expect:
        jsonObject2 == result
    }

    def "GetTestCycleCaseCountInVersion()"() {
        given: '查询project下的测试循环'
        when:
        def entity = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/count/color/in/version/{versionId}", JSONArray.class, projectId, versionId)
        then:
        entity.statusCode.is2xxSuccessful()

    }

    def "GetTestCycleVersion"() {
        given: '查询项目下的计划'
        Map<String, Object> searchParamMap = new HashMap<>()
        searchParamMap.put("cycleName", "发布11")

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/query/version', searchParamMap, PageInfo.class, 12L)
        then: '返回值'
        1 * testCaseService.getTestCycleVersionInfo(_, _) >> new ResponseEntity<PageInfo<ProductVersionPageDTO>>(HttpStatus.OK)
    }

    def "CloneCycle"() {
        given: '克隆循环'
        TestCycleVO testCycleDTO = new TestCycleVO()
        testCycleDTO.setVersionId(99L)
        testCycleDTO.setCycleName("cloneCycleTest")

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/clone/folder/{cycleId}', testCycleDTO, TestCycleVO, testCycleDTOS.get(0).getCycleId(), projectId)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.versionId == 99L
        entity.body.cycleName == "cloneCycleTest"

        and: '设置值'
        testCycleDTOS.add(entity.body)
    }

    def "CloneFolder"() {
        given: '克隆文件夹'
        TestCycleVO testCycleDTO = new TestCycleVO()
        testCycleDTO.setCycleName("cloneCycleFolderTest")

        when:
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/clone/folder/{cycleId}', testCycleDTO, TestCycleVO, testCycleDTOS.get(1).getCycleId(), projectId)
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.cycleName == "cloneCycleFolderTest"

        and: '设置值'
        testCycleDTOS.add(entity.body)
    }

    def "GetFolderByCycleId"() {
        given: '通过cycleId获取目录下所有的文件夹'
        TestCycleVO testCycleDTO = new TestCycleVO()
        testCycleDTO.setCycleName("cloneCycleFolderTest")

        when:
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/cycle/query/folder/cycleId/{cycleId}', List, projectId, testCycleDTOS.get(0).getCycleId())
        then: '返回值'
        entity.statusCode.is2xxSuccessful()
        entity.body.size() == 1
        List<TestCycleVO> list = entity.body
        list.get(0).cycleName == "testFolderUpdate"
    }

    def "SynchroFolder"() {
        given: '同步文件夹'
        testIssueFolderRelDO.setIssueId(888L)
        testIssueFolderRelDO.setProjectId(1L)
        testIssueFolderRelDO.setVersionId(1L)
        testIssueFolderRelDO.setFolderId(testCycleDTOS.get(1).getFolderId())

        def id = testCycleDTOS.get(1).getFolderId()

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
        TestCycleCaseDTO resCycleCase = testCycleCaseMapper.selectOne(testCycleCaseDO)

        testCaseStepDO1.setIssueId(889L)
        testCaseStepDO1.setRank("0|c04564:")
        testCaseStepMapper.insert(testCaseStepDO1)
        testCaseStepDO2.setIssueId(888L)
        testCaseStepDO2.setRank("0|c04564:")
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

    def "SynchroFolderInCycle"() {
        given: '同步cycleId下文件夹'
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
        given: '同步versionId下文件夹'
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


    def "GetCyclesInVersion"() {
        given: '查询version下所有cycle'
        when:
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/cycle/get/cycles/all/in/version/{versionId}', List, projectId, versionId)
        then:
        entity.statusCode.is2xxSuccessful()
    }


    def "BatchChangeAssignedInOneCycle"() {
        given: '批量修改cycle下所有的case的指定人'
        when:
        restTemplate.put("/v1/projects/{project_id}/cycle/batch/change/cycleCase/assignedTo/{userId}/in/cycle/{cycleId}", null, projectId, 20645L, cycleIds.get(1))

        then:
        noExceptionThrown()
        List<TestCycleCaseDTO> caseDOS = testCycleCaseMapper.select(new TestCycleCaseDTO(cycleId: cycleIds.get(1)))
        for (TestCycleCaseDTO caseDO : caseDOS) {
            caseDO.getAssignedTo() == 20645L
            caseDO.setAssignedTo(0L)
            testCycleCaseMapper.updateByPrimaryKey(caseDO)
        }


        when:
        restTemplate.put("/v1/projects/{project_id}/cycle/batch/change/cycleCase/assignedTo/{userId}/in/cycle/{cycleId}", null, projectId, 20645L, cycleIds.get(0))

        then:
        noExceptionThrown()
        List<TestCycleCaseDTO> caseDOS2 = testCycleCaseMapper.select(new TestCycleCaseDTO(cycleId: cycleIds.get(1)))
        for (TestCycleCaseDTO caseDO : caseDOS2) {
            caseDO.getAssignedTo() == 20645L
        }
    }

    def "GetTestCycleInVersionForBatchClone"() {
        given: '查询版本下的测试循环，批量克隆用'
        when:
//        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/cycle/clone/folder/{cycleId}',
//                testCycleDTO, TestCycleVO, testCycleDTOS.get(0).getCycleId(), projectId)
        def entity = restTemplate.exchange("/v1/projects/{project_id}/cycle/batch/clone/query/{versionId}",
                HttpMethod.GET,
                null,
                JSONObject.class,
                projectId,
                versionId
        )

        then:
        entity.statusCode.is2xxSuccessful()
    }

    def "BatchCloneCycles"() {
        given: '批量克隆循环及选定的文件夹'
        List<BatchCloneCycleVO> batchCloneCycleVOList = new ArrayList<BatchCloneCycleVO>()
        BatchCloneCycleVO batchCloneCycleVO = new BatchCloneCycleVO()
        batchCloneCycleVO.setCycleId(1L)
        Long[] foldersIs = [1L, 2L]
        batchCloneCycleVO.setFolderIds()
        batchCloneCycleVO.setFolderIds(foldersIs)
        batchCloneCycleVOList.add(batchCloneCycleVO)

        HttpEntity<List<BatchCloneCycleVO>> httpEntity = new HttpEntity<List<BatchCloneCycleVO>>(batchCloneCycleVOList, null)

        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/cycle/batch/clone/{versionId}",
                HttpMethod.POST,
                httpEntity,
                ResponseEntity.class,
                projectId,
                versionId
        )
        then:
        2 * notifyService.postWebSocket(_, _, _)
        entity.statusCode.is2xxSuccessful()
    }

    def "QueryLatestLoadHistory"() {
        given: '查询最近一次批量克隆记录'
        when:
        def entity = restTemplate.exchange("/v1/projects/{project_id}/cycle/batch/clone/latest",
                HttpMethod.GET,
                null,
                TestFileLoadHistoryVO.class,
                projectId,
                versionId
        )
        then:
        entity.statusCode.is2xxSuccessful()

    }

    def "Delete"() {
        given: '删除测试循环'
        testCycleCaseMapper.delete(testCycleCaseDO)
        testIssueFolderRelMapper.delete(testIssueFolderRelDO)
        testIssueFolderRelMapper.delete(testIssueFolderRelDO2)
        testIssueFolderRelMapper.delete(insertFolderRel)
        testCaseStepMapper.delete(testCaseStepDO1)
        testCaseStepMapper.delete(testCaseStepDO2)
        testIssueFolderMapper.delete(resFolderDO)

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