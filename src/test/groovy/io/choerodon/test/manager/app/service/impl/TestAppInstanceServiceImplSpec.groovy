//package io.choerodon.test.manager.app.service.impl
//
//import com.alibaba.fastjson.JSON
//import io.choerodon.test.manager.api.vo.ApplicationDeployVO
//import io.choerodon.test.manager.app.service.TestAppInstanceService
//import org.assertj.core.util.Maps
//import spock.lang.Shared
//import spock.lang.Specification
//
///**
// * @author zongw.lee@gmail.com
// * @since 2018/12/03
// */
//class TestAppInstanceServiceImplSpec extends Specification {
//
//    @Shared
//    TestAppInstanceService instanceService
//
//
//    def "CreateBySchedule"() {
//        given:
//        instanceService = new TestAppInstanceServiceImpl()
//        ApplicationDeployVO deployDTO = new ApplicationDeployVO()
//        Map<String,Object> map = Maps.newHashMap("deploy", JSON.toJSONString(deployDTO))
//        map.put("projectId", 144)
//        map.put("userId",1)
//
//        when:
//        instanceService.createBySchedule(map)
//
//        then:
//        thrown(IllegalArgumentException)
//    }
//}
