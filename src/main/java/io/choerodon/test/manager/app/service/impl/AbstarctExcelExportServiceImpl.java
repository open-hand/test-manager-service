package io.choerodon.test.manager.app.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import io.choerodon.test.manager.app.service.ExcelExportService;

/**
 * Created by zongw.lee@gmail.com on 11/5/18
 */
public abstract class AbstarctExcelExportServiceImpl<T, R> implements ExcelExportService<T, R> {

    Log log = LogFactory.getLog(this.getClass());

    @Override
    public Workbook exportWorkBookWithOneSheet(Map<Long, List<R>> cycleCaseMap, String projectName, T cycle, Workbook workbook) {
        Sheet sheet = workbook.createSheet();
        CellStyle headerRowStyle = workbook.createCellStyle();
        CellStyle caseStyle1 = workbook.createCellStyle();
        CellStyle caseStyle2 = workbook.createCellStyle();

        populateCellStyle(headerRowStyle, caseStyle1, caseStyle2, workbook);

        populateSheetStyle(sheet);

        //生成excel头部
        int i = populateVersionHeader(sheet, projectName, cycle, headerRowStyle);
        // 生成列名
        i = populateHeader(sheet, i, cycle, headerRowStyle);
        Iterator<Map.Entry<Long, List<R>>> iterator = cycleCaseMap.entrySet().iterator();
        if (log.isDebugEnabled()) {
            log.debug("构建WorkBook开始，总共" + cycleCaseMap.values().size() + "条大测试数据");
        }

        Queue styleQueue = Queues.newArrayDeque(Lists.newArrayList(caseStyle1, caseStyle2));
        while (iterator.hasNext()) {
            //打印出哪一行出错的信息
            i = populateBody(sheet, i, iterator.next().getValue(), styleQueue);
            iterator.remove();
        }
        if (log.isDebugEnabled()) {
            log.debug("WorkBook sheet页构建完成。。");
        }
        return workbook;
    }

    @Override
    public void populateCellStyle(CellStyle headerRowStyle, CellStyle caseStyle1, CellStyle caseStyle2, Workbook workbook) {
        //初始化cellStyle
        headerRowStyle.setBorderLeft(BorderStyle.THIN);//左边框
        headerRowStyle.setBorderRight(BorderStyle.THIN);//右边框
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("宋体");
        headerRowStyle.setFont(font);

        caseStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        caseStyle1.setFillForegroundColor(HSSFColor.HSSFColorPredefined.TAN.getIndex());
        caseStyle1.setBorderLeft(BorderStyle.THIN);//左边框
        caseStyle1.setBorderRight(BorderStyle.THIN);//右边框
        caseStyle1.setAlignment(HorizontalAlignment.LEFT);
        caseStyle1.setVerticalAlignment(VerticalAlignment.TOP);
        caseStyle2.setBorderLeft(BorderStyle.THIN);
        caseStyle2.setBorderRight(BorderStyle.THIN);
        caseStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        caseStyle2.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        caseStyle2.setAlignment(HorizontalAlignment.LEFT);
        caseStyle2.setVerticalAlignment(VerticalAlignment.TOP);
    }

    @Override
    public void populateSheetStyle(Sheet sheet) {
        //初始化SheetStyle
        sheet.setDefaultColumnWidth(20);
        sheet.setDefaultRowHeight((short) 500);
    }
}
