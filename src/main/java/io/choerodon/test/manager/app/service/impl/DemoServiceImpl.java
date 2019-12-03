package io.choerodon.test.manager.app.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.github.pagehelper.PageInfo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.TestIssueFolderRelDTO;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.RedisTemplateUtil;

/**
 * Created by WangZhe@choerodon.io on 2019-02-15.
 * Email: ettwz@hotmail.com
 */
@Service
public class DemoServiceImpl implements DemoService {

    private static final String DATE_FORMATTER = "yyyy-MM-dd";
    private static final String REDIS_COUNT_KEY = "summary:";
    private static final String RANK_1 = "0|c00000:";
    private static final String RANK_2 = "0|c00004:";
    private static final String RANK_3 = "0|c00008:";
    private static final String RANK_4 = "0|c0000c:";
    private static final String RANK_5 = "0|c0000g:";
    private static final String RANK_6 = "0|c0000a:";
    private static final String RANK_7 = "0|c0000k:";
    private static final String RANK_8 = "0|c0000o:";
    private static final String STRING_1 = "维护配送信息";

    @Autowired
    private TestIssueFolderService testIssueFolderService;

    @Autowired
    private TestCaseStepService testCaseStepService;

    @Autowired
    private TestCycleService testCycleService;

    @Autowired
    private TestStatusService testStatusService;

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    private TestCycleCaseStepService testCycleCaseStepService;

    @Autowired
    private TestCycleCaseDefectRelService testCycleCaseDefectRelService;

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    private TestCaseStepMapper testCaseStepMapper;

    @Autowired
    private TestIssueFolderRelMapper testIssueFolderRelMapper;

    @Autowired
    private TestCycleMapper testCycleMapper;

    @Autowired
    private TestStatusMapper testStatusMapper;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    private TestCycleCaseStepMapper testCycleCaseStepMapper;

    @Autowired
    private TestCycleCaseHistoryMapper testCycleCaseHistoryMapper;

    @Autowired
    private TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public OrganizationRegisterEventPayload demoInit(DemoPayload demoPayload) {

        DemoPayload.Organization organization = demoPayload.getOrganization();
        DemoPayload.User user = demoPayload.getUser();
        DemoPayload.User userA = demoPayload.getUserA();
        DemoPayload.User userB = demoPayload.getUserB();
        DemoPayload.Project project = demoPayload.getProject();
        DemoPayload.TestData testData = demoPayload.getTestData();

        List<Long> testIssueIds = testData.getTestIssueIds();
        long versionId = testData.getVersionId();
        long projectId = project.getId();
        long userId = user.getId();
        long organizationId = organization.getId();
        Date dateOne = testData.getDateOne();
        Date dateTwo = testData.getDateTwo();
        Date dateThree = testData.getDateThree();
        Date dateFour = testData.getDateFour();
        Date dateFive = testData.getDateFive();
        Date dateSix = testData.getDateSix();

        List<Long> issueFolderIds = initIssueFolders(versionId, projectId, userId, dateOne);
        initIssueSteps(testIssueIds, projectId, userId, dateOne);
        initIssueFolderRels(issueFolderIds, testIssueIds, projectId, versionId, userId, dateOne);
        List<Long> cycleIds = initCycles(projectId, versionId, dateOne, dateThree, dateFour, dateSix, userId);
        Map<Long, List<Long>> phaseIdsMap = initCycleFolders(projectId, cycleIds, versionId, dateOne, dateTwo, dateThree, dateFour, dateFive, dateSix, issueFolderIds, userId);
        Long statusWIPId = initTestStatus(projectId, userId, dateOne);
        Long[] defectExecution = updateExecutionStatus(phaseIdsMap, testIssueIds, statusWIPId, projectId, organizationId, userId, dateTwo, dateThree);
        initExecutionDefect(defectExecution, projectId, organizationId, userId, dateThree);

        OrganizationRegisterEventPayload organizationRegisterEventPayload = new OrganizationRegisterEventPayload();

        OrganizationRegisterEventPayload.Organization organizationNew = new OrganizationRegisterEventPayload.Organization();
        OrganizationRegisterEventPayload.Project projectNew = new OrganizationRegisterEventPayload.Project();
        OrganizationRegisterEventPayload.User userNew = new OrganizationRegisterEventPayload.User();
        OrganizationRegisterEventPayload.User userANew = new OrganizationRegisterEventPayload.User();
        OrganizationRegisterEventPayload.User userBNew = new OrganizationRegisterEventPayload.User();

        organizationNew.setCode(organization.getCode());
        organizationNew.setId(organization.getId());
        organizationNew.setName(organization.getName());

        projectNew.setCode(project.getCode());
        projectNew.setId(project.getId());
        projectNew.setName(project.getName());

        userNew.setEmail(user.getEmail());
        userNew.setId(user.getId());
        userNew.setLoginName(user.getLoginName());

        userANew.setEmail(userA.getEmail());
        userANew.setId(userA.getId());
        userANew.setLoginName(userA.getLoginName());

        userBNew.setEmail(userB.getEmail());
        userBNew.setId(userB.getId());
        userBNew.setLoginName(userB.getLoginName());

        organizationRegisterEventPayload.setOrganization(organizationNew);
        organizationRegisterEventPayload.setProject(projectNew);
        organizationRegisterEventPayload.setUser(userNew);
        organizationRegisterEventPayload.setUserA(userANew);
        organizationRegisterEventPayload.setUserB(userBNew);

        return organizationRegisterEventPayload;
    }

    private List<Long> initIssueFolders(Long versionId, Long projectId, Long userId, Date date) {
        List<Long> issueFolderIds = new ArrayList<>();

        issueFolderIds.add(insertIssueFolder(versionId, projectId, "账户登录"));
        issueFolderIds.add(insertIssueFolder(versionId, projectId, "商品列表"));
        issueFolderIds.add(insertIssueFolder(versionId, projectId, "商品详情查看"));
        issueFolderIds.add(insertIssueFolder(versionId, projectId, STRING_1));
        issueFolderIds.add(insertIssueFolder(versionId, projectId, "提交订单"));

        testIssueFolderMapper.updateAuditFields(issueFolderIds.toArray(new Long[issueFolderIds.size()]), userId, date);

        return issueFolderIds;
    }

    private long insertIssueFolder(Long versionId, Long projectId, String folderName) {
        TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();

        testIssueFolderVO.setName(folderName);
        testIssueFolderVO.setProjectId(projectId);
        testIssueFolderVO.setVersionId(versionId);
        testIssueFolderVO.setType("cycle");

        return testIssueFolderService.create(projectId,testIssueFolderVO).getFolderId();
    }

    private void initIssueSteps(List<Long> testIssueIds, Long projectId, Long userId, Date date) {

        insertIssueSteps(RANK_1, testIssueIds.get(1), "打开登录页", "", "登录页正常打开", projectId);
        insertIssueSteps(RANK_2, testIssueIds.get(1), "输入正确的用户名", "XXX", "用户名正常输入", projectId);
        insertIssueSteps(RANK_3, testIssueIds.get(1), "输入正确的用户密码", "XXX", "用户密码正常输入，且加密显示", projectId);
        insertIssueSteps(RANK_4, testIssueIds.get(1), "点击登录按钮", "", "登陆成功", projectId);

        insertIssueSteps(RANK_1, testIssueIds.get(2), "打开登录页", "", "登录页正常打开", projectId);
        insertIssueSteps(RANK_2, testIssueIds.get(2), "输入不存在的用户名", "ZZZ", "提示用户不存在", projectId);
        insertIssueSteps(RANK_3, testIssueIds.get(2), "输入正确的用户名", "XXX", "用户名正常输入，且加密显示", projectId);
        insertIssueSteps(RANK_6, testIssueIds.get(2), "不输入密码直接登录", "", "登录失败，提示密码为必输字段", projectId);
        insertIssueSteps(RANK_4, testIssueIds.get(2), "输入错误的用户密码", "ZZZ", "用户密码正常输入，且加密显示", projectId);
        insertIssueSteps(RANK_5, testIssueIds.get(2), "点击登录按钮", "", "登录失败，提示密码错误", projectId);

        insertIssueSteps(RANK_1, testIssueIds.get(3), "用户登录", "正确的用户名、密码", "登陆成功", projectId);
        insertIssueSteps(RANK_2, testIssueIds.get(3), "点击配送信息界面", "", "页面成功转跳", projectId);
        insertIssueSteps(RANK_3, testIssueIds.get(3), "点击加号进入新增地址页面", "", "新增地址页面转跳成功", projectId);
        insertIssueSteps(RANK_4, testIssueIds.get(3), "输入配送地址", "VVVVV", "配送地址成功输入", projectId);
        insertIssueSteps(RANK_5, testIssueIds.get(3), "输入收件人", "XXX", "收件人成功输入", projectId);
        insertIssueSteps(RANK_7, testIssueIds.get(3), "输入电话号码", "1111111", "用户电话成功输入", projectId);
        insertIssueSteps(RANK_8, testIssueIds.get(3), "点击保存按钮", "", "保存成功", projectId);

        insertIssueSteps(RANK_1, testIssueIds.get(4), "进入商品详情页面", "商品：A", "成功展示商品详情", projectId);
        insertIssueSteps(RANK_2, testIssueIds.get(4), "选择颜色、尺码", "颜色：红，尺码：XL", "成功选择商品选项", projectId);
        insertIssueSteps(RANK_3, testIssueIds.get(4), "点击直接购买按钮", "", "快速下单页面正常转跳", projectId);
        insertIssueSteps(RANK_4, testIssueIds.get(4), "选择配送信息", "配送地址：VVVVV", "成功选择用户配送信息", projectId);
        insertIssueSteps(RANK_5, testIssueIds.get(4), "点击立即下单按钮", "", "下单成功", projectId);
        insertIssueSteps(RANK_7, testIssueIds.get(4), "页面转跳", "", "转跳到我的订单页面", projectId);

        List<Long> testIssueIdsNew = new ArrayList<>(testIssueIds);
        testIssueIdsNew.remove(0);

        testCaseStepMapper.updateAuditFields(testIssueIdsNew.toArray(new Long[testIssueIdsNew.size()]), userId, date);
    }

    private void insertIssueSteps(String rank, Long issueId, String testStep, String testData, String expectedResult, Long projectId) {
        TestCaseStepVO testCaseStepVO = new TestCaseStepVO();

        testCaseStepVO.setRank(rank);
        testCaseStepVO.setIssueId(issueId);
        testCaseStepVO.setTestStep(testStep);
        testCaseStepVO.setTestData(testData);
        testCaseStepVO.setExpectedResult(expectedResult);

        testCaseStepService.changeStep(testCaseStepVO, projectId,false);
    }

    private void initIssueFolderRels(List<Long> issueFolderIds, List<Long> testIssueIds, Long projectId, Long versionId, Long userId, Date date) {
        insertIssueFolderRel(issueFolderIds.get(0), versionId, projectId, testIssueIds.get(1));
        insertIssueFolderRel(issueFolderIds.get(0), versionId, projectId, testIssueIds.get(2));
        insertIssueFolderRel(issueFolderIds.get(3), versionId, projectId, testIssueIds.get(3));
        insertIssueFolderRel(issueFolderIds.get(4), versionId, projectId, testIssueIds.get(4));

        testIssueFolderRelMapper.updateAuditFields(projectId, userId, date);
    }

    private void insertIssueFolderRel(Long folderId, Long versionId, Long projectId, Long issueId) {
        TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO();

        testIssueFolderRelVO.setFolderId(folderId);
        testIssueFolderRelVO.setVersionId(versionId);
        testIssueFolderRelVO.setProjectId(projectId);
        testIssueFolderRelVO.setIssueId(issueId);

        TestIssueFolderRelDTO testIssueFolderRelDTO = modelMapper.map(testIssueFolderRelVO, TestIssueFolderRelDTO.class);
        testIssueFolderRelMapper.insert(testIssueFolderRelDTO);
    }

    private List<Long> initCycles(Long projectId, Long versionId, Date dateOne, Date dateThree, Date dateFour, Date dateSix, Long userId) {
        List<Long> cycleIds = new ArrayList<>();

        cycleIds.add(insertCycle(projectId, "开发阶段测试", versionId, "开发环境", dateOne, dateThree));
        cycleIds.add(insertCycle(projectId, "回归测试-UAT", versionId, "UAT环境", dateFour, dateSix));

        testCycleMapper.updateAuditFields(cycleIds.toArray(new Long[cycleIds.size()]), userId, dateOne);

        return cycleIds;
    }

    private Long insertCycle(Long projectId, String cycleName, Long versionId, String environment, Date fromDate, Date toDate) {
        TestCycleVO testCycleVO = new TestCycleVO();

        testCycleVO.setCycleName(cycleName);
        testCycleVO.setVersionId(versionId);
        testCycleVO.setEnvironment(environment);
        testCycleVO.setFromDate(fromDate);
        testCycleVO.setToDate(toDate);
        testCycleVO.setType("cycle");
        testCycleVO.setProjectId(projectId);

        return testCycleService.insert(projectId, testCycleVO).getCycleId();
    }

    private Map<Long, List<Long>> initCycleFolders(Long projectId, List<Long> cycleIds, Long versionId, Date dateOne, Date dateTwo, Date dateThree, Date dateFour, Date dateFive, Date dateSix, List<Long> issueFolderIds, Long userId) {
        List<Long> cycleFolderIdsOne = new ArrayList<>();
        List<Long> cycleFolderIdsTwo = new ArrayList<>();

        cycleFolderIdsOne.add(insertCycleFolder(projectId, cycleIds.get(0), "提交订单", versionId, changeDateTimeStart(dateTwo), changeDateTimeEnd(dateThree), issueFolderIds.get(4)));
        cycleFolderIdsOne.add(insertCycleFolder(projectId, cycleIds.get(0), "账户登录", versionId, changeDateTimeStart(dateOne), changeDateTimeEnd(dateTwo), issueFolderIds.get(0)));
        cycleFolderIdsOne.add(insertCycleFolder(projectId, cycleIds.get(0), STRING_1, versionId, changeDateTimeStart(dateTwo), changeDateTimeEnd(dateTwo), issueFolderIds.get(3)));

        cycleFolderIdsTwo.add(insertCycleFolder(projectId, cycleIds.get(1), "账户登录", versionId, changeDateTimeStart(dateFour), changeDateTimeEnd(dateFive), issueFolderIds.get(0)));
        cycleFolderIdsTwo.add(insertCycleFolder(projectId, cycleIds.get(1), STRING_1, versionId, changeDateTimeStart(dateFive), changeDateTimeEnd(dateFive), issueFolderIds.get(3)));
        cycleFolderIdsTwo.add(insertCycleFolder(projectId, cycleIds.get(1), "提交订单", versionId, changeDateTimeStart(dateFive), changeDateTimeEnd(dateSix), issueFolderIds.get(4)));

        testCycleMapper.updateAuditFields(cycleFolderIdsOne.toArray(new Long[cycleFolderIdsOne.size()]), userId, dateOne);
        testCycleMapper.updateAuditFields(cycleFolderIdsTwo.toArray(new Long[cycleFolderIdsTwo.size()]), userId, dateFour);

        Map<Long, List<Long>> phaseIdsMap = new HashMap<>();

        phaseIdsMap.put(0L, cycleFolderIdsOne);
        phaseIdsMap.put(1L, cycleFolderIdsTwo);

        return phaseIdsMap;
    }

    private Date changeDateTimeStart(Date date) {
        Date newDate = (Date) date.clone();

        newDate.setHours(0);
        newDate.setMinutes(0);
        newDate.setSeconds(0);

        return newDate;
    }

    private Date changeDateTimeEnd(Date date) {
        Date newDate = (Date) date.clone();

        newDate.setHours(23);
        newDate.setMinutes(59);
        newDate.setSeconds(59);

        return newDate;
    }

    private Long insertCycleFolder(Long projectId, Long parentCycleId, String cycleName, Long versionId, Date fromDate, Date toDate, Long folderId) {
        TestCycleVO testCycleVO = new TestCycleVO();

        testCycleVO.setParentCycleId(parentCycleId);
        testCycleVO.setCycleName(cycleName);
        testCycleVO.setVersionId(versionId);
        testCycleVO.setFromDate(fromDate);
        testCycleVO.setToDate(toDate);
        testCycleVO.setType("folder");
        testCycleVO.setFolderId(folderId);
        testCycleVO.setProjectId(projectId);

        return testCycleService.insert(projectId, testCycleVO).getCycleId();
    }

    private long initTestStatus(Long projectId, Long userId, Date date) {
        TestStatusVO testStatusVO = new TestStatusVO();

        testStatusVO.setStatusName("WIP");
        testStatusVO.setDescription("Work In Process");
        testStatusVO.setStatusColor("rgba(248,231,28,1)");
        testStatusVO.setStatusType("CYCLE_CASE");
        testStatusVO.setProjectId(projectId);

        Long statusID = testStatusService.insert(testStatusVO).getStatusId();

        testStatusMapper.updateAuditFields(statusID, userId, date);

        return statusID;
    }

    private Long[] updateExecutionStatus(Map<Long, List<Long>> phaseIdsMap, List<Long> testIssueIds, Long statusWIPId, Long projectId, Long organizationId, Long userId, Date dateTwo, Date dateThree) {
        TestCycleCaseVO testCycleCaseVO = new TestCycleCaseVO();
        Long[] defectExecution = new Long[2];
        List<Long> executionIdsOne = new ArrayList<>();
        List<Long> executionIdsTwo = new ArrayList<>();
        List<Long> executionIdsThree = new ArrayList<>();

        Pageable pageable = PageRequest.of(1, 30,new Sort(Sort.Direction.ASC, "cycle_id"));

        List<Long> phaseIdsOne = phaseIdsMap.get(0L);
        List<Long> phaseIdsTwo = phaseIdsMap.get(1L);


        for (Long phaseId : phaseIdsOne) {
            testCycleCaseVO.setCycleId(phaseId);
            PageInfo<TestCycleCaseVO> executionDTOs = testCycleCaseService.queryByCycle(testCycleCaseVO, pageable, projectId, organizationId);

            for (TestCycleCaseVO executionDTO : executionDTOs.getList()) {
                if (executionDTO.getIssueId().equals(testIssueIds.get(2))) {
                    updateExecutionStepStatus(executionDTO.getExecuteId(), 2, projectId, organizationId);
                    executionDTO.setExecutionStatus(statusWIPId);
                    executionIdsOne.add(executionDTO.getExecuteId());
                } else if (executionDTO.getIssueId().equals(testIssueIds.get(4))) {
                    updateExecutionStepStatus(executionDTO.getExecuteId(), 4, projectId, organizationId);
                    executionDTO.setExecutionStatus(3L);
                    defectExecution[0] = executionDTO.getExecuteId();
                    defectExecution[1] = testIssueIds.get(0);
                    executionIdsTwo.add(executionDTO.getExecuteId());
                } else {
                    updateExecutionStepStatus(executionDTO.getExecuteId(), 0, projectId, organizationId);
                    executionDTO.setExecutionStatus(2L);
                    executionIdsOne.add(executionDTO.getExecuteId());
                }
                executionDTO.setAssignedTo(userId);
                testCycleCaseService.changeOneCase(executionDTO, projectId);
            }
        }

        for (Long phaseId : phaseIdsTwo) {
            testCycleCaseVO.setCycleId(phaseId);
            PageInfo<TestCycleCaseVO> executionDTOs = testCycleCaseService.queryByCycle(testCycleCaseVO, pageable, projectId, organizationId);

            for (TestCycleCaseVO executionDTO : executionDTOs.getList()) {
                executionIdsThree.add(executionDTO.getExecuteId());
            }
        }

        updateExecutionAuditFields(executionIdsOne, userId, dateTwo);
        updateExecutionAuditFields(executionIdsTwo, userId, dateThree);
        updateExecutionAuditFields(executionIdsThree, userId, dateThree);

        String key = REDIS_COUNT_KEY + projectId + ":" + LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMATTER));
        RedisAtomicLong entityIdCounter = redisTemplateUtil.getRedisAtomicLong(key, redisTemplate);
        entityIdCounter.set(0L);

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
        List<TestCycleCaseStepVO> testCaseStepDTOs = testCycleCaseStepService.querySubStep(caseId, projectId, organizationId);
        switch (flag) {
            case 2:
                testCaseStepDTOs.get(0).setStepStatus(5L);
                testCaseStepDTOs.get(1).setStepStatus(6L);
                testCaseStepDTOs.get(2).setStepStatus(5L);
                testCaseStepDTOs.get(3).setStepStatus(6L);
                testCaseStepDTOs.get(4).setStepStatus(5L);
                testCaseStepDTOs.get(5).setStepStatus(6L);
                break;
            case 4:
                testCaseStepDTOs.get(0).setStepStatus(5L);
                testCaseStepDTOs.get(1).setStepStatus(5L);
                testCaseStepDTOs.get(2).setStepStatus(5L);
                testCaseStepDTOs.get(3).setStepStatus(5L);
                testCaseStepDTOs.get(4).setStepStatus(6L);
                break;
            default:
                for (TestCycleCaseStepVO testCycleCaseStepVO : testCaseStepDTOs) {
                    testCycleCaseStepVO.setStepStatus(5L);
                }
                break;
        }
//        testCycleCaseStepService.update(testCaseStepDTOs);
    }

    private void initExecutionDefect(Long[] defectExecution, Long projectId, Long organizationId, Long userId, Date date) {
        TestCycleCaseDefectRelVO testCycleCaseDefectRelVO = new TestCycleCaseDefectRelVO();
        testCycleCaseDefectRelVO.setDefectType("CYCLE_CASE");
        testCycleCaseDefectRelVO.setDefectLinkId(defectExecution[0]);
        testCycleCaseDefectRelVO.setIssueId(defectExecution[1]);
        testCycleCaseDefectRelVO.setProjectId(projectId);

        Long defectId = testCycleCaseDefectRelService.insert(testCycleCaseDefectRelVO, projectId, organizationId).getId();

        testCycleCaseDefectRelMapper.updateAuditFields(defectId, userId, date);
        testCycleCaseHistoryMapper.updateAuditFields(defectExecution, userId, date);
    }
}