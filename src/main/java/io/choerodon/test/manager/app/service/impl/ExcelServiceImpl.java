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
import io.choerodon.test.manager.api.vo.agile.ProjectDTO;
import io.choerodon.test.manager.infra.feign.BaseFeignClient;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.hzero.boot.file.FileClient;
import org.hzero.boot.message.MessageClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

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
    private static final String EXPORT_ERROR_NOCASE_IN_FOLDER= "no-case-in-folder";
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
    private TestCaseMapper testCaseMapper;

//    @Autowired
//    private FileService fileService;

//    @Autowired
//    private NotifyService notifyService;

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

    @Autowired
    private UserService userService;

    @Autowired
    private FileClient fileClient;

    @Autowired
    private BaseFeignClient baseFeignClient;

    @Autowired
    private MessageClient messageClient;
    /**
     * 失败导出重试
     *
     * @param projectId
     * @param fileHistoryId
     * @param lUserId
     */
    @Override
    public void exportFailCaseByTransaction(Long projectId, Long fileHistoryId, Long lUserId) {
        TestFileLoadHistoryDTO testFileLoadHistoryDTO = new TestFileLoadHistoryDTO();
        testFileLoadHistoryDTO.setId(fileHistoryId);
        TestFileLoadHistoryWithRateVO testFileLoadHistoryWithRateVO = modelMapper.map(testFileLoadHistoryMapper
                .selectByPrimaryKey(testFileLoadHistoryDTO), TestFileLoadHistoryWithRateVO.class);

        TestIssueFolderDTO testIssueFolderDTO = testIssueFolderMapper.selectByPrimaryKey(testFileLoadHistoryWithRateVO.getLinkedId());
        if(ObjectUtils.isEmpty(testIssueFolderDTO)){
            throw new CommonException("error.folder.has.deleted");
        }
        exportCaseFolderByTransaction(testFileLoadHistoryWithRateVO.getProjectId(),testFileLoadHistoryWithRateVO.getLinkedId(),null,null,lUserId,true,fileHistoryId);

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
        ProjectDTO projectDTO = baseFeignClient.queryProject(projectId).getBody();
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        testCycleDTO.setCycleId(cycleId);

        List<TestCycleDTO> testCycleDTOList = cycleMapper.queryChildCycle(testCycleDTO);
        List<Long> cycleIds = Stream.concat(testCycleDTOList.stream().map(TestCycleDTO::getCycleId), Stream.of(cycleId)).collect(Collectors.toList());
        testFileLoadHistoryWithRateVO.setRate(15.0);
        messageClient.sendByUserId(userId,NOTIFYCYCLECODE,JSON.toJSONString(testFileLoadHistoryWithRateVO));
        TestCycleVO cycle = modelMapper.map(cycleMapper.selectOne(testCycleDTO), TestCycleVO.class);

        testFileLoadHistoryWithRateVO.setName(cycle.getCycleName());

        testFileLoadHistoryWithRateVO.setRate(35.0);
        messageClient.sendByUserId(userId,NOTIFYCYCLECODE,JSON.toJSONString(testFileLoadHistoryWithRateVO));
        testCycleService.populateUsers(Lists.newArrayList(cycle));

        testFileLoadHistoryWithRateVO.setRate(55.0);
        messageClient.sendByUserId(userId,NOTIFYCYCLECODE,JSON.toJSONString(testFileLoadHistoryWithRateVO));
        Map<Long, List<TestCycleCaseVO>> cycleCaseMap = Optional.ofNullable(testCycleCaseService.queryCaseAllInfoInCyclesOrVersions(cycleIds.toArray(new Long[cycleIds.size()]), null, projectId, organizationId))
                .orElseGet(ArrayList::new).stream().collect(Collectors.groupingBy(TestCycleCaseVO::getCycleId));
        int sum = 0;
        for (List<TestCycleCaseVO> list : cycleCaseMap.values()) {
            sum += list.size();
        }
        testFileLoadHistoryWithRateVO.setRate(65.0);
        messageClient.sendByUserId(userId,NOTIFYCYCLECODE,JSON.toJSONString(testFileLoadHistoryWithRateVO));
        ExcelExportService service = new <TestCycleVO, TestCycleCaseVO>CycleCaseExcelExportServiceImpl();
        Workbook workbook = ExcelUtil.getWorkBook(ExcelUtil.Mode.XSSF);
        printDebug(EXPORTSUCCESSINFO + ExcelUtil.Mode.XSSF);
        String projectName = testCaseService.getProjectInfo(projectId).getName();
        service.exportWorkBookWithOneSheet(cycleCaseMap, projectName, cycle, workbook);
        testFileLoadHistoryWithRateVO.setRate(95.0);
        messageClient.sendByUserId(userId,NOTIFYCYCLECODE,JSON.toJSONString(testFileLoadHistoryWithRateVO));
        String fileName = projectName + "-" + cycle.getCycleName() + FILESUFFIX;
        downloadWorkBook(projectDTO.getOrganizationId(),workbook, fileName, testFileLoadHistoryWithRateVO, userId, sum, NOTIFYCYCLECODE);
    }

    /**
     * 导出文件夹下所有的测试用例
     *
     * @param projectId
     * @param folderId
     * @param request
     * @param response
     * @param userId
     * @param
     */
    @Override
    @Async
    public void exportCaseFolderByTransaction(Long projectId, Long folderId, HttpServletRequest request, HttpServletResponse response, Long userId,Boolean retry,Long fileHistoryId) {
        TestFileLoadHistoryWithRateVO testFileLoadHistoryWithRateVO = null;
        ProjectDTO projectDTO = baseFeignClient.queryProject(projectId).getBody();
        if (Boolean.TRUE.equals(retry)) {
            TestFileLoadHistoryDTO testFileLoadHistoryDTO = new TestFileLoadHistoryDTO();
            testFileLoadHistoryDTO.setId(fileHistoryId);
             testFileLoadHistoryWithRateVO = modelMapper.map(testFileLoadHistoryMapper
                    .selectByPrimaryKey(testFileLoadHistoryDTO), TestFileLoadHistoryWithRateVO.class);
        } else {
            ExcelUtil.setExcelHeader(request);
            Assert.notNull(projectId, "error.export.cycle.in.one.folderId.not.be.null");
            //插入导出历史
            testFileLoadHistoryWithRateVO = insertHistory(projectId, folderId,
                TestFileLoadHistoryEnums.Source.FOLDER, TestFileLoadHistoryEnums.Action.DOWNLOAD_CASE);
        }
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
        //notifyService.postWebSocket(NOTIFYISSUECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
        messageClient.sendByUserId(userId,NOTIFYISSUECODE,JSON.toJSONString(testFileLoadHistoryWithRateVO));
        List<ExcelCaseVO> excelCaseVOS = handleCase(projectId,folderId);
        if(CollectionUtils.isEmpty(excelCaseVOS)){
            testFileLoadHistoryWithRateVO.setFailedCount(Integer.toUnsignedLong(0));
            testFileLoadHistoryWithRateVO.setStatus(TestFileLoadHistoryEnums.Status.FAILURE.getTypeValue());
            testFileLoadHistoryWithRateVO.setMessage("文件夹下无用例");
            testFileLoadHistoryWithRateVO.setCode(EXPORT_ERROR_NOCASE_IN_FOLDER);
            TestFileLoadHistoryDTO testIssueFolderRelDO = modelMapper.map(testFileLoadHistoryWithRateVO, TestFileLoadHistoryDTO.class);
            testFileLoadHistoryMapper.updateByPrimaryKey(testIssueFolderRelDO);
            //notifyService.postWebSocket(NOTIFYISSUECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
            messageClient.sendByUserId(userId,NOTIFYISSUECODE,JSON.toJSONString(testFileLoadHistoryWithRateVO));
            throw new CommonException("error.folder.no.has.case");
        }
        int sum = excelCaseVOS.size();
        Map<Long, List<ExcelCaseVO>> map = new HashMap<>();
        map.put(1L, excelCaseVOS);
        testFileLoadHistoryWithRateVO.setRate(15.0);
        //notifyService.postWebSocket(NOTIFYISSUECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
        messageClient.sendByUserId(userId,NOTIFYISSUECODE,JSON.toJSONString(testFileLoadHistoryWithRateVO));
        //表格生成相关的文件内容
        service.exportWorkBookWithOneSheet(map, projectName, modelMapper.map(testIssueFolderDTO, TestIssueFolderVO.class), workbook);
        testFileLoadHistoryWithRateVO.setRate(80.0);
        //notifyService.postWebSocket(NOTIFYISSUECODE, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
        messageClient.sendByUserId(userId,NOTIFYISSUECODE,JSON.toJSONString(testFileLoadHistoryWithRateVO));
//        workbook.setSheetHidden(0, true);
        workbook.setActiveSheet(1);
//        workbook.setSheetName(0, LOOKUPSHEETNAME);
//        workbook.setSheetOrder(LOOKUPSHEETNAME, workbook.getNumberOfSheets() - 1);
        workbook.removeSheetAt(0);
        String fileName = projectName + "-" + workbook.getSheetName(0).substring(2) + "-" + folderName + FILESUFFIX;
        // 将workbook上载到对象存储服务中
        downloadWorkBook(projectDTO.getOrganizationId(),workbook, fileName, testFileLoadHistoryWithRateVO, userId, sum, NOTIFYISSUECODE);
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

//        Long[] versionsId = testCaseService.getVersionIds(projectId);

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
//        for (Long versionId : versionsId) {
//            Object needMap = ((HashMap<Long, List<TestIssueFolderRelVO>>) map).clone();
//            service.exportWorkBookWithOneSheet((Map<Long, List>) needMap, projectName,
//                    modelMapper.map(testIssueFolderDTO, TestIssueFolderVO.class), workbook);
//        }
        //准备README页
        ExcelExportService readMeService = new <ExcelReadMeVO, ExcelReadMeOptionVO>ReadMeExcelExportServiceImpl();
        Map<String, List<ExcelReadMeOptionVO>> readMeMap = new HashMap<>();
        ExcelReadMeVO readMeDTO = new ExcelReadMeVO();
        readMeMap.put(readMeDTO.getHeader(), populateReadMeOptions());
        readMeService.exportWorkBookWithOneSheet(readMeMap, projectName, readMeDTO, workbook);

//        workbook.setSheetName(versionsId.length + 1, "README");
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
     * @param organizationId
     * @param workbook
     * @param fileName
     * @param testFileLoadHistoryWithRateVO
     * @param userId
     * @param sum
     * @param code
     */
    private void downloadWorkBook(Long organizationId,Workbook workbook, String fileName, TestFileLoadHistoryWithRateVO testFileLoadHistoryWithRateVO, Long userId, int sum, String code) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
            workbook.write(os);
            byte[] content = os.toByteArray();
            MultipartFile file = new MultipartExcel("file", fileName, EXCELCONTENTTYPE, content);

            testFileLoadHistoryWithRateVO.setRate(99.9);
            //notifyService.postWebSocket(code, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
            messageClient.sendByUserId(userId,code,JSON.toJSONString(testFileLoadHistoryWithRateVO));
            testFileLoadHistoryWithRateVO.setLastUpdateDate(new Date());
            testFileLoadHistoryWithRateVO.setFileStream(Arrays.toString(content));

            //返回上载结果
            String path = fileClient.uploadFile(organizationId,TestAttachmentCode.ATTACHMENT_BUCKET,null, fileName, file);

            //判断是否返回是url
            String regex = "(https?)://[-A-Za-z0-9\\u4E00-\\u9FA5+&@#/%?=~_|!:,.;]+[-A-Za-z0-9\\u4E00-\\u9FA5+&@#/%=~_|]+[.]xlsx";//设置正则表达式
            Pattern pat = Pattern.compile(regex.trim());//比对
            Matcher mat = pat.matcher(Optional.ofNullable(path).orElseGet(String::new).trim());
            if (mat.matches()) {
                testFileLoadHistoryWithRateVO.setFileStream(null);
                testFileLoadHistoryWithRateVO.setSuccessfulCount(Integer.toUnsignedLong(sum));
                testFileLoadHistoryWithRateVO.setStatus(TestFileLoadHistoryEnums.Status.SUCCESS.getTypeValue());
                testFileLoadHistoryWithRateVO.setFileUrl(path);
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

                //notifyService.postWebSocket(code, String.valueOf(userId), JSON.toJSONString(testFileLoadHistoryWithRateVO));
                messageClient.sendByUserId(userId,code,JSON.toJSONString(testFileLoadHistoryWithRateVO));
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

    private List<ExcelCaseVO> handleCase(Long projectId,Long folderId){
        List<ExcelCaseVO> excelCaseVOS = new ArrayList<>();
        List<Long> caseIdList = testCaseService.listAllCaseByFolderId(projectId, folderId);
        if(CollectionUtils.isEmpty(caseIdList)){
            return excelCaseVOS;
        }
        excelCaseVOS = testCaseMapper.excelCaseList(projectId, caseIdList);
        List<Long> userIdList = excelCaseVOS.stream().map(ExcelCaseVO::getLastUpdatedBy).collect(Collectors.toList());
        Map<Long, UserMessageDTO> userMessageDTOMap = userService.queryUsersMap(userIdList);
        if (!MapUtils.isEmpty(userMessageDTOMap)) {
            excelCaseVOS.forEach(e -> {
                e.setCaseNum(e.getProjectCode() +"-"+ e.getCaseNum());
                if (!ObjectUtils.isEmpty(e.getLastUpdatedBy()) && !e.getLastUpdatedBy().equals(0L)) {
                    e.setExecutor(userMessageDTOMap.get(e.getLastUpdatedBy()).getRealName());
                }
            });
        }
        return excelCaseVOS;
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
