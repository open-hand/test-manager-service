package io.choerodon.test.manager.api.controller.v1

import io.choerodon.core.exception.CommonException
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.TestStatusDTO
import io.choerodon.test.manager.app.service.TestStatusService
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE
import io.choerodon.test.manager.infra.mapper.TestStatusMapper
import org.apache.commons.lang.StringUtils
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
class TestStatusControllerSpec extends Specification {
    @Autowired
    TestStatusService testStatusService

    @Autowired
    TestStatusMapper testStatusMapper

    @Autowired
    TestRestTemplate restTemplate

    @Shared
    List statusId = new ArrayList<>()
    @Shared
    def projectId = 144L

    def "Insert"() {
        given:
        TestStatusDTO status = new TestStatusDTO()
        status.setDescription("突发情况")
        status.setStatusName("突发")
        status.setStatusColor("rgba(0,191,165,21)")
        status.setStatusType(TestStatusE.STATUS_TYPE_CASE)
        status.setProjectId(projectId)
        TestStatusDTO status1 = new TestStatusDTO()
        status1.setStatusId(1l)

        when: '向插入issues的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/status', status, TestStatusDTO)
        then:
        entity.statusCode.is2xxSuccessful()

        and:
        entity.body != null
        StringUtils.equals(entity.getBody().statusName, "突发")

        and: '设置值'
        statusId.add(entity.body.statusId)

        when: '向插入issues的接口发请求'
        restTemplate.postForEntity('/v1/projects/{project_id}/status', status1, TestStatusDTO)
        then:
        thrown(CommonException)
    }

    def "Query"() {
        given: '给定一个查询类型DTO'
        TestStatusDTO status = new TestStatusDTO()
        status.setStatusId(statusId[0])
        status.setProjectId(projectId)
        status.setStatusType(TestStatusE.STATUS_TYPE_CASE)

        when: '向查询issues的接口发请求'
        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/status', status, List)

        then:
        entity.statusCode.is2xxSuccessful()

        and: '设置值'
        List<TestStatusDTO> testStatusDTOS = entity.body

        expect: '设置期望值'
        testStatusDTOS.size() > 0
    }


    def "Update"() {
        given:
        TestStatusDTO statusNew = new TestStatusDTO()
        statusNew.setStatusId(statusId[0])
        statusNew.setStatusType(TestStatusE.STATUS_TYPE_CASE)
        statusNew.setDescription("修改描述")
        statusNew.setStatusName("修改名字")
        statusNew.setStatusColor("rgba(0,191,165,31)")
        statusNew.setObjectVersionNumber(1L)
        TestStatusDTO exceptionStatus = new TestStatusDTO()
        exceptionStatus.setStatusId(999L)
        exceptionStatus.setStatusName("修改名字异常")

        when: '向修改issues的接口发请求'
        restTemplate.put('/v1/projects/{project_id}/status/update', TestStatusDTO, statusNew)

        then: '返回值'
        TestStatusDTO testStatusDTO = testStatusMapper.selectByPrimaryKey(statusId[0])

        expect: '验证更新是否成功'
        testStatusDTO.statusType == TestStatusE.STATUS_TYPE_CASE
        testStatusDTO.description == "修改描述"
        testStatusDTO.statusName == "修改名字"
        testStatusDTO.statusColor == "rgba(0,191,165,31)"
        testStatusDTO.objectVersionNumber == 2L

//        when:
//        TestStatusDTO status2 = testStatusService.update(statusNew)
//        then:
//        StringUtils.equals(status2.getStatusName(), "修改名字")
        when: '向修改issues的接口发请求'
        restTemplate.put('/v1/projects/{project_id}/status/update', TestStatusDTO, exceptionStatus)
        then:
        thrown(CommonException)
    }

    def "Delete"() {
        given:
        TestStatusDTO statusDelete = new TestStatusDTO()
        statusDelete.setStatusType(TestStatusE.STATUS_TYPE_CASE)
        statusDelete.setDescription("修改描述")
        statusDelete.setStatusName("修改名字")
        statusDelete.setStatusColor("rgba(0,191,165,31)")
        statusDelete.setStatusId(statusId[0])
        statusDelete.setProjectId(projectId)

        expect:
        restTemplate.delete('/v1/projects/{project_id}/status/{statusId}', TestStatusDTO, statusDelete)
    }
}
