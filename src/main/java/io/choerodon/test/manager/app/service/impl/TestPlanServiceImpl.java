package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ServiceUnavailableException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.utils.PageUtils;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.api.vo.agile.*;
import io.choerodon.test.manager.app.assembler.TestCycleAssembler;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.constant.SagaTopicCodeConstants;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestPlanInitStatus;
import io.choerodon.test.manager.infra.enums.TestPlanStatus;
import io.choerodon.test.manager.infra.feign.operator.AgileClientOperator;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import io.choerodon.test.manager.infra.util.PageUtil;
import io.choerodon.test.manager.infra.util.RankUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * @author: 25499
 * @date: 2019/11/26 14:17
 * @description:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestPlanServiceImpl implements TestPlanService {
    @Autowired
    private TestPlanMapper testPlanMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestIssueFolderService testIssueFolderService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCycleService testCycleService;

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    private TransactionalProducer producer;

    @Autowired
    private UserService userService;

    @Autowired
    private TestStatusMapper testStatusMapper;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private TestCycleAssembler testCycleAssembler;
    @Autowired
    private TestCycleMapper testCycleMapper;
    @Autowired
    private TestCaseLinkMapper testCaseLinkMapper;
    @Autowired
    private AgileClientOperator agileClientOperator;
    @Autowired
    private TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestPlanServiceImpl.class);

    private static final String ISSUE = "issue";
    private static final String BUG = "bug";


    @Override
    public TestPlanVO update(Long projectId, TestPlanVO testPlanVO) {
        if (!ObjectUtils.isEmpty(testPlanVO.getName()) && Boolean.TRUE.equals(checkNameUpdate(projectId, testPlanVO.getName(), testPlanVO.getPlanId()))) {
            throw new CommonException("error.update.plan.name.exist");
        }
        TestPlanDTO testPlan = testPlanMapper.selectByPrimaryKey(testPlanVO.getPlanId());
        if (TestPlanStatus.DOING.getStatus().equals(testPlan.getInitStatus())) {
            throw new CommonException("The plan is currently being operated");
        }
        TestPlanDTO testPlanDTO = modelMapper.map(testPlanVO, TestPlanDTO.class);
        testPlanVO.setProjectId(projectId);
        if (testPlanMapper.updateByPrimaryKeySelective(testPlanDTO) != 1) {
            throw new CommonException("error.update.plan");
        }
        testCycleAssembler.updatePlanTime(projectId, testPlanVO);
        return modelMapper.map(testPlanMapper.selectByPrimaryKey(testPlanDTO.getPlanId()), TestPlanVO.class);
    }

    private boolean checkNameUpdate(Long projectId, String name, Long planId) {
        TestPlanDTO search = new TestPlanDTO();
        search.setProjectId(projectId);
        search.setName(name);
        TestPlanDTO testPlanDTO = testPlanMapper.selectOne(search);
        return testPlanDTO != null && !planId.equals(testPlanDTO.getPlanId());
    }

    @Override
    public void operatePlanCalendar(Long projectId, TestCycleVO testCycleVO, Boolean isCycle) {
        if (Boolean.TRUE.equals(isCycle)) {
            testCycleService.update(projectId, testCycleVO);
        } else {
            TestPlanVO testPlanVO = new TestPlanVO();
            testPlanVO.setPlanId(testCycleVO.getCycleId());
            testPlanVO.setStartDate(testCycleVO.getFromDate());
            testPlanVO.setEndDate(testCycleVO.getToDate());
            testPlanVO.setObjectVersionNumber(testCycleVO.getObjectVersionNumber());
            update(projectId, testPlanVO);
        }
    }

    @Override
    public void orderByFromDate(Long projectId, Long planId) {
        List<TestCycleDTO> testCycleList = testCycleService.listByPlanIds(Collections.singletonList(planId), projectId);
        if (CollectionUtils.isEmpty(testCycleList)) {
            return;
        }
        final String[] rank = {RankUtil.mid()};
        testCycleList = testCycleList.stream().sorted(Comparator.comparing(TestCycleDTO::getFromDate))
                .peek(testCycle -> testCycle.setRank(rank[0] = RankUtil.genNext(rank[0]))).collect(Collectors.toList());
        testCycleList.forEach(cycle -> testCycleMapper.updateOptional(cycle, "rank"));
    }

    @Override
    @Async
    public void delete(Long projectId, Long planId) {
        baseDelete(planId);
        List<TestCycleDTO> testCycleDTOS = testCycleService.listByPlanIds(Arrays.asList(planId), projectId);
        List<Long> collect = testCycleDTOS.stream().map(TestCycleDTO::getCycleId).collect(Collectors.toList());
        testCycleService.batchDelete(collect);
    }

    @Override
    @Saga(code = SagaTopicCodeConstants.TEST_MANAGER_CREATE_PLAN,
            description = "test-manager创建测试计划", inputSchema = "{}")
    public TestPlanDTO create(Long projectId, TestPlanVO testPlanVO) {
        testPlanVO.setProjectId(projectId);
        checkPlan(testPlanVO);
        // 创建计划
        TestPlanDTO testPlan = modelMapper.map(testPlanVO, TestPlanDTO.class);
        testPlan.setStatusCode(TestPlanStatus.TODO.getStatus());
        testPlan.setInitStatus("creating");
        TestPlanDTO testPlanDTO = baseCreate(testPlan);
        testPlanVO.setPlanId(testPlanDTO.getPlanId());
        testPlanVO.setObjectVersionNumber(testPlanDTO.getObjectVersionNumber());
        producer.apply(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("")
                        .withSagaCode(SagaTopicCodeConstants.TEST_MANAGER_CREATE_PLAN)
                        .withPayloadAndSerialize(testPlanVO)
                        .withRefId("")
                        .withSourceId(projectId),
                builder -> {
                });

        return testPlanDTO;
    }

    private void checkPlan(TestPlanVO testPlanVO) {
        if (ObjectUtils.isEmpty(testPlanVO.getManagerId())) {
            throw new CommonException("error.create.plan.manager.null");
        }

        if (ObjectUtils.isEmpty(testPlanVO.getStartDate()) || ObjectUtils.isEmpty(testPlanVO.getEndDate())) {
            throw new CommonException("error.create.plan.date.scope.null");
        }
        if (Boolean.TRUE.equals(checkName(testPlanVO.getProjectId(), testPlanVO.getName()))) {
            throw new CommonException("error.create.plan.name.exist");
        }
    }

    @Override
    public TestTreeIssueFolderVO buildPlanTree(Long projectId, String statusCode) {
        TestPlanDTO testPlanDTO = new TestPlanDTO();
        testPlanDTO.setProjectId(projectId);
        testPlanDTO.setStatusCode(statusCode);
        List<TestPlanDTO> testPlanDTOS = testPlanMapper.select(testPlanDTO);
        if (CollectionUtils.isEmpty(testPlanDTOS)) {
            return new TestTreeIssueFolderVO();
        }
        List<Long> planIds = testPlanDTOS.stream().map(TestPlanDTO::getPlanId).collect(Collectors.toList());
        List<TestCycleDTO> testCycle = testCycleService.listByPlanIds(planIds, projectId);
        Map<Long, List<TestCycleDTO>> cycleMap = testCycle.stream().collect(Collectors.groupingBy(TestCycleDTO::getPlanId));
        TestTreeIssueFolderVO testTreeIssueFolderVO = new TestTreeIssueFolderVO();
        List<Long> root = new ArrayList<>();
        List<TestTreeFolderVO> treeList = new ArrayList<>();
        testPlanDTOS.stream()
                .sorted(Comparator.comparing(TestPlanDTO::getPlanId).reversed())
                .forEach(v -> {
                    root.add(v.getPlanId());
                    TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();
                    testIssueFolderVO.setProjectId(v.getProjectId());
                    testIssueFolderVO.setName(v.getName());
                    testIssueFolderVO.setFolderId(v.getPlanId());
                    testIssueFolderVO.setInitStatus(v.getInitStatus());
                    testIssueFolderVO.setObjectVersionNumber(v.getObjectVersionNumber());
                    testIssueFolderVO.setFromDate(v.getStartDate());
                    testIssueFolderVO.setToDate(v.getEndDate());
                    TestTreeFolderVO planTreeVO = new TestTreeFolderVO();
                    planTreeVO.setId(v.getPlanId());
                    planTreeVO.setIssueFolderVO(testIssueFolderVO);
                    planTreeVO.setChildrenLoading(false);
                    planTreeVO.setExpanded(false);
                    planTreeVO.setTopLevel(true);
                    planTreeVO.setHasCase(false);

                    List<TestCycleDTO> testCycleDTOS = cycleMap.get(v.getPlanId());
                    if (CollectionUtils.isEmpty(testCycleDTOS)) {
                        planTreeVO.setHasChildren(false);
                        treeList.add(planTreeVO);
                        return;
                    }
                    treeList.add(planTreeVO);
                    Map<Long, List<Long>> cycleIdMap = testCycleDTOS.stream().map(testCycleDTO -> {
                        if (testCycleDTO.getParentCycleId() == null) {
                            testCycleDTO.setParentCycleId(0L);
                        }
                        return testCycleDTO;
                    }).collect(Collectors.groupingBy(TestCycleDTO::getParentCycleId, Collectors.mapping(TestCycleDTO::getCycleId, Collectors.toList())));
                    List<Long> folderRoot = cycleIdMap.get(0L);
                    if (!CollectionUtils.isEmpty(folderRoot)) {
                        planTreeVO.setHasChildren(true);
                        planTreeVO.setChildren(folderRoot);
                    } else {
                        planTreeVO.setHasChildren(false);
                    }
                    List<TestTreeFolderVO> folderVo = testCycleDTOS.stream().map(cycleDTO -> buildTreeCycleCaseToFolder(cycleDTO, cycleIdMap)).collect(Collectors.toList());
                    treeList.addAll(folderVo);
                });
        testTreeIssueFolderVO.setRootIds(root);
        testTreeIssueFolderVO.setTreeFolder(treeList);
        return testTreeIssueFolderVO;
    }

    private TestTreeFolderVO buildTreeCycleCaseToFolder(TestCycleDTO testCycleDTO, Map<Long, List<Long>> cycleMap) {
        TestTreeFolderVO testTreeFolderVO = new TestTreeFolderVO();
        testTreeFolderVO.setId(testCycleDTO.getCycleId());
        if (!ObjectUtils.isEmpty(cycleMap.get(testCycleDTO.getCycleId()))) {
            testTreeFolderVO.setChildren(cycleMap.get(testCycleDTO.getCycleId()));
            testTreeFolderVO.setHasChildren(true);
        } else {
            testTreeFolderVO.setHasChildren(false);
        }
        testTreeFolderVO.setHasCase(testCycleDTO.getCaseCount() > 0);
        testTreeFolderVO.setIssueFolderVO(testCycleService.cycleToIssueFolderVO(testCycleDTO));
        testTreeFolderVO.setExpanded(false);
        testTreeFolderVO.setChildrenLoading(false);
        testTreeFolderVO.setPlanId(testCycleDTO.getPlanId());
        testTreeFolderVO.setTopLevel(false);
        return testTreeFolderVO;
    }

    @Override
    public void baseUpdate(TestPlanDTO testPlanDTO) {
        if (ObjectUtils.isEmpty(testPlanDTO)) {
            throw new CommonException("error.test.plan.is.null");
        }
        DBValidateUtil.executeAndvalidateUpdateNum(testPlanMapper::updateByPrimaryKeySelective, testPlanDTO, 1, "error.update.test.plan");
    }

    @Override
    public void sagaCreatePlan(TestPlanVO testPlanVO) {
        List<TestIssueFolderDTO> testIssueFolderDTOS = new ArrayList<>();
        List<Long> caseIds = new ArrayList<>();
        // 是否自选
        if (Boolean.FALSE.equals(testPlanVO.getCustom())) {
            testIssueFolderDTOS.addAll(testIssueFolderService.listByProject(testPlanVO.getProjectId()).stream()
                    .filter(dto -> StringUtils.isBlank(dto.getInitStatus()) || StringUtils.equals(TestPlanInitStatus.SUCCESS, dto.getInitStatus()))
                    .collect(Collectors.toList()));
        } else {
            createPlanCustomCase(testPlanVO, testIssueFolderDTOS, caseIds);
            if (Boolean.TRUE.equals(testPlanVO.getSprintLink())) {
                createPlanSprintCase(testPlanVO, testIssueFolderDTOS, caseIds);
            }
        }
        TestPlanDTO testPlanDTO = testPlanMapper.selectByPrimaryKey(testPlanVO.getPlanId());
        // 创建测试循环
        List<TestCycleDTO> testCycleDTOS = testCycleService.batchInsertByFoldersAndPlan(testPlanDTO, testIssueFolderDTOS);
        if (CollectionUtils.isEmpty(testCycleDTOS)) {
            testPlanDTO.setInitStatus(TestPlanInitStatus.SUCCESS);
            baseUpdate(testPlanDTO);
            return;
        }
        // 创建测试循环用例
        Map<Long, TestCycleDTO> testCycleMap = testCycleDTOS.stream().collect(Collectors.toMap(TestCycleDTO::getFolderId, Function.identity()));
        testCycleCaseService.batchInsertByTestCase(testCycleMap, caseIds, testPlanVO.getProjectId(), testPlanVO.getPlanId());
        TestPlanDTO testPlan = new TestPlanDTO();
        testPlan.setPlanId(testPlanVO.getPlanId());
        testPlan.setInitStatus(TestPlanInitStatus.SUCCESS);
        testPlan.setObjectVersionNumber(testPlanVO.getObjectVersionNumber());
        baseUpdate(testPlan);
    }

    private void createPlanSprintCase(TestPlanVO testPlanVO, List<TestIssueFolderDTO> testIssueFolderDTOS, List<Long> caseIds) {
        if (testPlanVO.getSprintId() == null) {
            return;
        }
        Long projectId = testPlanVO.getProjectId();
        Set<Long> selectedCaseIdsSet = new HashSet<>(caseIds);
        Set<Long> selectedFolderIdsSet = testIssueFolderDTOS.stream().map(TestIssueFolderDTO::getFolderId).collect(Collectors.toSet());
        Set<Long> folderIdsSet = new HashSet<>();
        Map<String, Object> otherArgs = new HashMap<>(1);
        otherArgs.put("sprint", Stream.of(testPlanVO.getSprintId()).collect(Collectors.toList()));
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setOtherArgs(otherArgs);
        List<Long> issueIds = agileClientOperator.queryIssueIdsByOptions(projectId, searchDTO);

        if (CollectionUtils.isEmpty(issueIds)) {
            return;
        }

        List<TestCaseDTO> caseList = testCaseLinkMapper.listByIssueIds(issueIds);
        if (CollectionUtils.isEmpty(caseList)) {
            return;
        }
        caseList.forEach(testCase -> {
            if (!selectedCaseIdsSet.contains(testCase.getCaseId())) {
                caseIds.add(testCase.getCaseId());
            }
            if (!selectedFolderIdsSet.contains(testCase.getFolderId())) {
                folderIdsSet.add(testCase.getFolderId());
            }
        });
        if (CollectionUtils.isNotEmpty(folderIdsSet)) {
            testIssueFolderDTOS.addAll(testIssueFolderService.listFolderByFolderIds(projectId, new ArrayList<>(folderIdsSet)));
        }
    }

    @Override
    public TestPlanVO queryPlan(Long projectId, Long planId) {
        TestPlanDTO testPlanDTO = new TestPlanDTO();
        testPlanDTO.setPlanId(planId);
        TestPlanDTO testPlan = testPlanMapper.selectOne(testPlanDTO);
        TestPlanVO testPlanVO = modelMapper.map(testPlan, TestPlanVO.class);
        Long managerId = testPlan.getManagerId();
        testPlanVO.setManagerUser(getUserById(managerId));
        testPlanVO.setProductVersionDTO(
                agileClientOperator.queryProductVersionById(projectId, testPlan.getProductVersionId()));
        testPlanVO.setSprintNameDTO(
                agileClientOperator.querySprintNameById(projectId, testPlan.getSprintId()));
        return testPlanVO;
    }


    private UserDO getUserById(Long userId) {
        if (userId != null) {
            Map<Long, UserDO> map = userService.query(new Long[]{userId});
            return map.get(userId);
        }
        return null;
    }

    @Override
    public TestPlanReporterInfoVO reporterInfo(Long projectId, Long planId) {
        TestPlanReporterInfoVO result = new TestPlanReporterInfoVO();
        TestPlanDTO testPlanDTO = testPlanMapper.selectByPrimaryKey(planId);
        if (testPlanDTO == null) {
            throw new CommonException("error.test.plan.not.existed");
        }
        Long managerId = testPlanDTO.getManagerId();
        UserDO user = getUserById(managerId);
        result.setManager(user);
        result.setStartDate(testPlanDTO.getStartDate());
        result.setEndDate(testPlanDTO.getEndDate());
        Integer totalCaseCount = testCycleCaseMapper.selectCaseCount(planId);
        result.setTotalCaseCount(totalCaseCount);
        List<Long> issueIds = testCaseLinkMapper.selectIssueIdByPlanId(planId, null);
        int relatedIssueCount = 0;
        int totalBugCount = 0;
        int solvedBugCount = 0;
        Set<Long> bugIds = testCycleCaseDefectRelMapper.selectIssueIdByPlanId(planId, null);

        Set<Long> allIssueIds = new HashSet<>(issueIds);
        allIssueIds.addAll(bugIds);
        if (!allIssueIds.isEmpty()) {
            List<IssueLinkVO> issueInfos;
            try {
                issueInfos = agileClientOperator.queryIssues(projectId, new ArrayList<>(allIssueIds));
            } catch (ServiceUnavailableException e) {
                LOGGER.error("feign exception: {}", e);
                result.setRelatedIssueCount(relatedIssueCount);
                result.setTotalBugCount(totalBugCount);
                result.setSolvedBugCount(solvedBugCount);
                return result;
            }
            Map<Long, IssueLinkVO> map = issueInfos.stream().collect(Collectors.toMap(IssueLinkVO::getIssueId, Function.identity()));
            for (Long id : issueIds) {
                if (!ObjectUtils.isEmpty(map.get(id))) {
                    relatedIssueCount++;
                }
            }
            for (Long id : bugIds) {
                IssueLinkVO issue = map.get(id);
                if (!ObjectUtils.isEmpty(issue)) {
                    totalBugCount++;
                    StatusVO status = issue.getStatusVO();
                    if (!ObjectUtils.isEmpty(status)
                            && Boolean.TRUE.equals(status.getCompleted())) {
                        solvedBugCount++;
                    }
                }
            }
        }
        result.setPassedIssueCount(calculatePassedIssueCount(issueIds, planId, projectId));
        result.setRelatedIssueCount(relatedIssueCount);
        result.setTotalBugCount(totalBugCount);
        result.setSolvedBugCount(solvedBugCount);
        return result;
    }

    private Integer calculatePassedIssueCount(List<Long> issueIds,
                                              Long planId,
                                              Long projectId) {
        int count = 0;
        if (ObjectUtils.isEmpty(issueIds)) {
            return count;
        }
        String statusName = "通过";
        Long statusId = queryStatusIdByName(projectId, statusName);
        if (statusId == null) {
            throw new CommonException("error.test.status.not.existed.name." + statusName);
        }
        List<TestPlanReporterIssueVO> issues = queryIssue(issueIds, planId, null, ISSUE);
        for (TestPlanReporterIssueVO vo : issues) {
            boolean passed = true;
            for (TestFolderCycleCaseVO testFolderCycleCase : vo.getTestFolderCycleCases()) {
                if (!statusId.equals(testFolderCycleCase.getExecutionStatus())) {
                    passed = false;
                    break;
                }
            }
            if (passed) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Page<TestPlanReporterIssueVO> pagedQueryIssues(Long projectId,
                                                          Long planId,
                                                          PageRequest pageRequest,
                                                          TestPlanReporterIssueVO query) {
        List<Long> issueIds = testCaseLinkMapper.selectIssueIdByPlanId(planId, query);
        if (issueIds.isEmpty()) {
            return PageUtil.empty(pageRequest);
        }
        return pagedQueryByIssueIds(projectId, planId, pageRequest, query, issueIds, ISSUE);
    }

    private Page<TestPlanReporterIssueVO> pagedQueryByIssueIds(Long projectId,
                                                               Long planId,
                                                               PageRequest pageRequest,
                                                               TestPlanReporterIssueVO query,
                                                               List<Long> issueIds,
                                                               String queryType) {
        String statusName = "通过";
        Long statusId = queryStatusIdByName(projectId, statusName);
        if (statusId == null) {
            throw new CommonException("error.test.status.not.existed.name." + statusName);
        }
        query.setPassStatusId(statusId);
        Page<IssueLinkVO> page;
        try {
            page = agileClientOperator
                    .pagedQueryIssueByOptions(projectId, pageRequest.getPage(), pageRequest.getSize(), new IssueQueryVO(issueIds, query));
            List<IssueLinkVO> content = page.getContent();
            if (content.isEmpty()) {
                return PageUtil.empty(pageRequest);
            }
            Map<Long, IssueLinkVO> issueMap = new HashMap<>();
            Set<Long> userId = new HashSet<>();
            content.forEach(c -> {
                issueMap.put(c.getIssueId(), c);
                if (c.getAssigneeId() != null) {
                    userId.add(c.getAssigneeId());
                }
            });
            Map<Long, UserMessageDTO> userMap = userService.queryUsersMap(new ArrayList<>(userId));
            List<Long> existedIssueIds = new ArrayList<>(issueMap.keySet());
            List<TestPlanReporterIssueVO> issues = queryIssue(existedIssueIds, planId, query, queryType);
            List<TestPlanReporterIssueVO> result = new ArrayList<>();
            issues.forEach(r -> {
                Long issueId = r.getIssueId();
                IssueLinkVO issue = issueMap.get(issueId);
                if (issue == null) {
                    return;
                }
                r.setSummary(issue.getSummary());
                Long assigneeId = issue.getAssigneeId();
                if (assigneeId != null) {
                    r.setAssignee(userMap.get(assigneeId));
                }
                r.setStatusMapVO(issue.getStatusVO());
                int count = 0;
                List<TestFolderCycleCaseVO> caseList = r.getTestFolderCycleCases();
                if (!ObjectUtils.isEmpty(caseList)) {
                    for (TestFolderCycleCaseVO t : caseList) {
                        if (query.getPassStatusId().equals(t.getExecutionStatus())) {
                            count++;
                        }
                    }
                }
                r.setPassedCaseCount(count);
                result.add(r);
            });
            return PageUtils.copyPropertiesAndResetContent(page, result);
        } catch (Exception e) {
            LOGGER.error("feign exception: {}", e);
            return PageUtil.empty(pageRequest);
        }
    }

    private List<TestPlanReporterIssueVO> queryIssue(List<Long> existedIssueIds,
                                                     Long planId,
                                                     TestPlanReporterIssueVO query,
                                                     String queryType) {
        if (ISSUE.equals(queryType)) {
            return testCaseLinkMapper.selectWithCaseByIssueIds(existedIssueIds, planId, query);
        } else if (BUG.equals(queryType)) {
            return testCycleCaseDefectRelMapper.selectWithCaseByIssueIds(existedIssueIds, planId, query);
        } else {
            throw new CommonException("error.illegal.query.type." + queryType);
        }
    }

    @Override
    public Page<TestPlanReporterIssueVO> pagedQueryBugs(Long projectId,
                                                        Long planId,
                                                        PageRequest pageRequest,
                                                        TestPlanReporterIssueVO query) {

        Set<Long> bugIds = testCycleCaseDefectRelMapper.selectIssueIdByPlanId(planId, query);
        if (bugIds.isEmpty()) {
            return PageUtil.empty(pageRequest);
        }
        return pagedQueryByIssueIds(projectId, planId, pageRequest, query, new ArrayList<>(bugIds), BUG);
    }

    @Override
    public Boolean checkName(Long projectId, String name) {
        TestPlanDTO search = new TestPlanDTO();
        search.setProjectId(projectId);
        search.setName(name);
        return testPlanMapper.selectOne(search) != null;
    }

    private Long queryStatusIdByName(Long projectId, String name) {
        TestStatusDTO dto = new TestStatusDTO();
        dto.setProjectId(projectId);
        dto.setStatusType("CYCLE_CASE");
        dto.setStatusName(name);
        List<TestStatusDTO> result = testStatusMapper.queryAllUnderProject(dto);
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0).getStatusId();
        }
    }

    @Override
    public void updateStatusCode(Long projectId, TestPlanDTO testPlanDTO) {
        testPlanDTO.setProjectId(projectId);
        baseUpdate(testPlanDTO);
    }

    @Override
    @Saga(code = SagaTopicCodeConstants.TEST_MANAGER_CLONE_PLAN,
            description = "test-manager 复制测试计划", inputSchema = "{}")
    public TestPlanVO clone(Long projectId, Long planId, String name) {
        if (Boolean.TRUE.equals(checkName(projectId, name))) {
            throw new CommonException("error.clone.plan.name.exist");
        }
        TestPlanDTO testPlanDTO = testPlanMapper.selectByPrimaryKey(planId);
        testPlanDTO.setPlanId(null);
        testPlanDTO.setCreatedBy(null);
        testPlanDTO.setLastUpdateDate(null);
        testPlanDTO.setCreationDate(null);
        testPlanDTO.setLastUpdatedBy(null);
        testPlanDTO.setObjectVersionNumber(null);
        testPlanDTO.setName(name);
        testPlanDTO.setInitStatus(TestPlanInitStatus.CREATING);
        testPlanDTO.setStatusCode(TestPlanStatus.TODO.getStatus());
        baseCreate(testPlanDTO);
        Map<String, Long> map = new HashMap<>();
        map.put("older", planId);
        map.put("new", testPlanDTO.getPlanId());
        producer.apply(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("")
                        .withSagaCode(SagaTopicCodeConstants.TEST_MANAGER_CLONE_PLAN)
                        .withPayloadAndSerialize(map)
                        .withRefId("")
                        .withSourceId(projectId),
                builder -> {
                });
        return modelMapper.map(testPlanDTO, TestPlanVO.class);
    }

    @Override
    public void sagaClonePlan(Map<String, Long> map) {
        Long copyPlanId = map.get("older");
        Long newPlanId = map.get("new");
        TestPlanDTO testPlanDTO = testPlanMapper.selectByPrimaryKey(newPlanId);
        testCycleService.cloneCycleByPlanId(copyPlanId, newPlanId, testPlanDTO.getProjectId());
        testPlanDTO.setInitStatus(TestPlanInitStatus.SUCCESS);
        baseUpdate(testPlanDTO);
    }

    @Override
    public List<FormStatusVO> planStatus(Long projectId, Long planId) {
        // 查询项目下自定义和默认状态
        TestStatusDTO testStatusDTO = new TestStatusDTO();
        testStatusDTO.setProjectId(projectId);
        testStatusDTO.setStatusType("CYCLE_CASE");
        List<FormStatusVO> formStatusVOS = testCycleCaseMapper.selectPlanStatus(planId);
        List<Long> collect = formStatusVOS.stream().map(FormStatusVO::getStatusId).collect(Collectors.toList());
        List<TestStatusDTO> testStatusDTOList = testStatusMapper.queryAllUnderProject(testStatusDTO)
                .stream().filter(e -> !collect.contains(e.getStatusId())).collect(Collectors.toList());
        List<FormStatusVO> formStatusVOList = modelMapper.map(testStatusDTOList, new TypeToken<List<FormStatusVO>>() {
        }.getType());
        formStatusVOList.stream().forEach(e -> e.setCounts(0L));
        formStatusVOS.addAll(formStatusVOList);
        return formStatusVOS;
    }

    @Override
    public List<TestPlanVO> projectPlan(Long projectId) {
        TestPlanDTO testPlanDTO = new TestPlanDTO();
        testPlanDTO.setProjectId(projectId);
        List<TestPlanDTO> testPlanDTOS = testPlanMapper.select(testPlanDTO);
        List<TestPlanVO> testPlanVOS = modelMapper.map(testPlanDTOS, new TypeToken<List<TestPlanVO>>() {
        }.getType());
        Map<Long, SprintNameDTO> sprintMap = agileClientOperator.querySprintMapByProject(projectId);
        Map<Long, ProductVersionDTO> productVersionMap = agileClientOperator.queryProductVersionMapByProject(projectId);
        testPlanVOS.forEach(testPlanVO -> {
            testPlanVO.setSprintNameDTO(sprintMap.get(testPlanVO.getSprintId()));
            testPlanVO.setProductVersionDTO(productVersionMap.get(testPlanVO.getProductVersionId()));
        });
        return testPlanVOS;
    }

    @Override
    @Saga(code = SagaTopicCodeConstants.TEST_MANAGER_PLAN_FAIL,
            description = "test-manager 改变测试测试计划的状态为fail", inputSchema = "{}")
    public void setPlanInitStatusFail(TestPlanVO testPlanVO) {
        producer.apply(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("")
                        .withSagaCode(SagaTopicCodeConstants.TEST_MANAGER_PLAN_FAIL)
                        .withPayloadAndSerialize(testPlanVO)
                        .withRefId("")
                        .withSourceId(testPlanVO.getProjectId()),
                builder -> {
                });
    }

    @Override
    public TestPlanVO queryPlanInfo(Long projectId, Long planId) {
        // 查询计划的信息
        TestPlanDTO testPlanDTO = testPlanMapper.selectByPrimaryKey(planId);
        if (ObjectUtils.isEmpty(testPlanDTO)) {
            return new TestPlanVO();
        }
        TestPlanVO testPlanVO = modelMapper.map(testPlanDTO, TestPlanVO.class);
        testPlanVO.setProductVersionDTO(
                agileClientOperator.queryProductVersionById(projectId, testPlanVO.getProductVersionId()));
        testPlanVO.setSprintNameDTO(
                agileClientOperator.querySprintNameById(projectId, testPlanVO.getSprintId()));
        return testPlanVO;
    }

    @Override
    public TestPlanDTO baseCreate(TestPlanDTO testPlanDTO) {
        if (ObjectUtils.isEmpty(testPlanDTO)) {
            throw new CommonException("error.test.plan.is.not.null");
        }
        if (testPlanMapper.insertSelective(testPlanDTO) != 1) {
            throw new CommonException("error.insert.test.plan");
        }
        return testPlanMapper.selectByPrimaryKey(testPlanDTO.getPlanId());
    }

    private void baseDelete(Long planId) {
        if (testPlanMapper.deleteByPrimaryKey(planId) != 1) {
            throw new CommonException("error.delete.test.plan");
        }
    }


    private void buildTree(List<Long> root, Long cycleId, Map<Long, TestCycleDTO> allFolderMap, Map<Long, TestTreeFolderVO> map, Map<Long, List<TestCycleDTO>> parentMap, Long planId) {
        TestCycleDTO testCycleDTO = allFolderMap.get(cycleId);
        if (ObjectUtils.isEmpty(testCycleDTO)) {
            return;
        }
        TestTreeFolderVO testTreeFolderVO = null;
        // 读取map中是否存在当前文件夹信息
        if (!ObjectUtils.isEmpty(map.get(cycleId))) {
            testTreeFolderVO = map.get(cycleId);
        }
        // 不存在就新建
        if (ObjectUtils.isEmpty(testTreeFolderVO)) {
            testTreeFolderVO = new TestTreeFolderVO();
            bulidTestTreeFolderVO(testTreeFolderVO, testCycleDTO, parentMap, map);
        }

        // 判断是不是顶层文件夹,是顶层文件夹就结束递归
        if (ObjectUtils.isEmpty(testCycleDTO.getParentCycleId()) || testCycleDTO.getParentCycleId() == 0) {
            if (!root.contains(testCycleDTO.getCycleId())) {
                root.add(testCycleDTO.getCycleId());
            }
        } else {
            folderParentNotZero(root, testCycleDTO, allFolderMap, map, parentMap, planId);
        }
    }

    private void bulidTestTreeFolderVO(TestTreeFolderVO testTreeFolderVO, TestCycleDTO testCycleDTO, Map<Long, List<TestCycleDTO>> parentMap, Map<Long, TestTreeFolderVO> map) {
        testTreeFolderVO.setId(testCycleDTO.getCycleId());
        if (!ObjectUtils.isEmpty(testCycleDTO.getParentCycleId()) && CollectionUtils.isEmpty(parentMap.get(testCycleDTO.getCycleId()))) {
            testTreeFolderVO.setHasChildren(false);
        } else {
            testTreeFolderVO.setHasChildren(true);
        }
        testTreeFolderVO.setHasCase(testCycleDTO.getCaseCount() == 0);
        testTreeFolderVO.setIssueFolderVO(testCycleService.cycleToIssueFolderVO(testCycleDTO));
        testTreeFolderVO.setExpanded(false);
        testTreeFolderVO.setChildrenLoading(false);
        testTreeFolderVO.setPlanId(testCycleDTO.getPlanId());
        testTreeFolderVO.setTopLevel(false);
        map.put(testCycleDTO.getCycleId(), testTreeFolderVO);
    }

    /**
     * 构建测试计划树时，文件夹的父文件夹不为0时的处理逻辑
     *
     * @param root
     * @param testCycleDTO
     * @param allFolderMap
     * @param map
     * @param parentMap
     */
    private void folderParentNotZero(List<Long> root, TestCycleDTO testCycleDTO, Map<Long, TestCycleDTO> allFolderMap, Map<Long, TestTreeFolderVO> map, Map<Long, List<TestCycleDTO>> parentMap, Long planId) {
        // 查看当前文件夹的父文件夹是否存在
        TestTreeFolderVO parentTreeFolderVO = null;
        if (!ObjectUtils.isEmpty(map.get(testCycleDTO.getParentCycleId()))) {
            parentTreeFolderVO = map.get(testCycleDTO.getParentCycleId());
        }
        if (ObjectUtils.isEmpty(parentTreeFolderVO)) {
            // 不存在就创建父级文件夹,并加入map中
            parentTreeFolderVO = new TestTreeFolderVO();
            if (ObjectUtils.isEmpty(allFolderMap.get(testCycleDTO.getParentCycleId()))) {
                return;
            }
            parentTreeFolderVO.setChildren(Arrays.asList(testCycleDTO.getCycleId()));
            TestCycleDTO parentCycle = allFolderMap.get(testCycleDTO.getParentCycleId());
            bulidTestTreeFolderVO(parentTreeFolderVO, parentCycle, parentMap, map);
        } else {
            //存在就更新父文件夹的Children值
            List<Long> children = new ArrayList<>();
            if (!ObjectUtils.isEmpty(parentTreeFolderVO.getChildren())) {
                children.addAll(parentTreeFolderVO.getChildren());
            }
            if (!children.contains(testCycleDTO.getCycleId())) {
                children.add(testCycleDTO.getCycleId());
            }
            parentTreeFolderVO.setChildren(children);
            map.put(testCycleDTO.getParentCycleId(), parentTreeFolderVO);
        }
        //使用父文件夹递归
        buildTree(root, testCycleDTO.getParentCycleId(), allFolderMap, map, parentMap, planId);
    }

    /**
     * 创建计划自选用例时，对用例的逻辑处理
     *
     * @param testPlanVO
     * @param testIssueFolderDTOS
     * @param caseIds
     */
    private void createPlanCustomCase(TestPlanVO testPlanVO, List<TestIssueFolderDTO> testIssueFolderDTOS, List<Long> caseIds) {
        Map<Long, CaseSelectVO> maps = testPlanVO.getCaseSelected();
        List<Long> folderIds = maps.keySet().stream().collect(Collectors.toList());
        testIssueFolderDTOS.addAll(testIssueFolderService.listFolderByFolderIds(testPlanVO.getProjectId(), folderIds));
        Set<Long> unSelectFolderIds = new HashSet<>();
        List<Long> unSelectCaseIds = new ArrayList<>();
        Set<Long> allSelectFolderIds = new HashSet<>();
        for (Map.Entry<Long, CaseSelectVO> entry : maps.entrySet()) {
            CaseSelectVO caseSelectVO = entry.getValue();
            // 判断是否是自选
            if (Boolean.FALSE.equals(caseSelectVO.getCustom())) {
                allSelectFolderIds.add(entry.getKey());
            } else {
                // 判断是反选还是正向选择
                if (CollectionUtils.isEmpty(caseSelectVO.getSelected())) {
                    unSelectFolderIds.add(entry.getKey());
                    unSelectCaseIds.addAll(caseSelectVO.getUnSelected());
                } else {
                    caseIds.addAll(caseSelectVO.getSelected());
                }
            }
        }
        if (!CollectionUtils.isEmpty(unSelectCaseIds) && !CollectionUtils.isEmpty(unSelectFolderIds)) {
            caseIds.addAll(testCaseMapper.listUnSelectCaseId(testPlanVO.getProjectId(), unSelectCaseIds, unSelectFolderIds));
        }
        if (!CollectionUtils.isEmpty(allSelectFolderIds)) {
            caseIds.addAll(testCaseMapper.listCaseIds(testPlanVO.getProjectId(), allSelectFolderIds, null));
        }
    }

}
