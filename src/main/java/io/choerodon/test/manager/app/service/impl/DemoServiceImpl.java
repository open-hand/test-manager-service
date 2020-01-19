package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import io.choerodon.test.manager.api.vo.event.OrganizationRegisterEventPayload;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestStatusType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.mapper.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Created by WangZhe@choerodon.io on 2019-02-15.
 * Email: ettwz@hotmail.com
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DemoServiceImpl implements DemoService {

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
    private ModelMapper modelMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private TestPlanServcie testPlanServcie;

    @Autowired
    private TestCaseService testCaseService;

    @Override
    public OrganizationRegisterEventPayload demoInit(DemoPayload demoPayload) {

        DemoPayload.Organization organization = demoPayload.getOrganization();
        DemoPayload.User user = demoPayload.getUser();
        DemoPayload.User userA = demoPayload.getUserA();
        DemoPayload.User userB = demoPayload.getUserB();
        DemoPayload.Project project = demoPayload.getProject();
        DemoPayload.TestData testData = demoPayload.getTestData();

        long versionId = 0L;
        long projectId = project.getId();
        long userId = user.getId();
        Date dateOne = testData.getDateOne();
        Date dateTwo = testData.getDateTwo();
        Date dateThree = testData.getDateThree();
        Date dateFour = testData.getDateFour();
        Date dateFive = testData.getDateFive();
        Date dateSix = testData.getDateSix();

        List<Long> issueFolderIds = initIssueFolders(versionId, projectId, userId, dateOne);
        List<Long> caseIds = initCase(issueFolderIds, projectId, userId, dateTwo);
        initIssueSteps(caseIds, projectId, userId, dateThree);
        // 创建计划
        Long planId = initPlan(project, userId, dateTwo);
        // 初始化循环文件夹
        List<Long> phaseIdsMap = initCycleFolders(planId, projectId, versionId, dateOne, dateTwo, dateThree, dateFour, dateFive, dateSix, issueFolderIds, userId);
        // 初始化执行
        Long defaultCaseStatus = testStatusMapper.getDefaultStatus(TestStatusType.STATUS_TYPE_CASE);
        List<TestCycleCaseDTO> cycleCase = initCycleCase(projectId, phaseIdsMap, userId, dateFive, caseIds,defaultCaseStatus,userA.getId(),userB.getId());
        // 初始化执行步骤
        Long defaultStatus = testStatusMapper.getDefaultStatus(TestStatusType.STATUS_TYPE_CASE_STEP);
        initCycleCaseStep(cycleCase,userId, dateSix,caseIds,projectId,defaultStatus);


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

    private void initCycleCaseStep(List<TestCycleCaseDTO> cycleCase, long userId, Date dateOne, List<Long> caseIds,Long project,Long status) {
        List<TestCaseStepDTO> testCaseStepDTOS = testCaseStepMapper.listByCaseIds(caseIds);
        Map<Long, List<TestCaseStepDTO>> caseStepMap = testCaseStepDTOS.stream().collect(Collectors.groupingBy(TestCaseStepDTO::getIssueId));
        List<TestCycleCaseStepDTO> stepDTOS = new ArrayList<>();
        cycleCase.forEach(v -> {
            List<TestCaseStepDTO> testCaseStep = caseStepMap.get(v.getCaseId());
            if(CollectionUtils.isEmpty(testCaseStep)){
                return;
            }
            testCaseStep.forEach(testCaseStepDTO -> {
                TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
                modelMapper.map(testCaseStepDTO,testCycleCaseStepDTO);
                testCycleCaseStepDTO.setExecuteId(v.getExecuteId());
                testCycleCaseStepDTO.setStepStatus(status);
                stepDTOS.add(testCycleCaseStepDTO);
            });
        });
        testCycleCaseStepMapper.batchInsertTestCycleCaseSteps(stepDTOS);
        List<Long> cycleCaseIds = cycleCase.stream().map(TestCycleCaseDTO::getExecuteId).collect(Collectors.toList());
        testCycleCaseStepMapper.updateAuditFields(cycleCaseIds.toArray(new Long[cycleCaseIds.size()]),userId,dateOne);
    }

    private List<TestCycleCaseDTO> initCycleCase(long projectId, List<Long> phaseIdsMap, long userId, Date dateOne, List<Long> caseIds,Long statusWIPId,Long userA,Long userB) {
        List<TestCycleCaseDTO> cycleCase = new ArrayList<>();
        cycleCase.add(insertCycleCase("用户登录",RANK_1, statusWIPId,projectId, phaseIdsMap.get(1), userId, dateOne, caseIds.get(0),userA));
        cycleCase.add(insertCycleCase("登录错误操作",RANK_2,statusWIPId, projectId, phaseIdsMap.get(1), userId, dateOne, caseIds.get(1),userB));
        cycleCase.add(insertCycleCase("通过商品详情快速下单",RANK_3, statusWIPId,projectId, phaseIdsMap.get(0), userId, dateOne, caseIds.get(2),userA));
        cycleCase.add(insertCycleCase("用户维护配送信息", RANK_4,statusWIPId,projectId, phaseIdsMap.get(2), userId, dateOne, caseIds.get(3),userB));
        return cycleCase;
    }

    private TestCycleCaseDTO insertCycleCase(String summary,String rank,Long statusWIPId, long projectId, Long cycleId, long userId, Date dateOne, Long caseId,Long assignedTo) {
        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setCycleId(cycleId);
        testCycleCaseDTO.setProjectId(projectId);
        testCycleCaseDTO.setCaseId(caseId);
        testCycleCaseDTO.setSummary(summary);
        testCycleCaseDTO.setVersionNum(1L);
        testCycleCaseDTO.setCreatedBy(userId);
        testCycleCaseDTO.setLastUpdatedBy(userId);
        testCycleCaseDTO.setCreationDate(dateOne);
        testCycleCaseDTO.setLastUpdateDate(dateOne);
        testCycleCaseDTO.setRank(rank);
        testCycleCaseDTO.setExecutionStatus(statusWIPId);
        testCycleCaseDTO.setAssignedTo(assignedTo);
        return testCycleCaseService.baseInsert(testCycleCaseDTO);
    }

    private Long initPlan(DemoPayload.Project project, long userId, Date dateTwo) {

        TestPlanDTO testPlanDTO = new TestPlanDTO();
        testPlanDTO.setName("V1.0全流程测试");
        testPlanDTO.setDescription("1.0版本功能全流程测试；\n" +
                "测试周期：1周；\n" +
                "自动同步用例库");
        testPlanDTO.setStartDate(dateTwo);
        testPlanDTO.setEndDate(dateTwo);
        testPlanDTO.setInitStatus("success");
        testPlanDTO.setStatusCode("todo");
        testPlanDTO.setAutoSync(false);
        testPlanDTO.setProjectId(project.getId());
        testPlanDTO.setManagerId(userId);
        return testPlanServcie.baseCreate(testPlanDTO).getPlanId();

    }


    private List<Long> initCase(List<Long> issueFolderIds, Long projectId, Long userId, Date date) {
        List<Long> list = new ArrayList<>();
        list.add(insertIssueCase(issueFolderIds.get(0),RANK_1, projectId, userId, date, "用户登录"));
        list.add(insertIssueCase(issueFolderIds.get(0),RANK_2, projectId, userId, date, "登录错误操作"));
        list.add(insertIssueCase(issueFolderIds.get(4), RANK_3,projectId, userId, date, "通过商品详情快速下单"));
        list.add(insertIssueCase(issueFolderIds.get(3), RANK_4,projectId, userId, date, "用户维护配送信息"));
        testCaseMapper.updateAuditFields(projectId,list.toArray(new Long[list.size()]), userId, date);
        return list;
    }

    private Long insertIssueCase(Long folderId, String rank,Long projectId, Long userId, Date date, String summary) {
        TestCaseVO testCaseVO = new TestCaseVO();
        testCaseVO.setProjectId(projectId);
        testCaseVO.setFolderId(folderId);
        testCaseVO.setSummary(summary);
        testCaseVO.setRank(rank);
        return testCaseService.createTestCase(projectId,testCaseVO).getCaseId();
    }

    private List<Long> initIssueFolders(Long versionId, Long projectId, Long userId, Date date) {
        List<Long> issueFolderIds = new ArrayList<>();
        long parentId = insertIssueFolder(versionId, projectId, "1.0版本", 0L);
        issueFolderIds.add(insertIssueFolder(versionId, projectId, "账户登录", parentId));
        issueFolderIds.add(insertIssueFolder(versionId, projectId, "商品列表", parentId));
        issueFolderIds.add(insertIssueFolder(versionId, projectId, "商品详情查看", parentId));
        issueFolderIds.add(insertIssueFolder(versionId, projectId, STRING_1, parentId));
        issueFolderIds.add(insertIssueFolder(versionId, projectId, "提交订单", parentId));
        testIssueFolderMapper.updateAuditFields(issueFolderIds.toArray(new Long[issueFolderIds.size()]), userId, date);
        return issueFolderIds;
    }

    private long insertIssueFolder(Long versionId, Long projectId, String folderName, Long parentId) {
        TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();
        testIssueFolderVO.setParentId(parentId);
        testIssueFolderVO.setName(folderName);
        testIssueFolderVO.setProjectId(projectId);
        testIssueFolderVO.setVersionId(versionId);
        testIssueFolderVO.setType("cycle");
        return testIssueFolderService.create(projectId, testIssueFolderVO).getFolderId();
    }

    private void initIssueSteps(List<Long> testIssueIds, Long projectId, Long userId, Date date) {

        insertIssueSteps(RANK_1, testIssueIds.get(0), "打开登录页", "", "登录页正常打开", projectId);
        insertIssueSteps(RANK_2, testIssueIds.get(0), "输入正确的用户名", "XXX", "用户名正常输入", projectId);
        insertIssueSteps(RANK_3, testIssueIds.get(0), "输入正确的用户密码", "XXX", "用户密码正常输入，且加密显示", projectId);
        insertIssueSteps(RANK_4, testIssueIds.get(0), "点击登录按钮", "", "登陆成功", projectId);

        insertIssueSteps(RANK_1, testIssueIds.get(1), "打开登录页", "", "登录页正常打开", projectId);
        insertIssueSteps(RANK_2, testIssueIds.get(1), "输入不存在的用户名", "ZZZ", "提示用户不存在", projectId);
        insertIssueSteps(RANK_3, testIssueIds.get(1), "输入正确的用户名", "XXX", "用户名正常输入，且加密显示", projectId);
        insertIssueSteps(RANK_6, testIssueIds.get(1), "不输入密码直接登录", "", "登录失败，提示密码为必输字段", projectId);
        insertIssueSteps(RANK_4, testIssueIds.get(1), "输入错误的用户密码", "ZZZ", "用户密码正常输入，且加密显示", projectId);
        insertIssueSteps(RANK_5, testIssueIds.get(1), "点击登录按钮", "", "登录失败，提示密码错误", projectId);

        insertIssueSteps(RANK_1, testIssueIds.get(2), "用户登录", "正确的用户名、密码", "登陆成功", projectId);
        insertIssueSteps(RANK_2, testIssueIds.get(2), "点击配送信息界面", "", "页面成功转跳", projectId);
        insertIssueSteps(RANK_3, testIssueIds.get(2), "点击加号进入新增地址页面", "", "新增地址页面转跳成功", projectId);
        insertIssueSteps(RANK_4, testIssueIds.get(2), "输入配送地址", "VVVVV", "配送地址成功输入", projectId);
        insertIssueSteps(RANK_5, testIssueIds.get(2), "输入收件人", "XXX", "收件人成功输入", projectId);
        insertIssueSteps(RANK_7, testIssueIds.get(2), "输入电话号码", "1111111", "用户电话成功输入", projectId);
        insertIssueSteps(RANK_8, testIssueIds.get(2), "点击保存按钮", "", "保存成功", projectId);

        insertIssueSteps(RANK_1, testIssueIds.get(3), "进入商品详情页面", "商品：A", "成功展示商品详情", projectId);
        insertIssueSteps(RANK_2, testIssueIds.get(3), "选择颜色、尺码", "颜色：红，尺码：XL", "成功选择商品选项", projectId);
        insertIssueSteps(RANK_3, testIssueIds.get(3), "点击直接购买按钮", "", "快速下单页面正常转跳", projectId);
        insertIssueSteps(RANK_4, testIssueIds.get(3), "选择配送信息", "配送地址：VVVVV", "成功选择用户配送信息", projectId);
        insertIssueSteps(RANK_5, testIssueIds.get(3), "点击立即下单按钮", "", "下单成功", projectId);
        insertIssueSteps(RANK_7, testIssueIds.get(3), "页面转跳", "", "转跳到我的订单页面", projectId);

        testCaseStepMapper.updateAuditFields(testIssueIds.toArray(new Long[testIssueIds.size()]), userId, date);
    }

    private void insertIssueSteps(String rank, Long issueId, String testStep, String testData, String expectedResult, Long projectId) {
        TestCaseStepVO testCaseStepVO = new TestCaseStepVO();
        testCaseStepVO.setRank(rank);
        testCaseStepVO.setIssueId(issueId);
        testCaseStepVO.setTestStep(testStep);
        testCaseStepVO.setTestData(testData);
        testCaseStepVO.setExpectedResult(expectedResult);

        testCaseStepService.changeStep(testCaseStepVO, projectId, false);
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

    private List<Long> initCycleFolders(Long planId, Long projectId, Long versionId, Date dateOne, Date dateTwo, Date dateThree, Date dateFour, Date dateFive, Date dateSix, List<Long> issueFolderIds, Long userId) {
        List<Long> cycleFolderIdsOne = new ArrayList<>();
        Long parentId = insertCycleFolder(projectId,RANK_1, 0L, "1.0版本", versionId, changeDateTimeStart(dateTwo), changeDateTimeEnd(dateThree), null, planId);
        cycleFolderIdsOne.add(insertCycleFolder(projectId, RANK_2,parentId, "账户登录", versionId, changeDateTimeStart(dateOne), changeDateTimeEnd(dateTwo), issueFolderIds.get(0), planId));
        cycleFolderIdsOne.add(insertCycleFolder(projectId, RANK_3,parentId, "提交订单", versionId, changeDateTimeStart(dateTwo), changeDateTimeEnd(dateThree), issueFolderIds.get(4), planId));
        cycleFolderIdsOne.add(insertCycleFolder(projectId, RANK_4,parentId, STRING_1, versionId, changeDateTimeStart(dateTwo), changeDateTimeEnd(dateTwo), issueFolderIds.get(3), planId));
        testCycleMapper.updateAuditFields(cycleFolderIdsOne.toArray(new Long[cycleFolderIdsOne.size()]), userId, dateOne);
        return cycleFolderIdsOne;
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

    private Long insertCycleFolder(Long projectId, String rank,Long parentCycleId, String cycleName, Long versionId, Date fromDate, Date toDate, Long folderId, Long planId) {
        TestCycleDTO testCycleVO = new TestCycleDTO();
        testCycleVO.setParentCycleId(parentCycleId);
        testCycleVO.setCycleName(cycleName);
        testCycleVO.setVersionId(versionId);
        testCycleVO.setFromDate(fromDate);
        testCycleVO.setToDate(toDate);
        testCycleVO.setType("folder");
        testCycleVO.setFolderId(folderId);
        testCycleVO.setProjectId(projectId);
        testCycleVO.setPlanId(planId);
        testCycleVO.setRank(rank);
        testCycleMapper.insert(testCycleVO);
        return testCycleVO.getCycleId();
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