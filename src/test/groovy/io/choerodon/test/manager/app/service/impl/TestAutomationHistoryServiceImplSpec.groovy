package io.choerodon.test.manager.app.service.impl

import io.choerodon.devops.api.vo.AppServiceVersionRespVO
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.DevopsService
import io.choerodon.test.manager.app.service.TestAutomationHistoryService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.infra.dto.TestAppInstanceDTO
import io.choerodon.test.manager.infra.dto.TestAutomationHistoryDTO
import io.choerodon.test.manager.infra.mapper.TestAutomationHistoryMapper
import io.choerodon.test.manager.infra.mapper.TestCycleMapper
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
class TestAutomationHistoryServiceImplSpec extends Specification {

    @Autowired
    TestAutomationHistoryService testAutomationHistoryService

    @Autowired
    UserService userService

    @Autowired
    DevopsService devopsService

    @Autowired
    TestCycleMapper testCycleMapper

    @Autowired
    TestAutomationHistoryMapper testAutomationHistoryMapper

    @Autowired
    ModelMapper modelMapper

    @Shared
    Long projectId = 1L

    def "QueryWithInstance"() {
        given:

        List<Long> versionId = new ArrayList<Long>()
        versionId.add(1L)

        Map map = new HashMap()
        PageRequest pageRequest = new PageRequest(1, 10)
        TestAutomationHistoryDTO testAutomationHistoryDTO = new TestAutomationHistoryDTO()
        TestAppInstanceDTO testAppInstanceDTO = new TestAppInstanceDTO()
        testAppInstanceDTO.setAppId(1L)
        testAppInstanceDTO.setAppVersionId(1L)
        testAutomationHistoryDTO.setTestAppInstanceDTO(testAppInstanceDTO)


        Map<Long, AppServiceVersionRespVO> respVOHashMap = new HashMap<Long, AppServiceVersionRespVO>()
        AppServiceVersionRespVO appServiceVersionRespVO = new AppServiceVersionRespVO()
        appServiceVersionRespVO.setVersion("VERSION")
        respVOHashMap.put(1L, new AppServiceVersionRespVO())

        when:
        testAutomationHistoryService.queryWithInstance(map, pageRequest, projectId)

        then:
        1 * devopsService.getAppversion(_, _) >> respVOHashMap
        0 * devopsService.getAppVersionId(_, _, _) >> versionId
        1 * userService.populateTestAutomationHistory(_)
    }

    def "QueryFrameworkByResultId"() {
        given:
        Long resultId = 1L
        TestAutomationHistoryDTO testAutomationHistoryDTO = new TestAutomationHistoryDTO()
        testAutomationHistoryDTO.setResultId(resultId)
        testAutomationHistoryDTO.setProjectId(projectId)
        testAutomationHistoryDTO.setFramework("Framework")
        testAutomationHistoryMapper.insert(testAutomationHistoryDTO)

        when:
        String string = testAutomationHistoryService.queryFrameworkByResultId(projectId, resultId)

        then:
        string == "Framework"
        noExceptionThrown()

    }

    def "ShutdownInstance"() {
        given:
        Long instanceId = 1L
        Long status = 1L

        when:
        testAutomationHistoryService.shutdownInstance(instanceId, status)
        then:
        noExceptionThrown()
    }
}
