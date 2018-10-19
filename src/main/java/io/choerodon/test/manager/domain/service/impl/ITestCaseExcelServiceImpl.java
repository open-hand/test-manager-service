package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.agile.api.dto.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.dto.ExcelLookupCaseDTO;
import io.choerodon.test.manager.api.dto.TestCaseStepDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderRelDTO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
import io.choerodon.test.manager.domain.test.manager.factory.TestIssueFolderEFactory;
import io.choerodon.test.manager.infra.common.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

/**
 * Created by zongw.lee@gmail.com on 15/10/2018
 */
public class ITestCaseExcelServiceImpl extends IAbstarctExcelServiceImpl<TestIssueFolderDTO, TestIssueFolderRelDTO> {

    @Autowired
    TestCaseService testCaseService;

    @Autowired
    UserService userService;

    private ExcelLookupCaseDTO excelLookupCaseDTO;

    private static final String PRIORITIES = "priorities";
    private static final String VERSIONS = "versions";
    private static final String LABELS = "labels";
    private static final String COMPONENTS = "components";
    private static final String STATUS = "status";
    private static final String FOLDERS = "folders";
    private static final String USERS = "users";

    private enum CaseHeader {
        COLUMN1("文件夹*"), COLUMN2("用例编号*"), COLUMN3("用例概要*"), COLUMN4("优先级*"), COLUMN5("用例描述*"),
        COLUMN6("经办人*"), COLUMN7("版本*"), COLUMN8("模块*"), COLUMN9("标签*"), COLUMN10("状态*"), COLUMN11("测试步骤"),
        COLUMN12("测试数据"), COLUMN13("预期结果");
        private String chinese;
        private String us;

        CaseHeader(String chinese) {
            this.chinese = chinese;
        }

        public String getValue(String type) {
            switch (type) {
                case "zn_ch":
                    return chinese;
                case "us_":
                    return us;
                default:
                    return chinese;
            }
        }
    }

    @Override
    public int populateVersionHeader(Sheet sheet, String projectName, TestIssueFolderDTO folder, CellStyle rowStyle) {
        if (sheet.getWorkbook().getNumberOfSheets() == 1) {
            return 0;
        }
        Row row1 = ExcelUtil.createRow(sheet, 0, rowStyle);
        ExcelUtil.createCell(row1, 0, ExcelUtil.CellType.TEXT, "项目：" + projectName);
        ExcelUtil.createCell(row1, 1, ExcelUtil.CellType.TEXT, "版本：" + folder.getVersionId());
        return 2;
    }

    @Override
    public int populateHeader(Sheet sheet, int rowNum, TestIssueFolderDTO folder, CellStyle rowStyle) {
        Assert.notNull(folder, "error.cycle.are.not.exist");
        Assert.notNull(sheet, "error.sheet.are.be.null");
        Row row = ExcelUtil.createRow(sheet, rowNum, rowStyle);
        int i = 0;
        if (sheet.getWorkbook().getNumberOfSheets() == 1) {
            log.debug("开始准备lookup sheet页数据...");
            try {
                prepareLookupData(folder);
            }catch (NullPointerException e){
                throw new CommonException("缺失导出信息",e);
            }
        } else {
            for (CaseHeader value : CaseHeader.values()) {
                ExcelUtil.createCell(row, i++, ExcelUtil.CellType.TEXT, value.getValue("zn_ch"));
            }
        }
        return rowNum + 1;
    }

    @Override
    public int populateBody(Sheet sheet, int column, List<TestIssueFolderRelDTO> folderRelDTOS, Queue<CellStyle> rowStyles) {
        if (sheet.getWorkbook().getNumberOfSheets() == 1) {
            setLookupData(sheet,column,rowStyles);
            return column;
        } else {
            for (TestIssueFolderRelDTO folderRel : folderRelDTOS) {
                CellStyle style;
                if (ObjectUtils.isEmpty(rowStyles)) {
                    style = null;
                } else {
                    style = rowStyles.poll();
                    rowStyles.offer(style);
                }

                column = populateCase(sheet, column, folderRel, style);
            }
            return column;
        }
    }

    private int populateCase(Sheet sheet, int columnNum, TestIssueFolderRelDTO folderRel, CellStyle rowStyles) {
        Row row = ExcelUtil.createRow(sheet, columnNum, rowStyles);
        Optional.ofNullable(folderRel.getFolderName()).ifPresent(v -> ExcelUtil.createCell(row, 0, ExcelUtil.CellType.TEXT, v));
        if (!ObjectUtils.isEmpty(folderRel.getIssueInfosDTO())) {
            Optional.ofNullable(folderRel.getIssueInfosDTO().getIssueNum()).ifPresent(v -> ExcelUtil.createCell(row, 1, ExcelUtil.CellType.TEXT, v));
            Optional.ofNullable(folderRel.getIssueInfosDTO().getSummary()).ifPresent(v -> ExcelUtil.createCell(row, 2, ExcelUtil.CellType.TEXT, v));
            Optional.ofNullable(folderRel.getIssueInfosDTO().getPriorityName()).ifPresent(v -> ExcelUtil.createCell(row, 3, ExcelUtil.CellType.TEXT, v));
            //接口修改后，改成描述
            Optional.ofNullable(folderRel.getIssueInfosDTO().getSummary()).ifPresent(v -> ExcelUtil.createCell(row, 4, ExcelUtil.CellType.TEXT, v));
            Optional.ofNullable(folderRel.getIssueInfosDTO().getAssigneeName()).ifPresent(v -> ExcelUtil.createCell(row, 5, ExcelUtil.CellType.TEXT, v));
            Optional.ofNullable(folderRel.getIssueInfosDTO().getVersionIssueRelDTOList().get(0).getName()).ifPresent(v -> ExcelUtil.createCell(row, 6, ExcelUtil.CellType.TEXT, v));
        }

        return columnNum + populateCycleCaseStep(sheet, columnNum, folderRel.getTestCaseStepDTOS(), rowStyles) + 1;
    }

    private int populateCycleCaseStep(Sheet sheet, int column, List<TestCaseStepDTO> caseSteps, CellStyle rowStyles) {
        if (caseSteps == null || caseSteps.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < caseSteps.size(); i++) {
            Row row;
            if (i == 0) {
                row = sheet.getRow(column);
            } else {
                row = ExcelUtil.createRow(sheet, column, rowStyles);
            }
            doPopulateCaseStep(row, caseSteps.get(i));
            column++;
        }

        return caseSteps.size() - 1;
    }

    private void doPopulateCaseStep(Row row, TestCaseStepDTO caseStep) {
        Optional.ofNullable(caseStep.getTestStep()).ifPresent(v -> ExcelUtil.createCell(row, 3, ExcelUtil.CellType.TEXT, v));
        Optional.ofNullable(caseStep.getTestData()).ifPresent(v -> ExcelUtil.createCell(row, 4, ExcelUtil.CellType.TEXT, v));
        Optional.ofNullable(caseStep.getExpectedResult()).ifPresent(v -> ExcelUtil.createCell(row, 5, ExcelUtil.CellType.TEXT, v));
    }

    private void prepareLookupData(TestIssueFolderDTO folder) throws NullPointerException {
        Long projectId = folder.getProjectId();

        List<LookupValueDTO> lookupValueDTOS = testCaseService.queryLookupValueByCode(projectId, "priority").getLookupValues();

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(0);
        pageRequest.setSize(999999999);
        pageRequest.setSort(new Sort(Sort.Direction.ASC, "componentId"));

        List<UserDTO> userDTOS = userService.list(pageRequest, projectId, null, null).getBody();
        List<ProductVersionDTO> productVersionDTOS = new ArrayList<>(testCaseService.getVersionInfo(projectId).values());

        TestIssueFolderE foldE = TestIssueFolderEFactory.create();
        foldE.setProjectId(projectId);
        foldE.setVersionId(Optional.ofNullable(folder.getVersionId()).orElseGet(null));
        foldE.setVersionId(Optional.ofNullable(folder.getVersionId()).orElseGet(null));

        List<TestIssueFolderDTO> testIssueFolderDTOS = ConvertHelper.convertList(foldE.queryAllUnderProject(), TestIssueFolderDTO.class);
        List<IssueLabelDTO> issueLabelDTOS = testCaseService.listIssueLabel(projectId);
        List<ComponentForListDTO> componentForListDTOS = testCaseService.listByProjectId(projectId, null, null, null, pageRequest);
        List<IssueStatusDTO> issueStatusDTOS = testCaseService.listStatusByProjectId(projectId);

        this.excelLookupCaseDTO = new ExcelLookupCaseDTO(lookupValueDTOS, userDTOS, productVersionDTOS, testIssueFolderDTOS,
                issueLabelDTOS, componentForListDTOS, issueStatusDTOS);
    }


    private int populateLookup(Sheet sheet, int columnNum,Class clazz, String name,Queue<CellStyle> rowStyles) {
        CellStyle style;
        if (ObjectUtils.isEmpty(rowStyles)) {
            style = null;
        } else {
            style = rowStyles.poll();
            rowStyles.offer(style);
        }

        Row row = ExcelUtil.createRow(sheet, columnNum, style);
        Row headRow = ExcelUtil.createRow(sheet, columnNum, style);

        ExcelUtil.createCell(headRow, 0, ExcelUtil.CellType.TEXT, name);
        int i = 0;
        for (Field field : clazz.getDeclaredFields()) {
            if (field != null) {
                ExcelUtil.createCell(row, i++, ExcelUtil.CellType.TEXT, field);
            }
        }

        return 2;
    }

    /**
     *
     * @param sheet
     * @param typeName  给需要lookup数据起的总名称
     * @param end-start lookup选项的长度
     */
    private void initNameMapping(Sheet sheet,String typeName, int start,int end) {
        Name name = sheet.getWorkbook().createName();
        name.setNameName(typeName);
        name.setRefersToFormula(sheet.getSheetName() + "!$A$"+ start +":$A$" + end);
    }

    /**
     * 生成下拉框
     * @param sheet
     * @param formulaString 期望那些值作为下拉选项
     * @param startRow
     * @param lastRow
     */
    private void setDataValidationByFormula(Sheet sheet,String formulaString, int startRow,int lastRow, int firstCol,int lastCol) {
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet)sheet);
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
                .createFormulaListConstraint(formulaString);
        CellRangeAddressList addressList = new CellRangeAddressList(startRow, lastRow, firstCol, lastCol);
        XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);

       sheet.addValidationData(validation);
    }

    private void setLookupData(Sheet sheet,int column,Queue<CellStyle> rowStyles){
        List<LookupValueDTO> lookupValueDTOS = excelLookupCaseDTO.getLookupValueDTOS();

        List<UserDTO> userDTOS = excelLookupCaseDTO.getUserDTOS();

        List<ProductVersionDTO> productVersionDTOS = excelLookupCaseDTO.getProductVersionDTOS();

        List<TestIssueFolderDTO> testIssueFolderDTOS = excelLookupCaseDTO.getTestIssueFolderDTOS();

        List<IssueLabelDTO> issueLabelDTOS = excelLookupCaseDTO.getIssueLabelDTOS();

        List<ComponentForListDTO>  componentForListDTOS = excelLookupCaseDTO.getComponentForListDTOS();

        List<IssueStatusDTO> issueStatusDTOS = excelLookupCaseDTO.getIssueStatusDTOS();


        for (LookupValueDTO v : lookupValueDTOS) {
            column += populateLookup(sheet,column,v.getClass(),"优先级",rowStyles);
        }
        for (ProductVersionDTO v : productVersionDTOS) {
            column += populateLookup(sheet,column,v.getClass(),"版本",rowStyles);
        }
        for (IssueLabelDTO v : issueLabelDTOS) {
            column += populateLookup(sheet,column,v.getClass(),"标签",rowStyles);
        }
        for (ComponentForListDTO v : componentForListDTOS) {
            column += populateLookup(sheet,column,v.getClass(),"模块",rowStyles);
        }
        for (IssueStatusDTO v : issueStatusDTOS) {
            column += populateLookup(sheet,column,v.getClass(),"状态",rowStyles);
        }
        for (TestIssueFolderDTO v : testIssueFolderDTOS) {
            column += populateLookup(sheet,column,v.getClass(),"文件夹",rowStyles);
        }
        for (UserDTO v : userDTOS) {
            column += populateLookup(sheet,column,v.getClass(),"经办人",rowStyles);
        }

        //设置映射
        initNameMapping(sheet,PRIORITIES,1,lookupValueDTOS.size());
        initNameMapping(sheet,VERSIONS,lookupValueDTOS.size(),productVersionDTOS.size());
        initNameMapping(sheet,LABELS,productVersionDTOS.size(),issueLabelDTOS.size());
        initNameMapping(sheet,COMPONENTS,issueLabelDTOS.size(),componentForListDTOS.size());
        initNameMapping(sheet,STATUS,componentForListDTOS.size(),issueStatusDTOS.size());
        initNameMapping(sheet,FOLDERS,issueStatusDTOS.size(),testIssueFolderDTOS.size());
        initNameMapping(sheet,USERS,testIssueFolderDTOS.size(),userDTOS.size());

        //设置下拉框的值
        setDataValidationByFormula(sheet,PRIORITIES,4,lookupValueDTOS.size(),3,3);
        setDataValidationByFormula(sheet,VERSIONS,lookupValueDTOS.size(),productVersionDTOS.size(),7,7);
        setDataValidationByFormula(sheet,LABELS,productVersionDTOS.size(),issueLabelDTOS.size(),6,6);
        setDataValidationByFormula(sheet,COMPONENTS,issueLabelDTOS.size(),componentForListDTOS.size(),8,8);
        setDataValidationByFormula(sheet,STATUS,componentForListDTOS.size(),issueStatusDTOS.size(),10,10);
        setDataValidationByFormula(sheet,FOLDERS,issueStatusDTOS.size(),testIssueFolderDTOS.size(),0,0);
        setDataValidationByFormula(sheet,USERS,testIssueFolderDTOS.size(),userDTOS.size(),5,5);
    }

}
