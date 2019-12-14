package io.choerodon.test.manager.api.controller.v1

import com.github.pagehelper.PageInfo
import io.choerodon.test.manager.api.vo.devops.AppServiceVersionRespVO
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.DevopsService
import io.choerodon.test.manager.app.service.FileService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.infra.dto.TestAppInstanceDTO
import io.choerodon.test.manager.infra.dto.TestAppInstanceLogDTO
import io.choerodon.test.manager.infra.dto.TestAutomationHistoryDTO
import io.choerodon.test.manager.infra.mapper.TestAppInstanceLogMapper
import io.choerodon.test.manager.infra.mapper.TestAppInstanceMapper
import io.choerodon.test.manager.infra.mapper.TestAutomationHistoryMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise


import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 User: wangxiang
 Date: 2019/8/29
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestAutomationHistoryControllerSpec extends Specification {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    FileService fileService

    @Autowired
    DevopsService devopsService

    @Autowired
    TestAutomationHistoryMapper testAutomationHistoryMapper

    @Autowired
    UserService userService

    @Autowired
    TestAppInstanceLogMapper appInstanceLogMapper

    @Autowired
    TestAppInstanceMapper testAppInstanceMapper

    @Shared
    Long projectId = 1L

    def "QueryWithInstance"() {
        given: "QueryWithInstance"
        TestAutomationHistoryDTO historyDTO = new TestAutomationHistoryDTO()
        historyDTO.setProjectId(1L)
        historyDTO.setTestStatus(0L)
        historyDTO.setInstanceId(1L)
        testAutomationHistoryMapper.insert(historyDTO)


        List<Long> versionIdList = new ArrayList<Long>()
        versionIdList.add(1L)
        Map<String, Object> map = new HashMap<>()
        map.put("test_status", 0)
        map.put("filter", "automation-test-att-662-603-1027-skk5h")
        map.put("appId", 1L)

        Map<Long, AppServiceVersionRespVO> reslutMap = new HashMap<>()
        AppServiceVersionRespVO respVO1 = new AppServiceVersionRespVO()
        AppServiceVersionRespVO respVO2 = new AppServiceVersionRespVO()
        respVO1.setId(1L)
        respVO2.setId(2L)
        reslutMap.put(1L, respVO1)
        reslutMap.put(2L, respVO2)

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<Map<String, Object>>(map, null)
        when:
        def res = restTemplate.exchange("/v1/projects/{project_id}/test/automation/queryWithHistroy",
                HttpMethod.POST,
                httpEntity,
                PageInfo.class,
                projectId)
        then:
        1 * devopsService.getAppVersionId(_, _, _) >> versionIdList
        1 * devopsService.getAppversion(_, _) >> reslutMap
        1 * userService.populateTestAutomationHistory(_)

        res.statusCode.is2xxSuccessful()
        res.getBody().list.size() > 0

    }

    def "QueryLog"() {

        given: 'QueryLog'
        TestAppInstanceLogDTO logDTO = new TestAppInstanceLogDTO()
        logDTO.setId(1L)
        logDTO.setLog("{\n" +
                "\"test\":\"log\"\n" +
                "}")
        appInstanceLogMapper.insert(logDTO)

        def all = appInstanceLogMapper.selectAll()

        def logId = 1L
        when:
        //def res = restTemplate.getForEntity("/v1/projects/{project_id}/test/automation/queryLog/{logId}", String, projectId, logId)
        def res = restTemplate.exchange("/v1/projects/{project_id}/test/automation/queryLog/{logId}",
                HttpMethod.GET,
                null,
                String.class,
                projectId,
                logId)
        then:
        res.statusCode.is2xxSuccessful()
        res.getBody() == logDTO.getLog()

    }
}
