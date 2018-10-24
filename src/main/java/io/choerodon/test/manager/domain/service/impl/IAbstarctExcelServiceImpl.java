package io.choerodon.test.manager.domain.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import io.choerodon.test.manager.domain.service.IExcelService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class IAbstarctExcelServiceImpl<T,R> implements IExcelService<T,R> {

    Log log = LogFactory.getLog(this.getClass());

    @Override
    public Workbook exportWorkBookWithOneSheet(Map<Long, List<R>> cycleCaseMap, String projectName, T cycle,Workbook workbook) {
        Sheet sheet = workbook.createSheet();
        CellStyle headerRowStyle = workbook.createCellStyle();
        CellStyle caseStyle1 = workbook.createCellStyle();
        CellStyle caseStyle2 = workbook.createCellStyle();

        populateCellStyle(headerRowStyle,caseStyle1,caseStyle2);

        populateSheetStyle(sheet);

        int i = populateVersionHeader(sheet, projectName, cycle, headerRowStyle);
        i = populateHeader(sheet, i, cycle, headerRowStyle);
        Iterator<Map.Entry<Long, List<R>>> iterator = cycleCaseMap.entrySet().iterator();
        if (log.isDebugEnabled()) {
            log.debug("构建WorkBook开始，总共" + cycleCaseMap.values().size() + "条大测试数据");
        }

        Queue styleQueue = Queues.newArrayDeque(Lists.newArrayList(caseStyle1, caseStyle2));
        while (iterator.hasNext()) {
//            打印出哪一行出错的信息
            i = populateBody(sheet, i, iterator.next().getValue(), styleQueue);
            iterator.remove();
        }
        if (log.isDebugEnabled()) {
            log.debug("WorkBook sheet页构建完成。。");
        }
        return workbook;
    }

    @Override
    public void populateCellStyle(CellStyle headerRowStyle,CellStyle caseStyle1,CellStyle caseStyle2) {
        //初始化cellStyle
        headerRowStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerRowStyle.setFillForegroundColor((short) 22);

        caseStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
        caseStyle1.setFillForegroundColor((short) 26);
        caseStyle1.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
        caseStyle1.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
        caseStyle2.setFillPattern(CellStyle.SOLID_FOREGROUND);
        caseStyle2.setFillForegroundColor((short) 9);
        caseStyle2.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        caseStyle2.setBorderRight(XSSFCellStyle.BORDER_THIN);
    }

    @Override
    public void populateSheetStyle(Sheet sheet) {
        //初始化SheetStyle
        sheet.setDefaultColumnWidth(20);
        sheet.setDefaultRowHeight((short) 500);
    }

}
