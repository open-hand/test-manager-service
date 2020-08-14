package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import feign.FeignException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.vo.agile.*;


import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.ExcelReadMeOptionVO;
import io.choerodon.test.manager.api.vo.TestFileLoadHistoryWithRateVO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.ExcelTitleName;
import io.choerodon.test.manager.infra.enums.TestAttachmentCode;
import io.choerodon.test.manager.infra.enums.TestFileLoadHistoryEnums;
import io.choerodon.test.manager.infra.feign.BaseFeignClient;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import io.choerodon.test.manager.infra.mapper.TestFileLoadHistoryMapper;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper;
import io.choerodon.test.manager.infra.util.ExcelTitleUtil;
import io.choerodon.test.manager.infra.util.ExcelUtil;
import io.choerodon.test.manager.infra.util.MultipartExcel;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.hzero.boot.file.FileClient;
import org.hzero.boot.message.MessageClient;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelImportServiceImpl.class);
    private static final String IMPORT_NOTIFY_CODE = "test-issue-import";
    private static final String IMPORT_ERROR = "test-issue-import-error";
    private static final String HIDDEN_USER = "hidden_user";
    private static final ExcelReadMeOptionVO[] README_OPTIONS = new ExcelReadMeOptionVO[5];
    private static final TestCaseStepDTO[] EXAMPLE_TEST_CASE_STEPS = new TestCaseStepDTO[3];
    private static final IssueCreateDTO[] EXAMPLE_ISSUES = new IssueCreateDTO[3];
    private static final String TYPE_CYCLE = "cycle";
    public static final int EXCEL_WIDTH_PX = 256;


    static {
        README_OPTIONS[0] = new ExcelReadMeOptionVO("用例概要*", true);
        README_OPTIONS[1] = new ExcelReadMeOptionVO("前置条件", false);
        //README_OPTIONS[2] = new ExcelReadMeOptionVO("优先级", false);
//        README_OPTIONS[2] = new ExcelReadMeOptionVO("被指定人", false);
        //README_OPTIONS[3] = new ExcelReadMeOptionVO("模块", false);
//        README_OPTIONS[3] = new ExcelReadMeOptionVO("关联的issue", false);
        README_OPTIONS[2] = new ExcelReadMeOptionVO("测试步骤", false);
        README_OPTIONS[3] = new ExcelReadMeOptionVO("测试数据", false);
        README_OPTIONS[4] = new ExcelReadMeOptionVO("预期结果", false);

        for (int i = 0; i < EXAMPLE_TEST_CASE_STEPS.length; i++) {
            EXAMPLE_TEST_CASE_STEPS[i] = new TestCaseStepDTO();
            EXAMPLE_TEST_CASE_STEPS[i].setTestStep("步骤" + (i + 1));
            EXAMPLE_TEST_CASE_STEPS[i].setTestData("数据" + (i + 1));
            EXAMPLE_TEST_CASE_STEPS[i].setExpectedResult("结果" + (i + 1));
        }

        for (int i = 0; i < EXAMPLE_ISSUES.length; i++) {
            EXAMPLE_ISSUES[i] = new IssueCreateDTO();
            EXAMPLE_ISSUES[i].setSummary("概要" + (i + 1));
            EXAMPLE_ISSUES[i].setDescription("前置条件" + (i + 1));
        }
    }

    @Autowired
    private ExcelService excelService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCaseStepService testCaseStepService;

//    @Autowired
//    private NotifyService notifyService;

    @Autowired
    private TestFileLoadHistoryMapper testFileLoadHistoryMapper;

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    private TestCaseFeignClient testCaseFeignClient;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseFeignClient baseFeignClient;

    @Autowired
    private FileClient fileClient;

    @Autowired
    private MessageClient messageClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean cancelFileUpload(Long historyId) {
        return testFileLoadHistoryMapper.cancelFileUpload(historyId) == 1;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void downloadImportTemp(HttpServletRequest request, HttpServletResponse response, Long organizationId, Long projectId) {
        ExcelUtil.setExcelHeaderByStream(request, response);
        excelService.downloadWorkBookByStream(buildImportTemp(organizationId, projectId), response);
    }

    @Async
    @Override
    public void importIssueByExcel(Long projectId, Long folderId, Long userId, Workbook issuesWorkbook) {
        ProjectDTO projectDTO = baseFeignClient.queryProject(projectId).getBody();
        // 默认是导入到导入文件夹，不存在则创建
        Sheet testCasesSheet = issuesWorkbook.getSheet("测试用例");
        TestFileLoadHistoryDTO testFileLoadHistoryDTO = initLoadHistory(projectId, folderId, userId);
        TestFileLoadHistoryEnums.Status status = TestFileLoadHistoryEnums.Status.SUCCESS;
        List<Long> issueIds = new ArrayList<>();

        if(ObjectUtils.isEmpty(testCasesSheet) || isOldExcel(issuesWorkbook, ExcelTitleName.EXCEL_HEADERS)){
            logger.info("错误的模板文件");
            // 更新创建历史记录
            testFileLoadHistoryDTO.setMessage("错误的模板文件");
            finishImport(testFileLoadHistoryDTO, userId, TestFileLoadHistoryEnums.Status.FAILURE);
            return;
        }
        //测试用例页为空，则更新文件导入历史之后直接返回
        if (isEmptyTemp(testCasesSheet)) {
            logger.info("空模板");
            // 更新创建历史记录
            testFileLoadHistoryDTO.setMessage("空模板");
            finishImport(testFileLoadHistoryDTO, userId, TestFileLoadHistoryEnums.Status.FAILURE);
            return;
        }
        Iterator<Row> rowIterator = rowIteratorSkipFirst(testCasesSheet);
        Map<String, Integer> headerLocationMap = getHeaderLocationMap(testCasesSheet);
        ExcelTitleUtil excelTitleUtil = new ExcelTitleUtil(headerLocationMap);
        double nonBlankRowCount = (testCasesSheet.getPhysicalNumberOfRows() - 1) / 95.;
        double progress = 0.;
        long successfulCount = 0L;
        long failedCount = 0L;
        List<Integer> errorRowIndexes = new ArrayList<>();
        //IssueDTO issueDTO = null;
        TestCaseDTO testCaseDTO = null;
        Row currentRow;
        logger.info("开始导入");
        //更新文件和用例的关联表
        while (rowIterator.hasNext()) {
            currentRow = rowIterator.next();
            if (Objects.equals(TestFileLoadHistoryEnums.Status.valueOf(testFileLoadHistoryMapper
                        .queryLoadHistoryStatus(testFileLoadHistoryDTO.getId())), TestFileLoadHistoryEnums.Status.CANCEL)) {
                    status = TestFileLoadHistoryEnums.Status.CANCEL;
                    logger.info("已取消");
                removeRow(currentRow);
                    if (!issueIds.isEmpty()) {
                        testCaseService.batchDeleteIssues(projectId, issueIds);
                    }
                    break;
                }

            if (isIssueHeaderRow(currentRow, excelTitleUtil)) {
                    //插入用例
                testCaseDTO = processIssueHeaderRow(currentRow, projectId, folderId, excelTitleUtil);
                    if (testCaseDTO == null) {
                        failedCount++;
                    } else {
                        successfulCount++;
                        issueIds.add(testCaseDTO.getCaseId());
                    }
                }
                //processRow(issueDTO, currentRow, errorRowIndexes, excelTitleUtil);
                // 插入循环步骤
            processRow(testCaseDTO, currentRow, errorRowIndexes ,excelTitleUtil);
                updateProgress(testFileLoadHistoryDTO, userId, ++progress / nonBlankRowCount);
            }

        testFileLoadHistoryDTO.setSuccessfulCount(successfulCount);
        testFileLoadHistoryDTO.setFailedCount(failedCount);

        if (!errorRowIndexes.isEmpty() && status != TestFileLoadHistoryEnums.Status.CANCEL) {
            logger.info("导入数据有误，上传 error workbook");
            shiftErrorRowsToTop(testCasesSheet, errorRowIndexes);
            status = checkoutStatus(uploadErrorWorkbook(projectDTO.getOrganizationId(),issuesWorkbook, testFileLoadHistoryDTO), status);
        }

        finishImport(testFileLoadHistoryDTO, userId, status);
    }

    private boolean isOldExcel(Workbook workbook, String[] headers) {
        //判断是否为旧模版
        Sheet sheet = workbook.getSheetAt(1);
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            return true;
        }
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            Cell cell = headerRow.getCell(i);
            if (isCellEmpty(cell)) {
                return true;
            }
            if (!header.equals(cell.toString())) {
                return true;
            }
        }
        return false;
    }

    protected boolean isCellEmpty(Cell cell) {
        return cell == null || cell.toString().equals("") || cell.getCellType() == XSSFCell.CELL_TYPE_BLANK;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Workbook buildImportTemp(Long organizationId, Long projectId) {

        Workbook importTemp = ExcelUtil.getWorkBook(ExcelUtil.Mode.XSSF);
        List<UserDTO> userDTOS = userService.list(new PageRequest(0, 99999), projectId, null, null).getBody().getContent();

        List<String> userNameList = new ArrayList<>();
        for (UserDTO userDTO : userDTOS) {
            userNameList.add(userDTO.getLoginName() + userDTO.getRealName());
        }

        addReadMeSheet(importTemp);
        addTestCaseSheet(importTemp, userNameList);

        return importTemp;
    }

    private void addReadMeSheet(Workbook workbook) {
        Sheet readMeSheet = workbook.createSheet("README");
        workbook.setSheetOrder("README", 0);

        fillReadMeSheet(readMeSheet);
        setReadMeSheetStyle(readMeSheet);
    }

    private void addTestCaseSheet(Workbook workbook, List<String> userNameList) {
        Sheet testCaseSheet = workbook.createSheet("测试用例");
        workbook.setSheetOrder("测试用例", 1);

        fillTestCaseSheet(testCaseSheet);
        setTestCaseSheetStyle(testCaseSheet);

//        ExcelUtil.dropDownList2007(workbook, testCaseSheet, userNameList, 1, 500, 2, 2, HIDDEN_USER, 2);
    }

    // 填充测试用例页内容
    private void fillTestCaseSheet(Sheet testCaseSheet) {
        writeHeader(testCaseSheet, 0, 0);
    }

    private void writeHeader(Sheet sheet, int rowNum, int colNum) {
        Row header = ExcelUtil.getOrCreateRow(sheet, rowNum);
        for (int i = 0; i < README_OPTIONS.length; i++) {
            Cell cell = header.createCell(i + colNum, CELL_TYPE_STRING);
            cell.setCellValue(README_OPTIONS[i].getFiled());
        }
    }

    // 设置 README 页样式
    private void setReadMeSheetStyle(Sheet readMeSheet) {
        setSheetBaseStyle(readMeSheet);
    }

    // 设置通用的单元格样式
    private void setSheetBaseStyle(Sheet sheet) {
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        sheet.setDefaultColumnWidth(16);
        for (Row row : sheet) {
            for (Cell cell : row) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    // 设置测试用例页样式
    private void setTestCaseSheetStyle(Sheet testCaseSheet) {
        setSheetBaseStyle(testCaseSheet);
        // 设置列宽度
        testCaseSheet.setColumnWidth(0,EXCEL_WIDTH_PX * 48);
        testCaseSheet.setColumnWidth(1,EXCEL_WIDTH_PX * 32);
        testCaseSheet.setColumnWidth(2,EXCEL_WIDTH_PX * 48);
        testCaseSheet.setColumnWidth(3,EXCEL_WIDTH_PX * 16);
        testCaseSheet.setColumnWidth(4,EXCEL_WIDTH_PX * 48);

        CellStyle cellStyle = testCaseSheet.getWorkbook().createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        Font font = testCaseSheet.getWorkbook().createFont();
        font.setColor(Font.COLOR_RED);
        font.setBold(true);
        cellStyle.setFont(font);

        testCaseSheet.setDefaultColumnStyle(README_OPTIONS.length, cellStyle);
    }

    // 填充 README 页内容
    private void fillReadMeSheet(Sheet readMeSheet) {
        readMeSheet.createRow(0).createCell(0, CELL_TYPE_STRING).setCellValue("字段是否为必填项");
        readMeSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, README_OPTIONS.length));

        readMeSheet.getRow(0).createCell(README_OPTIONS.length + 1).setCellValue("请至下一页，填写信息");
        readMeSheet.addMergedRegion(new CellRangeAddress(0, 7, README_OPTIONS.length + 1, README_OPTIONS.length + 3));

        readMeSheet.createRow(1).createCell(0, CELL_TYPE_STRING).setCellValue("是否必填/字段");
        readMeSheet.createRow(2).createCell(0, CELL_TYPE_STRING).setCellValue("必填项");
        readMeSheet.createRow(3).createCell(0, CELL_TYPE_STRING).setCellValue("选填项");

        int i = 0;
        while (i < README_OPTIONS.length) {
            ExcelReadMeOptionVO optionDTO = README_OPTIONS[i++];
            readMeSheet.getRow(1).createCell(i, CELL_TYPE_STRING).setCellValue(optionDTO.getFiled());
            readMeSheet.getRow(optionDTO.getRequired() ? 2 : 3).createCell(i, CELL_TYPE_STRING).setCellValue("√");
        }

        writeHeader(readMeSheet, 0, 0);

        readMeSheet.createRow(8).createCell(1, CELL_TYPE_STRING).setCellValue("示例");
        readMeSheet.addMergedRegion(new CellRangeAddress(8, 8, 1, 9));
        writeExample(readMeSheet, 9, 1, EXAMPLE_ISSUES[0], EXAMPLE_TEST_CASE_STEPS);
        writeExample(readMeSheet, 10, 1, EXAMPLE_ISSUES[1], EXAMPLE_TEST_CASE_STEPS[0]);
        writeExample(readMeSheet, 11, 1, EXAMPLE_ISSUES[2],
                EXAMPLE_TEST_CASE_STEPS[0],
                EXAMPLE_TEST_CASE_STEPS[1]
        );
    }

    private void writeExample(Sheet sheet, int rowNum, int colNum, IssueCreateDTO issueCreateDTO, TestCaseStepDTO... steps) {
        Row row = ExcelUtil.getOrCreateRow(sheet, rowNum);
        row.createCell(colNum, CELL_TYPE_STRING).setCellValue(issueCreateDTO.getSummary());
        row.createCell(colNum + 1, CELL_TYPE_STRING).setCellValue(issueCreateDTO.getDescription());
//        row.createCell(colNum + 2, CELL_TYPE_STRING).setCellValue("1234张三");
        //row.createCell(colNum + 3, CELL_TYPE_STRING).setCellValue("测试模块");
//        row.createCell(colNum + 2, CELL_TYPE_STRING).setCellValue("XX-111");

        for (int i = 0; i < steps.length; i++) {
            row = ExcelUtil.getOrCreateRow(sheet, i + rowNum);
            row.createCell(colNum + 2, CELL_TYPE_STRING).setCellValue(steps[i].getTestStep());
            row.createCell(colNum + 3, CELL_TYPE_STRING).setCellValue(steps[i].getTestData());
            row.createCell(colNum + 4, CELL_TYPE_STRING).setCellValue(steps[i].getExpectedResult());
        }
    }

    @Transactional
    public TestIssueFolderDTO getFolder(Long projectId, Long versionId, String folderName) {
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        testIssueFolderDTO.setProjectId(projectId);
        testIssueFolderDTO.setName(folderName);

        TestIssueFolderDTO targetTestIssueFolderDTO = testIssueFolderMapper.selectOne(testIssueFolderDTO);
        if (targetTestIssueFolderDTO == null) {
            testIssueFolderDTO.setType(TYPE_CYCLE);
            logger.info("{} 文件夹不存在，创建", folderName);

            if (testIssueFolderDTO.getFolderId() != null) {
                throw new CommonException("error.issue.folder.insert.folderId.should.be.null");
            }
            testIssueFolderMapper.insert(testIssueFolderDTO);

            return testIssueFolderDTO;
        }

        logger.info("{} 文件夹已存在", folderName);
        return targetTestIssueFolderDTO;
    }

    private TestFileLoadHistoryDTO initLoadHistory(Long projectId, Long folderId, Long userId) {
        TestFileLoadHistoryDTO testFileLoadHistoryDTO = new TestFileLoadHistoryDTO();
        testFileLoadHistoryDTO.setProjectId(projectId);
        testFileLoadHistoryDTO.setActionType(TestFileLoadHistoryEnums.Action.UPLOAD_CASE.getTypeValue());
        testFileLoadHistoryDTO.setSourceType(TestFileLoadHistoryEnums.Source.FOLDER.getTypeValue());
        testFileLoadHistoryDTO.setLinkedId(folderId);
        testFileLoadHistoryDTO.setStatus(TestFileLoadHistoryEnums.Status.SUSPENDING.getTypeValue());
        testFileLoadHistoryDTO.setCreationDate(new Date());
        testFileLoadHistoryDTO.setSuccessfulCount(0L);
        testFileLoadHistoryDTO.setFailedCount(0L);
        testFileLoadHistoryDTO.setCreatedBy(userId);

        testFileLoadHistoryMapper.insert(testFileLoadHistoryDTO);

        return testFileLoadHistoryMapper.selectByPrimaryKey(testFileLoadHistoryDTO);
    }

    private boolean isEmptyTemp(Sheet sheet) {
        Iterator<Row> iterator = sheet.rowIterator();
        if (!iterator.hasNext()) {
            return true;
        }

        iterator.next();
        while (iterator.hasNext()) {
            if (!isEmptyRow(iterator.next())) {
                return false;
            }
        }
        return true;
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }

        for (int i = 0; i < README_OPTIONS.length; i++) {
            if (!ExcelUtil.isBlank(row.getCell(i))) {
                return false;
            }
        }

        return true;
    }

    private void finishImport(TestFileLoadHistoryDTO testFileLoadHistoryDTO, Long userId, TestFileLoadHistoryEnums.Status status) {
        testFileLoadHistoryDTO.setLastUpdateDate(new Date());
        testFileLoadHistoryDTO.setStatus(status.getTypeValue());
        if(testFileLoadHistoryMapper.updateByPrimaryKey(testFileLoadHistoryDTO)!=1){
            throw new CommonException("error.update.file.history");
        }

        updateProgress(testFileLoadHistoryDTO, userId, 100.0);
    }


    private Iterator<Row> rowIteratorSkipFirst(Sheet sheet) {
        Iterator<Row> rowIterator = sheet.rowIterator();
        Row headerRow = rowIterator.next();
        int x = 0;
        while (!ExcelUtil.getStringValue(headerRow.getCell(0)).equals(ExcelTitleName.FOLDER)
                && !ExcelUtil.getStringValue(headerRow.getCell(0)).equals(ExcelTitleName.CASE_SUMMARY)) {
            if (rowIterator.hasNext()) {
                headerRow = rowIterator.next();
            }
            x++;
            if (x > 100) {
                throw new CommonException("error.rowIteratorSkipFirst.notFoundHeader");
            }
        }
        return rowIterator;
    }

    private Map<String, Integer> getHeaderLocationMap(Sheet sheet) {
        Iterator<Row> rowIterator = sheet.rowIterator();
        Row headerRow = rowIterator.next();
        int x = 0;
        while (!ExcelUtil.getStringValue(headerRow.getCell(0)).equals(ExcelTitleName.FOLDER)
                && !ExcelUtil.getStringValue(headerRow.getCell(0)).equals(ExcelTitleName.CASE_SUMMARY)) {
            if (rowIterator.hasNext()) {
                headerRow = rowIterator.next();
            }
            x++;
            if (x > 100) {
                throw new CommonException("error.rowIteratorSkipFirst.notFoundHeader");
            }
        }
        Map<String, Integer> headerLocationMap = new HashMap<>();
        int i = 0;
        String titleName = ExcelUtil.getStringValue(headerRow.getCell(i));
        while (!titleName.equals("")) {
            headerLocationMap.put(titleName, i);
            i++;
            titleName = ExcelUtil.getStringValue(headerRow.getCell(i));
        }
        return headerLocationMap;
    }

    private void removeRow(Row row) {
        for (int i = 0; i <= README_OPTIONS.length; i++) {
            if (row.getCell(i) != null) {
                row.removeCell(row.getCell(i));
            }
        }
    }

    private boolean isIssueHeaderRow(Row row, ExcelTitleUtil excelTitleUtil) {
        if (row.getRowNum() == 0) {
            return false;
        }
        if (row.getRowNum() == 1) {
            return true;
        }

        String summary = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.CASE_SUMMARY, row));
        String description = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.CASE_DESCRIPTION, row));
        String user = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.ASSIGNER, row));
        String issueLink = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.LINK_ISSUE, row));
        return StringUtils.isNotBlank(summary) || StringUtils.isNotBlank(description)
                || StringUtils.isNotBlank(user) || StringUtils.isNotBlank(issueLink);
    }

    private TestCaseDTO processIssueHeaderRow(Row row, Long projectId, Long folderId, ExcelTitleUtil excelTitleUtil) {
        if (ExcelUtil.isBlank(excelTitleUtil.getCell(ExcelTitleName.CASE_SUMMARY, row))) {
            markAsError(row, "测试概要不能为空");
            return null;
        }

        String description = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.CASE_DESCRIPTION, row));
        String summary = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.CASE_SUMMARY, row));

        IssueCreateDTO issueCreateDTO = new IssueCreateDTO();
        issueCreateDTO.setProjectId(projectId);
        issueCreateDTO.setSummary(summary);
        issueCreateDTO.setDescription(description);
        issueCreateDTO.setFolderId(folderId);

        if (!ExcelUtil.isBlank(excelTitleUtil.getCell(ExcelTitleName.LINK_ISSUE, row))) {
            String issueNumString = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.LINK_ISSUE, row));
            if (issueNumString.contains("-")) {
                issueNumString = issueNumString.split("-")[1];
            }
            IssueNumDTO issueNumDTO;

            try {
                issueNumDTO = testCaseFeignClient.queryIssueByIssueNum(projectId, issueNumString).getBody();
            } catch (FeignException e) {
                markAsError(row, "关联问题编号有误，仅支持关联故事、任务、缺陷类型，请检查录入的关联问题编号。");
                return null;
            }

            // Todo:重构
            // 查询所有的linktype
            List<IssueLinkTypeDTO> issueLinkTypeDTOList = testCaseFeignClient.listIssueLinkType(projectId, null,
                    new IssueLinkTypeSearchDTO()).getBody().getContent();

            TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
            testCaseLinkDTO.setIssueId(issueNumDTO.getIssueId());
            testCaseLinkDTO.setLinkTypeId(issueLinkTypeDTOList.get(0).getLinkTypeId());
            List<TestCaseLinkDTO> testCaseLinkDTOList = new ArrayList<>();
            testCaseLinkDTOList.add(testCaseLinkDTO);
            issueCreateDTO.setTestCaseLinkDTOList(testCaseLinkDTOList);

        }
        TestCaseDTO testCaseDTO = testCaseService.importTestCase(issueCreateDTO, projectId, "test");
        return testCaseDTO;
    }

    private void processRow(TestCaseDTO testCaseDTO, Row row, List<Integer> errorRowIndexes, ExcelTitleUtil excelTitleUtil) {
        if (testCaseDTO == null) {
            errorRowIndexes.add(row.getRowNum());
            return;
        }

        TestCaseStepProDTO testCaseStepProDTO = buildTestCaseStepDTO(testCaseDTO.getCaseId(), row, excelTitleUtil);
        if (testCaseStepProDTO != null) {
            testCaseStepService.createOneStep(testCaseStepProDTO);
        }

        removeRow(row);
    }

    private void updateProgress(TestFileLoadHistoryDTO testFileLoadHistoryDTO, Long userId, double rate) {
        TestFileLoadHistoryWithRateVO testFileLoadHistoryWithRateVO = modelMapper
                .map(testFileLoadHistoryDTO, TestFileLoadHistoryWithRateVO.class);
        testFileLoadHistoryWithRateVO.setRate(rate);
        if(TestFileLoadHistoryEnums.Status.FAILURE.getTypeValue().equals(testFileLoadHistoryWithRateVO.getStatus())){
            testFileLoadHistoryWithRateVO.setCode(IMPORT_ERROR);
            //notifyService.postWebSocket(IMPORT_NOTIFY_CODE, userId.toString(), JSON.toJSONString(testFileLoadHistoryWithRateVO));
            messageClient.sendByUserId(userId,IMPORT_NOTIFY_CODE,JSON.toJSONString(testFileLoadHistoryWithRateVO));
        }else {
            //notifyService.postWebSocket(IMPORT_NOTIFY_CODE, userId.toString(), JSON.toJSONString(testFileLoadHistoryWithRateVO));
            messageClient.sendByUserId(userId,IMPORT_NOTIFY_CODE,JSON.toJSONString(testFileLoadHistoryWithRateVO));
        }

        logger.info("导入进度：{}", rate);
        if (rate == 100.) {
            logger.info("完成");
        }
    }

    private void shiftErrorRowsToTop(Sheet sheet, List<Integer> errorRowIndexes) {
        int i = 0;
        while (i < errorRowIndexes.size()) {
            shiftRow(sheet, errorRowIndexes.get(i), ++i);
        }
    }

    private String uploadErrorWorkbook(Long organizationId,Workbook errorWorkbook, TestFileLoadHistoryDTO testFileLoadHistoryDTO) {
        String url = fileClient.uploadFile(organizationId,TestAttachmentCode.ATTACHMENT_BUCKET, null,
                new MultipartExcel("file", ".xlsx", errorWorkbook));

        boolean failed = false;
        if (url.startsWith("{")) {
            JSONObject jsonObject = JSON.parseObject(url);
            failed = jsonObject.containsKey("failed") && jsonObject.getBooleanValue("failed");
        }
        if (!failed) {
            testFileLoadHistoryDTO.setFileUrl(url);
            logger.debug(testFileLoadHistoryDTO.getFileUrl());
            return url;
        } else {
            testFileLoadHistoryDTO.setFileStream(ExcelUtil.getBytes(errorWorkbook));
            return null;
        }
    }

    private void markAsError(Row row, String errorMsg) {
        ExcelUtil.getOrCreateCell(row, README_OPTIONS.length, CELL_TYPE_STRING).setCellValue(errorMsg);

        logger.info("行 {} 发生错误：{}", row.getRowNum() + 1, errorMsg);
    }

    private TestCaseStepProDTO buildTestCaseStepDTO(Long issueId, Row row, ExcelTitleUtil excelTitleUtil) {
        String testStep = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.TEST_STEP, row));
        String testData = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.TEST_DATA, row));
        String expectedResult = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.EXPECT_RESULT, row));

        TestCaseStepProDTO testCaseStepDTO = null;
        if (StringUtils.isNotBlank(testStep) || StringUtils.isNotBlank(testData) || StringUtils.isNotBlank(expectedResult)) {
            testCaseStepDTO = new TestCaseStepProDTO();
            testCaseStepDTO.setTestStep(testStep);
            testCaseStepDTO.setTestData(testData);
            testCaseStepDTO.setExpectedResult(expectedResult);
            testCaseStepDTO.setIssueId(issueId);
        }

        return testCaseStepDTO;
    }

    private void shiftRow(Sheet sheet, int from, int to) {
        Row fromRow = sheet.getRow(from);
        Row toRow = ExcelUtil.getOrCreateRow(sheet, to);
        Cell fromCell;
        Cell toCell;
        for (int i = 0; i <= README_OPTIONS.length; i++) {
            fromCell = fromRow.getCell(i);
            if (fromCell != null) {
                fromRow.removeCell(fromCell);
                toCell = ExcelUtil.getOrCreateCell(toRow, i, CELL_TYPE_STRING);
                toCell.setCellValue(ExcelUtil.getStringValue(fromCell));
            }
        }
        toCell = toRow.getCell(README_OPTIONS.length);
        if (!ExcelUtil.isBlank(toCell)) {
            toCell.setCellStyle(sheet.getColumnStyle(README_OPTIONS.length));
        }
    }

    private TestFileLoadHistoryEnums.Status checkoutStatus(String uploadError, TestFileLoadHistoryEnums.Status status) {
        if (uploadError == null) {
            status = TestFileLoadHistoryEnums.Status.FAILURE;
        }
        return status;
    }

}
