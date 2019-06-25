package io.choerodon.test.manager.app.service.impl


import io.choerodon.agile.api.dto.UserDO
import io.choerodon.agile.api.dto.UserDTO
import com.github.pagehelper.PageInfo
import io.choerodon.base.domain.PageRequest
import io.choerodon.test.manager.api.dto.TestAutomationHistoryDTO
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO
import io.choerodon.test.manager.infra.common.utils.LongUtils
import io.choerodon.test.manager.infra.feign.UserFeignClient
import org.assertj.core.util.Lists
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class UserServiceImplSpec extends Specification {
    UserServiceImpl userService;
    UserFeignClient client;

    def setup() {
        client = Mock(UserFeignClient)
        userService = new UserServiceImpl(userFeignClient: client)
    }

    def "Query"() {
        given:
        UserDO user = new UserDO(id: 1L)
        when:
        userService.query([1, 2, 3] as Long[])
        then:
        1 * client.listUsersByIds(_, false) >> new ResponseEntity<>(Lists.newArrayList(user), HttpStatus.OK)
        when:
        userService.query([] as Long[])
        then:
        0 * client.listUsersByIds(_, false)
    }

    def "List"() {
        given:
        PageRequest pr = new PageRequest()
        UserDTO user = new UserDTO(id: 1L)

        PageInfo<UserDTO> page1 = new PageInfo<>(Lists.newArrayList(user))

        when:
        userService.list(pr, 1l, "参数", 2l)
        then:
        1 * client.list(_, _, _, _, _) >> new ResponseEntity<>(page1, HttpStatus.OK)
    }

    def "populateUsersInHistory"() {
        TestCycleCaseHistoryDTO dto = new TestCycleCaseHistoryDTO(lastUpdatedBy: 111L);
        TestCycleCaseHistoryDTO dto1 = new TestCycleCaseHistoryDTO(lastUpdatedBy: 0L);
        List<TestCycleCaseHistoryDTO> dtos = Lists.newArrayList(dto)
        dtos.add(dto1)
        when:
        userService.populateUsersInHistory(dtos)
        then:
        1 * client.listUsersByIds(_, false) >> new ResponseEntity(Lists.newArrayList(new UserDO(id: 111L)), HttpStatus.OK)
        when:
        userService.populateUsersInHistory(new ArrayList<TestCycleCaseHistoryDTO>())
        then:
        0 * client.listUsersByIds(_, false)
        when:
        userService.populateUsersInHistory(Lists.newArrayList(new TestCycleCaseHistoryDTO(lastUpdatedBy: 111L)))
        then:
        1 * client.listUsersByIds(_, false) >> new ResponseEntity(Lists.newArrayList(new UserDO(id: 111L)), HttpStatus.OK)
    }

    def "populateTestCycleCaseDTO"() {
        TestCycleCaseDTO dto = new TestCycleCaseDTO(lastUpdatedBy: 111L, assignedTo: 0L);
        when:
        userService.populateTestCycleCaseDTO(dto)
        then:
        1 * client.listUsersByIds(_, false) >> new ResponseEntity(Lists.newArrayList(new UserDO(id: 111L)), HttpStatus.OK)
        when:
        userService.populateTestCycleCaseDTO(new TestCycleCaseDTO(lastUpdatedBy: 0L))
        then:
        0 * client.listUsersByIds(_, false)
    }

    def "populateTestAutomationHistory"() {
        given:
        PageInfo page = new PageInfo(Lists.newArrayList(new TestAutomationHistoryDTO(createdBy: 11L)))
        when:
        userService.populateTestAutomationHistory(page);
        then:
        1 * client.listUsersByIds(_, false) >> new ResponseEntity(Lists.newArrayList(new UserDO(id: 11)), HttpStatus.OK)
        when:
        page.setList([])
        userService.populateTestAutomationHistory(page);
        then:
        0 * client.listUsersByIds(_, false)


    }

    def "isUserId"() {
        expect:
        LongUtils.isUserId(param1) == result
        where:
        param1 | result
        0L     | false
        1L     | true
        null   | false
    }
}
