package io.choerodon.test.manager.app.service.impl

import io.choerodon.core.convertor.ConvertHelper
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.api.vo.TestCaseStepVO
import io.choerodon.test.manager.app.service.TestCaseStepService
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService
import io.choerodon.test.manager.infra.dto.TestCaseStepDTO
import io.choerodon.test.manager.infra.mapper.TestCaseStepMapper
import io.choerodon.test.manager.infra.mapper.TestCycleCaseDefectRelMapper
import io.choerodon.test.manager.infra.mapper.TestCycleCaseStepMapper
import io.choerodon.test.manager.infra.util.DBValidateUtil
import org.junit.runner.RunWith
import org.modelmapper.ModelMapper
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by 842767365@qq.com on 7/27/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
//@PowerMockIgnore( ["javax.management.*","javax.net.ssl.*"])
//@PrepareForTest(DBValidateUtil.class)
//@RunWith(PowerMockRunner.class)
//@PowerMockRunnerDelegate(Sputnik.class)
class TestCaseStepServiceImplSpec extends Specification {

//    @Rule  //spring 与 powermock 组合需要使用PowerMockRule
//    public PowerMockRule rule = new PowerMockRule();
    @Autowired
    TestCaseStepService service

    @Autowired
    TestCycleCaseAttachmentRelService testCycleCaseAttachmentRelService

    @Autowired
    TestCaseStepMapper testCaseStepMapper

    @Autowired
    private TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper

    @Autowired
    private TestCycleCaseStepMapper testCycleCaseStepMapper

    @Autowired
    private ModelMapper modelMapper

//    ITestCaseStepService stepService
//    ITestStatusService statusService

//    def setup() {
//        given:
//        stepService = Mock(ITestCaseStepService)
//        statusService = Mock(ITestStatusService)
//        service = new TestCaseStepServiceImpl(iTestCaseStepService: stepService, iTestStatusService: statusService)
//    }


    def "RemoveStep"() {
        TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO()
        testCaseStepDTO.setIssueId(1L)
        testCaseStepMapper.insert(testCaseStepDTO)
        List<TestCaseStepDTO> selectAllPre = testCaseStepMapper.selectAll()
        TestCaseStepVO testCaseStepVO = modelMapper.map(testCaseStepDTO, TestCaseStepVO)
        when:
        service.removeStep(testCaseStepVO)
        then:
        List<TestCaseStepDTO> selectAllAft = testCaseStepMapper.selectAll()
        selectAllAft.size() == selectAllPre.size() - 1
    }

    def "Query"() {
        TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO()
        testCaseStepDTO.setIssueId(1L)
        testCaseStepMapper.insert(testCaseStepDTO)
        TestCaseStepVO testCaseStepVO = modelMapper.map(testCaseStepDTO, TestCaseStepVO)
        when:
        List<TestCaseStepVO> query = service.query(testCaseStepVO)
        then:
        query.size() > 0
    }

//    def "ChangeStep"() {
//        given:
//
//        PowerMockito.mockStatic(DBValidateUtil.class)
//        PowerMockito.when(DBValidateUtil.executeAndvalidateUpdateNum())
//        TestCaseStepVO testCaseStepVO = new TestCaseStepVO()
//        testCaseStepVO.setStepId(1L)
//        testCaseStepVO.setIssueId(55L)
//        testCaseStepVO.setObjectVersionNumber(1L)
//        when:
//        TestCaseStepVO TestCaseStepVO = service.changeStep(testCaseStepVO, 1l)
//        then:
//        TestCaseStepVO.getIssueId() == testCaseStepVO.getIssueId()
//    }

//
//    def "Clone"() {
//
//        given:
//        TestCaseStepVO testCaseStepVO = new TestCaseStepVO()
//        when:
//        service.batchClone(testCaseStepVO,)
//    }
}
