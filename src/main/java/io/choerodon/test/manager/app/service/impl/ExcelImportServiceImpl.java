package io.choerodon.test.manager.app.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.choerodon.core.client.MessageClientC7n;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.vo.ExcelReadMeOptionVO;
import io.choerodon.test.manager.api.vo.TestFileLoadHistoryWebsocketVO;
import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
import io.choerodon.test.manager.api.vo.agile.IssueCreateDTO;
import io.choerodon.test.manager.api.vo.agile.IssueNumDTO;
import io.choerodon.test.manager.api.vo.agile.ProjectDTO;
import io.choerodon.test.manager.api.vo.agile.UserDTO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.constant.ExcelSheetConstants;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.ExcelTitleName;
import io.choerodon.test.manager.infra.enums.TestCycleType;
import io.choerodon.test.manager.infra.enums.TestFileLoadHistoryEnums;
import io.choerodon.test.manager.infra.feign.operator.AgileClientOperator;
import io.choerodon.test.manager.infra.feign.operator.RemoteIamOperator;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hzero.boot.file.FileClient;
import org.hzero.core.base.BaseConstants;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.EncryptType;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import static io.choerodon.test.manager.infra.enums.ExcelTitleName.EXCEL_HEADERS;

@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelImportServiceImpl.class);
    private static final String IMPORT_NOTIFY_CODE = "test-issue-import";
    private static final String IMPORT_ERROR = "test-issue-import-error";
    private static final String HIDDEN_PRIORITY = "hidden_priority";
    private static final ExcelReadMeOptionVO[] README_OPTIONS = new ExcelReadMeOptionVO[10];
    private static final TestCaseStepDTO[] EXAMPLE_TEST_CASE_STEPS = new TestCaseStepDTO[3];
    private static final IssueCreateDTO[] EXAMPLE_ISSUES = new IssueCreateDTO[2];
    public static final int EXCEL_WIDTH_PX = 256;
    private static final int SUMMARY_MAX_SIZE = 44;
    private static final String REDIS_STATUS_KEY = "test:fileStatus:";
    private static final int[] README_SHEET_COLUMN_WIDTH = {3000, 12000, 3500, 3500, 7000, 3500, 3500, 8000, 5000, 5000, 5000};

    static {
        README_OPTIONS[0] = new ExcelReadMeOptionVO(ExcelTitleName.FOLDER_PATH, true, "必填项，“目录”请填写完整路径，用“/”分隔。若您输入的目录在系统中不存在，则自动新增");
        README_OPTIONS[1] = new ExcelReadMeOptionVO(ExcelTitleName.CASE_NUM, false, "如果更新已有用例，编号必填；如果新增用例，则无需填写");
        README_OPTIONS[2] = new ExcelReadMeOptionVO(ExcelTitleName.CUSTOM_NUM, false, "非必填项");
        README_OPTIONS[3] = new ExcelReadMeOptionVO(ExcelTitleName.CASE_SUMMARY, true, "必填项，限制44个字符以内");
        README_OPTIONS[4] = new ExcelReadMeOptionVO(ExcelTitleName.PRIORITY, true, "必填项");
        README_OPTIONS[5] = new ExcelReadMeOptionVO(ExcelTitleName.LINK_ISSUE, false, "直接输入工作项编号即可；支持关联多个, 需要单元格内换行");
        README_OPTIONS[6] = new ExcelReadMeOptionVO(ExcelTitleName.CASE_DESCRIPTION, false, "非必填项");
        README_OPTIONS[7] = new ExcelReadMeOptionVO(ExcelTitleName.TEST_STEP, false, "非必填项");
        README_OPTIONS[8] = new ExcelReadMeOptionVO(ExcelTitleName.TEST_DATA, false, "非必填项");
        README_OPTIONS[9] = new ExcelReadMeOptionVO(ExcelTitleName.EXPECT_RESULT, false, "非必填项");

        for (int i = 0; i < EXAMPLE_TEST_CASE_STEPS.length; i++) {
            EXAMPLE_TEST_CASE_STEPS[i] = new TestCaseStepDTO();
            EXAMPLE_TEST_CASE_STEPS[i].setTestStep("请填写测试步骤" + (i + 1));
            EXAMPLE_TEST_CASE_STEPS[i].setTestData("请填写测试数据" + (i + 1));
            EXAMPLE_TEST_CASE_STEPS[i].setExpectedResult("请填写预期结果" + (i + 1));
        }
        EXAMPLE_ISSUES[0] = new IssueCreateDTO();
        EXAMPLE_ISSUES[0].setFolderPath("敏捷管理/协作/任务看板");
        EXAMPLE_ISSUES[0].setCaseNum("C7N-100");
        EXAMPLE_ISSUES[0].setCustomNum("Scrum-245");
        EXAMPLE_ISSUES[0].setSummary("这里是用例C7N-100的概要");
        EXAMPLE_ISSUES[0].setPriorityCode("高");
        EXAMPLE_ISSUES[0].setRelateIssueNums("issue-1;\nissue-2;");
        EXAMPLE_ISSUES[0].setDescription("请填写前置条件信息-导入更新");

        EXAMPLE_ISSUES[1] = new IssueCreateDTO();
        EXAMPLE_ISSUES[1].setFolderPath("敏捷管理/协作/工作列表");
        EXAMPLE_ISSUES[1].setCustomNum("Scrum-123");
        EXAMPLE_ISSUES[1].setSummary("这里是用例C7N-99的概要");
        EXAMPLE_ISSUES[1].setPriorityCode("中");
        EXAMPLE_ISSUES[1].setRelateIssueNums("issue-3;\nissue-4;");
        EXAMPLE_ISSUES[1].setDescription("请填写前置条件信息-导入新增");
    }

    @Autowired
    private ExcelService excelService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCaseStepService testCaseStepService;

    @Autowired
    private TestFileLoadHistoryMapper testFileLoadHistoryMapper;

    @Autowired
    private TestIssueFolderService testIssueFolderService;
    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    private AgileClientOperator agileClientOperator;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RemoteIamOperator remoteIamOperator;

    @Autowired
    private FileClient fileClient;

    @Autowired
    private MessageClientC7n messageClientC7n;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestPriorityMapper testPriorityMapper;
    @Autowired
    private TestProjectInfoMapper testProjectInfoMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TestCaseMapper testCaseMapper;
    @Autowired
    private TestCaseLinkService testCaseLinkService;
    @Autowired
    private FilePathService filePathService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean cancelFileUpload(Long historyId) {
        redisUtil.delete(REDIS_STATUS_KEY + historyId);
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
    public void importIssueByExcel(Long projectId, Long userId, InputStream inputStream,
                                   EncryptType encryptType, RequestAttributes requestAttributes) {
        TestFileLoadHistoryDTO testFileLoadHistoryDTO = initLoadHistory(projectId, 0L, userId);
        Workbook issuesWorkbook;
        try {
            issuesWorkbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            throw new CommonException("error.io.new.workbook", e);
        }
        // 添加加密信息上下文
        EncryptContext.setEncryptType(encryptType.name());
        RequestContextHolder.setRequestAttributes(requestAttributes);
        ProjectDTO projectDTO = remoteIamOperator.getProjectById(projectId);
        TestProjectInfoDTO testProjectInfoDTO = new TestProjectInfoDTO();
        testProjectInfoDTO.setProjectId(projectId);
        TestProjectInfoDTO testProjectInfo = testProjectInfoMapper.selectOne(testProjectInfoDTO);
        // 默认是导入到导入文件夹，不存在则创建
        Sheet testCasesSheet = issuesWorkbook.getSheet(ExcelSheetConstants.TEST_CASE_SHEET_NAME);
        TestFileLoadHistoryEnums.Status status = TestFileLoadHistoryEnums.Status.SUCCESS;
        TestPriorityDTO priorityDTO = new TestPriorityDTO();
        priorityDTO.setOrganizationId(ConvertUtils.getOrganizationId(projectId));
        Map<String, Long> priorityMap = testPriorityMapper.select(priorityDTO)
                .stream().collect(Collectors.toMap(TestPriorityDTO::getName, TestPriorityDTO::getId));

        if (ObjectUtils.isEmpty(testCasesSheet) || isOldExcel(issuesWorkbook, EXCEL_HEADERS)) {
            logger.info("错误的模板文件");
            // 更新创建历史记录
            testFileLoadHistoryDTO.setMessage("错误的模板文件");
            finishImport(testFileLoadHistoryDTO, userId, TestFileLoadHistoryEnums.Status.FAILURE);
            return;
        }
        double nonBlankRowCount = getRealRowCount(testCasesSheet, EXCEL_HEADERS.length);
        //测试用例页为空，则更新文件导入历史之后直接返回
        if (nonBlankRowCount == 0) {
            logger.info("空模板");
            // 更新创建历史记录
            testFileLoadHistoryDTO.setMessage("空模板");
            finishImport(testFileLoadHistoryDTO, userId, TestFileLoadHistoryEnums.Status.FAILURE);
            return;
        }
        Map<String, Integer> headerLocationMap = new HashMap<>(EXCEL_HEADERS.length);
        Iterator<Row> rowIterator = rowIteratorSkipFirst(testCasesSheet, headerLocationMap);
        ExcelTitleUtil excelTitleUtil = new ExcelTitleUtil(headerLocationMap);
        double progress = 0.;
        double lastRate = 0.;
        long successfulCount = 0L;
        long failedCount = 0L;
        List<Integer> errorRowIndexes = new ArrayList<>();
        //IssueDTO issueDTO = null;
        IssueCreateDTO issueCreateDTO = null;
        Row currentRow;
        logger.info("开始导入");
        //更新文件和用例的关联表
        List<IssueCreateDTO> issueCreateDTOList = new ArrayList<>();
        List<IssueCreateDTO> issueUpdateDTOList = new ArrayList<>();
        List<Long> issueUpdateIds = new ArrayList<>();
        while (rowIterator.hasNext()) {
            currentRow = rowIterator.next();
            if (isSkip(currentRow, EXCEL_HEADERS.length)) {
                // 如果当前行全部为空，则跳过
                continue;
            }

            if (isIssueHeaderRow(currentRow, excelTitleUtil)) {
                boolean isUpdate = !ExcelUtil.isBlank(excelTitleUtil.getCell(ExcelTitleName.CASE_NUM, currentRow));
                //处理用例
                issueCreateDTO = processIssueHeaderRow(currentRow, projectId, excelTitleUtil, priorityMap, isUpdate, testProjectInfo);

                if (issueCreateDTO == null) {
                    failedCount++;
                } else {
                    successfulCount++;
                    addIssueList(issueCreateDTO, issueCreateDTOList, issueUpdateDTOList, isUpdate, issueUpdateIds);
                }
                lastRate = updateProgress(testFileLoadHistoryDTO, userId, progress / nonBlankRowCount * 100, lastRate);
            }
            // 插入循环步骤
            processRow(issueCreateDTO, currentRow, errorRowIndexes, excelTitleUtil);
            if (issueCreateDTOList.size() >= 100) {
                insertCase(issueCreateDTOList, testProjectInfo);
            }
            if (issueUpdateDTOList.size() >= 100) {
                updateCase(projectId, issueUpdateDTOList, testProjectInfo, issueUpdateIds);
            }
            ++progress;
        }
        if (!CollectionUtils.isEmpty(issueCreateDTOList)) {
            insertCase(issueCreateDTOList, testProjectInfo);
        }
        if (!CollectionUtils.isEmpty(issueUpdateDTOList)) {
            updateCase(projectId, issueUpdateDTOList, testProjectInfo, issueUpdateIds);
        }
        testProjectInfoMapper.updateByPrimaryKeySelective(testProjectInfo);
        testFileLoadHistoryDTO.setSuccessfulCount(successfulCount);
        testFileLoadHistoryDTO.setFailedCount(failedCount);

        if (!errorRowIndexes.isEmpty()) {
            logger.info("导入数据有误，上传 error workbook");
            shiftErrorRowsToTop(testCasesSheet, errorRowIndexes);
            status = checkoutStatus(uploadErrorWorkbook(projectDTO.getOrganizationId(), issuesWorkbook, testFileLoadHistoryDTO), status);
        }

        finishImport(testFileLoadHistoryDTO, userId, status);
    }

    private void addIssueList(IssueCreateDTO issueCreateDTO,
                              List<IssueCreateDTO> issueCreateDTOList,
                              List<IssueCreateDTO> issueUpdateDTOList,
                              boolean isUpdate,
                              List<Long> issueUpdateIds) {
        if (Boolean.TRUE.equals(isUpdate)) {
            if (issueUpdateIds.contains(issueCreateDTO.getCaseId())) {
                issueUpdateDTOList.removeIf(v -> Objects.equals(issueCreateDTO.getCaseId(), v.getCaseId()));
                issueUpdateIds.remove(issueCreateDTO.getCaseId());
            }
            issueUpdateIds.add(issueCreateDTO.getCaseId());
            issueUpdateDTOList.add(issueCreateDTO);
        } else {
            issueCreateDTOList.add(issueCreateDTO);
        }
    }

    private void insertCase(List<IssueCreateDTO> issueCreateDTOList, TestProjectInfoDTO testProjectInfo) {
        testCaseService.batchImportTestCase(issueCreateDTOList, testProjectInfo);
        issueCreateDTOList.clear();
    }

    private void updateCase(Long projectId, List<IssueCreateDTO> issueUpdateDTOList, TestProjectInfoDTO testProjectInfo, List<Long> issueUpdateIds) {
        issueUpdateDTOList.forEach(updateCase -> {
            // 删除测试步骤
            testCaseStepService.removeStepByIssueId(projectId, updateCase.getCaseId());
            // 删除关联工作项
            testCaseLinkService.batchDeleteByCaseId(projectId, updateCase.getCaseId());
        });
        testCaseService.batchUpdateTestCase(issueUpdateDTOList, testProjectInfo);
        issueUpdateDTOList.clear();
        issueUpdateIds.clear();
    }

    /**
     * @param sheet
     * @param columnNum 数据页总共有多少列数据
     * @return
     */
    private Integer getRealRowCount(Sheet sheet, int columnNum) {
        if (isEmptyTemp(sheet)) {
            return 0;
        }
        Integer count = 0;
        for (int r = 1; r <= sheet.getPhysicalNumberOfRows(); r++) {
            Row row = sheet.getRow(r);
            //row为空跳过
            if (isSkip(row, columnNum)) {
                continue;
            }
            count++;
        }
        return count;
    }


    private boolean isSkip(Row row, int columnNum) {
        if (row == null) {
            return true;
        }
        //所有列都为空才跳过
        boolean skip = true;
        for (int i = 0; i < columnNum; i++) {
            Cell cell = row.getCell(i);
            skip = skip && isCellEmpty(cell);
        }
        return skip;
    }

    private boolean isOldExcel(Workbook workbook, String[] headers) {
        //判断是否为旧模版
        Sheet sheet = workbook.getSheet(ExcelSheetConstants.TEST_CASE_SHEET_NAME);
        if (sheet == null) {
            int numberOfSheets = workbook.getNumberOfSheets();
            sheet = workbook.getSheetAt(numberOfSheets > 1 ? 1 : 0);
            if (sheet == null) {
                return true;
            }
        }
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
        TestPriorityDTO priorityDTO = new TestPriorityDTO();
        priorityDTO.setOrganizationId(organizationId);
        List<String> priorityNameList = testPriorityMapper.select(priorityDTO)
                .stream().map(TestPriorityDTO::getName).collect(Collectors.toList());

        List<String> userNameList = new ArrayList<>();
        for (UserDTO userDTO : userDTOS) {
            userNameList.add(userDTO.getLoginName() + userDTO.getRealName());
        }

        addReadMeSheet(importTemp);
        addTestCaseSheet(importTemp, priorityNameList);

        return importTemp;
    }

    @Override
    public void validateFileSize(MultipartFile excelFile) {
        Long size = excelFile.getSize();
        Long maxSize = 1L * 1024 * 1024;
        if (size > maxSize) {
            throw new CommonException("error.test.case.import.max.size");
        }
    }

    private void addReadMeSheet(Workbook workbook) {
        Sheet readMeSheet = workbook.createSheet("README");
        workbook.setSheetOrder("README", 0);

        fillReadMeSheet(readMeSheet, workbook);
        setReadMeSheetColumnWidthAndRowHeight(readMeSheet);
    }

    private void setReadMeSheetColumnWidthAndRowHeight(Sheet sheet) {
        for (int i = 0; i < README_SHEET_COLUMN_WIDTH.length; i++) {
            sheet.setColumnWidth(i, README_SHEET_COLUMN_WIDTH[i]);
        }
        for (int i = 0; i < 20; i++) {
            if (0 < i && i < README_OPTIONS.length + 1 && README_OPTIONS[i - 1].getDescription().length() > 26) {
                // 字数超过长时设置高度
                ExcelUtil.getOrCreateRow(sheet, i + 1).setHeight((short) 640);
            }
        }
    }

    private void addTestCaseSheet(Workbook workbook, List<String> priorityNameList) {
        Sheet testCaseSheet = workbook.createSheet(ExcelSheetConstants.TEST_CASE_SHEET_NAME);
        workbook.setSheetOrder(ExcelSheetConstants.TEST_CASE_SHEET_NAME, 1);

        fillTestCaseSheet(workbook, testCaseSheet);
        setTestCaseSheetStyle(testCaseSheet);

        ExcelUtil.dropDownList2007(workbook, testCaseSheet, priorityNameList, 1, 500, 4, 4, HIDDEN_PRIORITY, 2);
    }

    // 填充测试用例页内容
    private void fillTestCaseSheet(Workbook workbook, Sheet testCaseSheet) {
        Row header = ExcelUtil.getOrCreateRow(testCaseSheet, 0);
        header.setHeight((short) 320);
        CellStyle cellStyle = ExcelUtil.createCellStyle(workbook, null, VerticalAlignment.CENTER, true);
        ExcelUtil.createFont(workbook.createFont(), cellStyle, "宋体", null, true);
        cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        for (int i = 0; i < README_OPTIONS.length; i++) {
            Cell cell = header.createCell(i, CellType.STRING);
            cell.setCellValue(README_OPTIONS[i].getFiled());
            cell.setCellStyle(cellStyle);
        }
    }

    // 设置测试用例页样式
    private void setTestCaseSheetStyle(Sheet testCaseSheet) {
        // 设置列宽度
        testCaseSheet.setColumnWidth(0, EXCEL_WIDTH_PX * 25);
        testCaseSheet.setColumnWidth(1, EXCEL_WIDTH_PX * 13);
        testCaseSheet.setColumnWidth(2, EXCEL_WIDTH_PX * 13);
        testCaseSheet.setColumnWidth(3, EXCEL_WIDTH_PX * 50);
        testCaseSheet.setColumnWidth(4, EXCEL_WIDTH_PX * 10);
        testCaseSheet.setColumnWidth(5, EXCEL_WIDTH_PX * 15);
        testCaseSheet.setColumnWidth(6, EXCEL_WIDTH_PX * 30);
        testCaseSheet.setColumnWidth(7, EXCEL_WIDTH_PX * 45);
        testCaseSheet.setColumnWidth(8, EXCEL_WIDTH_PX * 20);
        testCaseSheet.setColumnWidth(9, EXCEL_WIDTH_PX * 45);
        CellStyle cellStyle = ExcelUtil.createCellStyle(testCaseSheet.getWorkbook());
        ExcelUtil.createFont(testCaseSheet.getWorkbook().createFont(), cellStyle, null, Font.COLOR_RED, true);
        testCaseSheet.setDefaultColumnStyle(README_OPTIONS.length, cellStyle);
    }

    // 填充 README 页内容
    private void fillReadMeSheet(Sheet readMeSheet, Workbook workbook) {
        // 加粗字体，蓝色填充样式
        CellStyle boldFontStyle = ExcelUtil.createCellStyle(workbook);
        ExcelUtil.createFont(workbook.createFont(), boldFontStyle, null, null, true);
        boldFontStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.TAN.getIndex());
        boldFontStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        readMeSheet.createRow(0).createCell(0, CellType.STRING).setCellValue("导入用例支持导入新增和导入更新，导入更新需填写和系统一致的用例编号，否则无法更新成功");
        readMeSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        readMeSheet.getRow(0).getCell(0).setCellStyle(boldFontStyle);
        // 红色字体，居中样式
        CellStyle redCenterFontStyle = ExcelUtil.createCellStyle(workbook);
        readMeSheet.createRow(1).createCell(2, CellType.STRING).setCellValue("请至下一页，填写信息");
        readMeSheet.getRow(1).getCell(2).setCellStyle(redCenterFontStyle);
        readMeSheet.addMergedRegion(new CellRangeAddress(1, README_OPTIONS.length, 2, 3));

        setReadMeSheetOptions(readMeSheet, workbook);
        readMeSheet.createRow(12).createCell(1, CellType.STRING).setCellValue("示例：");
        writeExampleHeader(workbook, readMeSheet);
        writeExample(workbook, readMeSheet, 14, "导入更新", EXAMPLE_ISSUES[0], EXAMPLE_TEST_CASE_STEPS);
        writeExample(workbook, readMeSheet, 17, "导入新增", EXAMPLE_ISSUES[1], EXAMPLE_TEST_CASE_STEPS);
    }

    private void setReadMeSheetOptions(Sheet readMeSheet, Workbook workbook) {
        // 红色字体样式
        CellStyle redFontStyle = ExcelUtil.createCellStyle(workbook, null, VerticalAlignment.CENTER, true);
        Font font = workbook.createFont();
        ExcelUtil.createFont(font, redFontStyle, "宋体", Font.COLOR_RED, null);
        // 自适应样式
        CellStyle fontStyle = ExcelUtil.createCellStyle(workbook, null, VerticalAlignment.CENTER, true);
        Font redFont = workbook.createFont();
        ExcelUtil.createFont(redFont, fontStyle, "宋体", null, null);

        int i = 0;
        while (i < README_OPTIONS.length) {
            ExcelReadMeOptionVO optionDTO = README_OPTIONS[i++];
            ExcelUtil.getOrCreateRow(readMeSheet, i).createCell(0, CellType.STRING).setCellValue(optionDTO.getFiled());
            readMeSheet.getRow(i).createCell(1, CellType.STRING).setCellValue(optionDTO.getDescription());
            if (Boolean.TRUE.equals(optionDTO.getRequired())) {
                readMeSheet.getRow(i).getCell(0).setCellStyle(redFontStyle);
                readMeSheet.getRow(i).getCell(1).setCellStyle(redFontStyle);
            } else {
                readMeSheet.getRow(i).getCell(0).setCellStyle(fontStyle);
                readMeSheet.getRow(i).getCell(1).setCellStyle(fontStyle);
            }
        }
        XSSFRichTextString ts = new XSSFRichTextString(readMeSheet.getRow(2).getCell(1).getStringCellValue());
        ts.applyFont(0, 14, redFont);
        ts.applyFont(14, ts.length(), font);
        readMeSheet.getRow(2).getCell(1).setCellValue(ts);
    }

    private void writeExampleHeader(Workbook workbook, Sheet readMeSheet) {
        Row row = ExcelUtil.getOrCreateRow(readMeSheet, 13);
        CellStyle cellStyle = ExcelUtil.createCellStyle(workbook, null, VerticalAlignment.CENTER, true);
        Font font = workbook.createFont();
        ExcelUtil.createFont(font, cellStyle, "宋体", null, null);
        cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        for (int j = 0; j < README_OPTIONS.length; j++) {
            row.createCell(j + 1).setCellValue(README_OPTIONS[j].getFiled());
            row.getCell(j + 1).setCellStyle(cellStyle);
        }
    }

    private void writeExample(Workbook workbook, Sheet sheet, int rowNum, String type, IssueCreateDTO issueCreateDTO, TestCaseStepDTO... steps) {
        // 红色字体样式
        CellStyle redFontStyle = ExcelUtil.createCellStyle(workbook, VerticalAlignment.CENTER);
        ExcelUtil.createFont(workbook.createFont(), redFontStyle, "宋体", Font.COLOR_RED, null);
        Row row = ExcelUtil.getOrCreateRow(sheet, rowNum);
        row.setHeight((short) 640);
        row.createCell(0).setCellValue(type);
        row.getCell(0).setCellStyle(redFontStyle);

        row.createCell(1, CellType.STRING).setCellValue(issueCreateDTO.getFolderPath());
        row.createCell(2, CellType.STRING).setCellValue(issueCreateDTO.getCaseNum());
        row.createCell(3, CellType.STRING).setCellValue(issueCreateDTO.getCustomNum());
        row.createCell(4, CellType.STRING).setCellValue(issueCreateDTO.getSummary());
        row.createCell(5, CellType.STRING).setCellValue(issueCreateDTO.getPriorityCode());
        row.createCell(6, CellType.STRING).setCellValue(issueCreateDTO.getRelateIssueNums());
        // 用例需要换行
        row.getCell(6).setCellStyle(ExcelUtil.createCellStyle(workbook, null, null, true));

        row.createCell(7, CellType.STRING).setCellValue(issueCreateDTO.getDescription());

        for (int i = 0; i < steps.length; i++) {
            row = ExcelUtil.getOrCreateRow(sheet, i + rowNum);
            row.createCell(8, CellType.STRING).setCellValue(steps[i].getTestStep());
            row.createCell(9, CellType.STRING).setCellValue(steps[i].getTestData());
            row.createCell(10, CellType.STRING).setCellValue(steps[i].getExpectedResult());
        }
    }

    @Transactional
    public TestIssueFolderVO createFolder(Long projectId, Long parentId, String folderName) {
        TestIssueFolderVO testIssueFolder = new TestIssueFolderVO();
        testIssueFolder.setProjectId(projectId);
        testIssueFolder.setName(folderName);
        testIssueFolder.setParentId(parentId);
        testIssueFolder.setType(TestCycleType.CYCLE);
        testIssueFolder.setVersionId((long) BaseConstants.Digital.ZERO);
        try {
            testIssueFolderService.create(projectId, testIssueFolder);
        } catch (Exception e) {
            // 创建失败返回null
            return null;
        }
        return testIssueFolder;
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
        if (testFileLoadHistoryMapper.updateByPrimaryKey(testFileLoadHistoryDTO) != 1) {
            throw new CommonException("error.update.file.history");
        }
        redisUtil.delete(REDIS_STATUS_KEY + testFileLoadHistoryDTO.getId());
        updateProgress(testFileLoadHistoryDTO, userId, 100.0, 100.0);
    }


    private Iterator<Row> rowIteratorSkipFirst(Sheet sheet, Map<String, Integer> headerLocationMap) {
        Iterator<Row> rowIterator = sheet.rowIterator();
        Row headerRow = rowIterator.next();
        int x = 0;
        while (!ExcelUtil.getStringValue(headerRow.getCell(0)).equals(ExcelTitleName.FOLDER_PATH)
                && !ExcelUtil.getStringValue(headerRow.getCell(1)).equals(ExcelTitleName.CASE_NUM)) {
            if (rowIterator.hasNext()) {
                headerRow = rowIterator.next();
            }
            x++;
            if (x > 100) {
                throw new CommonException("error.rowIteratorSkipFirst.notFoundHeader");
            }
        }

        int i = 0;
        String titleName = ExcelUtil.getStringValue(headerRow.getCell(i));
        while (!"".equals(titleName)) {
            headerLocationMap.put(titleName, i);
            i++;
            titleName = ExcelUtil.getStringValue(headerRow.getCell(i));
        }
        return rowIterator;
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

    private IssueCreateDTO processIssueHeaderRow(Row row,
                                                 Long projectId,
                                                 ExcelTitleUtil excelTitleUtil,
                                                 Map<String, Long> priorityMap,
                                                 boolean isUpdate,
                                                 TestProjectInfoDTO testProjectInfo) {
        if (ExcelUtil.isBlank(excelTitleUtil.getCell(ExcelTitleName.CASE_SUMMARY, row))) {
            markAsError(row, "用例概要不能为空");
            return null;
        }

        if (ExcelUtil.isBlank(excelTitleUtil.getCell(ExcelTitleName.PRIORITY, row))) {
            markAsError(row, "优先级不能为空");
            return null;
        }
        if (ExcelUtil.isBlank(excelTitleUtil.getCell(ExcelTitleName.FOLDER_PATH, row))) {
            markAsError(row, "目录不能为空");
            return null;
        }

        String description = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.CASE_DESCRIPTION, row));
        String summary = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.CASE_SUMMARY, row)).trim();
        String priority = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.PRIORITY, row));
        String customNum = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.CUSTOM_NUM, row));
        String folderPath = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.FOLDER_PATH, row));
        if (summary.length() > SUMMARY_MAX_SIZE) {
            markAsError(row, "概要长度不能超过44个字符");
            return null;
        }
        if (Objects.isNull(priorityMap.get(priority))) {
            markAsError(row, "优先级不存在");
            return null;
        }
        // 递归查询用例所属目录
        Long folderId = getFolderIdByPath(projectId, folderPath, 0L);
        if (Objects.isNull(folderId)) {
            markAsError(row, "请填写正确的目录结构，文件夹下已有测试用例，不允许插入文件夹");
            return null;
        }
        if (!CollectionUtils.isEmpty(queryFolderByNameAndParentId(projectId, null, folderId))) {
            markAsError(row, "请填写正确的目录结构，文件夹下已有文件夹，不允许插入测试用例");
            return null;
        }
        IssueCreateDTO issueCreateDTO = new IssueCreateDTO();
        issueCreateDTO.setProjectId(projectId);
        issueCreateDTO.setSummary(summary);
        // 设置前置条件
        issueCreateDTO.setDescription("<p>" + description + "</p>");
        issueCreateDTO.setFolderId(folderId);
        issueCreateDTO.setPriorityId(priorityMap.get(priority));
        issueCreateDTO.setCustomNum(customNum);
        issueCreateDTO.setTestCaseStepProList(new ArrayList<>());
        // 校验custom重复
        if (!ObjectUtils.isEmpty(customNum)) {
            if (customNum.length() > 50) {
                markAsError(row, "自定义编号长度不能超过50个字符");
                return null;
            }
            String reg = "^([A-Za-z0-9]+(-[A-Za-z0-9]+)*)$";
            Boolean matches = Pattern.matches(reg, customNum);
            if (!matches) {
                markAsError(row, "由大小写字母、数字、\"-\"组成，不能以\"-\"开头或结尾，且不能连续出现两个\"-\"。");
                return null;
            }
        }
        if (!ExcelUtil.isBlank(excelTitleUtil.getCell(ExcelTitleName.LINK_ISSUE, row))) {
            //处理关联问题
            String issueNumString = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.LINK_ISSUE, row));
            ProjectDTO projectDTO = remoteIamOperator.getProjectById(projectId);
            // 工作项前缀
            String agileProjectCode = projectDTO.getAgileProjectCode();
            String issueNumPrefix = agileProjectCode + BaseConstants.Symbol.MIDDLE_LINE;
            // 如果匹配到了, 则关联对应工作项
            Pattern compile = Pattern.compile(issueNumPrefix + "[0-9]+");
            List<TestCaseLinkDTO> testCaseLinkDTOList = new ArrayList<>();
            String[] splitArr = issueNumString.split("\n");
            for (String issRelStr : splitArr) {
                Matcher matcher1 = compile.matcher(issRelStr);
                if (!matcher1.find()) {
                    markAsError(row, "关联工作项编号格式错误！");
                    return null;
                }
                String issueNum = matcher1.group().substring(issueNumPrefix.length());
                IssueNumDTO issueNumDTO;
                try {
                    issueNumDTO = agileClientOperator.queryIssueByIssueNum(projectId, issueNum);
                    if (ObjectUtils.isEmpty(issueNumDTO)) {
                        markAsError(row, "关联工作项编号有误，仅支持关联故事、任务、子任务、缺陷类型，请检查录入的关联问题编号。");
                        return null;
                    }
                    TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
                    testCaseLinkDTO.setIssueId(issueNumDTO.getIssueId());
                    testCaseLinkDTOList.add(testCaseLinkDTO);
                } catch (FeignException e) {
                    markAsError(row, "关联工作项编号有误，仅支持关联故事、任务、子任务、缺陷类型，请检查录入的关联问题编号。");
                    return null;
                }
            }
            issueCreateDTO.setTestCaseLinkDTOList(testCaseLinkDTOList);
        }
        // 处理更新用例
        handleUpdateCaseByCaseNum(isUpdate, row, testProjectInfo, issueCreateDTO, excelTitleUtil);
        if (Boolean.TRUE.equals(isUpdate) && Objects.isNull(issueCreateDTO.getCaseId())) {
            // 用例编号不存在
            return null;
        }
        return issueCreateDTO;
    }

    private void handleUpdateCaseByCaseNum(boolean isUpdate,
                                           Row row,
                                           TestProjectInfoDTO testProjectInfo,
                                           IssueCreateDTO issueCreateDTO,
                                           ExcelTitleUtil excelTitleUtil) {
        if (Boolean.TRUE.equals(isUpdate)) {
            String caseNumStr = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.CASE_NUM, row));

            String prefix = testProjectInfo.getProjectCode() + BaseConstants.Symbol.MIDDLE_LINE;
            if (!StringUtils.startsWith(caseNumStr, prefix)) {
                markAsError(row, "用例编号不存在");
                return;
            }
            String caseNum = caseNumStr.substring(prefix.length());
            TestCaseDTO select = new TestCaseDTO();
            select.setProjectId(testProjectInfo.getProjectId());
            select.setCaseNum(caseNum);
            select = testCaseMapper.selectOne(select);
            if (Objects.isNull(select)) {
                markAsError(row, "用例编号不存在");
                return;
            }
            issueCreateDTO.setCaseId(select.getCaseId());
            issueCreateDTO.setCaseNum(caseNum);
        }
    }

    private Long getFolderIdByPath(Long projectId, String folderPath, Long parentId) {
        Long folderId = null;
        String folderName = folderPath;
        if (folderPath.contains("/")) {
            List<String> folderNamse = Arrays.asList(folderPath.split("/"));
            if (!CollectionUtils.isEmpty(folderNamse)) {
                folderName = Arrays.asList(folderPath.split("/")).get(0);
            }
        }
        // 可能存在重名
        List<TestIssueFolderVO> folderList = queryFolderByNameAndParentId(projectId, folderName, parentId);
        if (CollectionUtils.isEmpty(folderList)) {
            logger.info("{} 文件夹不存在，创建", folderName);
            TestIssueFolderVO folder = createFolder(projectId, parentId, folderName);
            if (folder == null) {
                return null;
            }
            folderList = Collections.singletonList(folder);
        }
        for (TestIssueFolderVO testIssueFolderDTO : folderList) {
            folderId = testIssueFolderDTO.getFolderId();
            // 父级目录可能重名，但根据parentId可获取唯一父级路径
            if (folderPath.contains("/")) {
                String currentFolderPath = folderPath.substring(folderPath.indexOf('/') + 1);
                folderId = getFolderIdByPath(projectId, currentFolderPath, folderId);
            }
            // 子级目录可能重名，默认取第一个
            if (!Objects.isNull(folderId)) {
                break;
            }
        }
        return folderId;
    }

    private List<TestIssueFolderVO> queryFolderByNameAndParentId(Long projectId, String folderName, Long parentId) {
        TestIssueFolderDTO folder = new TestIssueFolderDTO();
        folder.setProjectId(projectId);
        folder.setName(folderName);
        folder.setParentId(parentId);
        folder.setType(TestCycleType.CYCLE);
        return modelMapper.map(testIssueFolderMapper.select(folder), new TypeToken<List<TestIssueFolderVO>>() {
        }.getType());
    }

    private Set<String> splitByRegex(String value) {
        String regex1 = ",";
        String regex2 = "，";
        Set<String> result = new HashSet<>();
        String[] array = value.split(regex1);
        for (String str : array) {
            result.addAll(Arrays.asList(str.split(regex2)));
        }
        return result;
    }

    private void processRow(IssueCreateDTO testCaseDTO, Row row, List<Integer> errorRowIndexes, ExcelTitleUtil excelTitleUtil) {
        if (testCaseDTO == null) {
            errorRowIndexes.add(row.getRowNum());
            return;
        }
        buildTestCaseStepDTO(testCaseDTO, row, excelTitleUtil);
        removeRow(row);
    }

    private double updateProgress(TestFileLoadHistoryDTO testFileLoadHistoryDTO, Long userId, double rate, double lastRate) {
        TestFileLoadHistoryWebsocketVO websocketVO = modelMapper
                .map(testFileLoadHistoryDTO, TestFileLoadHistoryWebsocketVO.class);
        websocketVO.setRate(rate);
        if (TestFileLoadHistoryEnums.Status.FAILURE.getTypeValue().equals(websocketVO.getStatus())) {
            websocketVO.setCode(IMPORT_ERROR);
        }
        String websocketKey = IMPORT_NOTIFY_CODE + "-" + testFileLoadHistoryDTO.getProjectId();
        if (rate == 0.0 || rate == 100.0 || rate - lastRate > 3) {
            messageClientC7n.sendByUserId(userId, websocketKey, toJson(websocketVO));
            lastRate = rate;
            logger.info("导入进度：{}", rate);
        }
        if (rate == 100.) {
            logger.info("完成");
        }
        return lastRate;
    }

    private void shiftErrorRowsToTop(Sheet sheet, List<Integer> errorRowIndexes) {
        int i = 0;
        while (i < errorRowIndexes.size()) {
            shiftRow(sheet, errorRowIndexes.get(i), ++i);
        }
    }

    private String toJson(TestFileLoadHistoryWebsocketVO websocketVO) {
        try {
            return objectMapper.writeValueAsString(websocketVO);
        } catch (IOException e) {
            logger.error("json convert fail");
            throw new CommonException(e);
        }
    }

    private String uploadErrorWorkbook(Long organizationId, Workbook errorWorkbook, TestFileLoadHistoryDTO testFileLoadHistoryDTO) {
        String url = fileClient.uploadFile(organizationId, filePathService.bucketName(), filePathService.dirName(),
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
        ExcelUtil.getOrCreateCell(row, README_OPTIONS.length, CellType.STRING).setCellValue(errorMsg);
        logger.info("行 {} 发生错误：{}", row.getRowNum() + 1, errorMsg);
    }

    private void buildTestCaseStepDTO(IssueCreateDTO issueCreateDTO, Row row, ExcelTitleUtil excelTitleUtil) {
        String testStep = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.TEST_STEP, row));
        String testData = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.TEST_DATA, row));
        String expectedResult = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.EXPECT_RESULT, row));

        TestCaseStepProDTO testCaseStepDTO = null;
        if (StringUtils.isNotBlank(testStep) || StringUtils.isNotBlank(testData) || StringUtils.isNotBlank(expectedResult)) {
            testCaseStepDTO = new TestCaseStepProDTO();
            testCaseStepDTO.setTestStep(testStep);
            testCaseStepDTO.setTestData(testData);
            testCaseStepDTO.setExpectedResult(expectedResult);
            issueCreateDTO.getTestCaseStepProList().add(testCaseStepDTO);
        }
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
                toCell = ExcelUtil.getOrCreateCell(toRow, i, CellType.STRING);
                toCell.setCellValue(ExcelUtil.getStringValue(fromCell));
            }
        }
        // 将错误信息设置为红色
        toCell = toRow.getCell(README_OPTIONS.length);
        if (!ExcelUtil.isBlank(toCell)) {
            CellStyle cellStyle = ExcelUtil.createCellStyle(sheet.getWorkbook());
            ExcelUtil.createFont(sheet.getWorkbook().createFont(), cellStyle, null, Font.COLOR_RED, false);
            toCell.setCellStyle(cellStyle);
        }
    }

    private TestFileLoadHistoryEnums.Status checkoutStatus(String uploadError, TestFileLoadHistoryEnums.Status status) {
        if (uploadError == null) {
            status = TestFileLoadHistoryEnums.Status.FAILURE;
        }
        return status;
    }

    private Long getStatus(Long fileHistoryId) {
        String key = REDIS_STATUS_KEY + fileHistoryId;
        Long result;
        Object status = redisUtil.get(key);
        if (status == null) {
            result = testFileLoadHistoryMapper.queryLoadHistoryStatus(fileHistoryId);
            redisUtil.set(key, String.valueOf(result));
        } else {
            result = Long.parseLong(status.toString());
        }
        return result;
    }
}
