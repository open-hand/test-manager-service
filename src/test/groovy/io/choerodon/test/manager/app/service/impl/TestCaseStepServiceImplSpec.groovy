//package io.choerodon.test.manager.app.service.impl
//
//import io.choerodon.core.convertor.ConvertHelper
//import io.choerodon.test.manager.api.vo.TestCaseStepVO
//import io.choerodon.test.manager.app.service.TestCaseStepService
//import io.choerodon.test.manager.domain.service.ITestCaseStepService
//import io.choerodon.test.manager.domain.service.ITestStatusService
//import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE
//import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE
//import org.junit.runner.RunWith
//import org.powermock.api.mockito.PowerMockito
//import org.powermock.core.classloader.annotations.PrepareForTest
//import org.powermock.modules.junit4.PowerMockRunner
//import org.powermock.modules.junit4.PowerMockRunnerDelegate
//import org.spockframework.runtime.Sputnik
//import spock.lang.Specification
//
///**
// * Created by 842767365@qq.com on 7/27/18.
// */
////@SpringBootTest(webEnvironment = RANDOM_PORT)
////@Import(IntegrationTestConfiguration)
////@Stepwise
////@PowerMockIgnore( ["javax.management.*","javax.net.ssl.*"])
//@PrepareForTest(ConvertHelper.class)
//@RunWith(PowerMockRunner.class)
//@PowerMockRunnerDelegate(Sputnik.class)
//class TestCaseStepServiceImplSpec extends Specification {
//
////    @Rule  //spring 与 powermock 组合需要使用PowerMockRule
////    public PowerMockRule rule = new PowerMockRule();
//
//    TestCaseStepService service;
//    ITestCaseStepService stepService
//    ITestStatusService statusService
//
//
//    def setup(){
//        given:
//        stepService=Mock(ITestCaseStepService)
//        statusService=Mock(ITestStatusService)
//        service=new TestCaseStepServiceImpl(iTestCaseStepService:stepService,iTestStatusService:statusService)
//    }
//
//
//    def "RemoveStep"() {
//        when:
//        service.removeStep(null)
//        then:
//        1*stepService.removeStep(_)
//    }
//
//
//    def "Query"() {
//        when:
//        service.query(null)
//        then:
//        1*stepService.query(_)
//    }
//
//
//    def "ChangeStep"() {
//        given:
//        def stepE = Mock(TestCaseStepE)
//        stepE.getStepId() >> 1L
//        TestCaseStepVO vo = new TestCaseStepVO()
//        PowerMockito.mockStatic(ConvertHelper.class)
//        PowerMockito.when(modeMapper.map(vo, TestCaseStepE.class)).thenReturn(stepE)
//        when:
//        service.changeStep(vo, 1l)
//        then:
//        1 * stepE.changeOneStep() >> new TestCaseStepE()
//        0 * statusService.getDefaultStatusId(TestStatusType.STATUS_TYPE_CASE_STEP)
//    }
//
//    def "runCycleCaseStep"() {
//    }
//
//    def "BatchInsertStep"() {
//    }
//
//    def "Clone"() {
//    }
//}
