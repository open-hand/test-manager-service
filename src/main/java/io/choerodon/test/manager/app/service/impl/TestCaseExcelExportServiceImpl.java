package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.vo.ExcelCaseVO;
import io.choerodon.test.manager.api.vo.ExcelLookupCaseVO;
import io.choerodon.test.manager.api.vo.TestCaseStepVO;
import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
import io.choerodon.test.manager.api.vo.agile.IssueStatusDTO;
import io.choerodon.test.manager.api.vo.agile.LookupValueDTO;
import io.choerodon.test.manager.api.vo.agile.ProductVersionDTO;
import io.choerodon.test.manager.api.vo.agile.UserDTO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper;
import io.choerodon.test.manager.infra.util.ExcelUtil;
import io.choerodon.test.manager.infra.util.SpringUtil;

/**
 * Created by zongw.lee@gmail.com on 15/10/2018
 */
public class TestCaseExcelExportServiceImpl extends AbstarctExcelExportServiceImpl<TestIssueFolderVO, ExcelCaseVO> {

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    private ModelMapper modelMapper;

    private static final int MAXROWS = 1048575;

    private int lookEnd;
    private int productEnd;
    private int statusEnd;
    private int folderEnd;
    private int userEnd;

    private Map<Long, ProductVersionDTO> versionInfo;

    public TestCaseExcelExportServiceImpl() {
        SpringUtil.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);
    }

    private ExcelLookupCaseVO excelLookupCaseVO;

    private static final String PRIORITIES = "priorities";
    private static final String STATUS = "status";
    private static final String FOLDERS = "folders";
    private static final String USERS = "users";
    private static final String API_TYPE = "api";
    private static final String REPLAY = "replay";
    private static final String UI = "ui";

    private enum CaseHeader {
        COLUMN1("目录*"), COLUMN2("用例编号"), COLUMN3("自定义编号"), COLUMN4("用例概要*"), COLUMN5("优先级*"),
        COLUMN6("关联工作项"), COLUMN7("前置条件"), COLUMN8("测试步骤*"), COLUMN9("测试数据"), COLUMN10("预期结果*");
        private String chinese;

        CaseHeader(String chinese) {
            this.chinese = chinese;
        }

        public String getValue() {
            return chinese;
        }
    }


    @Override
    public void populateSheetStyle(Sheet sheet) {
        //初始化SheetStyle
        // 设置列宽度
        sheet.setColumnWidth(0, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 16);
        sheet.setColumnWidth(1, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 16);
        sheet.setColumnWidth(2, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 48);
        sheet.setColumnWidth(3, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 16);
        sheet.setColumnWidth(4, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 16);
        sheet.setColumnWidth(5, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 48);
        sheet.setColumnWidth(6, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 16);
        sheet.setColumnWidth(7, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 48);
        sheet.setColumnWidth(8, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 16);
        sheet.setColumnWidth(9, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 48);
        sheet.setDefaultRowHeight((short) 500);
    }

    @Override
    public int populateVersionHeader(Sheet sheet, String projectName, TestIssueFolderVO folder, CellStyle rowStyle) {
        return 0;
    }

    @Override
    public int populateHeader(Sheet sheet, int rowNum, TestIssueFolderVO folder, CellStyle headerStyle) {
        Assert.notNull(folder, "error.cycle.are.not.exist");
        Assert.notNull(sheet, "error.sheet.are.be.null");
        Row row = ExcelUtil.getOrCreateRow(sheet, rowNum);
        int i = 0;
        headerStyle.setWrapText(true);
        headerStyle.setAlignment(HorizontalAlignment.LEFT);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        row.setHeight((short) 320);
        for (CaseHeader value : CaseHeader.values()) {
            ExcelUtil.createCell(row, i, ExcelUtil.CellType.TEXT, value.getValue());
            row.getCell(i++).setCellStyle(headerStyle);
        }
        // 设置列宽度
        sheet.setColumnWidth(0, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 25);
        sheet.setColumnWidth(1, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 13);
        sheet.setColumnWidth(2, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 13);
        sheet.setColumnWidth(3, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 50);
        sheet.setColumnWidth(4, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 10);
        sheet.setColumnWidth(5, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 30);
        sheet.setColumnWidth(6, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 30);
        sheet.setColumnWidth(7, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 45);
        sheet.setColumnWidth(8, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 20);
        sheet.setColumnWidth(9, ExcelImportServiceImpl.EXCEL_WIDTH_PX * 45);
        return rowNum + 1;
    }

    @Override
    public int populateBody(Sheet sheet, int column, List<ExcelCaseVO> cycleCases, Queue<CellStyle> cellStyles) {

        for (ExcelCaseVO caseVO : cycleCases) {
            CellStyle style;
            if (ObjectUtils.isEmpty(cellStyles)) {
                style = null;
            } else {
                style = cellStyles.poll();
                cellStyles.offer(style);
            }
            column = populateCase(sheet, column, caseVO, style);
        }
        sheet.setColumnHidden(14, true);
        //如果是模板默认加四百行lookup公式
        if (cycleCases.size() == 1 && cycleCases.get(0).getCaseId() == null) {
            sheet.setColumnHidden(2, true);
            sheet.setColumnHidden(6, true);
            column += addLookupFormula(sheet, column, cellStyles);
        }
        return column;
    }


    public int addLookupFormula(Sheet sheet, int column, Queue<CellStyle> rowStyles) {
        int addDataSize = (403 - column) > 0 ? (403 - column) : 0;
        for (int i = 0; i < addDataSize; i++) {
            CellStyle style;
            if (ObjectUtils.isEmpty(rowStyles)) {
                style = null;
            } else {
                style = rowStyles.poll();
                rowStyles.offer(style);
            }

            Row row = ExcelUtil.createRow(sheet, column++, style);

            ExcelUtil.createCell(row, 10, ExcelUtil.CellType.TEXT, "").setCellFormula(
                    getLookupString("A" + (row.getRowNum() + 1), statusEnd + 2, folderEnd, 2));

            ExcelUtil.createCell(row, 11, ExcelUtil.CellType.TEXT, "").setCellFormula(
                    getLookupString("D" + (row.getRowNum() + 1), 2, lookEnd, 2));

            ExcelUtil.createCell(row, 12, ExcelUtil.CellType.TEXT, "").setCellFormula(
                    getLookupString("F" + (row.getRowNum() + 1), folderEnd + 2, userEnd, 2));
        }
        return column;
    }

    private int populateCase(Sheet sheet, int columnNum, ExcelCaseVO excelCaseVO, CellStyle cellStyle) {
        Row row = ExcelUtil.getOrCreateRow(sheet, columnNum);
        row.setHeight((short) 320);
        if (!ObjectUtils.isEmpty(excelCaseVO)) {
            Optional.ofNullable(excelCaseVO.getFolderName()).ifPresent(v -> ExcelUtil.createCell(row, 0, ExcelUtil.CellType.TEXT, v));
            Optional.ofNullable(excelCaseVO.getCaseNum()).ifPresent(v -> ExcelUtil.createCell(row, 1, ExcelUtil.CellType.TEXT, v));
            Optional.ofNullable(excelCaseVO.getCustomNum()).ifPresent(v -> ExcelUtil.createCell(row, 2, ExcelUtil.CellType.TEXT, v));
            Optional.ofNullable(excelCaseVO.getSummary()).ifPresent(v -> ExcelUtil.createCell(row, 3, ExcelUtil.CellType.TEXT, v));
            Optional.ofNullable(excelCaseVO.getPriorityName()).ifPresent(v -> ExcelUtil.createCell(row, 4, ExcelUtil.CellType.TEXT, v));
            Optional.ofNullable(excelCaseVO.getReleatedIssues()).ifPresent(v -> ExcelUtil.createCell(row, 5, ExcelUtil.CellType.TEXT, v));
            //接口修改后，改成描述
            Optional.ofNullable(ExcelUtil.getColumnWithoutRichText(excelCaseVO.getDescription())).ifPresent(v -> ExcelUtil.createCell(row, 6, ExcelUtil.CellType.TEXT, v));
        }


        for (int i=0; i<10; i++) {
            ExcelUtil.getOrCreateCell(row, i, CellType.STRING).setCellStyle(cellStyle);
        }
        return columnNum + populateCycleCaseStep(sheet, columnNum, excelCaseVO.getCaseSteps(), cellStyle) + 1;
    }

    private int populateCycleCaseStep(Sheet sheet, int column, List<TestCaseStepVO> caseSteps, CellStyle cellStyle) {
        if (caseSteps == null || caseSteps.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < caseSteps.size(); i++) {
            Row row;
            if (i == 0) {
                row = sheet.getRow(column);
            } else {
                row = ExcelUtil.getOrCreateRow(sheet, column);
            }
            row.setHeight((short) 320);
            doPopulateCaseStep(row, caseSteps.get(i), cellStyle);
            column++;
        }

        return caseSteps.size() - 1;
    }

    private void doPopulateCaseStep(Row row, TestCaseStepVO caseStep, CellStyle cellStyle) {
        Optional.ofNullable(caseStep.getTestStep()).ifPresent(v -> ExcelUtil.createCell(row, 7, ExcelUtil.CellType.TEXT, v));
        Optional.ofNullable(caseStep.getTestData()).ifPresent(v -> ExcelUtil.createCell(row, 8, ExcelUtil.CellType.TEXT, v));
        Optional.ofNullable(caseStep.getExpectedResult()).ifPresent(v -> ExcelUtil.createCell(row, 9, ExcelUtil.CellType.TEXT, v));
        for (int i=0; i<10; i++) {
            ExcelUtil.getOrCreateCell(row, i, CellType.STRING).setCellStyle(cellStyle);
        }
    }

    private void prepareLookupData(TestIssueFolderVO folder) {
        Long projectId = folder.getProjectId();

        List<LookupValueDTO> lookupValueDTOS = testCaseService.queryLookupValueByCode("priority").getLookupValues();

        PageRequest pageRequest = new PageRequest(0, 999999999, Sort.Direction.ASC, "componentId");

        List<UserDTO> userDTOS = userService.list(pageRequest, projectId, null, null).getBody().getContent();

        userDTOS.forEach(v -> v.setLoginName(v.getLoginName() + v.getRealName()));

        List<ProductVersionDTO> productVersionDTOS = new ArrayList<>();

        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        testIssueFolderDTO.setProjectId(projectId);

        List<TestIssueFolderVO> testIssueFolderVOS = modelMapper.map(testIssueFolderMapper.select(testIssueFolderDTO).stream().filter(issueFolderDTO -> !API_TYPE.equals(issueFolderDTO.getType()) && !REPLAY.equals(issueFolderDTO.getType()) && !UI.equals(issueFolderDTO.getType())).collect(Collectors.toList()),
                new TypeToken<List<TestIssueFolderVO>>() {
                }.getType());
        List<IssueStatusDTO> issueStatusDTOS = testCaseService.listStatusByProjectId(projectId);

        //加1的原因是每一个新数据开始时都会有一个header
        //从2开始，所以第一个end是size+1
        lookEnd = lookupValueDTOS.size() + 1;
//        productEnd = lookEnd + productVersionDTOS.size() + 1;
        statusEnd = productEnd + issueStatusDTOS.size() + 1;
        folderEnd = statusEnd + testIssueFolderVOS.size() + 1;
        userEnd = folderEnd + userDTOS.size() + 1;

        this.excelLookupCaseVO = new ExcelLookupCaseVO(lookupValueDTOS, userDTOS, productVersionDTOS, testIssueFolderVOS, issueStatusDTOS);
    }

    /**
     * @param sheet
     * @param typeName 给需要lookup数据起的总名称
     * @param start    需要mapping的开始行数
     * @param end      需要mapping的结尾行数
     */
    private void initNameMapping(Sheet sheet, String typeName, String cloumn, int start, int end) {
        Name name = sheet.getWorkbook().createName();
        name.setNameName(typeName);
        name.setRefersToFormula("Sheet0!$" + cloumn + "$" + start + ":$" + cloumn + "$" + end);
    }

    /**
     * 生成下拉框
     *
     * @param sheet
     * @param formulaString 下拉框的唯一名
     * @param firstCol      下拉框是哪些列
     * @param lastCol
     */
    private void setDataValidationByFormula(Sheet sheet, String formulaString, int firstCol, int lastCol) {
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
                .createFormulaListConstraint(formulaString);
        CellRangeAddressList addressList = new CellRangeAddressList(3, MAXROWS, firstCol, lastCol);
        XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);

        sheet.addValidationData(validation);
    }

    private void setLookupData(Sheet sheet, int column, CellStyle rowStyle) {
        List<LookupValueDTO> lookupValueDTOS = excelLookupCaseVO.getLookupValueDTOS();

        List<UserDTO> userDTOS = excelLookupCaseVO.getUserDTOS();

        List<ProductVersionDTO> productVersionDTOS = excelLookupCaseVO.getProductVersionDTOS();

        List<TestIssueFolderVO> testIssueFolderVOS = excelLookupCaseVO.getTestIssueFolderVOS();

        List<IssueStatusDTO> issueStatusDTOS = excelLookupCaseVO.getIssueStatusDTOS();

        //生成每一列的列名
//        column += populateLookupHeader(sheet, column, rowStyle, "优先级");
//        for (LookupValueDTO v : lookupValueDTOS) {
//            column += populateLookupValue(sheet, column, v, rowStyle);
//        }

        column += populateLookupHeader(sheet, column, rowStyle, "版本");
        for (ProductVersionDTO v : productVersionDTOS) {
            column += populateVersion(sheet, column, v, rowStyle);
        }

        column += populateLookupHeader(sheet, column, rowStyle, "状态");
        for (IssueStatusDTO v : issueStatusDTOS) {
            column += populateIssueStatus(sheet, column, v, rowStyle);
        }

        column += populateLookupHeader(sheet, column, rowStyle, "文件夹");
        for (TestIssueFolderVO v : testIssueFolderVOS) {
            column += populateFolder(sheet, column, v, rowStyle);
        }

        column += populateLookupHeader(sheet, column, rowStyle, "经办人");
        for (UserDTO v : userDTOS) {
            column += populateUser(sheet, column, v, rowStyle);
        }
    }

    private int populateLookupHeader(Sheet sheet, int columnNum, CellStyle style, String name) {
        Row headRow = ExcelUtil.createRow(sheet, columnNum, style);
        ExcelUtil.createCell(headRow, 0, ExcelUtil.CellType.TEXT, name);
        return 1;
    }

    private int populateLookupValue(Sheet sheet, int columnNum, LookupValueDTO lookupValueDTO, CellStyle style) {
        Row row = ExcelUtil.createRow(sheet, columnNum, style);
        Optional.ofNullable(lookupValueDTO.getValueCode()).ifPresent(v -> ExcelUtil.createCell(row, 1, ExcelUtil.CellType.TEXT, v));
        Optional.ofNullable(lookupValueDTO.getName()).ifPresent(v -> ExcelUtil.createCell(row, 0, ExcelUtil.CellType.TEXT, v));
        return 1;
    }

    private int populateVersion(Sheet sheet, int columnNum, ProductVersionDTO productVersionDTO, CellStyle style) {
        Row row = ExcelUtil.createRow(sheet, columnNum, style);
        Optional.ofNullable(productVersionDTO.getName()).ifPresent(v -> ExcelUtil.createCell(row, 0, ExcelUtil.CellType.TEXT, v));
        ExcelUtil.createCell(row, 1, ExcelUtil.CellType.TEXT, JSON.toJSONString(productVersionDTO));
        return 1;
    }

    private int populateIssueStatus(Sheet sheet, int columnNum, IssueStatusDTO issueStatusDTO, CellStyle style) {
        Row row = ExcelUtil.createRow(sheet, columnNum, style);
        Optional.ofNullable(issueStatusDTO.getName()).ifPresent(v -> ExcelUtil.createCell(row, 0, ExcelUtil.CellType.TEXT, v));
        return 1;
    }

    private int populateFolder(Sheet sheet, int columnNum, TestIssueFolderVO testIssueFolderVO, CellStyle style) {
        Row row = ExcelUtil.createRow(sheet, columnNum, style);
        ExcelUtil.createCell(row, 1, ExcelUtil.CellType.NUMBER, testIssueFolderVO.getFolderId());
        Optional.ofNullable(testIssueFolderVO.getName()).ifPresent(v -> ExcelUtil.createCell(row, 0, ExcelUtil.CellType.TEXT, v));
        return 1;
    }

    private int populateUser(Sheet sheet, int columnNum, UserDTO userDTO, CellStyle style) {
        Row row = ExcelUtil.createRow(sheet, columnNum, style);
        ExcelUtil.createCell(row, 1, ExcelUtil.CellType.NUMBER, userDTO.getId());
        Optional.ofNullable(userDTO.getLoginName()).ifPresent(v -> ExcelUtil.createCell(row, 0, ExcelUtil.CellType.TEXT, v));
        return 1;
    }

    /**
     * @param param             需要匹配的目标区域
     * @param startRow          源匹配开始行(需要匹配到后的值行也一样)
     * @param endRow            源匹配结束行(需要匹配到后的值行也一样)
     * @param destinatNumClounm 需要匹配到后的值返回列
     * @return String 匹配表达式
     */
    private String getLookupString(String param, int startRow, int endRow, int destinatNumClounm) {
        String vlookup = "VLOOKUP(" + param + ",Sheet0!$A$" + startRow + ":$B$" + endRow
                + "," + destinatNumClounm + ",FALSE)";
        return "IF(ISNA(" + vlookup + "),\"\"," + vlookup + ")";
    }

    private void setAllNameMapping(Sheet sheet, TestIssueFolderVO folder) {
        //确保folder中设置了正确versionId
        //设置映射
        if (sheet.getWorkbook().getNumberOfSheets() == 2) {
            initNameMapping(sheet, PRIORITIES, "A", 2, lookEnd);
            initNameMapping(sheet, STATUS, "A", productEnd + 2, statusEnd);
            initNameMapping(sheet, USERS, "A", folderEnd + 2, userEnd);
        }
        int start = 0;
        boolean flag = false;
        int properFolderSize = 0;
        if (folder.getVersionId() != null) {
            for (TestIssueFolderVO folderDTO : excelLookupCaseVO.getTestIssueFolderVOS()) {
                if (folderDTO.getVersionId().equals(folder.getVersionId())) {
                    properFolderSize++;
                    flag = true;
                } else if (!flag) {
                    start++;
                }
            }
        } else {
            properFolderSize = folderEnd - statusEnd - 1;
        }
        initNameMapping(sheet, sheet.getSheetName() + FOLDERS, "A", statusEnd + 2 + start, statusEnd + 1 + start + properFolderSize);
    }
}
