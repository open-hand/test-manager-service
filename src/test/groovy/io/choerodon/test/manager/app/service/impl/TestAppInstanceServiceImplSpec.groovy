package io.choerodon.test.manager.app.service.impl

import com.alibaba.fastjson.JSON
import io.choerodon.devops.api.dto.ReplaceResult
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.ApplicationDeployDTO
import io.choerodon.test.manager.app.service.TestAppInstanceService
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceE
import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationHistoryE
import io.choerodon.test.manager.domain.test.manager.entity.TestEnvCommand
import io.choerodon.test.manager.infra.mapper.TestAppInstanceMapper
import io.choerodon.test.manager.infra.mapper.TestAutomationHistoryMapper
import io.choerodon.test.manager.infra.mapper.TestEnvCommandMapper
import org.assertj.core.util.Maps
import org.codehaus.groovy.tools.shell.CommandException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author zongw.lee@gmail.com 
 * @since 2018/12/03
 */
class TestAppInstanceServiceImplSpec extends Specification {

    @Shared
    TestAppInstanceService instanceService


    def "CreateBySchedule"() {
        given:
        instanceService = new TestAppInstanceServiceImpl()
        ApplicationDeployDTO deployDTO = new ApplicationDeployDTO()
        Map<String,Object> map = Maps.newHashMap("deploy", JSON.toJSONString(deployDTO))
        map.put("projectId", 144)
        map.put("userId",1)

        when:
        instanceService.createBySchedule(map)

        then:
        thrown(IllegalArgumentException)
    }
}
