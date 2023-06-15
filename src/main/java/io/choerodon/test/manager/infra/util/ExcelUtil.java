package io.choerodon.test.manager.infra.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.multipart.MultipartFile;

public class ExcelUtil {

    private static final String ERROR_IO_WORKBOOK_WRITE_OUTPUTSTREAM = "error.io.workbook.write.output.stream";

    public enum Mode {
        SXSSF("SXSSF"), HSSF("HSSF"), XSSF("XSSF");
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

    public static CellStyle createCellStyle(Workbook workbook, Boolean wrapText) {
        return createCellStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, wrapText);
    }

    public static CellStyle createCellStyle(Workbook workbook) {
        return createCellStyle(workbook, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null);
    }

    public static CellStyle createCellStyle(Workbook workbook, HorizontalAlignment horizontalAlignment) {
        return createCellStyle(workbook, horizontalAlignment, VerticalAlignment.CENTER, null);
    }

    public static CellStyle createCellStyle(Workbook workbook, VerticalAlignment verticalAlignment) {
        return createCellStyle(workbook, HorizontalAlignment.CENTER, verticalAlignment, null);
    }

    public static CellStyle createCellStyle(Workbook workbook,
                                            HorizontalAlignment horizontalAlignment,
                                            VerticalAlignment verticalAlignment,
                                            Boolean wrapText) {
        CellStyle cellStyle = workbook.createCellStyle();
        Optional.ofNullable(horizontalAlignment).ifPresent(cellStyle::setAlignment);
        Optional.ofNullable(verticalAlignment).ifPresent(cellStyle::setVerticalAlignment);
        Optional.ofNullable(wrapText).ifPresent(cellStyle::setWrapText);
        return cellStyle;
    }

    public static void createFont(Font font,
                                  CellStyle cellStyle,
                                  String fontName,
                                  Short fontColor,
                                  Boolean bold) {
        Optional.ofNullable(fontName).ifPresent(font::setFontName);
        Optional.ofNullable(fontColor).ifPresent(font::setColor);
        Optional.ofNullable(bold).ifPresent(font::setBold);
        cellStyle.setFont(font);
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

    public static Cell getOrCreateCell(Row row, int colNum, org.apache.poi.ss.usermodel.CellType type) {
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum, type);
        }

        return cell;
    }

    public static String getColumnWithoutRichText(String rawText) {
        if (StringUtils.isEmpty(rawText)) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        try {
            JSONArray root = JSON.parseArray(rawText);
            for (Object o : root) {
                JSONObject object = (JSONObject) o;
                if (!(object.get("insert") instanceof JSONObject)) {
                    result.append(StringEscapeUtils.unescapeJava(object.getString("insert")));
                }
            }
        } catch (Exception e) {
            Document doc = Jsoup.parse(rawText);
            doc.body().children().forEach(element -> {
                String tagName = element.tag().getName();
                if (tagName == null) {
                    result.append(element.text()).append("\n");
                    return;
                }
                switch (tagName) {
                    case "figure":
                        break;
                    case "ol":
                    case "ul":
                        setListElementStr(result, element);
                        break;
                    default:
                        result.append(element.text()).append("\n");
                        break;
                }
            });
        }
        if (result.length() == 0) {
            return null;
        }
        return result.toString().trim();
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
        if (request.getHeader("User-Agent") != null && request.getHeader("User-Agent").contains("Firefox")) {
            charsetName = "GB2312";
        }
        return charsetName;
    }


    /**
     * @param wb               HSSFWorkbook对象
     * @param realSheet        需要操作的sheet对象
     * @param datas            下拉的列表数据
     * @param startRow         开始行
     * @param endRow           结束行
     * @param startCol         开始列
     * @param endCol           结束列
     * @param hiddenSheetName  隐藏的sheet名
     * @param hiddenSheetIndex 隐藏的sheet索引
     * @return
     * @throws Exception
     */
    public static XSSFWorkbook dropDownList2007(Workbook wb, Sheet realSheet, List<String> datas, int startRow, int endRow,
                                                int startCol, int endCol, String hiddenSheetName, int hiddenSheetIndex) {

        XSSFWorkbook workbook = (XSSFWorkbook) wb;
        // 创建一个数据源sheet
        XSSFSheet hidden = workbook.createSheet(hiddenSheetName);
        // 数据源sheet页不显示
        workbook.setSheetHidden(hiddenSheetIndex, true);
        if (datas == null || datas.isEmpty()) {
            return workbook;
        }
        // 将下拉列表的数据放在数据源sheet上
        XSSFRow row = null;
        XSSFCell cell = null;
        for (int i = 0; i < datas.size(); i++) {
            row = hidden.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(datas.get(i));
        }
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) realSheet);
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint(hiddenSheetName + "!$A$1:$A" + datas.size());
        CellRangeAddressList addressList = null;
        XSSFDataValidation validation = null;
        // 单元格样式
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        // 循环指定单元格下拉数据
        for (int i = startRow; i <= endRow; i++) {
            row = (XSSFRow) realSheet.createRow(i);
            cell = row.createCell(startCol);
            cell.setCellStyle(style);
            addressList = new CellRangeAddressList(i, i, startCol, endCol);
            validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
            realSheet.addValidationData(validation);
        }

        return workbook;
    }

    private static String setListElementStr(StringBuilder result, Element element) {
        element.children().forEach(childElement -> {
            result.append(getLiText(childElement)).append("\n");
        });
        return element.text();
    }

    private static String getLiText(Element element) {
        StringBuilder result = new StringBuilder();
        String liAllText = element.text();
        StringBuilder childListText = new StringBuilder();
        StringBuilder childRelText = new StringBuilder();
        element.children().forEach(childElement -> {
            String tagName = childElement.tag().getName();
            if ("ol".equals(tagName) || "ul".equals(tagName)) {
                childListText.append(" ").append(setListElementStr(childRelText, childElement));
            }
        });
        if (childListText.length() > 0) {
            int childTextStart = liAllText.indexOf(childListText.toString());
            if (childTextStart > -1) {
                result.append(liAllText, 0, childTextStart);
            } else {
                result.append(liAllText);
            }
            result.append("\n").append(childRelText.toString());
        } else {
            result.append(liAllText);
        }
        return result.toString().trim();
    }
}
