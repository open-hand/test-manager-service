package io.choerodon.test.manager.app.service.impl

import io.choerodon.agile.api.dto.UserDO
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO
import io.choerodon.test.manager.app.service.TestCycleCaseHistoryService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE
import org.apache.commons.collections.MapUtils
import org.assertj.core.util.Maps
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.util.AopTestUtils
import spock.lang.Specification
import spock.lang.Stepwise

import java.lang.reflect.Field

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by jialongZuo@hand-china.com on 8/24/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCycleCaseHistoryServiceImplSpec extends Specification {
    @Autowired
    TestCycleCaseHistoryService service

    def "Insert"() {
        given:
        TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO(oldValue: "old", newValue: "new", field: TestCycleCaseHistoryE.FIELD_STATUS, executeId: 1L,lastUpdatedBy: 1L)
        expect:
        service.insert(historyDTO)
    }

    def "Query"() {
        given:
        UserService client = Mock(UserService)
        TestCycleCaseHistoryService service=AopTestUtils.getTargetObject(service)
        Field userClient = service.getClass().getDeclaredFields()[1]
        userClient.setAccessible(true)
        userClient.set(service, client)
        when:
        def result = service.query(1L, new PageRequest(0,1))
        then:
        1 * client.query(_) >> Maps.newHashMap(1L, new UserDO(id:1L))
        result.size()==1
        when:
        def result1 = service.query(1L, new PageRequest(0,1))
        then:
        1 * client.query(_)>>new HashMap<>()
        result1.get(0).getUser()==null
    }
}
