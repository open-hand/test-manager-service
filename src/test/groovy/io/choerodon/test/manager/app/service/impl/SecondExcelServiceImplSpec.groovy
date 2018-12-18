//package io.choerodon.test.manager.app.service.impl
//
//import io.choerodon.test.manager.app.service.ExcelService
//import io.choerodon.test.manager.app.service.ExcelServiceHandler
//import org.junit.runner.RunWith
//import org.powermock.api.mockito.PowerMockito
//import org.powermock.core.classloader.annotations.PrepareForTest
//import org.powermock.modules.junit4.PowerMockRunner
//import org.powermock.modules.junit4.PowerMockRunnerDelegate
//import org.spockframework.runtime.Sputnik
//import org.springframework.aop.framework.AopContext
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.mock.web.MockHttpServletRequest
//import org.springframework.mock.web.MockHttpServletResponse
//import spock.lang.Specification
//
//@RunWith(PowerMockRunner.class)
//@PowerMockRunnerDelegate(Sputnik.class)
//@PrepareForTest([AopContext.class])
//class SecondExcelServiceImplSpec extends Specification {
//
//    @Autowired
//    ExcelServiceHandler excelServiceHandler
//
//    ExcelService excelService = Mock(ExcelService)
//
//    def setup() {
////        mock静态方法-CustomUserDetails
//        PowerMockito.mockStatic(AopContext)
//        PowerMockito.when(AopContext.currentProxy()).thenReturn(excelService)
//    }
//
//    def "ExportCycleCaseInOneCycle"() {
//        when:
//        excelServiceHandler.exportCycleCaseInOneCycle(1L, 1L, new MockHttpServletRequest(), new MockHttpServletResponse(), 1L)
//        then:
//        1 * excelService.exportCycleCaseInOneCycleByTransaction(_, _, _, _, _, _)
//    }
//
//    def "ExportCaseByProject"() {
//        when:
//        excelServiceHandler.exportCaseByProject(1L, new MockHttpServletRequest(), new MockHttpServletResponse(), 1L)
//        then:
//        1 * excelService.exportCaseProjectByTransaction(_, _, _, _, _)
//    }
//
//    def "ExportCaseByVersion"() {
//        when:
//        excelServiceHandler.exportCaseByVersion(1L, 1L, new MockHttpServletRequest(), new MockHttpServletResponse(), 1L)
//        then:
//        1 * excelService.exportCaseVersionByTransaction(_, _, _, _, _, _)
//    }
//
//    def "ExportCaseByFolder"() {
//        when:
//        excelServiceHandler.exportCaseByFolder(1L, 1L, new MockHttpServletRequest(), new MockHttpServletResponse(), 1L)
//        then:
//        1 * excelService.exportCaseFolderByTransaction(_, _, _, _, _, _)
//    }
//
//    def "exportFailCase"() {
//        when:
//        excelServiceHandler.exportFailCase(1L, 1L, new MockHttpServletRequest(), new MockHttpServletResponse(), 1L)
//        then:
//        1 * excelService.exportFailCaseByTransaction(_, _, _)
//    }
//}
