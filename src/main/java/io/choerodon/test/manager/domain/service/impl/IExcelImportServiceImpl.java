package io.choerodon.test.manager.domain.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.agile.api.dto.IssueTypeDTO;
import io.choerodon.agile.api.dto.VersionIssueRelDTO;
import io.choerodon.agile.infra.common.enums.IssueTypeCode;
import io.choerodon.agile.infra.common.utils.AgileUtil;
import io.choerodon.test.manager.api.dto.ExcelReadMeOptionDTO;
import io.choerodon.test.manager.api.dto.MultipartExcel;
import io.choerodon.test.manager.app.service.FileService;
import io.choerodon.test.manager.app.service.NotifyService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.domain.repository.TestFileLoadHistoryRepository;
import io.choerodon.test.manager.domain.service.IExcelImportService;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.infra.common.utils.ExcelUtil;
import io.choerodon.test.manager.infra.common.utils.SpringUtil;
import io.choerodon.test.manager.infra.feign.IssueFeignClient;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

@Service
public class IExcelImportServiceImpl implements IExcelImportService {

    private static final Logger logger = LoggerFactory.getLogger(IExcelImportServiceImpl.class);

    @Autowired
    private FileService fileService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private IssueFeignClient issueFeignClient;

    public void setIssueFeignClient(IssueFeignClient issueFeignClient) {
        this.issueFeignClient = issueFeignClient;
    }

    public void setTestCaseService(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }

    @Autowired
    private TestFileLoadHistoryRepository loadHistoryRepository;

    @Autowired
    private NotifyService notifyService;

    private static final String IMPORT_NOTIFY_CODE = "test-issue-import";

    private static final ExcelReadMeOptionDTO[] README_OPTIONS = new ExcelReadMeOptionDTO[5];

    private static final TestCaseStepE[] EXAMPLE_TEST_CASE_STEPS = new TestCaseStepE[3];

    private static final IssueCreateDTO[] EXAMPLE_ISSUES = new IssueCreateDTO[3];

    static {
        README_OPTIONS[0] = new ExcelReadMeOptionDTO("用例概要", true);
        README_OPTIONS[1] = new ExcelReadMeOptionDTO("用例描述", false);
        README_OPTIONS[2] = new ExcelReadMeOptionDTO("测试步骤", false);
        README_OPTIONS[3] = new ExcelReadMeOptionDTO("测试数据", false);
        README_OPTIONS[4] = new ExcelReadMeOptionDTO("预期结果", false);

        for (int i = 0; i < EXAMPLE_TEST_CASE_STEPS.length; i++) {
            EXAMPLE_TEST_CASE_STEPS[i] = new TestCaseStepE();
            EXAMPLE_TEST_CASE_STEPS[i].setTestStep("步骤" + (i + 1));
            EXAMPLE_TEST_CASE_STEPS[i].setTestData("数据" + (i + 1));
            EXAMPLE_TEST_CASE_STEPS[i].setExpectedResult("结果" + (i + 1));
        }

        for (int i = 0; i < EXAMPLE_ISSUES.length; i++) {
            EXAMPLE_ISSUES[i] = new IssueCreateDTO();
            EXAMPLE_ISSUES[i].setSummary("概要" + (i + 1));
            EXAMPLE_ISSUES[i].setDescription("描述" + (i + 1));
        }
    }

    @Override
    public Workbook buildImportTemp() {
        Workbook importTemp = ExcelUtil.getWorkBook(ExcelUtil.Mode.XSSF);

        addReadMeSheet(importTemp);
        addTestCaseSheet(importTemp);

        return importTemp;
    }

    @Override
    public boolean cancelFileUpload(Long historyId) {
        return loadHistoryRepository.cancelFileUpload(historyId);
    }

    @Override
    public void processRow(IssueDTO issueDTO, Row row, List<Integer> errorRowIndexes) {
        if (issueDTO == null) {
            errorRowIndexes.add(row.getRowNum());
            return;
        }

        TestCaseStepE testCaseStepE = buildTestCaseStepE(issueDTO.getIssueId(), row);
        if (testCaseStepE != null) {
            testCaseStepE.createOneStep();
        }

        removeRow(row);
    }

    @Override
    public TestFileLoadHistoryE initLoadHistory(Long projectId, Long folderId, Long userId) {
        TestFileLoadHistoryE loadHistoryE = new TestFileLoadHistoryE(projectId, TestFileLoadHistoryE.Action.UPLOAD_ISSUE,
                TestFileLoadHistoryE.Source.FOLDER, folderId, TestFileLoadHistoryE.Status.SUSPENDING);
        loadHistoryE.setCreationDate(new Date());
        loadHistoryE.setSuccessfulCount(0L);
        loadHistoryE.setFailedCount(0L);
        loadHistoryE.setCreatedBy(userId);
        return loadHistoryRepository.insertOne(loadHistoryE);
    }

    @Override
    public String uploadErrorWorkbook(Workbook errorWorkbook, TestFileLoadHistoryE loadHistoryE) {
        ResponseEntity<String> response = fileService.uploadFile(TestCycleCaseAttachmentRelE.ATTACHMENT_BUCKET, ".xlsx",
                new MultipartExcel("file", ".xlsx", errorWorkbook));

        boolean failed = false;
        if (response.getBody().startsWith("{")) {
            JSONObject jsonObject = JSON.parseObject(response.getBody());
            failed = jsonObject.containsKey("failed") && jsonObject.getBooleanValue("failed");
        }

        if (response.getStatusCode().is2xxSuccessful() && !failed) {
            loadHistoryE.setFileUrl(response.getBody());
            logger.debug(loadHistoryE.getFileUrl());
            return response.getBody();
        } else {
            loadHistoryE.setFileStream(ExcelUtil.getBytes(errorWorkbook));
            return null;
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

    @Override
    public void shiftErrorRowsToTop(Sheet sheet, List<Integer> errorRowIndexes) {
        int i = 0;
        while (i < errorRowIndexes.size()) {
            shiftRow(sheet, errorRowIndexes.get(i), ++i);
        }
    }

    @Override
    @Transactional
    public TestIssueFolderE getFolder(Long projectId, Long versionId, String folderName) {
        TestIssueFolderE folderE = SpringUtil.getApplicationContext().getBean(TestIssueFolderE.class);
        folderE.setProjectId(projectId);
        folderE.setVersionId(versionId);
        folderE.setName(folderName);
        TestIssueFolderE targetfolderE = folderE.queryOne(folderE);
        if (targetfolderE == null) {
            folderE.setType(TestIssueFolderE.TYPE_CYCLE);
            logger.info("{} 文件夹不存在，创建", folderName);
            return folderE.addSelf();
        }

        logger.info("{} 文件夹已存在", folderName);
        return targetfolderE;
    }

    @Override
    public boolean isCanceled(Long id) {
        return Objects.equals(loadHistoryRepository.queryLoadHistoryStatus(id), TestFileLoadHistoryE.Status.CANCEL);
    }

    @Override
    public boolean isIssueHeaderRow(Row row) {
        if (row.getRowNum() == 0) {
            return false;
        }
        if (row.getRowNum() == 1) {
            return true;
        }

        String summary = ExcelUtil.getStringValue(row.getCell(0));
        String description = ExcelUtil.getStringValue(row.getCell(1));
        return StringUtils.isNotBlank(summary) || StringUtils.isNotBlank(description);
    }

    private TestCaseStepE buildTestCaseStepE(Long issueId, Row row) {
        String testStep = ExcelUtil.getStringValue(row.getCell(2));
        String testData = ExcelUtil.getStringValue(row.getCell(3));
        String expectedResult = ExcelUtil.getStringValue(row.getCell(4));

        TestCaseStepE testCaseStepE = null;
        if (StringUtils.isNotBlank(testStep) || StringUtils.isNotBlank(testData) || StringUtils.isNotBlank(expectedResult)) {
            testCaseStepE = SpringUtil.getApplicationContext().getBean(TestCaseStepE.class);
            testCaseStepE.setTestStep(testStep);
            testCaseStepE.setTestData(testData);
            testCaseStepE.setExpectedResult(expectedResult);
            testCaseStepE.setIssueId(issueId);
        }

        return testCaseStepE;
    }

    @Override
    public Long getIssueTypeId(Long organizationId, Long projectId, String applyType, String issueTypeCode) {
        ResponseEntity<List<IssueTypeDTO>> response = issueFeignClient.queryIssueType(projectId, applyType, organizationId);
        if (response.getStatusCode().is2xxSuccessful()) {
            for (IssueTypeDTO issueTypeDTO : response.getBody()) {
                if (Objects.equals(issueTypeCode, issueTypeDTO.getTypeCode())) {
                    return issueTypeDTO.getId();
                }
            }
        }
        return null;
    }

    @Override
    public IssueDTO processIssueHeaderRow(Row row, Long organizationId, Long projectId, Long versionId, Long folderId) {
        if (ExcelUtil.isBlank(row.getCell(0))) {
            markAsError(row, "测试概要不能为空");
            return null;
        }

        String description = ExcelUtil.getStringValue(row.getCell(1));
        String summary = ExcelUtil.getStringValue(row.getCell(0));

        IssueCreateDTO issueCreateDTO = new IssueCreateDTO();
        issueCreateDTO.setProjectId(projectId);
        Long priorityId = AgileUtil.queryDefaultPriorityId(projectId, organizationId, issueFeignClient);
        issueCreateDTO.setPriorityCode("priority-" + priorityId);
        issueCreateDTO.setPriorityId(priorityId);
        issueCreateDTO.setSummary(summary);
        issueCreateDTO.setDescription(description);
        issueCreateDTO.setTypeCode(IssueTypeCode.ISSUE_TEST);
        issueCreateDTO.setIssueTypeId(AgileUtil.queryIssueTypeId(projectId, organizationId, IssueTypeCode.ISSUE_TEST, issueFeignClient));

        VersionIssueRelDTO versionIssueRelDTO = new VersionIssueRelDTO();
        versionIssueRelDTO.setVersionId(versionId);
        versionIssueRelDTO.setRelationType("fix");
        issueCreateDTO.setVersionIssueRelDTOList(Lists.newArrayList(versionIssueRelDTO));

        IssueDTO issueDTO = createIssue(projectId, issueCreateDTO);
        if (issueDTO != null) {
            TestIssueFolderRelE issueFolderRelE = SpringUtil.getApplicationContext().getBean(TestIssueFolderRelE.class);
            issueFolderRelE.setProjectId(projectId);
            issueFolderRelE.setVersionId(versionId);
            issueFolderRelE.setFolderId(folderId);
            issueFolderRelE.setIssueId(issueDTO.getIssueId());
            try {
                issueFolderRelE.addSelf();
            } catch (Exception e) {
                markAsError(row, "导入测试任务异常");
                return null;
            }
        } else {
            markAsError(row, "导入测试任务异常");
        }
        return issueDTO;
    }

    @Override
    public IssueDTO createIssue(Long projectId, IssueCreateDTO issueCreateDTO) {
        return testCaseService.createTest(issueCreateDTO, projectId, "test");
    }

    @Override
    public void removeRow(Row row) {
        for (int i = 0; i <= README_OPTIONS.length; i++) {
            if (row.getCell(i) != null) {
                row.removeCell(row.getCell(i));
            }
        }
    }

    @Override
    public void updateProgress(TestFileLoadHistoryE loadHistoryE, Long userId, double rate) {
        loadHistoryE.setRate(rate);
        notifyService.postWebSocket(IMPORT_NOTIFY_CODE, userId.toString(), JSON.toJSONString(loadHistoryE));

        logger.info("导入进度：{}", rate);
        if (rate == 100.) {
            logger.info("完成");
        }
    }

    @Override
    public void finishImport(TestFileLoadHistoryE loadHistoryE, Long userId, TestFileLoadHistoryE.Status status) {
        loadHistoryE.setLastUpdateDate(new Date());
        loadHistoryE.setStatus(status);

        loadHistoryE = loadHistoryRepository.update(loadHistoryE);
        updateProgress(loadHistoryE, userId, 100.);
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

    @Override
    public boolean isEmptyTemp(Sheet sheet) {
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

    @Override
    public Iterator<Row> rowIteratorSkipFirst(Sheet sheet) {
        Iterator<Row> rowIterator = sheet.rowIterator();
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }

        return rowIterator;
    }

    private void markAsError(Row row, String errorMsg) {
        ExcelUtil.getOrCreateCell(row, README_OPTIONS.length, CELL_TYPE_STRING).setCellValue(errorMsg);

        logger.info("行 {} 发生错误：{}", row.getRowNum() + 1, errorMsg);
    }

    private void addTestCaseSheet(Workbook workbook) {
        Sheet testCaseSheet = workbook.createSheet("测试用例");
        workbook.setSheetOrder("测试用例", 1);

        fillTestCaseSheet(testCaseSheet);
        setTestCaseSheetStyle(testCaseSheet);
    }

    private void addReadMeSheet(Workbook workbook) {
        Sheet readMeSheet = workbook.createSheet("README");
        workbook.setSheetOrder("README", 0);

        fillReadMeSheet(readMeSheet);
        setReadMeSheetStyle(readMeSheet);
    }

    private void writeHeader(Sheet sheet, int rowNum, int colNum) {
        Row header = ExcelUtil.getOrCreateRow(sheet, rowNum);
        for (int i = 0; i < README_OPTIONS.length; i++) {
            Cell cell = header.createCell(i + colNum, CELL_TYPE_STRING);
            if (README_OPTIONS[i].getRequired()) {
                cell.setCellValue(README_OPTIONS[i].getFiled() + "*");
            } else {
                cell.setCellValue(README_OPTIONS[i].getFiled());
            }
        }
    }

    private void writeExample(Sheet sheet, int rowNum, int colNum, IssueCreateDTO issueCreateDTO, TestCaseStepE... steps) {
        Row row = ExcelUtil.getOrCreateRow(sheet, rowNum);
        row.createCell(colNum, CELL_TYPE_STRING).setCellValue(issueCreateDTO.getSummary());
        row.createCell(colNum + 1, CELL_TYPE_STRING).setCellValue(issueCreateDTO.getDescription());

        for (int i = 0; i < steps.length; i++) {
            row = ExcelUtil.getOrCreateRow(sheet, i + rowNum);
            row.createCell(colNum + 2, CELL_TYPE_STRING).setCellValue(steps[i].getTestStep());
            row.createCell(colNum + 3, CELL_TYPE_STRING).setCellValue(steps[i].getTestData());
            row.createCell(colNum + 4, CELL_TYPE_STRING).setCellValue(steps[i].getExpectedResult());
        }
    }

    // 填充测试用例页内容
    private void fillTestCaseSheet(Sheet testCaseSheet) {
        writeHeader(testCaseSheet, 0, 0);
    }

    // 设置测试用例页样式
    private void setTestCaseSheetStyle(Sheet testCaseSheet) {
        setSheetBaseStyle(testCaseSheet);

        CellStyle cellStyle = testCaseSheet.getWorkbook().createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        Font font = testCaseSheet.getWorkbook().createFont();
        font.setColor(Font.COLOR_RED);
        font.setBold(true);
        cellStyle.setFont(font);

        testCaseSheet.setDefaultColumnStyle(README_OPTIONS.length, cellStyle);
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

    // 设置 README 页样式
    private void setReadMeSheetStyle(Sheet readMeSheet) {
        setSheetBaseStyle(readMeSheet);
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
            ExcelReadMeOptionDTO optionDTO = README_OPTIONS[i++];
            readMeSheet.getRow(1).createCell(i, CELL_TYPE_STRING).setCellValue(optionDTO.getFiled());
            readMeSheet.getRow(optionDTO.getRequired() ? 2 : 3).createCell(i, CELL_TYPE_STRING).setCellValue("√");
        }

        writeHeader(readMeSheet, 0, 0);

        readMeSheet.createRow(8).createCell(1, CELL_TYPE_STRING).setCellValue("示例");
        readMeSheet.addMergedRegion(new CellRangeAddress(8, 8, 1, 5));
        writeExample(readMeSheet, 9, 1, EXAMPLE_ISSUES[0], EXAMPLE_TEST_CASE_STEPS);
        writeExample(readMeSheet, 10, 1, EXAMPLE_ISSUES[1], EXAMPLE_TEST_CASE_STEPS[0]);
        writeExample(readMeSheet, 11, 1, EXAMPLE_ISSUES[2],
                EXAMPLE_TEST_CASE_STEPS[0],
                EXAMPLE_TEST_CASE_STEPS[1]
        );
    }
}
