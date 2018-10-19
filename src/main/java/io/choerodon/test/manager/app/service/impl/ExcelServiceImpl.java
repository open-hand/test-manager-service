package io.choerodon.test.manager.app.service.impl;

import com.google.common.collect.Lists;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.dto.*;
import io.choerodon.test.manager.app.service.ExcelService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.domain.service.IExcelService;
import io.choerodon.test.manager.domain.service.impl.ICycleCaseExcelServiceImpl;
import io.choerodon.test.manager.domain.service.impl.ITestCaseExcelServiceImpl;
import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderRelE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCaseStepEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestIssueFolderEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestIssueFolderRelEFactory;
import io.choerodon.test.manager.infra.common.utils.ExcelUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by 842767365@qq.com on 8/9/18.
 */

@Component
public class ExcelServiceImpl implements ExcelService {

    private static final String EXPORT_ERROR = "error.issue.export";
    private static final String EXPORT_ERROR_WORKBOOK_CLOSE = "error.issue.close.workbook";
    private static final String EXPORT_ERROR_SET_HEADER = "error.issue.set.header";

    private static final String FILENAME = "cheorodon";
    private static final String EXPORTSUCCESSINFO = "导出测试详情：创建workbook成功，类型:";


    Log log = LogFactory.getLog(this.getClass());
    @Autowired
    TestCycleCaseService testCycleCaseService;

    @Autowired
    IExcelService iExcelService;

    @Autowired
    TestCycleService testCycleService;

    @Autowired
    TestCaseService testCaseService;


    /**
     * 设置http请求报文为下载文件
     *
     * @param response
     * @param request
     * @param fileName
     * @throws UnsupportedEncodingException
     **/
    private void setExcelHeader(HttpServletResponse response, HttpServletRequest request, String fileName) {
        String charsetName = "UTF-8";
        if (request.getHeader("User-Agent").contains("Firefox")) {
            charsetName = "GB2312";
        }

        response.reset();
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        try {
            response.setHeader("Content-Disposition", "attachment;filename="
                    + new String((fileName + ".xlsx").getBytes(charsetName),
                    "ISO-8859-1"));
        } catch (UnsupportedEncodingException e1) {
            throw new CommonException(EXPORT_ERROR_SET_HEADER, e1);
        }

    }


    /**
     * 导出一个cycle下的测试详情，默认HSSFWorkBook
     *
     * @param cycleId
     * @param projectId
     */
    @Override
    public void exportCycleCaseInOneCycle(Long cycleId, Long projectId, HttpServletRequest request,
                                          HttpServletResponse response) {
        setExcelHeader(response, request, FILENAME);


        Assert.notNull(cycleId, "error.export.cycle.in.one.cycleId.not.be.null");
        TestCycleE cycleE = TestCycleEFactory.create();
        cycleE.setCycleId(cycleId);
        Long[] cycleIds = Stream.concat(cycleE.getChildFolder().stream().map(TestCycleE::getCycleId), Stream.of(cycleId)).toArray(Long[]::new);
        TestCycleDTO cycle = ConvertHelper.convert(cycleE.queryOne(), TestCycleDTO.class);
        testCycleService.populateVersion(cycle, projectId);
        testCycleService.populateUsers(Lists.newArrayList(cycle));
        Map<Long, List<TestCycleCaseDTO>> cycleCaseMap = Optional.ofNullable(testCycleCaseService.queryCaseAllInfoInCyclesOrVersions(cycleIds, null, projectId))
                .orElseGet(ArrayList::new).stream().collect(Collectors.groupingBy(TestCycleCaseDTO::getCycleId));
        IExcelService service = new <TestCycleDTO, TestCycleCaseDTO>ICycleCaseExcelServiceImpl();
        Workbook workbook = ExcelUtil.getWorkBook(ExcelUtil.Mode.XSSF);
        if (log.isDebugEnabled()) {
            log.debug(EXPORTSUCCESSINFO + ExcelUtil.Mode.XSSF);
        }
        Workbook needWorkbook = service.exportWorkBookWithOneSheet(cycleCaseMap, testCaseService.getProjectInfo(projectId).getName(), cycle, workbook);
        downloadWorkBook(needWorkbook, response);
    }

    /**
     * 导出项目下的所有用例，默认XSSF WorkBook
     *
     * @param projectId not null
     * @param request
     * @param response
     */
    @Override
    public void exportCaseByProject(Long projectId, HttpServletRequest request, HttpServletResponse response) {
        setExcelHeader(response, request, FILENAME);

        TestIssueFolderE folderE = TestIssueFolderEFactory.create();
        folderE.setProjectId(projectId);

        Long[] versionsId = testCaseService.getVersionIds(projectId);

        Workbook workbook = ExcelUtil.getWorkBook(ExcelUtil.Mode.XSSF);
        if (log.isDebugEnabled()) {
            log.debug(EXPORTSUCCESSINFO + ExcelUtil.Mode.XSSF);
        }
        IExcelService service = new <TestIssueFolderDTO, TestIssueFolderRelDTO>ITestCaseExcelServiceImpl();

        Workbook needWorkbook = null;

        for (Long versionId : versionsId) {
            folderE.setVersionId(versionId);
            needWorkbook = service.exportWorkBookWithOneSheet(populateFolder(folderE), testCaseService.getProjectInfo(projectId).getName(),
                    ConvertHelper.convert(folderE, TestIssueFolderDTO.class), workbook);
        }
        downloadWorkBook(needWorkbook != null ? needWorkbook : workbook, response);
    }

    /**
     * 导出项目下的所有用例，默认XSSF WorkBook
     *
     * @param versionId not null
     * @param request
     * @param response
     */
    @Override
    public void exportCaseByVersion(Long projectId, Long versionId, HttpServletRequest request, HttpServletResponse response) {
        setExcelHeader(response, request, FILENAME);

        Assert.notNull(versionId, "error.export.cycle.in.one.versionId.not.be.null");

        TestIssueFolderE folderE = TestIssueFolderEFactory.create();
        folderE.setProjectId(projectId);
        folderE.setVersionId(versionId);

        Workbook workbook = ExcelUtil.getWorkBook(ExcelUtil.Mode.XSSF);
        if (log.isDebugEnabled()) {
            log.debug(EXPORTSUCCESSINFO + ExcelUtil.Mode.XSSF);
        }
        IExcelService service = new <TestIssueFolderDTO, TestIssueFolderRelDTO>ITestCaseExcelServiceImpl();
        Workbook needWorkbook = service.exportWorkBookWithOneSheet(populateFolder(folderE), testCaseService.getProjectInfo(projectId).getName(), ConvertHelper.convert(folderE, TestIssueFolderDTO.class), workbook);
        downloadWorkBook(needWorkbook, response);
    }


    @Override
    public void exportCaseByFolder(Long projectId, Long folderId, HttpServletRequest request, HttpServletResponse response) {
        setExcelHeader(response, request, FILENAME);

        Assert.notNull(projectId, "error.export.cycle.in.one.folderId.not.be.null");

        TestIssueFolderE folderE = TestIssueFolderEFactory.create();
        folderE.setProjectId(projectId);
        folderE.setFolderId(folderId);

        Workbook workbook = ExcelUtil.getWorkBook(ExcelUtil.Mode.XSSF);
        if (log.isDebugEnabled()) {
            log.debug(EXPORTSUCCESSINFO + ExcelUtil.Mode.XSSF);
        }
        IExcelService service = new <TestIssueFolderDTO, TestIssueFolderRelDTO>ITestCaseExcelServiceImpl();
        Workbook needWorkbook = service.exportWorkBookWithOneSheet(populateFolder(folderE), testCaseService.getProjectInfo(projectId).getName(), ConvertHelper.convert(folderE, TestIssueFolderDTO.class), workbook);
        downloadWorkBook(needWorkbook, response);
    }

    @Override
    public void exportCaseTemplate(Long projectId, HttpServletRequest request, HttpServletResponse response) {
        setExcelHeader(response, request, FILENAME);

        Workbook workbook = ExcelUtil.getWorkBook(ExcelUtil.Mode.XSSF);
        if (log.isDebugEnabled()) {
            log.debug(EXPORTSUCCESSINFO + ExcelUtil.Mode.XSSF);
        }

        IExcelService service = new <TestIssueFolderDTO, TestIssueFolderRelDTO>ITestCaseExcelServiceImpl();
        Workbook needWorkbook = service.exportWorkBookWithOneSheet(new HashMap<>(), "模板项目", new TestIssueFolderDTO(), workbook);
        downloadWorkBook(needWorkbook, response);
    }


    private void downloadWorkBook(Workbook workbook, HttpServletResponse response) {
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

    private Map<Long, List<TestIssueFolderRelDTO>> populateFolder(TestIssueFolderE folderE) {
        List<TestIssueFolderE> folders = Optional.ofNullable(folderE.queryAllUnderProject()).orElseGet(ArrayList::new);

        Map<Long, List<TestIssueFolderRelDTO>> folderRelMap = new HashMap<>();

        for (TestIssueFolderE folder : folders) {
            TestIssueFolderRelE folderRelE = TestIssueFolderRelEFactory.create();
            folderRelE.setFolderId(folder.getFolderId());
            List<TestIssueFolderRelE> folderRels = folderRelE.queryAllUnderProject();

            List<TestIssueFolderRelDTO> folderRelDTOS = new ArrayList<>();

            List<Long> issueIds = folderRels.stream().map(TestIssueFolderRelE::getIssueId).collect(Collectors.toList());

            for(int j =0; j < issueIds.size()/400;j++) {
                Map<Long, IssueInfosDTO> issueInfosMap = new HashMap<>();

                Long[] toSendIds = issueIds.subList(j*400,(j+1)*400-1).toArray(new Long[400]);

                if (!ObjectUtils.isEmpty(issueIds)) {
                    log.debug("开始分批（400一批）获取issue信息，当前第："+ j+1 +"批");
                    issueInfosMap = testCaseService.getIssueInfoMap(folderE.getProjectId(), toSendIds, true);
                }

                TestCaseStepE caseStepE = TestCaseStepEFactory.create();

                Map<Long, List<TestCaseStepDTO>> caseStepMap = Optional.ofNullable(ConvertHelper.convertList(caseStepE.querySelf(), TestCaseStepDTO.class))
                        .orElseGet(ArrayList::new).stream().collect(Collectors.groupingBy(TestCaseStepDTO::getIssueId));

                for (TestIssueFolderRelE folderRel : folderRels) {
                    TestIssueFolderRelDTO needRel = ConvertHelper.convert(folderRel, TestIssueFolderRelDTO.class);
                    needRel.setIssueInfosDTO(issueInfosMap.get(folderRel.getIssueId()));
                    needRel.setFolderName(folder.getName());

                    needRel.setTestCaseStepDTOS(caseStepMap.get(folderRel.getIssueId()));

                    folderRelDTOS.add(needRel);
                }
            }

            if (folderE.getVersionId() == null && folderE.getFolderId() != null) {
                folderE.setVersionId(folder.getVersionId());
            }
            folderRelMap.put(folder.getFolderId(), folderRelDTOS);
        }
        return folderRelMap;
    }

}
