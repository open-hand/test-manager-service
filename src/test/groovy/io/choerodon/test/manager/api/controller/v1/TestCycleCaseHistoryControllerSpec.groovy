package io.choerodon.test.manager.api.controller.v1


import com.github.pagehelper.PageInfo
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.TestCycleCaseHistoryService
import io.choerodon.test.manager.app.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by jialongZuo@hand-china.com on 8/24/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCycleCaseHistoryControllerSpec extends Specification {
    @Autowired
    TestCycleCaseHistoryService service

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    UserService userService

    @Shared
    Long project_id = 1L

    def "Query"() {
        given: '查询历史'
        when:
        def result = restTemplate.getForEntity("/v1/projects/{project_id}/cycle/case/history/{cycleCaseId}?page={page}&size={size}",
                PageInfo,
                project_id,
                1, 0, 5)
        then:
        1 * userService.populateUsersInHistory(_)
        result.statusCode.is2xxSuccessful()
        result.getBody().list.size() >= 0
    }
}
