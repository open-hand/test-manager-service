package io.choerodon.test.manager.app.service.impl


import io.choerodon.agile.api.vo.ProductVersionPageDTO
import io.choerodon.agile.api.vo.UserDO
import com.github.pagehelper.PageInfo
import io.choerodon.core.exception.CommonException
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.vo.TestCycleVO
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.TestCycleService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.infra.enums.TestCycleType
import io.choerodon.test.manager.infra.feign.ProductionVersionClient
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.util.AopTestUtils
import spock.lang.Specification
import spock.lang.Stepwise

import java.lang.reflect.Field

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by jialongZuo@hand-china.com on 8/23/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCycleServiceImplSpec extends Specification {
    @Autowired
    TestCycleService testCycleService

    @Autowired
    UserService userService

    @Autowired
    TestCaseService testCaseService

    def "Insert"() {
        given:
        TestCycleVO cycle = new TestCycleVO()
        cycle.setCycleName("发布")
        cycle.setVersionId(226L)
        cycle.setType("cycle")
        when:
        TestCycleVO cycle1 = testCycleService.insert(11L, cycle);
        then:
        cycle1.getCycleId() != null
        StringUtils.equals(cycle1.getCycleName(), "发布")
        when:
        testCycleService.insert(11L, cycle);
        then:
        thrown(CommonException)
    }

    def "Delete"() {
        given:
        TestCycleVO cycle = new TestCycleVO()
        cycle.setCycleName("发布1")
        cycle.setVersionId(224L)
        cycle.setType("cycle")
        TestCycleVO cycle1 = testCycleService.insert(11L, cycle);
        TestCycleVO cycle2 = new TestCycleVO(cycleId: cycle1.getCycleId());
        expect:
        testCycleService.delete(cycle2, 11L)
    }

    def "Update"() {
        given:
        TestCycleVO cycle = new TestCycleVO()
        cycle.setCycleName("发布2")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleType.CYCLE)
        TestCycleVO cycle1 = testCycleService.insert(11L, cycle)

        when:
        cycle1.setCycleName("纠正")
        cycle1.setVersionId(167L)
        cycle1.setObjectVersionNumber(1l)
        cycle1.setRank(null)
        TestCycleVO cycle3 = testCycleService.update(11L, cycle1)
        then:
        cycle3 != null
        StringUtils.equals(cycle3.getCycleName(), "纠正")
    }

    def "QueryOne"() {
        given:
        TestCycleVO cycle = new TestCycleVO()
        cycle.setCycleName("发布3")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleType.CYCLE)
        TestCycleVO cycle1 = testCycleService.insert(11L, cycle)

        when:
        TestCycleVO cycle2 = testCycleService.getOneCycle(cycle1.getCycleId())

        then:
        cycle2 != null
        StringUtils.equals(cycle2.getCycleName(), "发布3")
    }

//    def "GetTestCycle"() {
//        given:
//        TestCycleVO cycle = new TestCycleVO()
//        cycle.setCycleName("发布4")
//        cycle.setVersionId(226L)
//        cycle.setType(TestCycleE.CYCLE)
//        cycle.setCreatedBy(4L)
//        TestCycleVO cycle1 = testCycleService.insert(cycle)
//        TestStatusE status = TestStatusEFactory.create()
//        status.setProjectId(99L)
//        status.setStatusName("color1")
//        status.setStatusColor("red")
//        status.setStatusType(TestStatusType.STATUS_TYPE_CASE)
//        status = status.addSelf()
//
//        ProductionVersionClient client = Mock(ProductionVersionClient)
//        UserService client1 = Mock(UserService)
//        TestCycleService service = AopTestUtils.getTargetObject(testCycleService);
//        Field field = service.getClass().getDeclaredFields()[1]
//        field.setAccessible(true)
//        field.set(service, client)
//        Field field1 = service.getClass().getDeclaredFields()[3]
//        field1.setAccessible(true)
//        field1.set(service, client1)
//        UserDO userDO = new UserDO(id: 4L, loginName: "loginName", realName: "realName1")
//        Map<Long, UserDO> users = new HashMap()
//        users.put(4L, userDO)
//
//        Map map = new HashMap()
//        map.put(1L, new ProductVersionDTO(versionId: 226l))
//
//        TestCycleCaseVO case1 = new TestCycleCaseVO(cycleId: cycle1.getCycleId(), issueId: 1L, versionId: 226l, executionStatus: status.getStatusId(), assignedTo: 4L)
//        TestCycleCaseE caseE = modeMapper.map(case1, TestCycleCaseE.class);
//        caseE.addSelf()
//
//        ProductVersionDTO v1 = new ProductVersionDTO(versionId: 226l)
//        when:
//        JSONObject jsob = service.getTestCycle(226l, null)
//        then:
//        1 * testCaseService.getVersionInfo(_) >> map
//        1 * client1.query(_) >> users
//        jsob != null
//
//        when:
//        service.getTestCycle(226l, null)
//        then:
//        1 * testCaseService.getVersionInfo(_) >> new HashMap<>()
//        0 * client1.query(_)
//    }


    def "GetTestCycleVersion"() {
        given:

        Map<String, Object> searchParamMap = new HashMap<>()
        searchParamMap.put("cycleName", "发布11")
        ProductionVersionClient client = Mock(ProductionVersionClient)
        TestCycleService service = AopTestUtils.getTargetObject(testCycleService)
        Field field = service.getClass().getDeclaredFields()[1]
        field.setAccessible(true)
        //field.set(service, client)

        when:
        service.getTestCycleVersion(12L, searchParamMap)
        then:
        1 * testCaseService.getTestCycleVersionInfo(_, _) >> new ResponseEntity<PageInfo<ProductVersionPageDTO>>(new ArrayList<>(), HttpStatus.OK)
    }

    def "CloneCycle"() {
        given:
        TestCycleVO cycle = new TestCycleVO()
        cycle.setCycleName("发布6")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleType.CYCLE)
        cycle.setFromDate(new Date(1546272000000))
        cycle.setToDate(new Date(1548863999000))
        TestCycleVO cycle1 = testCycleService.insert(1L, cycle)

        TestCycleVO folder = new TestCycleVO()
        folder.setCycleName("发布1")
        folder.setVersionId(226L)
        folder.setType(TestCycleType.FOLDER)
        folder.setParentCycleId(cycle1.getCycleId())
        folder.setFromDate(new Date(1547481600000))
        folder.setToDate(new Date(1547567999000))
        TestCycleVO folder1 = testCycleService.insert(1L, folder)


        when:
        TestCycleVO cycle2 = testCycleService.cloneCycle(cycle1.getCycleId(), 2l, "newCycle", 1l)

        then:
        cycle2.getCycleId() != null
        cycle2.getCycleId() != cycle.getCycleId()
    }

    def "CloneFolder"() {
        given:
        TestCycleVO cycle = new TestCycleVO()
        cycle.setCycleName("发布7")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleType.CYCLE)
        cycle.setFromDate(new Date(1546272000000))
        cycle.setToDate(new Date(1548863999000))
        TestCycleVO cycle1 = testCycleService.insert(2L, cycle)

        TestCycleVO folder = new TestCycleVO()
        folder.setCycleName("发布8")
        folder.setVersionId(228L)
        folder.setType(TestCycleType.FOLDER)
        folder.setFromDate(new Date(1546272000000))
        folder.setToDate(new Date(1548863999000))
        folder.setParentCycleId(cycle1.getCycleId())
        folder = testCycleService.insert(2L, folder)

        TestCycleVO newFolder = new TestCycleVO()
        newFolder.setParentCycleId(cycle1.getCycleId())
        newFolder.setCycleName("newFolder")

        when:
        TestCycleVO folder2 = testCycleService.cloneFolder(folder.getCycleId(), newFolder, 2l)

        then:
        folder2.getCycleId() != null
        folder2.getCycleId() != folder.getCycleId()
    }

    def "GetFolderByCycleId"() {
        given:
        TestCycleVO cycle = new TestCycleVO()
        cycle.setCycleName("发布9")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleType.CYCLE)
        TestCycleVO cycle1 = testCycleService.insert(2L, cycle);

        when:
        List<TestCycleVO> cycles = testCycleService.getFolderByCycleId(1l)
        then:
        cycles != null
    }

    //此条勿删，测试方法
    def "populateUsers"() {
        given:
        Map userMap = new HashMap()
        userMap.put(20645L, new UserDO())
        userMap.put(20645L, new UserDO())

        List<TestCycleVO> dtos = new ArrayList<>()
        TestCycleVO testCycleDTO = new TestCycleVO()
        testCycleDTO.setCreatedBy(20645L)
        TestCycleVO testCycleDTO2 = new TestCycleVO()
        testCycleDTO2.setCreatedBy(0L)
        dtos.add(testCycleDTO)


        UserDO userDO = new UserDO(id: 20645L, loginName: "loginName", realName: "realName")
        Map<Long, UserDO> users = new HashMap()
        users.put(20645L, userDO)

        when:
        testCycleService.populateUsers(dtos)
        then:
        1 * userService.query(_) >> users
        and:
        dtos.add(testCycleDTO2)

        when:
        testCycleService.populateUsers(dtos)
        then:
        1 * userService.query(_) >> users
    }
}
