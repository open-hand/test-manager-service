package io.choerodon.test.manager.infra.common.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Optional;

public class ExcelUtil {

    public enum Mode {
        SXSSF("SXSSF"), HSSF("HSSF"),XSSF("XSSF");
        private String value;

        Mode(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public enum CellType {
        NUMBER(0), TEXT(1), FORMULA(2), BLANK(3), BOOLEAN(4), ERROR(5), DATE(0);
        private int type;

        CellType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

    }

    public static Row createRow(Sheet sheet, int rowNum, CellStyle rowStyle) {
        Row row = sheet.createRow(rowNum);
        Optional.ofNullable(rowStyle).ifPresent(row::setRowStyle);
        return row;
    }

    public static Cell createCell(Row row, int column, CellType type, Object value) {
        Cell cell = row.createCell(column, type.getType());
        cell.setCellStyle(row.getRowStyle());
        switch (type) {
            case TEXT:
                cell.setCellValue((String) value);
                break;
            case NUMBER:
                cell.setCellValue((Long) value);
                break;
            case DATE:
                cell.setCellValue((String) value);
                break;
            default:
                cell.setCellValue(value.toString());
        }
        return cell;
    }

    public static Workbook getWorkBook(Mode mode) {
        Workbook workbook;
        switch (mode) {
            case HSSF:
                workbook = new HSSFWorkbook();
                break;
            case SXSSF:
                workbook = new SXSSFWorkbook();
                break;
            case XSSF:
                workbook = new XSSFWorkbook();
                break;
            default:
                workbook = new SXSSFWorkbook();
        }
        return workbook;
    }

}
