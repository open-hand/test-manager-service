package io.choerodon.test.manager.app.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.test.manager.api.vo.agile.ProductVersionDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestAttachmentCode;
import io.choerodon.test.manager.infra.enums.TestFileLoadHistoryEnums;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.ExcelUtil;
import io.choerodon.test.manager.infra.util.MultipartExcel;

/**
 * Created by zongw.lee@gmail.com on 15/10/2018
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ExcelServiceImpl implements ExcelService {

    private static final String EXPORT_ERROR = "error.issue.export";
    private static final String EXPORT_ERROR_WORKBOOK_CLOSE = "error.issue.close.workbook";
    private static final String NOTIFYISSUECODE = "test-issue-export";
    private static final String NOTIFYCYCLECODE = "test-cycle-export";
    private static final String EXPORTSUCCESSINFO = "导出测试详情：创建workbook成功，类型:";
    private static final String LOOKUPSHEETNAME = "数据源页";
    private static final String FILESUFFIX = ".xlsx";
    private static final String EXCELCONTENTTYPE = "application/vnd.ms-excel";
    private Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    private TestCycleService testCycleService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private FileService fileService;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private TestFileLoadHistoryMapper testFileLoadHistoryMapper;

    @Autowired
    private TestCycleMapper cycleMapper;

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    private TestIssueFolderRelMapper testIssueFolderRelMapper;

    @Autowired
    private TestCaseStepMapper testCaseStepMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private  TestIssueFolderService testIssueFolderService;

    /**
     * 失败导出重试
     *
     * @param projectId
     * @param fileHistoryId
     * @param lUserId
     */
    @Override
    @Async
    public void exportFailCaseByTransaction(Long projectId, Long fileHistoryId, Long lUserId) {
        String userId = String.valueOf(lUserId);
        TestFileLoadHistoryDTO testFileLoadHistoryDTO = new TestFileLoadHistoryDTO();
        testFileLoadHistoryDTO.setId(fileHistoryId);
        TestFileLoadHistoryWithRateVO testFileLoadHistoryWithRateVO = modelMapper.map(testFileLoadHistoryMapper
                .selectByPrimaryKey(testFileLoadHistoryDTO), TestFileLoadHistoryWithRateVO.class);

        String projcetName = testCaseService.getProjectInfo(testFileLoadHistoryWithRateVO.getProjectId()).getName();
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        Map<Long, ProductVersionDTO> versions = testCaseService.getVersionInfo(testFileLoadHistoryWithRateVO.getProjectId());
        ProductVersionDTO version = null;

        switch (String.valueOf(testFileLoadHistoryWithRateVO.getSourceType())) {
            case "1":
                testFileLoadHistoryWithRateVO.setName(projcetName);
                break;
            case "2":
                version = versions.get(testFileLoadHistoryWithRateVO.getLinkedId());
                testFileLoadHistoryWithRateVO.setName(Optional.ofNullable(version).map(ProductVersionDTO::getName).orElse("版本已被删除"));
                break;
            case "3":
                testCycleDTO.setCycleId(testFileLoadHistoryWithRateVO.getLinkedId());
                testFileLoadHistoryWithRateVO.setName(Optional.ofNullable(cycleMapper.selectOne(testCycleDTO)).map(TestCycleDTO::getCycleName).orElse("循环已被删除"));
                break;
            default:
                testIssueFolderDTO = testIssueFolderMapper.selectByPrimaryKey(testFileLoadHistoryWithRateVO.getLinkedId());
                testFileLoadHistoryWithRateVO.setName(Optional.ofNullable(testIssueFolderDTO)
                        .map(TestIssueFolderDTO::getName).orElse("文件夹已被删除"));
                version = versions.get(Optional.ofNullable(testIssueFolderDTO).map(TestIssueFolderDTO::getFolderId).orElse(0L));
        }

        String fileName = projcetName + "-" + Optional.ofNullable(version).map(ProductVersionDTO::getName)
                + "-" + Optional.ofNullable(testIssueFolderDTO).map(TestIssueFolderDTO::getName) + "-"
                + Optional.ofNullable(testCycleDTO.getCycleName()) + "-失败重传" + FILESUFFIX;

        MultipartFile file = new MultipartExcel("file", fileName, EXCELCONTENTTYPE, testFileLoadHistoryWithRateVO.getFileStream().getBytes());

        testFileLoadHistoryWithRateVO.setRate(99.9);
        notifyService.postWebSocket(NOTIFYISSUECODE, userId, JSON.toJSONString(testFileLoadHistoryWithRateVO));

        ResponseEntity<String> res = fileService.uploadFile(TestAttachmentCode.ATTACHMENT_BUCKET, fileName, file);

        if (res.getStatusCode().is2xxSuccessful()) {
            testFileLoadHistoryWithRateVO.setLastUpdateDate(new Date());
            testFileLoadHistoryWithRateVO.setFileStream(null);
            testFileLoadHistoryWithRateVO.setSuccessfulCount(testFileLoadHistoryWithRateVO.getFailedCount());
            testFileLoadHistoryWithRateVO.setFailedCount(null);
            testFileLoadHistoryWithRateVO.setStatus(TestFileLoadHistoryEnums.Status.SUCCESS.getTypeValue());
            testFileLoadHistoryWithRateVO.setFileUrl(res.getBody());
            notifyService.postWebSocket(NOTIFYISSUECODE, userId, JSON.toJSONString(testFileLoadHistoryWithRateVO));
            TestFileLoadHistoryDTO testIssueFolderRelDO = modelMapper.map(testFileLoadHistoryWithRateVO, TestFileLoadHistoryDTO.class);
            testFileLoadHistoryMapper.updateByPrimaryKey(testIssueFolderRelDO);
        }
    }

    /**
     * 导出测试循环
     *
     * @param cycleId
     * @param projectId
     * @param request
     * @param response
     * @param userId
     * @param organizationId
     */
    @Override
    @Async
    public void exportCycleCaseInOneCycleByTransaction(Long cycleId, Long projectId, HttpServletRequest request,
                                                       HttpServletResponse response, Long userId, Long organizationId) {
        ExcelUtil.setExcelHeader(request);
        Assert.notNull(cycleId, "error.export.cycle.in.one.cycleId.not.be.null");
        TestFileLoadHistoryWithRateVO testFileLoadHistoryWithRateVO = insertHistory(projectId, cycleId,
                TestFileLoadHistoryEnums.Source.CYCLE, TestFileLoadHistoryEnums.Action.DOWNLOAD_CYCLE);

        TestCycleDTO testCycleDTO = new TestCycleDTO();
        testCycleDTO.setCycleId(cycleId);

        List<TestCycleDTO> testCycleDTOList = cycleMapper.queryChildCycle(testCycleDTO);
        List<Long> cycleIds = Stream.concat(testCycleDTOList.stream().map(TestCycleDTO::getCycleId), Stream.of(cycleId)).collect(Collectors.toList());
        testFileLoadHistoryWithRateVO.setRate(15.0);
        notifyService.postWebSocket(NOTIFYCYCLECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
        TestCycleVO cycle = modelMapper.map(cycleMapper.selectOne(testCycleDTO), TestCycleVO.class);

        testFileLoadHistoryWithRateVO.setName(cycle.getCycleName());

        testCycleService.populateVersion(cycle, projectId);
        testFileLoadHistoryWithRateVO.setRate(35.0);
        notifyService.postWebSocket(NOTIFYCYCLECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
        testCycleService.populateUsers(Lists.newArrayList(cycle));

        testFileLoadHistoryWithRateVO.setRate(55.0);
        notifyService.postWebSocket(NOTIFYCYCLECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
        Map<Long, List<TestCycleCaseVO>> cycleCaseMap = Optional.ofNullable(testCycleCaseService.queryCaseAllInfoInCyclesOrVersions(cycleIds.toArray(new Long[cycleIds.size()]), null, projectId, organizationId))
                .orElseGet(ArrayList::new).stream().collect(Collectors.groupingBy(TestCycleCaseVO::getCycleId));
        int sum = 0;
        for (List<TestCycleCaseVO> list : cycleCaseMap.values()) {
            sum += list.size();
        }
        testFileLoadHistoryWithRateVO.setRate(65.0);
        notifyService.postWebSocket(NOTIFYCYCLECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
        ExcelExportService service = new <TestCycleVO, TestCycleCaseVO>CycleCaseExcelExportServiceImpl();
        Workbook workbook = ExcelUtil.getWorkBook(ExcelUtil.Mode.XSSF);
        printDebug(EXPORTSUCCESSINFO + ExcelUtil.Mode.XSSF);
        String projectName = testCaseService.getProjectInfo(projectId).getName();
        service.exportWorkBookWithOneSheet(cycleCaseMap, projectName, cycle, workbook);
        testFileLoadHistoryWithRateVO.setRate(95.0);
        notifyService.postWebSocket(NOTIFYCYCLECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
        String fileName = projectName + "-" + cycle.getCycleName() + FILESUFFIX;
        downloadWorkBook(workbook, fileName, testFileLoadHistoryWithRateVO, userId, sum, NOTIFYCYCLECODE);
    }

    /**
     * 导出项目下所有测试用例
     *
     * @param projectId
     * @param request
     * @param response
     * @param userId
     * @param organizationId
     */
    @Override
    @Async
    public void exportCaseProjectByTransaction(Long projectId, HttpServletRequest request, HttpServletResponse response, Long userId, Long organizationId) {
        ExcelUtil.setExcelHeader(request);
        TestFileLoadHistoryWithRateVO testFileLoadHistoryWithRateVO = insertHistory(projectId, projectId,
                TestFileLoadHistoryEnums.Source.PROJECT, TestFileLoadHistoryEnums.Action.DOWNLOAD_ISSUE);

        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        testIssueFolderDTO.setProjectId(projectId);

        String projectName = testCaseService.getProjectInfo(projectId).getName();

        testFileLoadHistoryWithRateVO.setName(projectName);

        Long[] versionsId = testCaseService.getVersionIds(projectId);

        Workbook workbook = ExcelUtil.getWorkBook(ExcelUtil.Mode.XSSF);
        printDebug(EXPORTSUCCESSINFO + ExcelUtil.Mode.XSSF);
        ExcelExportService service = new <TestIssueFolderVO, TestIssueFolderRelVO>TestCaseExcelExportServiceImpl();

        service.exportWorkBookWithOneSheet(new HashMap<>(), projectName, modelMapper.map(testIssueFolderDTO, TestIssueFolderVO.class), workbook);
        testFileLoadHistoryWithRateVO.setRate(5.0);
        notifyService.postWebSocket(NOTIFYISSUECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));

        double versionOffset = 90.00 / versionsId.length;
        int i = 0;
        Map<Long, List<TestIssueFolderRelVO>> allRelMaps = new HashMap<>();
        //分别导出版本到各个sheet页中
        for (Long versionId : versionsId) {
            Map<Long, List<TestIssueFolderRelVO>> everyRelMaps = populateFolder(testIssueFolderDTO, userId,
                    5 + (versionOffset * (i++)), versionOffset, testFileLoadHistoryWithRateVO, organizationId);
            allRelMaps.putAll(everyRelMaps);
            service.exportWorkBookWithOneSheet(everyRelMaps, projectName, modelMapper.map(testIssueFolderDTO, TestIssueFolderVO.class), workbook);
        }
        int sum = 0;
        for (List<TestIssueFolderRelVO> list : allRelMaps.values()) {
            sum += list.size();
        }
        testFileLoadHistoryWithRateVO.setRate(95.0);
        notifyService.postWebSocket(NOTIFYISSUECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
        workbook.setSheetHidden(0, true);
        workbook.setActiveSheet(1);
        workbook.setSheetName(0, LOOKUPSHEETNAME);
        workbook.setSheetOrder(LOOKUPSHEETNAME, workbook.getNumberOfSheets() - 1);
        String fileName = projectName + FILESUFFIX;
        downloadWorkBook(workbook, fileName, testFileLoadHistoryWithRateVO, userId, sum, NOTIFYISSUECODE);
    }

    /**
     * 导出版本下所有测试用例
     *
     * @param projectId
     * @param versionId
     * @param request
     * @param response
     * @param userId
     * @param organizationId
     */
    @Override
    @Async
    public void exportCaseVersionByTransaction(Long projectId, Long versionId, HttpServletRequest request, HttpServletResponse response, Long userId, Long organizationId) {
        ExcelUtil.setExcelHeader(request);
        Assert.notNull(versionId, "error.export.cycle.in.one.versionId.not.be.null");

        TestFileLoadHistoryWithRateVO testFileLoadHistoryWithRateVO = insertHistory(projectId, versionId,
                TestFileLoadHistoryEnums.Source.VERSION, TestFileLoadHistoryEnums.Action.DOWNLOAD_ISSUE);
        String projectName = testCaseService.getProjectInfo(projectId).getName();

        String versionName = testCaseService.getVersionInfo(projectId).get(versionId).getName();

        testFileLoadHistoryWithRateVO.setName(versionName);

        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        testIssueFolderDTO.setProjectId(projectId);

        Workbook workbook = ExcelUtil.getWorkBook(ExcelUtil.Mode.XSSF);
        printDebug(EXPORTSUCCESSINFO + ExcelUtil.Mode.XSSF);
        ExcelExportService service = new <TestIssueFolderVO, TestIssueFolderRelVO>TestCaseExcelExportServiceImpl();
        service.exportWorkBookWithOneSheet(new HashMap<>(), projectName, modelMapper.map(testIssueFolderDTO, TestIssueFolderVO.class), workbook);

        testFileLoadHistoryWithRateVO.setRate(5.0);
        notifyService.postWebSocket(NOTIFYISSUECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));

        Map<Long, List<TestIssueFolderRelVO>> everyRelMaps = populateFolder(testIssueFolderDTO, userId, 5, 90, testFileLoadHistoryWithRateVO, organizationId);

        int sum = 0;
        for (List<TestIssueFolderRelVO> list : everyRelMaps.values()) {
            sum += list.size();
        }
        service.exportWorkBookWithOneSheet(everyRelMaps, projectName, modelMapper.map(testIssueFolderDTO, TestIssueFolderVO.class), workbook);

        workbook.setSheetHidden(0, true);
        workbook.setActiveSheet(1);
        workbook.setSheetName(0, LOOKUPSHEETNAME);
        workbook.setSheetOrder(LOOKUPSHEETNAME, workbook.getNumberOfSheets() - 1);
        String fileName = projectName + "-" + versionName + FILESUFFIX;
        downloadWorkBook(workbook, fileName, testFileLoadHistoryWithRateVO, userId, sum, NOTIFYISSUECODE);
    }

    /**
     * 导出文件夹下所有的测试用例
     *
     * @param projectId
     * @param folderId
     * @param request
     * @param response
     * @param userId
     * @param organizationId
     */
    @Override
    @Async
    public void exportCaseFolderByTransaction(Long projectId, Long folderId, HttpServletRequest request, HttpServletResponse response, Long userId, Long organizationId) {
        ExcelUtil.setExcelHeader(request);
        Assert.notNull(projectId, "error.export.cycle.in.one.folderId.not.be.null");
        //插入导出历史
        TestFileLoadHistoryWithRateVO testFileLoadHistoryWithRateVO = insertHistory(projectId, folderId,
                TestFileLoadHistoryEnums.Source.FOLDER, TestFileLoadHistoryEnums.Action.DOWNLOAD_ISSUE);

        String projectName = testCaseService.getProjectInfo(projectId).getName();

        //根据文件夹id查出当前文件夹名称
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        testIssueFolderDTO.setProjectId(projectId);
        testIssueFolderDTO.setFolderId(folderId);
        testIssueFolderDTO = testIssueFolderMapper.selectByPrimaryKey(folderId);
        String folderName = testIssueFolderDTO.getName();
        testFileLoadHistoryWithRateVO.setName(folderName);
        // 创建excel表格
        Workbook workbook = ExcelUtil.getWorkBook(ExcelUtil.Mode.XSSF);
        printDebug(EXPORTSUCCESSINFO + ExcelUtil.Mode.XSSF);
        ExcelExportService service = new <TestIssueFolderVO, TestIssueFolderRelVO>TestCaseExcelExportServiceImpl();

        //表格头部生成当前项目名称所属文件夹目录
        service.exportWorkBookWithOneSheet(new HashMap<>(), projectName, modelMapper.map(testIssueFolderDTO, TestIssueFolderVO.class), workbook);
        testFileLoadHistoryWithRateVO.setRate(5.0);
        notifyService.postWebSocket(NOTIFYISSUECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));

        Map<Long, List<TestIssueFolderRelVO>> everyRelMaps = populateFolder(testIssueFolderDTO, userId, 5, 90, testFileLoadHistoryWithRateVO, organizationId);

        int sum = 0;
        for (List<TestIssueFolderRelVO> list : everyRelMaps.values()) {
            sum += list.size();
        }
        //表格生成相关的文件内容
        service.exportWorkBookWithOneSheet(everyRelMaps, projectName, modelMapper.map(testIssueFolderDTO, TestIssueFolderVO.class), workbook);

        workbook.setSheetHidden(0, true);
        workbook.setActiveSheet(1);
        workbook.setSheetName(0, LOOKUPSHEETNAME);
        workbook.setSheetOrder(LOOKUPSHEETNAME, workbook.getNumberOfSheets() - 1);
        String fileName = projectName + "-" + workbook.getSheetName(0).substring(2) + "-" + folderName + FILESUFFIX;
        // 将workbook上载到对象存储服务中
        downloadWorkBook(workbook, fileName, testFileLoadHistoryWithRateVO, userId, sum, NOTIFYISSUECODE);
    }

    /**
     * 导出模板
     *
     * @param projectId
     * @param request
     * @param response
     */
    @Override
    public void exportCaseTemplate(Long projectId, HttpServletRequest request, HttpServletResponse response) {
        ExcelUtil.setExcelHeaderByStream(request, response);

        String projectName = testCaseService.getProjectInfo(projectId).getName();

        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        testIssueFolderDTO.setProjectId(projectId);

        Workbook workbook = ExcelUtil.getWorkBook(ExcelUtil.Mode.XSSF);
        printDebug(EXPORTSUCCESSINFO + ExcelUtil.Mode.XSSF);

        Long[] versionsId = testCaseService.getVersionIds(projectId);

        Map<Long, List<TestIssueFolderRelVO>> map = new HashMap<>();
        List<TestIssueFolderRelVO> testIssueFolderRelVOS = new ArrayList<>();
        TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO();
        IssueInfosVO issueInfosVO = new IssueInfosVO();
        issueInfosVO.setAssigneeId(1L);
        issueInfosVO.setPriorityCode("1");
        testIssueFolderRelVO.setFolderId(1L);
        testIssueFolderRelVO.setIssueInfosVO(issueInfosVO);
        testIssueFolderRelVOS.add(testIssueFolderRelVO);
        map.put(1L, testIssueFolderRelVOS);

        ExcelExportService service = new <TestIssueFolderVO, TestIssueFolderRelVO>TestCaseExcelExportServiceImpl();
        //准备lookup页
        service.exportWorkBookWithOneSheet(new HashMap<>(), projectName,
                modelMapper.map(testIssueFolderDTO, TestIssueFolderVO.class), workbook);
        for (Long versionId : versionsId) {
            Object needMap = ((HashMap<Long, List<TestIssueFolderRelVO>>) map).clone();
            service.exportWorkBookWithOneSheet((Map<Long, List>) needMap, projectName,
                    modelMapper.map(testIssueFolderDTO, TestIssueFolderVO.class), workbook);
        }
        //准备README页
        ExcelExportService readMeService = new <ExcelReadMeVO, ExcelReadMeOptionVO>ReadMeExcelExportServiceImpl();
        Map<String, List<ExcelReadMeOptionVO>> readMeMap = new HashMap<>();
        ExcelReadMeVO readMeDTO = new ExcelReadMeVO();
        readMeMap.put(readMeDTO.getHeader(), populateReadMeOptions());
        readMeService.exportWorkBookWithOneSheet(readMeMap, projectName, readMeDTO, workbook);

        workbook.setSheetName(versionsId.length + 1, "README");
        workbook.setSheetName(0, LOOKUPSHEETNAME);
        workbook.setSheetOrder(LOOKUPSHEETNAME, workbook.getNumberOfSheets() - 1);
        workbook.setSheetHidden(workbook.getNumberOfSheets() - 1, true);
        workbook.setSheetOrder("README", 0);
        workbook.setActiveSheet(0);
        downloadWorkBookByStream(workbook, response);
    }

    /**
     * 上载文件到minio
     *
     * @param workbook
     * @param fileName
     * @param testFileLoadHistoryWithRateVO
     * @param userId
     * @param sum
     * @param code
     */
    private void downloadWorkBook(Workbook workbook, String fileName, TestFileLoadHistoryWithRateVO testFileLoadHistoryWithRateVO, Long userId, int sum, String code) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
            workbook.write(os);
            byte[] content = os.toByteArray();
            MultipartFile file = new MultipartExcel("file", fileName, EXCELCONTENTTYPE, content);

            testFileLoadHistoryWithRateVO.setRate(99.9);
            notifyService.postWebSocket(code, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));

            testFileLoadHistoryWithRateVO.setLastUpdateDate(new Date());
            testFileLoadHistoryWithRateVO.setFileStream(Arrays.toString(content));

            //返回上载结果
            ResponseEntity<String> res = fileService.uploadFile(TestAttachmentCode.ATTACHMENT_BUCKET, fileName, file);

            //判断是否返回是url
            String regex = "(https?)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]+[.]xlsx";//设置正则表达式
            Pattern pat = Pattern.compile(regex.trim());//比对
            Matcher mat = pat.matcher(Optional.ofNullable(res.getBody()).orElseGet(String::new).trim());
            if (mat.matches()) {
                testFileLoadHistoryWithRateVO.setFileStream(null);
                testFileLoadHistoryWithRateVO.setSuccessfulCount(Integer.toUnsignedLong(sum));
                testFileLoadHistoryWithRateVO.setStatus(TestFileLoadHistoryEnums.Status.SUCCESS.getTypeValue());
                testFileLoadHistoryWithRateVO.setFileUrl(res.getBody());
            } else {
                testFileLoadHistoryWithRateVO.setFailedCount(Integer.toUnsignedLong(sum));
                testFileLoadHistoryWithRateVO.setStatus(TestFileLoadHistoryEnums.Status.FAILURE.getTypeValue());
            }
        } catch (Exception e) {
            testFileLoadHistoryWithRateVO.setFailedCount(Integer.toUnsignedLong(sum));
            testFileLoadHistoryWithRateVO.setStatus(TestFileLoadHistoryEnums.Status.FAILURE.getTypeValue());
            printDebug(e.getMessage());
        } finally {
            try {
                TestFileLoadHistoryDTO testIssueFolderRelDO = modelMapper.map(testFileLoadHistoryWithRateVO, TestFileLoadHistoryDTO.class);
                testFileLoadHistoryMapper.updateByPrimaryKey(testIssueFolderRelDO);
                testFileLoadHistoryWithRateVO.setFileStream(null);
                notifyService.postWebSocket(code, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
                workbook.close();
            } catch (IOException e) {
                log.warn(EXPORT_ERROR_WORKBOOK_CLOSE, e);
            }
        }
    }

    /**
     * 通过流同步下载
     *
     * @param workbook
     * @param response
     */
    @Override
    public void downloadWorkBookByStream(Workbook workbook, HttpServletResponse response) {
        try {
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new CommonException(EXPORT_ERROR, e);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                log.warn(EXPORT_ERROR_WORKBOOK_CLOSE, e);
            }
        }
    }

    /**
     * 按照一定格式装载信息到特定Map中
     *
     * @param testIssueFolderDTO
     * @param userId
     * @param startRate
     * @param offset
     * @return 适用于装载excel的Map
     */
    private Map<Long, List<TestIssueFolderRelVO>> populateFolder(TestIssueFolderDTO testIssueFolderDTO, Long userId, double startRate, double offset, TestFileLoadHistoryWithRateVO testFileLoadHistoryWithRateVO, Long organizationId) {
        // TODO：重构，取消项目和版本之后，假如选择的是父文件夹，那么要生成多个子文件夹
        List<TestIssueFolderDTO> folders;
        if (testIssueFolderDTO == null){
            throw  new CommonException("error.issue.folder.is.null");
        }
        folders = testIssueFolderService.queryChildFolder(testIssueFolderDTO.getFolderId());

        //List<TestIssueFolderDTO> folders = Optional.ofNullable(testIssueFolderMapper.select(testIssueFolderDTO)).orElseGet(ArrayList::new);

        //后面一个循环中使用了 两次进度增加 所以/2
        double folderOffset = offset / (folders.size() * 2);

        Map<Long, List<TestIssueFolderRelVO>> folderRelMap = new HashMap<>();

        TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();

        List<TestCaseStepVO> testCaseStepVOList = modelMapper.map(testCaseStepMapper
                .query(testCaseStepDTO), new TypeToken<List<TestCaseStepVO>>() {
        }.getType());
        Map<Long, List<TestCaseStepVO>> caseStepMap = Optional.ofNullable(testCaseStepVOList).orElseGet(ArrayList::new).stream().collect(Collectors.groupingBy(TestCaseStepVO::getIssueId));

        int i = 0;
        for (TestIssueFolderDTO folder : folders) {
            TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
            testIssueFolderRelDTO.setFolderId(folder.getFolderId());
            List<TestIssueFolderRelDTO> folderRels = testIssueFolderRelMapper.select(testIssueFolderRelDTO);

            List<TestIssueFolderRelVO> folderRelDTOS = new ArrayList<>();

            List<Long> issueIds = folderRels.stream().map(TestIssueFolderRelDTO::getIssueId).collect(Collectors.toList());
            testFileLoadHistoryWithRateVO.setRate(startRate + folderOffset * (++i));
            notifyService.postWebSocket(NOTIFYISSUECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));

            Map<Long, IssueInfosVO> issueInfosMap = batchGetIssueInfo(issueIds, testIssueFolderDTO, userId, startRate + (folderOffset * i),
                    folderOffset, testFileLoadHistoryWithRateVO, organizationId);

            testFileLoadHistoryWithRateVO.setRate(startRate + (folderOffset * (++i)));
            notifyService.postWebSocket(NOTIFYISSUECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));

            for (TestIssueFolderRelDTO folderRel : folderRels) {
                TestIssueFolderRelVO needRel = modelMapper.map(folderRel, TestIssueFolderRelVO.class);
                needRel.setIssueInfosVO(issueInfosMap.get(folderRel.getIssueId()));
                needRel.setFolderName(folder.getName());

                needRel.setTestCaseStepVOS(caseStepMap.get(folderRel.getIssueId()));

                folderRelDTOS.add(needRel);
            }

            folderRelMap.put(folder.getFolderId(), folderRelDTOS);
        }
        testFileLoadHistoryWithRateVO.setRate(startRate + offset);
        notifyService.postWebSocket(NOTIFYISSUECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
        return folderRelMap;
    }

    /**
     * 批量获取到issue信息
     *
     * @param issueIds
     * @param testIssueFolderDTO
     * @param userId
     * @param startRate
     * @param offset
     * @param testFileLoadHistoryWithRateVO
     * @param organizationId
     * @return
     */
    private Map<Long, IssueInfosVO> batchGetIssueInfo(List<Long> issueIds, TestIssueFolderDTO testIssueFolderDTO, Long userId, double startRate, double offset, TestFileLoadHistoryWithRateVO testFileLoadHistoryWithRateVO, Long organizationId) {
        Map<Long, IssueInfosVO> issueInfosMap = new HashMap<>();

        int flag = issueIds.size() / 40;
        double issuesOffset = offset / (flag + 1.00);
        for (int j = 0; j <= flag; j++) {
            Long[] toSendIds;
            if (issueIds.size() > 40 && j != flag) {
                toSendIds = issueIds.subList(j * 40, (j + 1) * 40).toArray(new Long[40]);
                testFileLoadHistoryWithRateVO.setRate(startRate + (j + 1) * issuesOffset);
                notifyService.postWebSocket(NOTIFYISSUECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
            } else {
                toSendIds = issueIds.subList(j * 40, issueIds.size()).toArray(new Long[40]);
            }
            if (!ObjectUtils.isEmpty(issueIds)) {
                printDebug("开始分批获取issue信息（最大40一批），当前第" + (j + 1) + "批");
                issueInfosMap.putAll(testCaseService.getIssueInfoMap(testIssueFolderDTO.getProjectId(), toSendIds, true, organizationId));
            }
        }
        return issueInfosMap;
    }

    /**
     * 傻瓜式装载read
     *
     * @return
     */
    private List<ExcelReadMeOptionVO> populateReadMeOptions() {
        List<ExcelReadMeOptionVO> optionDTOS = new ArrayList<>();
        optionDTOS.add(new ExcelReadMeOptionVO("文件夹", true));
        optionDTOS.add(new ExcelReadMeOptionVO("用例概要", true));
        optionDTOS.add(new ExcelReadMeOptionVO("用例编号", false));
        //optionDTOS.add(new ExcelReadMeOptionVO("优先级", true));
        optionDTOS.add(new ExcelReadMeOptionVO("用例描述", false));
        optionDTOS.add(new ExcelReadMeOptionVO("被指定人", false));
        optionDTOS.add(new ExcelReadMeOptionVO("状态", false));
        optionDTOS.add(new ExcelReadMeOptionVO("测试步骤", false));
        optionDTOS.add(new ExcelReadMeOptionVO("测试数据", false));
        optionDTOS.add(new ExcelReadMeOptionVO("预期结果", false));
        optionDTOS.add(new ExcelReadMeOptionVO("文件夹ID(系统自动生成)", null));
        optionDTOS.add(new ExcelReadMeOptionVO("优先级valueCode(系统自动生成)", null));
        optionDTOS.add(new ExcelReadMeOptionVO("经办人ID(系统自动生成)", null));

        return optionDTOS;
    }

    private void printDebug(String info) {
        if (log.isDebugEnabled()) {
            log.debug(info);
        }
    }

    private TestFileLoadHistoryWithRateVO insertHistory(Long projectId, Long optionalParam, TestFileLoadHistoryEnums.Source source, TestFileLoadHistoryEnums.Action action) {
        TestFileLoadHistoryWithRateVO testFileLoadHistoryWithRateVO = new TestFileLoadHistoryWithRateVO();
        testFileLoadHistoryWithRateVO.setProjectId(projectId);
        testFileLoadHistoryWithRateVO.setActionType(action.getTypeValue());
        testFileLoadHistoryWithRateVO.setSourceType(source.getTypeValue());
        testFileLoadHistoryWithRateVO.setLinkedId(optionalParam);
        testFileLoadHistoryWithRateVO.setStatus(TestFileLoadHistoryEnums.Status.SUSPENDING.getTypeValue());

        TestFileLoadHistoryDTO testFileLoadHistoryDTO = modelMapper.map(testFileLoadHistoryWithRateVO, TestFileLoadHistoryDTO.class);
        testFileLoadHistoryMapper.insert(testFileLoadHistoryDTO);

        return modelMapper.map(testFileLoadHistoryMapper.selectByPrimaryKey(testFileLoadHistoryDTO), TestFileLoadHistoryWithRateVO.class);
    }
}
