package io.choerodon.test.manager.api.controller.v1

import com.alibaba.fastjson.JSONObject
import io.choerodon.agile.api.dto.ProductVersionPageDTO
import io.choerodon.core.convertor.ConvertHelper
import io.choerodon.core.domain.Page
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO
import io.choerodon.test.manager.api.dto.TestCycleDTO
import io.choerodon.test.manager.app.service.TestCycleCaseService
import io.choerodon.test.manager.app.service.TestCycleService
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE
import io.choerodon.test.manager.domain.test.manager.factory.TestStatusEFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.apache.commons.lang.StringUtils
import org.springframework.http.ResponseEntity
import spock.lang.Specification

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
    }

    def "Delete"() {
        given:
        TestCycleDTO cycle = new TestCycleDTO()
        cycle.setCycleName("发布")
        cycle.setVersionId(226L)
        TestCycleDTO cycle1 = testCycleService.insert(cycle);

        TestCycleDTO cycle2 = new TestCycleDTO(cycleId: 1l);
        expect:
        testCycleService.delete(cycle2)
    }

    def "Update"() {
        given:
        TestCycleDTO cycle = new TestCycleDTO()
        cycle.setCycleName("发布")
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
        cycle.setCycleName("发布")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleE.CYCLE)
        TestCycleDTO cycle1 = testCycleService.insert(cycle)

        when:
        TestCycleDTO cycle2 = testCycleService.getOneCycle(1l)

        then:
        cycle2 != null
        StringUtils.equals(cycle2.getCycleName(),"发布")
    }

    //有问题
    def "GetTestCycle"() {
        given:
        TestCycleDTO cycle = new TestCycleDTO()
        cycle.setCycleName("发布")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleE.CYCLE)

        TestCycleDTO cycle1 = testCycleService.insert(cycle)
        TestStatusE status=TestStatusEFactory.create()
        status.setProjectId(99L)
        status.statusName("color1")
        status.setStatusColor("red")
        status.statusType(TestStatusE.STATUS_TYPE_CASE)
        status=status.addSelf()

        TestCycleCaseDTO case1=new TestCycleCaseDTO(cycleId:cycle1.getCycleId(),issueId: 1L,versionId: 226l,executionStatus:status.getStatusId())
        TestCycleCaseE caseE=ConvertHelper.convert(case1, TestCycleCaseE.class);
        caseE.addSelf()

        when:
        JSONObject jsob = testCycleService.getTestCycle(226l)

        then:
        jsob != null
    }

    //有问题
    def "GetTestCycleVersion"() {
        given:
        TestCycleDTO cycle = new TestCycleDTO()
        cycle.setCycleName("发布")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleE.CYCLE)
        TestCycleDTO cycle1 = testCycleService.insert(cycle)
        when:
        Map<String, Object> searchParamMap = new HashMap<>()
        searchParamMap.put("cycleName","发布")
        ResponseEntity<Page<ProductVersionPageDTO>> result = testCycleService.getTestCycleVersion(searchParamMap)
        then:
        result != null
    }

    def "CloneCycle"() {
        given:
        TestCycleDTO cycle = new TestCycleDTO()
        cycle.setCycleName("发布")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleE.CYCLE)
        TestCycleDTO cycle1 = testCycleService.insert(cycle)

        TestCycleDTO folder = new TestCycleDTO()
        folder.setCycleName("发布1")
        folder.setVersionId(226L)
        folder.setType(TestCycleE.FOLDER)
        folder.setParentCycleId(cycle.getCycleId())
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
        folder.setCycleName("发布1")
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
        cycle.setCycleName("发布")
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
        cycle.setCycleName("发布")
        cycle.setVersionId(226L)
        cycle.setType(TestCycleE.CYCLE)
        TestCycleDTO cycle1 = testCycleService.insert(cycle);

        when:
        List<TestCycleDTO> cycles = testCycleService.getFolderByCycleId(1l)
        then:
        cycles != null
    }
}
