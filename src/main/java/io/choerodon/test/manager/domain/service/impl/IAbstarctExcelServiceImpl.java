package io.choerodon.test.manager.domain.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import io.choerodon.test.manager.domain.service.IExcelService;
import io.choerodon.test.manager.infra.common.utils.ExcelUtil;
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
    public Workbook exportWorkBook(Map<Long, List<R>> cycleCaseMap, String projectName, T cycle) {
        Workbook workbook = getWorkBook(ExcelUtil.Mode.HSSF);
        if (log.isDebugEnabled()) {
            log.debug("导出测试详情：创建workbook成功，类型" +ExcelUtil.Mode.HSSF);
        }
        Sheet sheet = workbook.createSheet();
        //初始化cellStyle
        CellStyle headerRowStyle = workbook.createCellStyle();
        headerRowStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        headerRowStyle.setFillForegroundColor((short) 22);
        CellStyle caseStyle1 = workbook.createCellStyle();
        caseStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
        caseStyle1.setFillForegroundColor((short) 26);
        caseStyle1.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
        caseStyle1.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
        CellStyle caseStyle2 = workbook.createCellStyle();
        caseStyle2.setFillPattern(CellStyle.SOLID_FOREGROUND);
        caseStyle2.setFillForegroundColor((short) 9);
        caseStyle2.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        caseStyle2.setBorderRight(XSSFCellStyle.BORDER_THIN);

        populateSheetStyle(sheet);
        int i = populateVersionHeader(sheet, projectName, cycle, headerRowStyle);
        i = populateHeader(sheet, i, cycle, headerRowStyle);
        Iterator<Map.Entry<Long, List<R>>> iterator = cycleCaseMap.entrySet().iterator();
        if (log.isDebugEnabled()) {
            log.debug("构建WorkBook开始，总共" + cycleCaseMap.values().size() + "条测试执行");
        }

        Queue styleQueue = Queues.newArrayDeque(Lists.newArrayList(caseStyle1, caseStyle2));
        while (iterator.hasNext()) {
            i = populateBody(sheet, i, iterator.next().getValue(), styleQueue);
            iterator.remove();
        }
        if (log.isDebugEnabled()) {
            log.debug("WorkBook构建完成。。");
        }
        return workbook;
    }

}
