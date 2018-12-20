package io.choerodon.test.manager.infra.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Optional;

public class ExcelUtil {

    private static final String ERROR_IO_WORKBOOK_WRITE_OUTPUTSTREAM = "error.io.workbook.write.output.stream";

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

    private static final String ERROR_IO_NEW_WORKBOOK = "error.io.new.workbook";

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

    public static Workbook getWorkbookFromMultipartFile(Mode mode, MultipartFile excelFile) {
        try {
            switch (mode) {
                case HSSF:
                    return new HSSFWorkbook(excelFile.getInputStream());
                case XSSF:
                    return new XSSFWorkbook(excelFile.getInputStream());
                default:
                    return null;
            }
        } catch (IOException e) {
            throw new CommonException(ERROR_IO_NEW_WORKBOOK, e);
        }
    }

    public static byte[] getBytes(Workbook workbook) {
        try (ByteArrayOutputStream workbookOutputStream = new ByteArrayOutputStream()) {
            workbook.write(workbookOutputStream);
            return workbookOutputStream.toByteArray();
        } catch (IOException e) {
            throw new CommonException(ERROR_IO_WORKBOOK_WRITE_OUTPUTSTREAM, e);
        }
    }

    public static boolean isBlank(Cell cell) {
        return StringUtils.isBlank(getStringValue(cell));
    }

    private static String getStringValue(CellValue cellValue) {
        switch (cellValue.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cellValue.getStringValue();
            case Cell.CELL_TYPE_BOOLEAN:
                return Boolean.toString(cellValue.getBooleanValue());
            case Cell.CELL_TYPE_NUMERIC:
                return NumberToTextConverter.toText(cellValue.getNumberValue());
            default:
                return cellValue.getErrorValue() + "";
        }
    }

    private static String getDateValue(Cell cell) {
        short format = cell.getCellStyle().getDataFormat();
        if (format == 14 || format == 31 || format == 57 || format == 58) {
            return new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getJavaDate(cell.getNumericCellValue()));
        } else if (HSSFDateUtil.isCellDateFormatted(cell)) {
            return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
        } else {
            return NumberToTextConverter.toText(cell.getNumericCellValue());
        }
    }

    public static String getStringValue(Cell cell) {
        if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
            return "";
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_NUMERIC:
                return getDateValue(cell);
            case Cell.CELL_TYPE_ERROR:
                return cell.getErrorCellValue() + "";
            default:
                FormulaEvaluator evaluator;
                Workbook workbook = cell.getRow().getSheet().getWorkbook();
                if (workbook instanceof XSSFWorkbook) {
                    evaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
                } else if (workbook instanceof HSSFWorkbook) {
                    evaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
                } else {
                    return cell.getCellFormula();
                }

                return getStringValue(evaluator.evaluate(cell));
        }
    }

    public static Row getOrCreateRow(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }

        return row;
    }

    public static Cell getOrCreateCell(Row row, int colNum, int type) {
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum, type);
        }

        return cell;
    }

    public static String getColumnWithoutRichText(String rawText){
        if(StringUtils.isEmpty(rawText))
            return null;
        StringBuilder result =new StringBuilder();
        try {
            JSONArray root = JSONArray.parseArray(rawText);
            Iterator list=root.iterator();
            while (list.hasNext()){
                JSONObject object= (JSONObject) list.next();
                if(!(object.get("insert") instanceof JSONObject))
                    result.append(object.getString("insert"));
            }
        }catch (Exception e){
            return rawText;
        }
        if(result.length()==0)
            return null;
        return result.toString();
    }

    private static final String FILESUFFIX = ".xlsx";

    private static final String EXCELCONTENTTYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final String EXPORT_ERROR_SET_HEADER = "error.issue.set.workbook";


    public static void setExcelHeaderByStream(HttpServletRequest request, HttpServletResponse response) {
        String charsetName = setExcelHeader(request);
        response.reset();
        response.setContentType(EXCELCONTENTTYPE);
        response.setCharacterEncoding("utf-8");
        try {
            response.setHeader("Content-Disposition", "attachment;filename="
                    + new String((FILESUFFIX).getBytes(charsetName),
                    "ISO-8859-1"));
        } catch (UnsupportedEncodingException e1) {
            throw new CommonException(EXPORT_ERROR_SET_HEADER, e1);
        }

    }

    public static String setExcelHeader(HttpServletRequest request) {
        String charsetName = "UTF-8";
        if (request.getHeader("User-Agent").contains("Firefox")) {
            charsetName = "GB2312";
        }
        return charsetName;
    }

}
