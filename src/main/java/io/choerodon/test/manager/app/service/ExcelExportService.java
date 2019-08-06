package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Created by 842767365@qq.com on 8/9/18.
 */
public interface ExcelExportService<T, R> {
    int populateVersionHeader(Sheet sheet, String projectName, T cycle, CellStyle rowStyle);

    int populateHeader(Sheet sheet, int rowNum, T cycle, CellStyle rowStyle);

    void populateCellStyle(CellStyle headerRowStyle, CellStyle caseStyle1, CellStyle caseStyle2);

    void populateSheetStyle(Sheet sheet);

    int populateBody(Sheet sheet, int column, List<R> cycleCases, Queue<CellStyle> rowStyles);

    Workbook exportWorkBookWithOneSheet(Map<Long, List<R>> cycleCaseMap, String projectName, T cycle, Workbook workbook);

}
