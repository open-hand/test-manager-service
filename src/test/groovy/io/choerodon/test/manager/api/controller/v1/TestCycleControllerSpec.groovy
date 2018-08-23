package io.choerodon.test.manager.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.api.dto.ProductVersionDTO
import io.choerodon.agile.api.dto.ProductVersionPageDTO
import io.choerodon.agile.api.dto.UserDO
import io.choerodon.core.convertor.ConvertHelper
import io.choerodon.core.domain.Page
import io.choerodon.core.exception.CommonException
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO
import io.choerodon.test.manager.api.dto.TestCycleDTO
import io.choerodon.test.manager.app.service.TestCycleCaseService
import io.choerodon.test.manager.app.service.TestCycleService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE
import io.choerodon.test.manager.domain.test.manager.factory.TestStatusEFactory
import io.choerodon.test.manager.infra.feign.ProductionVersionClient
import org.assertj.core.util.Lists
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.apache.commons.lang.StringUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.util.AopTestUtils
import spock.lang.Specification

import java.lang.reflect.Field

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class TestCycleControllerSpec extends Specification {
    @Autowired
    TestCycleService testCycleService;

    @Autowired
    TestCycleCaseService cycleCaseService

    def "Insert"() {
        when:
        TestCycleDTO cycle = new TestCycleDTO()
        cycle.setCycleName("发布")
        cycle.setVersionId(226L)
        TestCycleDTO cycle1 = testCycleService.insert(cycle);
        then:
        cycle1 != null
        StringUtils.equals(cycle1.getCycleName(),"发布")
        when:
        testCycleService.insert(cycle);
        then:
        thrown(CommonException)
    }

    def "Delete"() {
        given:
        TestCycleDTO cycle = new TestCycleDTO()
        cycle.setCycleName("发布1")
        cycle.setVersionId(226L)
        cycle.setType("cycle")
        TestCycleDTO cycle1 = testCycleService.insert(cycle);
        TestCycleDTO cycle2 = new TestCycleDTO(cycleId: cycle1.getCycleId());
        expect:
        testCycleService.delete(cycle2, 11L)
    }

    def "Update"() {
        given:
        TestCycleDTO cycle = new TestCycleDTO()
        cycle.setCycleName("发布2")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleE.CYCLE)
        TestCycleDTO cycle1 = testCycleService.insert(cycle);

        when:
        cycle1.setCycleName("纠正")
        cycle1.setVersionId(167L)
        cycle1.setObjectVersionNumber(1l)
        TestCycleDTO cycle3 = testCycleService.update(cycle1);
        then:
        cycle3 != null
        StringUtils.equals(cycle3.getCycleName(),"纠正")
    }

    def "QueryOne"() {
        given:
        TestCycleDTO cycle = new TestCycleDTO()
        cycle.setCycleName("发布3")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleE.CYCLE)
        TestCycleDTO cycle1 = testCycleService.insert(cycle)

        when:
        TestCycleDTO cycle2 = testCycleService.getOneCycle(cycle1.getCycleId())

        then:
        cycle2 != null
        StringUtils.equals(cycle2.getCycleName(), "发布3")
    }


    def "GetTestCycle"() {
        given:
        TestCycleDTO cycle = new TestCycleDTO()
        cycle.setCycleName("发布4")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleE.CYCLE)
        cycle.setCreatedBy(4L)
        TestCycleDTO cycle1 = testCycleService.insert(cycle)
        TestStatusE status=TestStatusEFactory.create()
        status.setProjectId(99L)
        status.setStatusName("color1")
        status.setStatusColor("red")
        status.setStatusType(TestStatusE.STATUS_TYPE_CASE)
        status=status.addSelf()

        ProductionVersionClient client = Mock(ProductionVersionClient)
        UserService client1 = Mock(UserService)
        TestCycleService service = AopTestUtils.getTargetObject(testCycleService);
        Field field = service.getClass().getDeclaredFields()[1]
        field.setAccessible(true)
        field.set(service, client)
        Field field1 = service.getClass().getDeclaredFields()[3]
        field1.setAccessible(true)
        field1.set(service, client1)
        UserDO userDO = new UserDO(id: 4L, loginName: "loginName", realName: "realName1")
        Map<Long, UserDO> users = new HashMap()
        users.put(4L, userDO)

        TestCycleCaseDTO case1 = new TestCycleCaseDTO(cycleId: cycle1.getCycleId(), issueId: 1L, versionId: 226l, executionStatus: status.getStatusId(), assignedTo: 4L)
        TestCycleCaseE caseE=ConvertHelper.convert(case1, TestCycleCaseE.class);
        caseE.addSelf()

        ProductVersionDTO v1 = new ProductVersionDTO(versionId: 226l)
        when:
        JSONObject jsob = service.getTestCycle(226l)
        then:
        1 * client.listByProjectId(_) >> new ResponseEntity<Page<ProductVersionDTO>>(Lists.newArrayList(v1), HttpStatus.OK)
        1 * client1.query(_) >> users
        jsob != null
        when:
        service.getTestCycle(226l)
        then:
        1 * client.listByProjectId(_) >> new ResponseEntity<Page<ProductVersionDTO>>(Lists.newArrayList(), HttpStatus.OK)
        0 * client1.query(_)
    }


    def "GetTestCycleVersion"() {
        given:

        Map<String, Object> searchParamMap = new HashMap<>()
        searchParamMap.put("cycleName", "发布11")
        ProductionVersionClient client = Mock(ProductionVersionClient)
        TestCycleService service = AopTestUtils.getTargetObject(testCycleService);
        Field field = service.getClass().getDeclaredFields()[1]
        field.setAccessible(true)
        field.set(service, client)

        when:
        service.getTestCycleVersion(12L, searchParamMap)
        then:
        1 * client.listByOptions(_, _) >> new ResponseEntity<Page<ProductVersionPageDTO>>(new ArrayList<>(), HttpStatus.OK)
    }

    def "CloneCycle"() {
        given:
        TestCycleDTO cycle = new TestCycleDTO()
        cycle.setCycleName("发布6")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleE.CYCLE)
        TestCycleDTO cycle1 = testCycleService.insert(cycle)

        TestCycleDTO folder = new TestCycleDTO()
        folder.setCycleName("发布1")
        folder.setVersionId(226L)
        folder.setType(TestCycleE.FOLDER)
        folder.setParentCycleId(cycle1.getCycleId())
        TestCycleDTO folder1 = testCycleService.insert(folder)


        when:
        TestCycleDTO cycle2 = testCycleService.cloneCycle(cycle1.getCycleId(),2l,"newCycle",1l)

        then:
        cycle2.getCycleId() != null
        cycle2.getCycleId() != cycle.getCycleId()
    }

    def "CloneFolder"() {
        given:
        TestCycleDTO folder = new TestCycleDTO()
        folder.setCycleName("发布7")
        folder.setVersionId(228L)
        folder.setType(TestCycleE.FOLDER)
        folder.setParentCycleId(222L)
        testCycleService.insert(folder)

        TestCycleDTO cycle = new TestCycleDTO()
        cycle.setCycleName("newFolder")

        when:
        TestCycleDTO folder2 = testCycleService.cloneFolder(folder.getCycleId(),cycle,2l)

        then:
        folder2.getCycleId() != null
        folder2.getCycleId() != folder.getCycleId()
    }

    def "GetCyclesByVersionId"() {
        given:
        TestCycleDTO cycle = new TestCycleDTO()
        cycle.setCycleName("发布8")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleE.CYCLE)
        TestCycleDTO cycle1 = testCycleService.insert(cycle);

        when:
        List<TestCycleDTO> cycles = testCycleService.getCyclesByVersionId(226l)
        then:
        cycles !=null
        cycles.size() != 0
    }

    def "GetFolderByCycleId"() {
        given:
        TestCycleDTO cycle = new TestCycleDTO()
        cycle.setCycleName("发布9")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleE.CYCLE)
        TestCycleDTO cycle1 = testCycleService.insert(cycle);

        when:
        List<TestCycleDTO> cycles = testCycleService.getFolderByCycleId(1l)
        then:
        cycles != null
    }
}
