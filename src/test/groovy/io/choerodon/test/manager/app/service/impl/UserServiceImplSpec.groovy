package io.choerodon.test.manager.app.service.impl

import io.choerodon.agile.api.dto.UserDO
import io.choerodon.agile.api.dto.UserDTO
import io.choerodon.core.domain.Page
import io.choerodon.core.domain.PageInfo
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import io.choerodon.test.manager.infra.feign.UserFeignClient
import org.assertj.core.util.Lists
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class UserServiceImplSpec extends Specification {
    UserServiceImpl userService;
    UserFeignClient client;

    def setup(){
        client=Mock(UserFeignClient)
        userService=new UserServiceImpl(userFeignClient:client)
    }

    def "Query"() {
        given:
        UserDO user=new UserDO(id:1L)
        when:
        userService.query([1,2,3] as Long[])
        then:
        1*client.listUsersByIds(_)>>new ResponseEntity<>(Lists.newArrayList(user),HttpStatus.OK)
    }

    def "List"() {
        given:
        PageRequest pr = new PageRequest()
        UserDTO user=new UserDTO(id:1L)
        PageInfo info=new PageInfo(1,1)

        Page<UserDTO> page1 = new Page<>(Lists.newArrayList(user),info,1)

        when:
        userService.list(pr,1l,"参数",2l)
        then:
        1*client.list(_,_,_,_,_)>>new ResponseEntity<>(page1,HttpStatus.OK)
    }
}
