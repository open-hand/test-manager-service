package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.domain.service.ITestIssueFolderRelService;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderRelE;
import io.choerodon.test.manager.infra.common.utils.RedisTemplateUtil;
import io.choerodon.test.manager.infra.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by WangZhe@choerodon.io on 2019-02-15.
 * Email: ettwz@hotmail.com
 */
@Service
public class DemoServiceImpl implements DemoService {

    @Autowired
    TestIssueFolderService testIssueFolderService;

    @Autowired
    TestCaseStepService iTestCaseStepService;

    @Autowired
    ITestIssueFolderRelService iTestIssueFolderRelService;

    @Autowired
    TestCycleService testCycleService;

    @Autowired
    TestStatusService testStatusService;

    @Autowired
    TestCycleCaseService testCycleCaseService;

    @Autowired
    TestCycleCaseStepService testCycleCaseStepService;

    @Autowired
    TestCycleCaseDefectRelService testCycleCaseDefectRelService;

    @Autowired
    TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    TestCaseStepMapper testCaseStepMapper;

    @Autowired
    TestIssueFolderRelMapper testIssueFolderRelMapper;

    @Autowired
    TestCycleMapper testCycleMapper;

    @Autowired
    TestStatusMapper testStatusMapper;

    @Autowired
    TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    TestCycleCaseStepMapper testCycleCaseStepMapper;

    @Autowired
    TestCycleCaseHistoryMapper testCycleCaseHistoryMapper;

    @Autowired
    TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedisTemplateUtil redisTemplateUtil;

    private static final String DATE_FORMATTER = "yyyy-MM-dd";
    private static final String REDIS_COUNT_KEY = "summary:";

    @Override
    public void demoInit(DemoPayload demoPayload) {

        List<Long> testIssueIds = demoPayload.getTestIssueIds();
        long versionId = demoPayload.getVersionId();
        long projectId = demoPayload.getProjectId();
        long userId = demoPayload.getUserId();
        long organizationId = demoPayload.getOrganizationId();
        Date dateOne = demoPayload.getDateOne();
        Date dateTwo = demoPayload.getDateTwo();
        Date dateThree = demoPayload.getDateThree();
        Date dateFour = demoPayload.getDateFour();
        Date dateFive = demoPayload.getDateFive();
        Date dateSix = demoPayload.getDateSix();

        List<Long> issueFolderIds = initIssueFolders(versionId, projectId, userId, dateOne);
        initIssueSteps(testIssueIds, projectId, userId, dateOne);
        initIssueFolderRels(issueFolderIds, testIssueIds, projectId, versionId, userId, dateOne);
        List<Long> cycleIds = initCycles(versionId, dateOne, dateThree, dateFour, dateSix, userId);
        List<Long> phaseIds = initCycleFolders(cycleIds, versionId, dateOne, dateTwo, dateThree, dateFour, dateFive, dateSix, issueFolderIds, userId);
        Long statusWIPId = initTestStatus(projectId, userId, dateOne);
        Long[] defectExecution = updateExecutionStatus(phaseIds, testIssueIds, statusWIPId, projectId, organizationId, userId, dateTwo, dateThree);
        initExecutionDefect(defectExecution, projectId, organizationId, userId, dateThree);
    }

    private List<Long> initIssueFolders(Long versionId, Long projectId, Long userId, Date date) {
        List<Long> issueFolderIds = new ArrayList<>();

        issueFolderIds.add(insertIssueFolder(versionId, projectId, "账户登录"));
        issueFolderIds.add(insertIssueFolder(versionId, projectId, "商品列表"));
        issueFolderIds.add(insertIssueFolder(versionId, projectId, "商品详情查看"));
        issueFolderIds.add(insertIssueFolder(versionId, projectId, "维护配送信息"));
        issueFolderIds.add(insertIssueFolder(versionId, projectId, "提交订单"));

        testIssueFolderMapper.updateAuditFields(issueFolderIds.toArray(new Long[issueFolderIds.size()]), userId, date);

        return issueFolderIds;
    }

    private long insertIssueFolder(Long versionId, Long projectId, String folderName) {
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();

        testIssueFolderDTO.setName(folderName);
        testIssueFolderDTO.setProjectId(projectId);
        testIssueFolderDTO.setVersionId(versionId);
        testIssueFolderDTO.setType("cycle");

        return testIssueFolderService.insert(testIssueFolderDTO).getFolderId();
    }

    private void initIssueSteps(List<Long> testIssueIds, Long projectId, Long userId, Date date) {

        insertIssueSteps("0|c00000:", testIssueIds.get(1), "打开登录页", "", "登录页正常打开", projectId);
        insertIssueSteps("0|c00004:", testIssueIds.get(1), "输入正确的用户名", "XXX", "用户名正常输入", projectId);
        insertIssueSteps("0|c00008:", testIssueIds.get(1), "输入正确的用户密码", "XXX", "用户密码正常输入，且加密显示", projectId);
        insertIssueSteps("0|c0000c:", testIssueIds.get(1), "点击登录按钮", "", "登陆成功", projectId);

        insertIssueSteps("0|c00000:", testIssueIds.get(2), "打开登录页", "", "登录页正常打开", projectId);
        insertIssueSteps("0|c00004:", testIssueIds.get(2), "输入不存在的用户名", "ZZZ", "提示用户不存在", projectId);
        insertIssueSteps("0|c00008:", testIssueIds.get(2), "输入正确的用户名", "XXX", "用户名正常输入，且加密显示", projectId);
        insertIssueSteps("0|c0000a:", testIssueIds.get(2), "不输入密码直接登录", "", "登录失败，提示密码为必输字段", projectId);
        insertIssueSteps("0|c0000c:", testIssueIds.get(2), "输入错误的用户密码", "ZZZ", "用户密码正常输入，且加密显示", projectId);
        insertIssueSteps("0|c0000g:", testIssueIds.get(2), "点击登录按钮", "", "登录失败，提示密码错误", projectId);

        insertIssueSteps("0|c00000:", testIssueIds.get(3), "用户登录", "正确的用户名、密码", "登陆成功", projectId);
        insertIssueSteps("0|c00004:", testIssueIds.get(3), "点击配送信息界面", "", "页面成功转跳", projectId);
        insertIssueSteps("0|c00008:", testIssueIds.get(3), "点击加号进入新增地址页面", "", "新增地址页面转跳成功", projectId);
        insertIssueSteps("0|c0000c:", testIssueIds.get(3), "输入配送地址", "VVVVV", "配送地址成功输入", projectId);
        insertIssueSteps("0|c0000g:", testIssueIds.get(3), "输入收件人", "XXX", "收件人成功输入", projectId);
        insertIssueSteps("0|c0000k:", testIssueIds.get(3), "输入电话号码", "1111111", "用户电话成功输入", projectId);
        insertIssueSteps("0|c0000o:", testIssueIds.get(3), "点击保存按钮", "", "保存成功", projectId);

        insertIssueSteps("0|c00000:", testIssueIds.get(4), "进入商品详情页面", "商品：A", "成功展示商品详情", projectId);
        insertIssueSteps("0|c00004:", testIssueIds.get(4), "选择颜色、尺码", "颜色：红，尺码：XL", "成功选择商品选项", projectId);
        insertIssueSteps("0|c00008:", testIssueIds.get(4), "点击直接购买按钮", "", "快速下单页面正常转跳", projectId);
        insertIssueSteps("0|c0000c:", testIssueIds.get(4), "选择配送信息", "配送地址：VVVVV", "成功选择用户配送信息", projectId);
        insertIssueSteps("0|c0000g:", testIssueIds.get(4), "点击立即下单按钮", "", "下单成功", projectId);
        insertIssueSteps("0|c0000k:", testIssueIds.get(4), "页面转跳", "", "转跳到我的订单页面", projectId);

        testIssueIds.remove(0);
        testCaseStepMapper.updateAuditFields(testIssueIds.toArray(new Long[testIssueIds.size()]), userId, date);
    }

    private void insertIssueSteps(String rank, Long issueId, String testStep, String testData, String expectedResult, Long projectId) {
        TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();

        testCaseStepDTO.setRank(rank);
        testCaseStepDTO.setIssueId(issueId);
        testCaseStepDTO.setTestStep(testStep);
        testCaseStepDTO.setTestData(testData);
        testCaseStepDTO.setExpectedResult(expectedResult);

        iTestCaseStepService.changeStep(testCaseStepDTO, projectId);
    }

    private void initIssueFolderRels(List<Long> issueFolderIds, List<Long> testIssueIds, Long projectId, Long versionId, Long userId, Date date) {
        insertIssueFolderRel(issueFolderIds.get(0), versionId, projectId, testIssueIds.get(1));
        insertIssueFolderRel(issueFolderIds.get(0), versionId, projectId, testIssueIds.get(2));
        insertIssueFolderRel(issueFolderIds.get(3), versionId, projectId, testIssueIds.get(3));
        insertIssueFolderRel(issueFolderIds.get(4), versionId, projectId, testIssueIds.get(4));

        testIssueFolderRelMapper.updateAuditFields(projectId, userId, date);
    }

    private void insertIssueFolderRel(Long folderId, Long versionId, Long projectId, Long issueId) {
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();

        testIssueFolderRelDTO.setFolderId(folderId);
        testIssueFolderRelDTO.setVersionId(versionId);
        testIssueFolderRelDTO.setProjectId(projectId);
        testIssueFolderRelDTO.setIssueId(issueId);

        iTestIssueFolderRelService.insert(ConvertHelper.convert(testIssueFolderRelDTO, TestIssueFolderRelE.class));
    }

    private List<Long> initCycles(Long versionId, Date dateOne, Date dateThree, Date dateFour, Date dateSix, Long userId) {
        List<Long> cycleIds = new ArrayList<>();

        cycleIds.add(insertCycle("开发阶段测试", versionId, "开发环境", dateOne, dateThree));
        cycleIds.add(insertCycle("回归测试-UAT", versionId, "UAT环境", dateFour, dateSix));

        testCycleMapper.updateAuditFields(cycleIds.toArray(new Long[cycleIds.size()]), userId, dateOne);

        return cycleIds;
    }

    private Long insertCycle(String cycleName, Long versionId, String environment, Date fromDate, Date toDate) {
        TestCycleDTO testCycleDTO = new TestCycleDTO();

        testCycleDTO.setCycleName(cycleName);
        testCycleDTO.setVersionId(versionId);
        testCycleDTO.setEnvironment(environment);
        testCycleDTO.setFromDate(fromDate);
        testCycleDTO.setToDate(toDate);
        testCycleDTO.setType("cycle");

        return testCycleService.insert(testCycleDTO).getCycleId();
    }

    private List<Long> initCycleFolders(List<Long> cycleIds, Long versionId, Date dateOne, Date dateTwo, Date dateThree, Date dateFour, Date dateFive, Date dateSix, List<Long> issueFolderIds, Long userId) {
        List<Long> cycleFolderIds = new ArrayList<>();

        cycleFolderIds.add(insertCycleFolder(cycleIds.get(0), "提交订单", versionId, changeDateTimeStart(dateTwo), changeDateTimeEnd(dateThree), issueFolderIds.get(4)));
        cycleFolderIds.add(insertCycleFolder(cycleIds.get(0), "账户登录", versionId, changeDateTimeStart(dateOne), changeDateTimeEnd(dateTwo), issueFolderIds.get(0)));
        cycleFolderIds.add(insertCycleFolder(cycleIds.get(0), "维护配送信息", versionId, changeDateTimeStart(dateTwo), changeDateTimeEnd(dateTwo), issueFolderIds.get(3)));

        cycleFolderIds.add(insertCycleFolder(cycleIds.get(1), "账户登录", versionId, changeDateTimeStart(dateFive), changeDateTimeEnd(dateSix), issueFolderIds.get(0)));
        cycleFolderIds.add(insertCycleFolder(cycleIds.get(1), "维护配送信息", versionId, changeDateTimeStart(dateFour), changeDateTimeEnd(dateFive), issueFolderIds.get(3)));
        cycleFolderIds.add(insertCycleFolder(cycleIds.get(1), "提交订单", versionId, changeDateTimeStart(dateFive), changeDateTimeEnd(dateFive), issueFolderIds.get(4)));

        testCycleMapper.updateAuditFields(cycleFolderIds.toArray(new Long[cycleFolderIds.size()]), userId, dateOne);

        return cycleFolderIds;
    }

    private Date changeDateTimeStart(Date date) {
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);

        return date;
    }

    private Date changeDateTimeEnd(Date date) {
        date.setHours(23);
        date.setMinutes(59);
        date.setSeconds(59);

        return date;
    }

    private Long insertCycleFolder(Long parentCycleId, String cycleName, Long versionId, Date fromDate, Date toDate, Long folderId) {
        TestCycleDTO testCycleDTO = new TestCycleDTO();

        testCycleDTO.setParentCycleId(parentCycleId);
        testCycleDTO.setCycleName(cycleName);
        testCycleDTO.setVersionId(versionId);
        testCycleDTO.setFromDate(fromDate);
        testCycleDTO.setToDate(toDate);
        testCycleDTO.setType("folder");
        testCycleDTO.setFolderId(folderId);

        return testCycleService.insert(testCycleDTO).getCycleId();
    }

    private long initTestStatus(Long projectId, Long userId, Date date) {
        TestStatusDTO testStatusDTO = new TestStatusDTO();

        testStatusDTO.setStatusName("WIP");
        testStatusDTO.setDescription("Work In Process");
        testStatusDTO.setStatusColor("rgba(248,231,28,1)");
        testStatusDTO.setStatusType("CYCLE_CASE");
        testStatusDTO.setProjectId(projectId);

        Long statusID = testStatusService.insert(testStatusDTO).getStatusId();

        testStatusMapper.updateAuditFields(statusID, userId, date);

        return statusID;
    }

    private Long[] updateExecutionStatus(List<Long> phaseIds, List<Long> testIssueIds, Long statusWIPId, Long projectId, Long organizationId, Long userId, Date dateTwo, Date dateThree) {
        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        Long[] defectExecution = new Long[2];
        List<Long> executionIdsOne = new ArrayList<>();
        List<Long> executionIdsTwo = new ArrayList<>();

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(0);
        pageRequest.setSize(30);

        for (Long phaseId : phaseIds) {
            testCycleCaseDTO.setCycleId(phaseId);
            Page<TestCycleCaseDTO> executionDTOs = testCycleCaseService.queryByCycle(testCycleCaseDTO, pageRequest, projectId, organizationId);

            for (TestCycleCaseDTO executionDTO : executionDTOs) {
                if (executionDTO.getIssueId().equals(testIssueIds.get(2))) {
                    updateExecutionStepStatus(executionDTO.getExecuteId(), 2, projectId, organizationId);
                    executionDTO.setExecutionStatus(statusWIPId);
                    defectExecution[0] = executionDTO.getExecuteId();
                    defectExecution[1] = executionDTO.getIssueId();
                    executionIdsOne.add(executionDTO.getExecuteId());
                } else if (executionDTO.getIssueId().equals(testIssueIds.get(4))) {
                    updateExecutionStepStatus(executionDTO.getExecuteId(), 4, projectId, organizationId);
                    executionDTO.setExecutionStatus(3L);
                    executionIdsTwo.add(executionDTO.getExecuteId());
                } else {
                    updateExecutionStepStatus(executionDTO.getExecuteId(), 0, projectId, organizationId);
                    executionDTO.setExecutionStatus(2L);
                    executionIdsOne.add(executionDTO.getExecuteId());
                }
                testCycleCaseService.changeOneCase(executionDTO, projectId);
            }
        }
        updateExecutionAuditFields(executionIdsOne, userId, dateTwo);
        updateExecutionAuditFields(executionIdsTwo, userId, dateThree);

        String key = REDIS_COUNT_KEY + projectId + ":" + LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMATTER));
        redisTemplate.delete(key);

        String keyOne = REDIS_COUNT_KEY + projectId + ":" + new SimpleDateFormat(DATE_FORMATTER).format(dateTwo);
        String keyTwo = REDIS_COUNT_KEY + projectId + ":" + new SimpleDateFormat(DATE_FORMATTER).format(dateThree);

        RedisAtomicLong entityIdCounterOne = redisTemplateUtil.getRedisAtomicLong(keyOne, redisTemplate);
        RedisAtomicLong entityIdCounterTwo = redisTemplateUtil.getRedisAtomicLong(keyTwo, redisTemplate);

        entityIdCounterOne.set(3L);
        entityIdCounterTwo.set(1L);

        return defectExecution;
    }

    private void updateExecutionAuditFields(List<Long> executionIds, Long userId, Date date) {
        testCycleCaseMapper.updateAuditFields(executionIds.toArray(new Long[executionIds.size()]), userId, date);
        testCycleCaseStepMapper.updateAuditFields(executionIds.toArray(new Long[executionIds.size()]), userId, date);
        testCycleCaseHistoryMapper.updateAuditFields(executionIds.toArray(new Long[executionIds.size()]), userId, date);
    }

    private void updateExecutionStepStatus(Long caseId, int flag, Long projectId, Long organizationId) {
        List<TestCycleCaseStepDTO> testCaseStepDTOs = testCycleCaseStepService.querySubStep(caseId, projectId, organizationId);
        switch (flag) {
            case 2:
                testCaseStepDTOs.get(0).setStepStatus(5L);
                testCaseStepDTOs.get(1).setStepStatus(4L);
                testCaseStepDTOs.get(2).setStepStatus(5L);
                testCaseStepDTOs.get(3).setStepStatus(4L);
                testCaseStepDTOs.get(4).setStepStatus(5L);
                testCaseStepDTOs.get(5).setStepStatus(4L);
                break;
            case 4:
                testCaseStepDTOs.get(0).setStepStatus(5L);
                testCaseStepDTOs.get(1).setStepStatus(5L);
                testCaseStepDTOs.get(2).setStepStatus(5L);
                testCaseStepDTOs.get(3).setStepStatus(5L);
                testCaseStepDTOs.get(4).setStepStatus(4L);
                break;
            default:
                for (TestCycleCaseStepDTO testCycleCaseStepDTO : testCaseStepDTOs) {
                    testCycleCaseStepDTO.setStepStatus(5L);
                }
                break;
        }
        testCycleCaseStepService.update(testCaseStepDTOs);
    }

    private void initExecutionDefect(Long[] defectExecution, Long projectId, Long organizationId, Long userId, Date date) {
        TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = new TestCycleCaseDefectRelDTO();
        testCycleCaseDefectRelDTO.setDefectType("CYCLE_CASE");
        testCycleCaseDefectRelDTO.setDefectLinkId(defectExecution[0]);
        testCycleCaseDefectRelDTO.setIssueId(defectExecution[1]);

        Long defectId = testCycleCaseDefectRelService.insert(testCycleCaseDefectRelDTO, projectId, organizationId).getId();

        testCycleCaseDefectRelMapper.updateAuditFields(defectId, userId, date);
    }
}