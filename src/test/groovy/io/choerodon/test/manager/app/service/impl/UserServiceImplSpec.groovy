//package io.choerodon.test.manager.app.service.impl
//
//import com.github.pagehelper.PageInfo
//import io.choerodon.test.manager.api.vo.agile.UserDO
//import io.choerodon.test.manager.api.vo.agile.UserDTO
//import io.choerodon.test.manager.api.vo.TestAutomationHistoryVO
//import io.choerodon.test.manager.api.vo.TestCycleCaseHistoryVO
//import io.choerodon.test.manager.api.vo.TestCycleCaseVO
//import io.choerodon.test.manager.infra.feign.BaseFeignClient
//import io.choerodon.test.manager.infra.util.LongUtils
//import org.assertj.core.util.Lists
//import org.springframework.data.domain.PageRequest
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import spock.lang.Specification
//
//class UserServiceImplSpec extends Specification {
//    UserServiceImpl userService;
//    BaseFeignClient client;
//
//    def setup() {
//        client = Mock(BaseFeignClient)
//        userService = new UserServiceImpl(baseFeignClient: client)
//    }
//
//    def "Query"() {
//        given:
//        UserDO user = new UserDO(id: 1L)
//        when:
//        userService.query([1, 2, 3] as Long[])
//        then:
//        1 * client.listUsersByIds(_, false) >> new ResponseEntity<>(Lists.newArrayList(user), HttpStatus.OK)
//        when:
//        userService.query([] as Long[])
//        then:
//        0 * client.listUsersByIds(_, false)
//    }
//
//    def "List"() {
//        given:
//        PageRequest pr = new PageRequest(1, 1)
//        UserDTO user = new UserDTO(id: 1L)
//
//        PageInfo<UserDTO> page1 = new PageInfo<>(Lists.newArrayList(user))
//
//        when:
//        userService.list(pr, 1l, "参数", 2l)
//        then:
//        1 * client.list(_, _, _) >> new ResponseEntity<>(page1, HttpStatus.OK)
//    }
//
//    def "populateUsersInHistory"() {
//        TestCycleCaseHistoryVO dto = new TestCycleCaseHistoryVO(lastUpdatedBy: 111L);
//        TestCycleCaseHistoryVO dto1 = new TestCycleCaseHistoryVO(lastUpdatedBy: 0L);
//        List<TestCycleCaseHistoryVO> dtos = Lists.newArrayList(dto)
//        dtos.add(dto1)
//        when:
//        userService.populateUsersInHistory(dtos)
//        then:
//        1 * client.listUsersByIds(_, false) >> new ResponseEntity(Lists.newArrayList(new UserDO(id: 111L)), HttpStatus.OK)
//        when:
//        userService.populateUsersInHistory(new ArrayList<TestCycleCaseHistoryVO>())
//        then:
//        0 * client.listUsersByIds(_, false)
//        when:
//        userService.populateUsersInHistory(Lists.newArrayList(new TestCycleCaseHistoryVO(lastUpdatedBy: 111L)))
//        then:
//        1 * client.listUsersByIds(_, false) >> new ResponseEntity(Lists.newArrayList(new UserDO(id: 111L)), HttpStatus.OK)
//    }
//
//    def "populateTestCycleCaseDTO"() {
//        TestCycleCaseVO dto = new TestCycleCaseVO(lastUpdatedBy: 111L, assignedTo: 0L);
//        when:
//        userService.populateTestCycleCaseDTO(dto)
//        then:
//        1 * client.listUsersByIds(_, false) >> new ResponseEntity(Lists.newArrayList(new UserDO(id: 111L)), HttpStatus.OK)
//        when:
//        userService.populateTestCycleCaseDTO(new TestCycleCaseVO(lastUpdatedBy: 0L))
//        then:
//        0 * client.listUsersByIds(_, false)
//    }
//
//    def "populateTestAutomationHistory"() {
//        given:
//        PageInfo page = new PageInfo(Lists.newArrayList(new TestAutomationHistoryVO(createdBy: 11L)))
//        when:
//        userService.populateTestAutomationHistory(page);
//        then:
//        1 * client.listUsersByIds(_, false) >> new ResponseEntity(Lists.newArrayList(new UserDO(id: 11)), HttpStatus.OK)
//        when:
//        page.setList([])
//        userService.populateTestAutomationHistory(page);
//        then:
//        0 * client.listUsersByIds(_, false)
//
//
//    }
//
//    def "isUserId"() {
//        expect:
//        LongUtils.isUserId(param1) == result
//        where:
//        param1 | result
//        0L     | false
//        1L     | true
//        null   | false
//    }
//}
