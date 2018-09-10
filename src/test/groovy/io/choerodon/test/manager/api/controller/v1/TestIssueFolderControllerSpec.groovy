package io.choerodon.test.manager.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.TestIssueFolderService
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class TestIssueFolderControllerSpec extends Specification {
    @Autowired
    TestIssueFolderService testIssueFolderService

    @Autowired
    TestIssueFolderMapper testIssueFolderMapper

    @Autowired
    TestRestTemplate restTemplate

    @Shared
    List issueIdList = new ArrayList()

    @Shared
    def projectId = 144L

    def "Insert"() {
    }

    def "Update"() {
    }

    def "CopyFolder"() {
    }

    def "Query"() {
        when: '向查询issues的接口发请求'
        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issueFolder/query', projectId)

        then: '返回值'
        entity.statusCode.is2xxSuccessful()
    }

    def "Delete"() {
        when: '执行方法'
        restTemplate.delete('/v1/projects/{project_id}/issueFolder/{folderId}', projectId, folderId)

        then: '返回值'
        def result = testIssueFolderMapper.selectByPrimaryKey(issueId as Long)

        expect: '期望值'
        result == null

        where: '判断issue是否删除'
        issueId << issueIdList
    }

    def "MoveFolder"() {
    }
}
