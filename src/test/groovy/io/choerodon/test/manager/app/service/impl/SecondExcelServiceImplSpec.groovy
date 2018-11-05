package io.choerodon.test.manager.app.service.impl

import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.core.oauth.DetailsHelper
import io.choerodon.test.manager.app.service.ExcelService
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.GrantedAuthority
import spock.lang.Specification

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
@PrepareForTest([DetailsHelper.class])
class SecondExcelServiceImplSpec extends Specification {

    ExcelService excelService = Mock(ExcelService)

    def setup() {
       //mock静态方法-CustomUserDetails
        PowerMockito.mockStatic(DetailsHelper)
        CustomUserDetails details = new CustomUserDetails("user","pass",new ArrayList<GrantedAuthority>())
        details.setUserId(1L)
        PowerMockito.when(DetailsHelper.getUserDetails()).thenReturn(details)
    }

    def "ExportCycleCaseInOneCycle"() {
        when:
        excelService.exportCycleCaseInOneCycle(1L,1L,new MockHttpServletRequest(),new MockHttpServletResponse(),1L)
        then:
        noExceptionThrown()
    }

    def "ExportCaseByProject"() {
        when:
        excelService.exportCaseByProject(1L,new MockHttpServletRequest(),new MockHttpServletResponse(),1L)
        then:
        noExceptionThrown()
    }

    def "ExportCaseByVersion"() {
        when:
        excelService.exportCaseByVersion(1L,1L,new MockHttpServletRequest(),new MockHttpServletResponse(),1L)
        then:
        noExceptionThrown()
    }

    def "ExportCaseByFolder"() {
        when:
        excelService.exportCaseByFolder(1L,1L,new MockHttpServletRequest(),new MockHttpServletResponse(),1L)
        then:
        noExceptionThrown()
    }
}
