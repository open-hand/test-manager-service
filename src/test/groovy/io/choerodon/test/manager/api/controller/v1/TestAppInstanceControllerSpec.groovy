package io.choerodon.test.manager.api.controller.v1

import io.choerodon.asgard.api.dto.QuartzTask
import io.choerodon.asgard.api.dto.ScheduleMethodDTO
import io.choerodon.asgard.api.dto.ScheduleTaskDTO
import io.choerodon.devops.api.dto.ApplicationRepDTO
import io.choerodon.devops.api.dto.ApplicationVersionRepDTO
import io.choerodon.devops.api.dto.ReplaceResult
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.ApplicationDeployDTO
import io.choerodon.test.manager.app.service.ScheduleService
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.api.dto.TestAppInstanceDTO
import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationHistoryE
import io.choerodon.test.manager.domain.test.manager.entity.TestEnvCommand
import io.choerodon.test.manager.infra.mapper.TestAppInstanceMapper
import io.choerodon.test.manager.infra.mapper.TestAutomationHistoryMapper
import io.choerodon.test.manager.infra.mapper.TestEnvCommandMapper
import io.choerodon.test.manager.infra.mapper.TestEnvCommandValueMapper
import org.assertj.core.util.Lists
import org.assertj.core.util.Maps
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by zongw.lee@gmail.com on 27/11/2018.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestAppInstanceControllerSpec extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    TestCaseService testCaseService

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    TestEnvCommandValueMapper envCommandValueMapper
    @Autowired
    TestEnvCommandMapper envCommandMapper
    @Autowired
    TestAutomationHistoryMapper historyMapper
    @Autowired
    TestAppInstanceMapper instanceMapper

    @Shared
    List<Long> valueIds = new ArrayList()
    @Shared
    List<Long> instanceIds = new ArrayList<>()
    @Shared
    String values = "# Default values for test-manager-service.\n" +
            "\n" +
            "image:\n" +
            "  repository: registry.choerodon.com.cn/choerodon/example-front\n" +
            "  pullPolicy: Always\n" +
            "  \n" +
            "framework: moche";
    @Shared
    String changedValues = "# Default values for api-gateway.\n" +
            "\n" +
            "image:\n" +
            "  repository: registry.choerodon.com.cn/choerodon/example-front\n" +
            "  pullPolicy: Always\n" +
            "  \n" +
            "framework: moche2";

    def "Deploy"() {
        given:
        ApplicationDeployDTO deployDTO = new ApplicationDeployDTO(appId: 1L, appVersionId: 1L,
                environmentId: 1L, projectVersionId: 1L, code: "0.1.0-自动化测试部署测试", values: values)
        ApplicationDeployDTO deployDTO2 = new ApplicationDeployDTO(appId: 2L, appVersionId: 2L,
                environmentId: 2L, projectVersionId: 2L, code: "0.1.0-自动化测试部署测试2", values: values)

        when:
        def res = restTemplate.postForEntity("/v1/projects/{project_id}/app_instances",
                deployDTO, TestAppInstanceDTO, 144L)
        then:
        1 * testCaseService.previewValues(_, _, _) >> new ReplaceResult(yaml: values, deltaYaml: "")
        TestEnvCommand insertCommand = envCommandMapper.selectOne(new TestEnvCommand(instanceId: res.getBody().getId()))
        TestAutomationHistoryE historyE = historyMapper.selectOne(new TestAutomationHistoryE(projectId: 144L, framework: "moche",
                instanceId: res.getBody().getId(), testStatus: TestAutomationHistoryE.Status.NONEXECUTION))
        and:
        insertCommand.getCommandType().equals("create")
        insertCommand.valueId == null
        res.getBody().appId == 1L
        res.getBody().appVersionId == 1L
        historyE != null
        instanceIds.add(res.getBody().getId())
        deployDTO.setHistoryId(historyE.getId())

        when:
        res = restTemplate.postForEntity("/v1/projects/{project_id}/app_instances",
                deployDTO, TestAppInstanceDTO, 144L)
        then:
        1 * testCaseService.getVersionValue(_, _) >> values
        TestEnvCommand insertCommand2 = envCommandMapper.selectOne(new TestEnvCommand(instanceId: res.getBody().getId()))
        and:
        insertCommand2.commandType.equals("restart")
        insertCommand2.valueId == null
        instanceIds.add(res.getBody().getId())


        when:
        res = restTemplate.postForEntity("/v1/projects/{project_id}/app_instances",
                deployDTO2, TestAppInstanceDTO, 144L)
        then:
        1 * testCaseService.previewValues(_, _, _) >> new ReplaceResult(yaml: changedValues, deltaYaml: changedValues)
        TestEnvCommand insertCommand3 = envCommandMapper.selectOne(new TestEnvCommand(instanceId: res.getBody().getId()))
        TestAutomationHistoryE historyE3 = historyMapper.selectOne(new TestAutomationHistoryE(projectId: 144L, framework: "moche2",
                instanceId: res.getBody().getId(), testStatus: TestAutomationHistoryE.Status.NONEXECUTION))

        and:
        insertCommand3.getCommandType().equals("create")
        insertCommand3.valueId != null
        res.getBody().appId == 2L
        res.getBody().appVersionId == 2L
        historyE3 != null

        instanceIds.add(res.getBody().getId())
        deployDTO2.setHistoryId(historyE3.getId())

        when:
        res = restTemplate.postForEntity("/v1/projects/{project_id}/app_instances",
                deployDTO2, TestAppInstanceDTO, 144L)
        then:
        1 * testCaseService.previewValues(_, _, _) >> new ReplaceResult(yaml: changedValues, deltaYaml: changedValues)
        TestEnvCommand insertCommand4 = envCommandMapper.selectOne(new TestEnvCommand(instanceId: res.getBody().getId()))
        and:
        insertCommand4.commandType.equals("restart")
        insertCommand4.valueId != null
        valueIds.add(insertCommand4.getValueId())
        instanceIds.add(res.getBody().getId())
    }

    def "DeployBySchedule"() {
        given:
        List<ScheduleMethodDTO> methodDTOS = Lists.newArrayList(new ScheduleMethodDTO(id: 3L, code: "instance"),
                new ScheduleMethodDTO(id: 1L, code: "test-deploy-instance"), new ScheduleMethodDTO(id: 2L, code: "test"))
        ApplicationDeployDTO deployDTO = new ApplicationDeployDTO(appId: 1L, appVersionId: 1L,
                environmentId: 1L, projectVersionId: 1L, code: "0.1.0-自动化测试部署测试", values: values)
        ScheduleTaskDTO taskDTO = new ScheduleTaskDTO()
        taskDTO.setParams(Maps.newHashMap("deploy", deployDTO))

        when:
        restTemplate.postForEntity("/v1/projects/{project_id}/app_instances/schedule",
                taskDTO, QuartzTask, 144L)

        then:
        1 * scheduleService.getMethodByService(_, _) >> methodDTOS
        1 * testCaseService.queryByAppId(_, _) >> new ApplicationRepDTO(name: "定时任务测试应用")
        1 * testCaseService.getAppversion(_, _) >> Lists.newArrayList(new ApplicationVersionRepDTO(version: "定时任务测试应用版本"))
        1 * scheduleService.create(_, _) >> new QuartzTask()

        and:
        noExceptionThrown()
    }

    def "QueryValues"() {
        when:
        restTemplate.getForEntity("/v1/projects/{project_id}/app_instances/value?appId=1&envId=1&appVersionId=1",
                ReplaceResult, 144L)
        then:
        1 * testCaseService.getVersionValue(_, _) >> values
        noExceptionThrown()

        when: "deployValue不为空"
        restTemplate.getForEntity("/v1/projects/{project_id}/app_instances/value?appId=2&envId=2&appVersionId=2",
                ReplaceResult, 144L)
        then:
        1 * testCaseService.getVersionValue(_, _) >> values
        1 * testCaseService.previewValues(_, _, _) >> new ReplaceResult(yaml: values)
        noExceptionThrown()

        when: "错误yaml格式"
        restTemplate.getForEntity("/v1/projects/{project_id}/app_instances/value?appId=1111&envId=1111&appVersionId=1111",
                ReplaceResult, 144L)
        then:
        1 * testCaseService.getVersionValue(_, _) >> values
        noExceptionThrown()

        and: "清理数据"
        historyMapper.delete(new TestAutomationHistoryE(instanceId: instanceIds.get(0)))
        historyMapper.delete(new TestAutomationHistoryE(instanceId: instanceIds.get(1)))
        historyMapper.delete(new TestAutomationHistoryE(instanceId: instanceIds.get(2)))
        historyMapper.delete(new TestAutomationHistoryE(instanceId: instanceIds.get(3)))
        envCommandMapper.delete(new TestEnvCommand(instanceId: instanceIds.get(0)))
        envCommandMapper.delete(new TestEnvCommand(instanceId: instanceIds.get(1)))
        envCommandMapper.delete(new TestEnvCommand(instanceId: instanceIds.get(2)))
        envCommandMapper.delete(new TestEnvCommand(instanceId: instanceIds.get(3)))
        envCommandValueMapper.deleteByPrimaryKey(valueIds.get(0))
        instanceMapper.deleteByPrimaryKey(instanceIds.get(0))
        instanceMapper.deleteByPrimaryKey(instanceIds.get(1))
        instanceMapper.deleteByPrimaryKey(instanceIds.get(2))
        instanceMapper.deleteByPrimaryKey(instanceIds.get(3))
    }
}
