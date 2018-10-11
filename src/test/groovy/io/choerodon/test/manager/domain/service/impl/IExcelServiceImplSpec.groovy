package io.choerodon.test.manager.domain.service.impl

import io.choerodon.test.manager.api.dto.TestCycleDTO
import io.choerodon.test.manager.app.service.ExcelService
import io.choerodon.test.manager.app.service.impl.ExcelServiceImpl
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.lang.reflect.Method

class IExcelServiceImplSpec extends Specification {

    @Shared
    ExcelServiceImpl service=new ExcelServiceImpl()

    @Shared
    IExcelServiceImpl serviceImpl=new IExcelServiceImpl()

    def "setHeader"(){
        given:
        HttpServletResponse response= Mock(HttpServletResponse)
        HttpServletRequest request=Mock(HttpServletRequest)
        Method method= service.getClass().getDeclaredMethod("setExcelHeader",HttpServletResponse.class,HttpServletRequest.class,String.class)
        method.setAccessible(true)
        when:
        method.invoke(service,response,request,"filename")
        then:
        1*request.getHeader(_)>>"Firefox"
    }

    def "doExportCycleCaseInOneCycle"() {
//        given:
//        Method method= service.getClass().getDeclaredMethod("doExportCycleCaseInOneCycle",Map.class, Workbook.class,String.class, TestCycleDTO.class)
//        method.setAccessible(true)
//
//        when:
//        method.invoke(service)
//        then:

    }

    def "getWorkBook"() {
        when:
        Workbook workbook=serviceImpl.getWorkBook(IExcelServiceImpl.WorkBookFactory.Mode.SXSSF)
        then:
        workbook instanceof SXSSFWorkbook
        when:
        Workbook workbook1=serviceImpl.getWorkBook(IExcelServiceImpl.WorkBookFactory.Mode.HSSF)
        then:
        workbook1 instanceof HSSFWorkbook
    }


    def "PopulateCycleCaseStep"() {
    }

    def "DoPopulateCycleCaseStep"() {
    }

    def "PopulateVersionHeader"() {
    }

    def "PopulateCycleCaseHeader"() {
    }

    def "PopulateCycleHeader"() {
    }

    def "PopulateSheetStyle"() {
    }

    def "GetWorkBook"() {
    }
}
