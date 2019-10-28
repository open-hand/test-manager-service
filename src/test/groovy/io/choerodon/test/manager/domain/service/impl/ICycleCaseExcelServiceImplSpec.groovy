package io.choerodon.test.manager.domain.service.impl

import io.choerodon.test.manager.app.service.impl.CycleCaseExcelExportServiceImpl
import io.choerodon.test.manager.app.service.impl.ExcelServiceImpl
import spock.lang.Shared
import spock.lang.Specification

class   ICycleCaseExcelServiceImplSpec extends Specification {

    @Shared
    ExcelServiceImpl service=new ExcelServiceImpl()

    @Shared
    CycleCaseExcelExportServiceImpl serviceImpl=new CycleCaseExcelExportServiceImpl()


    def "doExportCycleCaseInOneCycle"() {
//        given:
//        Method method= service.getClass().getDeclaredMethod("doExportCycleCaseInOneCycle",Map.class, Workbook.class,String.class, TestCycleVO.class)
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
