//package io.choerodon.test.manager.app.service.impl
//
//import io.choerodon.core.oauth.CustomUserDetails
//import io.choerodon.core.oauth.DetailsHelper
//import io.choerodon.test.manager.app.service.ExcelService
//import io.choerodon.test.manager.app.service.ExcelServiceHandler
//import org.junit.runner.RunWith
//import org.mockito.Mockito
//import org.powermock.api.mockito.PowerMockito
//import org.powermock.core.classloader.annotations.PrepareForTest
//import org.powermock.modules.junit4.PowerMockRunner
//import org.powermock.modules.junit4.PowerMockRunnerDelegate
//import org.spockframework.runtime.Sputnik
//import org.springframework.mock.web.MockHttpServletRequest
//import org.springframework.mock.web.MockHttpServletResponse
//import org.springframework.security.core.GrantedAuthority
//import spock.lang.Shared
//import spock.lang.Specification
//
//import static org.mockito.Matchers.anyLong
//import static org.mockito.Matchers.anyObject
//
//@RunWith(PowerMockRunner.class)
//@PowerMockRunnerDelegate(Sputnik.class)
//@PrepareForTest(DetailsHelper.class)
//class ExcelServiceHandlerImplSpec extends Specification {
//
//    @Shared
//    ExcelService excelService
//
//    ExcelServiceHandler excelServiceHandler = new ExcelServiceHandlerImpl()
//
//    def setup() {
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        CustomUserDetails customUserDetails = new CustomUserDetails("lee", "zongw", authorities);
//        customUserDetails.setUserId(1L);
//
//        //mock静态方法 - CustomUserDetails
//        PowerMockito.mockStatic(DetailsHelper)
//        PowerMockito.when(DetailsHelper.getUserDetails()).thenReturn(customUserDetails)
//
//        excelService = Mockito.mock(ExcelService)
//        excelServiceHandler.setExcelService(excelService)
//    }
//
//    def "DownLoadByProject"() {
//        when:
//        excelServiceHandler.exportCaseByProject(1L, new MockHttpServletRequest(), new MockHttpServletResponse(), 1L)
//
//        then:
//        Mockito.verify(excelService).exportCaseProjectByTransaction(anyLong(), anyObject(), anyObject(), anyLong(), anyLong())
//        noExceptionThrown()
//    }
//
//    def "DownLoadByVersion"() {
//        when:
//        excelServiceHandler.exportCaseByVersion(1L, 1L, new MockHttpServletRequest(), new MockHttpServletResponse(), 1L)
//
//        then:
//        Mockito.verify(excelService).exportCaseVersionByTransaction(anyLong(), anyLong(), anyObject(), anyObject(), anyLong(), anyLong())
//        noExceptionThrown()
//    }
//
//    def "DownLoadByFolder"() {
//        when:
//        excelServiceHandler.exportCaseByFolder(1L, 1L, new MockHttpServletRequest(), new MockHttpServletResponse(), 1L)
//
//        then:
//        Mockito.verify(excelService).exportCaseFolderByTransaction(anyLong(), anyLong(), anyObject(), anyObject(), anyLong(), anyLong())
//        noExceptionThrown()
//    }
//
//    def "exportFailCase"() {
//        when:
//        excelServiceHandler.exportFailCase(1L, 1L)
//        then:
//        Mockito.verify(excelService).exportFailCaseByTransaction(anyLong(), anyLong(), anyLong())
//        noExceptionThrown()
//    }
//
//    def "exportCycleCaseInOneCycle"() {
//        when:
//        excelServiceHandler.exportCycleCaseInOneCycle(1L, 1L, new MockHttpServletRequest(), new MockHttpServletResponse(), 1L)
//
//        then:
//        Mockito.verify(excelService).exportCycleCaseInOneCycleByTransaction(anyLong(), anyLong(), anyObject(), anyObject(), anyLong(), anyLong())
//        noExceptionThrown()
//    }
//
//}
