package io.choerodon.test.manager.domain.service.impl


import io.choerodon.test.manager.app.service.impl.ExcelServiceImpl
import io.choerodon.test.manager.infra.common.utils.ExcelUtil
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.lang.reflect.Method

class ICycleCaseExcelServiceImplSpec extends Specification {

    @Shared
    ExcelServiceImpl service=new ExcelServiceImpl()

    @Shared
    ICycleCaseExcelServiceImpl serviceImpl=new ICycleCaseExcelServiceImpl()


    def "doExportCycleCaseInOneCycle"() {
//        given:
//        Method method= service.getClass().getDeclaredMethod("doExportCycleCaseInOneCycle",Map.class, Workbook.class,String.class, TestCycleDTO.class)
//        method.setAccessible(true)
//
//        when:
//        method.invoke(service)
//        then:

    }

//    def "getWorkBook"() {
//        when:
//        Workbook workbook=serviceImpl.getWorkBook(ExcelUtil.Mode.SXSSF)
//        then:
//        workbook instanceof SXSSFWorkbook
//        when:
//        Workbook workbook1=serviceImpl.getWorkBook(ExcelUtil.Mode.HSSF)
//        then:
//        workbook1 instanceof HSSFWorkbook
//    }


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
