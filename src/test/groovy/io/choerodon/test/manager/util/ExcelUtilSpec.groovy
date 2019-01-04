package io.choerodon.test.manager.util

import io.choerodon.test.manager.infra.common.utils.ExcelUtil
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import spock.lang.Specification

class ExcelUtilSpec extends Specification {


    def "GetWorkBook"() {
        when:
        def result= ExcelUtil.getWorkBook(ExcelUtil.Mode.HSSF)
        then:
        result instanceof HSSFWorkbook
        when:
        def result1= ExcelUtil.getWorkBook(ExcelUtil.Mode.SXSSF)
        then:
        result1 instanceof SXSSFWorkbook
        when:
        def result2= ExcelUtil.getWorkBook(ExcelUtil.Mode.XSSF)
        then:
        result2 instanceof XSSFWorkbook


    }

    def "GetWorkbookFromMultipartFile"() {
    }

    def "GetBytes"() {
    }

    def "IsBlank"() {
    }

    def "GetStringValue"() {
    }

    def "GetOrCreateRow"() {
    }

    def "GetOrCreateCell"() {
    }

    def "GetColumnWithoutRichText"() {
    }

    def "SetExcelHeaderByStream"() {
    }

    def "SetExcelHeader"() {
    }
}
