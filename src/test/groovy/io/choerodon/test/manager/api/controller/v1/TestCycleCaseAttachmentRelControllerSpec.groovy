//package io.choerodon.test.manager.api.controller.v1
//
//import com.google.common.collect.Lists
//import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService
//import io.choerodon.test.manager.app.service.impl.TestCycleCaseAttachmentRelServiceImpl
//import io.choerodon.test.manager.domain.service.ITestCycleCaseAttachmentRelService
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE
//import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseAttachmentRelEFactory
//import org.junit.runner.RunWith
//import org.powermock.api.mockito.PowerMockito
//import org.powermock.core.classloader.annotations.PrepareForTest
//import org.powermock.modules.junit4.PowerMockRunner
//import org.powermock.modules.junit4.PowerMockRunnerDelegate
//import org.spockframework.runtime.Sputnik
//import spock.lang.Specification
//
///**
// * Created by 842767365@qq.com on 8/22/18.
// */
//@PrepareForTest(TestCycleCaseAttachmentRelEFactory.class)
//@RunWith(PowerMockRunner.class)
//@PowerMockRunnerDelegate(Sputnik.class)
//class TestCycleCaseAttachmentRelControllerSpec extends Specification {
//
//    ITestCycleCaseAttachmentRelService iservice
//
//    TestCycleCaseAttachmentRelService service
//
//    def setup() {
//        iservice = Mock(ITestCycleCaseAttachmentRelService)
//        service = new TestCycleCaseAttachmentRelServiceImpl(iTestCycleCaseAttachmentRelService: iservice)
//    }
//
//    def "Delete"() {
//        when:
//        service.delete("buck", 1l)
//        then:
//        1 * iservice.delete(_, _)
//    }
//
//    def "Upload"() {
//        when:
//        service.upload("buck", "name", null, 1L, "CYCLE_CASE", "comment")
//        then:
//        1 * iservice.upload(_, _, _, _, _, _)
//    }
//
//    def "Delete1"() {
//        given:
//        TestCycleCaseAttachmentRelE returns = new TestCycleCaseAttachmentRelE()
//        TestCycleCaseAttachmentRelE returns1 = new TestCycleCaseAttachmentRelE()
//        TestCycleCaseAttachmentRelE attach = Mock(TestCycleCaseAttachmentRelE)
//        attach.querySelf() >> Lists.newArrayList(returns, returns1)
//        PowerMockito.mockStatic(TestCycleCaseAttachmentRelEFactory.class)
//        PowerMockito.when(TestCycleCaseAttachmentRelEFactory.create()).thenReturn(attach)
//        when:
//        service.delete(1L, TestCycleCaseAttachmentRelE.ATTACHMENT_CYCLE_CASE)
//        then:
//        2 * iservice.delete(_, _)
//    }
//}
