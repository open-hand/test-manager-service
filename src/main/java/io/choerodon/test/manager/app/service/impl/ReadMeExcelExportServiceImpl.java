package io.choerodon.test.manager.app.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

import io.choerodon.test.manager.api.vo.ExcelReadMeVO;
import io.choerodon.test.manager.api.vo.ExcelReadMeOptionVO;
import io.choerodon.test.manager.infra.util.ExcelUtil;

/**
 * Created by zongw.lee@gmail.com on 31/10/2018
 */
public class ReadMeExcelExportServiceImpl extends AbstarctExcelExportServiceImpl<ExcelReadMeVO, ExcelReadMeOptionVO> {

    private Map<Integer, Row> rowMap = new HashMap<>();

    private Row firstRow;

    private ExcelReadMeVO relReadMeDTO;

    private CellStyle getDefaultStyle(Sheet sheet) {
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 12);
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        style.setFont(font);
        return style;
    }

    @Override
    public int populateVersionHeader(Sheet sheet, String projectName, ExcelReadMeVO readMeDTO, CellStyle rowStyle) {
        sheet.setDefaultRowHeight((short) 500);
        sheet.setDefaultColumnWidth(15);

        Row row = ExcelUtil.createRow(sheet, 0, getDefaultStyle(sheet));
        firstRow = row;
        relReadMeDTO = readMeDTO;

        ExcelUtil.createCell(row, 0, ExcelUtil.CellType.TEXT, readMeDTO.getHeader());
        return 1;
    }

    @Override
    public int populateHeader(Sheet sheet, int rowNum, ExcelReadMeVO readMeDTO, CellStyle rowStyle) {
        CellStyle style = getDefaultStyle(sheet);
        Row row1 = ExcelUtil.createRow(sheet, 1, style);
        ExcelUtil.createCell(row1, 0, ExcelUtil.CellType.TEXT, readMeDTO.getBodyHeader());
        rowMap.put(1, row1);

        Row row2 = ExcelUtil.createRow(sheet, 2, style);
        ExcelUtil.createCell(row2, 0, ExcelUtil.CellType.TEXT, readMeDTO.getNecessary());
        rowMap.put(2, row2);

        Row row3 = ExcelUtil.createRow(sheet, 3, style);
        ExcelUtil.createCell(row3, 0, ExcelUtil.CellType.TEXT, readMeDTO.getOptional());
        rowMap.put(3, row3);

        return rowNum + 3;
    }

    @Override
    public int populateBody(Sheet sheet, int column, List<ExcelReadMeOptionVO> readMeOptionDTOS, Queue<CellStyle> rowStyles) {
        int i = 1;
        for (ExcelReadMeOptionVO readMeOptionDTO : readMeOptionDTOS) {
            ExcelUtil.createCell(rowMap.get(1), i, ExcelUtil.CellType.TEXT, readMeOptionDTO.getFiled());
            if (readMeOptionDTO.getRequired() != null) {
                if (readMeOptionDTO.getRequired() != null && readMeOptionDTO.getRequired()) {
                    ExcelUtil.createCell(rowMap.get(2), i, ExcelUtil.CellType.TEXT, "√");
                } else {
                    ExcelUtil.createCell(rowMap.get(3), i, ExcelUtil.CellType.TEXT, "√");
                }
            }
            i++;
        }
        ExcelUtil.createCell(firstRow, readMeOptionDTOS.size() + 1, ExcelUtil.CellType.TEXT, relReadMeDTO.getOperation());
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, readMeOptionDTOS.size()));
        sheet.addMergedRegion(new CellRangeAddress(0, 7, readMeOptionDTOS.size() + 1, readMeOptionDTOS.size() + 3));

        return column;
    }
}
