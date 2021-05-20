package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;

import io.choerodon.core.client.MessageClientC7n;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.vo.TestFileLoadHistoryWebsocketVO;
import io.choerodon.test.manager.api.vo.agile.*;


import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.ExcelReadMeOptionVO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.ExcelTitleName;
import io.choerodon.test.manager.infra.enums.TestAttachmentCode;
import io.choerodon.test.manager.infra.enums.TestFileLoadHistoryEnums;
import io.choerodon.test.manager.infra.feign.BaseFeignClient;
import io.choerodon.test.manager.infra.feign.operator.AgileClientOperator;
import io.choerodon.test.manager.infra.mapper.TestFileLoadHistoryMapper;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper;
import io.choerodon.test.manager.infra.mapper.TestPriorityMapper;
import io.choerodon.test.manager.infra.mapper.TestProjectInfoMapper;
import io.choerodon.test.manager.infra.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hzero.boot.file.FileClient;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.EncryptType;
import org.modelmapper.ModelMapper;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelImportServiceImpl.class);
    private static final String IMPORT_NOTIFY_CODE = "test-issue-import";
    private static final String IMPORT_ERROR = "test-issue-import-error";
    private static final String HIDDEN_USER = "hidden_user";
    private static final String HIDDEN_PRIORITY = "hidden_priority";
    private static final ExcelReadMeOptionVO[] README_OPTIONS = new ExcelReadMeOptionVO[8];
    private static final TestCaseStepDTO[] EXAMPLE_TEST_CASE_STEPS = new TestCaseStepDTO[3];
    private static final IssueCreateDTO[] EXAMPLE_ISSUES = new IssueCreateDTO[3];
    private static final String TYPE_CYCLE = "cycle";
    public static final int EXCEL_WIDTH_PX = 256;
    private static final int SUMMARY_MAX_SIZE = 44;
    private static final String REDIS_STATUS_KEY = "test:fileStatus:";
    protected static final String[] EXCEL_HEADERS = new String[]
            {
                    ExcelTitleName.CUSTOM_NUM,
                    ExcelTitleName.CASE_SUMMARY,
                    ExcelTitleName.PRIORITY,
                    ExcelTitleName.CASE_DESCRIPTION,
                    ExcelTitleName.LINK_ISSUE,
                    ExcelTitleName.TEST_STEP,
                    ExcelTitleName.TEST_DATA,
                    ExcelTitleName.EXPECT_RESULT
            };

    static {
        README_OPTIONS[0] = new ExcelReadMeOptionVO("自定义编号", false);
        README_OPTIONS[1] = new ExcelReadMeOptionVO("用例概要*", true);
        README_OPTIONS[2] = new ExcelReadMeOptionVO("优先级*", true);
        README_OPTIONS[3] = new ExcelReadMeOptionVO("前置条件", false);
        //README_OPTIONS[2] = new ExcelReadMeOptionVO("优先级", false);
//        README_OPTIONS[2] = new ExcelReadMeOptionVO("被指定人", false);
        //README_OPTIONS[3] = new ExcelReadMeOptionVO("模块", false);
        README_OPTIONS[4] = new ExcelReadMeOptionVO("关联问题", false);
        README_OPTIONS[5] = new ExcelReadMeOptionVO("测试步骤", false);
        README_OPTIONS[6] = new ExcelReadMeOptionVO("测试数据", false);
        README_OPTIONS[7] = new ExcelReadMeOptionVO("预期结果", false);


        for (int i = 0; i < EXAMPLE_TEST_CASE_STEPS.length; i++) {
            EXAMPLE_TEST_CASE_STEPS[i] = new TestCaseStepDTO();
            EXAMPLE_TEST_CASE_STEPS[i].setTestStep("步骤" + (i + 1));
            EXAMPLE_TEST_CASE_STEPS[i].setTestData("数据" + (i + 1));
            EXAMPLE_TEST_CASE_STEPS[i].setExpectedResult("结果" + (i + 1));
        }

        for (int i = 0; i < EXAMPLE_ISSUES.length; i++) {
            EXAMPLE_ISSUES[i] = new IssueCreateDTO();
            EXAMPLE_ISSUES[i].setSummary("概要" + (i + 1));
            EXAMPLE_ISSUES[i].setPriorityCode("优先级" + (i + 1));
            EXAMPLE_ISSUES[i].setDescription("前置条件" + (i + 1));
            EXAMPLE_ISSUES[i].setRelateIssueNums((i + 1) + "," + (i + 2));
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
    private AgileClientOperator agileClientOperator;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BaseFeignClient baseFeignClient;

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
    public void importIssueByExcel(Long projectId, Long folderId, Long userId, InputStream inputStream,
                                   EncryptType encryptType, RequestAttributes requestAttributes) {
        Workbook issuesWorkbook;
        try {
            issuesWorkbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            throw new CommonException("error.io.new.workbook", e);
        }
        // 添加加密信息上下文
        EncryptContext.setEncryptType(encryptType.name());
        RequestContextHolder.setRequestAttributes(requestAttributes);
        ProjectDTO projectDTO = baseFeignClient.queryProject(projectId).getBody();
        TestProjectInfoDTO testProjectInfoDTO = new TestProjectInfoDTO();
        testProjectInfoDTO.setProjectId(projectId);
        TestProjectInfoDTO testProjectInfo = testProjectInfoMapper.selectOne(testProjectInfoDTO);
        // 默认是导入到导入文件夹，不存在则创建
        Sheet testCasesSheet = issuesWorkbook.getSheet("测试用例");
        TestFileLoadHistoryDTO testFileLoadHistoryDTO = initLoadHistory(projectId, folderId, userId);
        TestFileLoadHistoryEnums.Status status = TestFileLoadHistoryEnums.Status.SUCCESS;
        List<Long> issueIds = new ArrayList<>();
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
        while (rowIterator.hasNext()) {
            currentRow = rowIterator.next();
            if (isSkip(currentRow, EXCEL_HEADERS.length)) {
                // 如果当前行全部为空，则跳过
                continue;
            }
            if (Objects.equals(TestFileLoadHistoryEnums.Status.valueOf(getStatus(testFileLoadHistoryDTO.getId())), TestFileLoadHistoryEnums.Status.CANCEL)) {
                status = TestFileLoadHistoryEnums.Status.CANCEL;
                logger.info("已取消");
                if (!issueIds.isEmpty()) {
                    testCaseService.batchDeleteIssues(projectId, issueIds);
                }
                break;
            }

            if (isIssueHeaderRow(currentRow, excelTitleUtil)) {
                //插入用例
                issueCreateDTO = processIssueHeaderRow(currentRow, projectId, folderId, excelTitleUtil, priorityMap);
                if (issueCreateDTO == null) {
                    failedCount++;
                } else {
                    successfulCount++;
                    issueCreateDTOList.add(issueCreateDTO);
                    lastRate = updateProgress(testFileLoadHistoryDTO, userId, progress / nonBlankRowCount * 100, lastRate);
                }
            }
            //processRow(issueDTO, currentRow, errorRowIndexes, excelTitleUtil);
            // 插入循环步骤
            processRow(issueCreateDTO, currentRow, errorRowIndexes, excelTitleUtil);
            if (issueCreateDTOList.size() >= 100) {
                List<Long> addIssueIds = insertCase(issueCreateDTOList, testProjectInfo);
                issueIds.addAll(addIssueIds);
                issueCreateDTOList.clear();
            }
            ++progress;
        }
        if (!CollectionUtils.isEmpty(issueCreateDTOList) && status != TestFileLoadHistoryEnums.Status.CANCEL) {
            insertCase(issueCreateDTOList, testProjectInfo);
            issueCreateDTOList.clear();
        }
        testProjectInfoMapper.updateByPrimaryKeySelective(testProjectInfo);
        testFileLoadHistoryDTO.setSuccessfulCount(successfulCount);
        testFileLoadHistoryDTO.setFailedCount(failedCount);

        if (!errorRowIndexes.isEmpty() && status != TestFileLoadHistoryEnums.Status.CANCEL) {
            logger.info("导入数据有误，上传 error workbook");
            shiftErrorRowsToTop(testCasesSheet, errorRowIndexes);
            status = checkoutStatus(uploadErrorWorkbook(projectDTO.getOrganizationId(), issuesWorkbook, testFileLoadHistoryDTO), status);
        }

        finishImport(testFileLoadHistoryDTO, userId, status);
    }

    private List<Long> insertCase(List<IssueCreateDTO> issueCreateDTOList, TestProjectInfoDTO testProjectInfo) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        List<Long> result = new ArrayList<>();
        List<TestCaseStepProDTO> addStepList = new ArrayList<>();
        testCaseService.batchImportTestCase(issueCreateDTOList, testProjectInfo);
        issueCreateDTOList.forEach(issueCreateDTO -> {
            result.add(issueCreateDTO.getCaseId());
            if (!CollectionUtils.isEmpty(issueCreateDTO.getTestCaseStepProList())) {
                String rank = null;
                for (TestCaseStepProDTO addStep : issueCreateDTO.getTestCaseStepProList()) {
                    rank = RankUtil.genNext(RankUtil.Operation.INSERT.getRank(rank, null));
                    addStep.setRank(rank);
                    addStep.setIssueId(issueCreateDTO.getCaseId());
                    addStep.setCreatedBy(userId);
                    addStep.setLastUpdatedBy(userId);
                    addStepList.add(addStep);
                }
            }
        });
        if (!CollectionUtils.isEmpty(addStepList)) {
            testCaseStepService.batchCreateOneStep(addStepList);
        }
        return result;
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
        TestPriorityDTO priorityDTO = new TestPriorityDTO();
        priorityDTO.setOrganizationId(organizationId);
        List<String> priorityNameList = testPriorityMapper.select(priorityDTO)
                .stream().map(TestPriorityDTO::getName).collect(Collectors.toList());

        List<String> userNameList = new ArrayList<>();
        for (UserDTO userDTO : userDTOS) {
            userNameList.add(userDTO.getLoginName() + userDTO.getRealName());
        }

        addReadMeSheet(importTemp);
        addTestCaseSheet(importTemp, userNameList, priorityNameList);

        return importTemp;
    }

    private void addReadMeSheet(Workbook workbook) {
        Sheet readMeSheet = workbook.createSheet("README");
        workbook.setSheetOrder("README", 0);

        fillReadMeSheet(readMeSheet);
        setReadMeSheetStyle(readMeSheet);
    }

    private void addTestCaseSheet(Workbook workbook, List<String> userNameList, List<String> priorityNameList) {
        Sheet testCaseSheet = workbook.createSheet("测试用例");
        workbook.setSheetOrder("测试用例", 1);

        fillTestCaseSheet(testCaseSheet);
        setTestCaseSheetStyle(testCaseSheet);

        ExcelUtil.dropDownList2007(workbook, testCaseSheet, priorityNameList, 1, 500, 2, 2, HIDDEN_PRIORITY, 2);
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
        testCaseSheet.setColumnWidth(0, EXCEL_WIDTH_PX * 48);
        testCaseSheet.setColumnWidth(1, EXCEL_WIDTH_PX * 16);
        testCaseSheet.setColumnWidth(2, EXCEL_WIDTH_PX * 32);
        testCaseSheet.setColumnWidth(3, EXCEL_WIDTH_PX * 32);
        testCaseSheet.setColumnWidth(4, EXCEL_WIDTH_PX * 48);
        testCaseSheet.setColumnWidth(5, EXCEL_WIDTH_PX * 16);
        testCaseSheet.setColumnWidth(6, EXCEL_WIDTH_PX * 48);

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

        Row row = ExcelUtil.getOrCreateRow(readMeSheet, 6);
        row.createCell(0, CELL_TYPE_STRING).setCellValue("注意");
        readMeSheet.addMergedRegion(new CellRangeAddress(6, 7, 0, 0));
        row.createCell(1, CELL_TYPE_STRING).setCellValue("关联问题直接输入问题的数字编号即可；\n可用逗号（支持中英文）连接多个，例如:1,2，3。");
        readMeSheet.addMergedRegion(new CellRangeAddress(6, 7, 1, 7));

        readMeSheet.createRow(8).createCell(1, CELL_TYPE_STRING).setCellValue("示例");
        readMeSheet.addMergedRegion(new CellRangeAddress(8, 8, 1, 10));
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
        row.createCell(colNum + 1, CELL_TYPE_STRING).setCellValue(issueCreateDTO.getPriorityCode());
        row.createCell(colNum + 2, CELL_TYPE_STRING).setCellValue(issueCreateDTO.getDescription());
        row.createCell(colNum + 3, CELL_TYPE_STRING).setCellValue(issueCreateDTO.getRelateIssueNums());
//        row.createCell(colNum + 2, CELL_TYPE_STRING).setCellValue("1234张三");
        //row.createCell(colNum + 3, CELL_TYPE_STRING).setCellValue("测试模块");
//        row.createCell(colNum + 2, CELL_TYPE_STRING).setCellValue("XX-111");

        for (int i = 0; i < steps.length; i++) {
            row = ExcelUtil.getOrCreateRow(sheet, i + rowNum);
            row.createCell(colNum + 4, CELL_TYPE_STRING).setCellValue(steps[i].getTestStep());
            row.createCell(colNum + 5, CELL_TYPE_STRING).setCellValue(steps[i].getTestData());
            row.createCell(colNum + 6, CELL_TYPE_STRING).setCellValue(steps[i].getExpectedResult());
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
        while (!ExcelUtil.getStringValue(headerRow.getCell(0)).equals(ExcelTitleName.FOLDER)
                && !ExcelUtil.getStringValue(headerRow.getCell(1)).equals(ExcelTitleName.CASE_SUMMARY)) {
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

    private IssueCreateDTO processIssueHeaderRow(Row row, Long projectId, Long folderId,
                                              ExcelTitleUtil excelTitleUtil, Map<String, Long> priorityMap) {
        if (ExcelUtil.isBlank(excelTitleUtil.getCell(ExcelTitleName.CASE_SUMMARY, row))) {
            markAsError(row, "用例概要不能为空");
            return null;
        }

        if (ExcelUtil.isBlank(excelTitleUtil.getCell(ExcelTitleName.PRIORITY, row))) {
            markAsError(row, "优先级不能为空");
            return null;
        }

        String description = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.CASE_DESCRIPTION, row));
        String summary = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.CASE_SUMMARY, row)).trim();
        String priority = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.PRIORITY, row));
        String customNum = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.CUSTOM_NUM, row));
        if(summary.length() > SUMMARY_MAX_SIZE){
            markAsError(row, "概要长度不能超过44个字符");
            return null;
        }
        if (Objects.isNull(priorityMap.get(priority))) {
            markAsError(row, "优先级不存在");
            return null;
        }

        IssueCreateDTO issueCreateDTO = new IssueCreateDTO();
        issueCreateDTO.setProjectId(projectId);
        issueCreateDTO.setSummary(summary);
        issueCreateDTO.setDescription(description);
        issueCreateDTO.setFolderId(folderId);
        issueCreateDTO.setPriorityId(priorityMap.get(priority));
        issueCreateDTO.setCustomNum(customNum);
        issueCreateDTO.setTestCaseStepProList(new ArrayList<>());
        // 校验custom重复
        if (!ObjectUtils.isEmpty(customNum)) {
            if (customNum.length() > 16) {
                markAsError(row, "自定义编号长度不能超过16个字符");
                return null;
            }
            String reg = "^(([A-Za-z]+)|([0-9]+)|([A-Za-z]+-[0-9]+))$";
            Boolean matches = Pattern.matches(reg, customNum);
            if (!matches) {
                markAsError(row, "自定义编号不符合规定");
                return null;
            }
        }
        if (!ExcelUtil.isBlank(excelTitleUtil.getCell(ExcelTitleName.LINK_ISSUE, row))) {
            //处理关联问题
            String issueNumString = ExcelUtil.getStringValue(excelTitleUtil.getCell(ExcelTitleName.LINK_ISSUE, row));
            String regex = "([0-9]+(，|,))*([0-9]+)";
            if (Pattern.matches(regex, issueNumString)) {
                List<String> relatedIssueNums = splitByRegex(issueNumString);
                List<TestCaseLinkDTO> testCaseLinkDTOList = new ArrayList<>();
                for (String issueNum : relatedIssueNums) {
                    IssueNumDTO issueNumDTO;
                    try {
                        issueNumDTO = agileClientOperator.queryIssueByIssueNum(projectId, issueNum);
                        if (ObjectUtils.isEmpty(issueNumDTO)) {
                            markAsError(row, "关联问题编号有误，仅支持关联故事、任务、子任务、缺陷类型，请检查录入的关联问题编号。");
                            return null;
                        }
                        TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
                        testCaseLinkDTO.setIssueId(issueNumDTO.getIssueId());
                        testCaseLinkDTOList.add(testCaseLinkDTO);
                    } catch (FeignException e) {
                        markAsError(row, "关联问题编号有误，仅支持关联故事、任务、子任务、缺陷类型，请检查录入的关联问题编号。");
                        return null;
                    }
                }
                issueCreateDTO.setTestCaseLinkDTOList(testCaseLinkDTOList);
            } else {
                markAsError(row, "关联问题编号格式错误！");
                return null;
            }

        }
        return issueCreateDTO;
    }

    private List<String> splitByRegex(String value) {
        String regex1 = ",";
        String regex2 = "，";
        List<String> result = new ArrayList<>();
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
        String url = fileClient.uploadFile(organizationId, TestAttachmentCode.ATTACHMENT_BUCKET, null,
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
