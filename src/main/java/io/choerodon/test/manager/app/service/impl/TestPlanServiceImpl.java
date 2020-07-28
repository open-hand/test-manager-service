package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.choerodon.test.manager.app.assembler.TestCycleAssembler;
import io.choerodon.test.manager.infra.mapper.TestCaseMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.test.manager.api.vo.agile.UserDO;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.constant.SagaTopicCodeConstants;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestPlanInitStatus;
import io.choerodon.test.manager.infra.enums.TestPlanStatus;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper;
import io.choerodon.test.manager.infra.mapper.TestPlanMapper;
import io.choerodon.test.manager.infra.mapper.TestStatusMapper;
import io.choerodon.test.manager.infra.util.DBValidateUtil;

/**
 * @author: 25499
 * @date: 2019/11/26 14:17
 * @description:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestPlanServiceImpl implements TestPlanServcie {
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

    @Override
    public TestPlanVO update(Long projectId, TestPlanVO testPlanVO) {
        TestPlanDTO testPlan = testPlanMapper.selectByPrimaryKey(testPlanVO.getPlanId());
        if (TestPlanStatus.DOING.getStatus().equals(testPlan.getInitStatus())) {
            throw new CommonException("The plan is currently being operated");
        }
        TestPlanDTO testPlanDTO = modelMapper.map(testPlanVO, TestPlanDTO.class);
        testPlanVO.setProjectId(projectId);
        if (testPlanMapper.updateByPrimaryKeySelective(testPlanDTO) != 1) {
            throw new CommonException("error.update.plan");
        }
        testCycleAssembler.updatePlanTime(projectId,testPlanVO);
        return modelMapper.map(testPlanMapper.selectByPrimaryKey(testPlanDTO.getPlanId()), TestPlanVO.class);
    }

    @Override
    public void operatePlanCalendar(Long projectId, TestCycleVO testCycleVO, Boolean isCycle) {
        if(Boolean.TRUE.equals(isCycle)){
            testCycleService.update(projectId,testCycleVO);
        }
        else {
            TestPlanVO testPlanVO = new TestPlanVO();
            testPlanVO.setPlanId(testCycleVO.getCycleId());
            testPlanVO.setStartDate(testCycleVO.getFromDate());
            testPlanVO.setEndDate(testCycleVO.getToDate());
            testPlanVO.setObjectVersionNumber(testCycleVO.getObjectVersionNumber());
            update(projectId,testPlanVO);
        }
    }

    @Override
    @Async
    public void delete(Long projectId, Long planId) {
        baseDelete(planId);
        List<TestCycleDTO> testCycleDTOS = testCycleService.listByPlanIds(Arrays.asList(planId),projectId);
        List<Long> collect = testCycleDTOS.stream().map(TestCycleDTO::getCycleId).collect(Collectors.toList());
        testCycleService.batchDelete(collect);
    }

    @Override
    @Saga(code = SagaTopicCodeConstants.TEST_MANAGER_CREATE_PLAN,
            description = "test-manager创建测试计划", inputSchema = "{}")
    public TestPlanDTO create(Long projectId, TestPlanVO testPlanVO) {
        checkPlan(testPlanVO);
        // 创建计划
        testPlanVO.setProjectId(projectId);
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

    private void checkPlan(TestPlanVO testPlanVO){
        if (ObjectUtils.isEmpty(testPlanVO.getManagerId())) {
            throw new CommonException("error.create.plan.manager.null");
        }

        if (ObjectUtils.isEmpty(testPlanVO.getStartDate()) || ObjectUtils.isEmpty(testPlanVO.getEndDate())) {
            throw new CommonException("error.create.plan.date.scope.null");
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
        List<TestCycleDTO> testCycle = testCycleService.listByPlanIds(planIds,projectId);
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
                    if(CollectionUtils.isEmpty(testCycleDTOS)){
                        planTreeVO.setHasChildren(false);
                        treeList.add(planTreeVO);
                        return;
                    }
                    treeList.add(planTreeVO);
                    Map<Long, List<Long>> cycleIdMap = testCycleDTOS.stream().map(testCycleDTO -> {
                        if(testCycleDTO.getParentCycleId() == null){
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
            testIssueFolderDTOS.addAll(testIssueFolderService.listByProject(testPlanVO.getProjectId()));
        } else {
            createPlanCustomCase(testPlanVO, testIssueFolderDTOS, caseIds);
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
        testCycleCaseService.batchInsertByTestCase(testCycleMap,caseIds,testPlanVO.getProjectId(),testPlanVO.getPlanId());
        TestPlanDTO testPlan = new TestPlanDTO();
        testPlan.setPlanId(testPlanVO.getPlanId());
        testPlan.setInitStatus(TestPlanInitStatus.SUCCESS);
        testPlan.setObjectVersionNumber(testPlanVO.getObjectVersionNumber());
        baseUpdate(testPlan);
    }

    @Override
    public TestPlanVO queryPlan(Long projectId, Long planId) {
        TestPlanDTO testPlanDTO = new TestPlanDTO();
        testPlanDTO.setPlanId(planId);
        TestPlanDTO testPlan = testPlanMapper.selectOne(testPlanDTO);
        TestPlanVO testPlanVO = modelMapper.map(testPlan, TestPlanVO.class);
        Long managerId = testPlan.getManagerId();
        if (!ObjectUtils.isEmpty(managerId)) {
            Map<Long, UserDO> query = userService.query(new Long[]{managerId});
            UserDO userDO = query.get(managerId);
            if (!ObjectUtils.isEmpty(userDO)) {
                testPlanVO.setManagerUser(userDO);
            }
        }
        return testPlanVO;
    }

    @Override
    public void updateStatusCode(Long projectId, TestPlanDTO testPlanDTO) {
        testPlanDTO.setProjectId(projectId);
        baseUpdate(testPlanDTO);
    }

    @Override
    @Saga(code = SagaTopicCodeConstants.TEST_MANAGER_CLONE_PLAN,
            description = "test-manager 复制测试计划", inputSchema = "{}")
    public TestPlanVO clone(Long projectId, Long planId) {
        TestPlanDTO testPlanDTO = testPlanMapper.selectByPrimaryKey(planId);
        testPlanDTO.setPlanId(null);
        testPlanDTO.setCreatedBy(null);
        testPlanDTO.setLastUpdateDate(null);
        testPlanDTO.setCreationDate(null);
        testPlanDTO.setLastUpdatedBy(null);
        testPlanDTO.setObjectVersionNumber(null);
        testPlanDTO.setName(String.format("%s-副本",testPlanDTO.getName()));
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
        testCycleService.cloneCycleByPlanId(copyPlanId, newPlanId,testPlanDTO.getProjectId());
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
                .stream().filter(e->!collect.contains(e.getStatusId())).collect(Collectors.toList());
        List<FormStatusVO> formStatusVOList = modelMapper.map(testStatusDTOList, new TypeToken<List<FormStatusVO>>() {
        }.getType());
        formStatusVOList.stream().forEach(e->e.setCounts(0L));
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
        return testPlanVO;
    }
    @Override
    public  TestPlanDTO baseCreate(TestPlanDTO testPlanDTO) {
        if (ObjectUtils.isEmpty(testPlanDTO)) {
            throw new CommonException("error.test.plan.is.not.null");
        }
        if(testPlanMapper.insertSelective(testPlanDTO) != 1){
           throw  new CommonException("error.insert.test.plan");
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
        testTreeFolderVO.setHasCase(testCycleDTO.getCaseCount()==0);
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
     * @param testPlanVO
     * @param testIssueFolderDTOS
     * @param caseIds
     */
    private void createPlanCustomCase(TestPlanVO testPlanVO, List<TestIssueFolderDTO> testIssueFolderDTOS,List<Long> caseIds) {
        Map<Long, CaseSelectVO> maps = testPlanVO.getCaseSelected();
        List<Long> folderIds = maps.keySet().stream().collect(Collectors.toList());
        testIssueFolderDTOS.addAll(testIssueFolderService.listFolderByFolderIds(testPlanVO.getProjectId(),folderIds));
        Set<Long> unSelectFolderIds = new HashSet<>();
        List<Long> unSelectCaseIds= new ArrayList<>();
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
        if(!CollectionUtils.isEmpty(unSelectCaseIds) && !CollectionUtils.isEmpty(unSelectFolderIds)){
            caseIds.addAll(testCaseMapper.listUnSelectCaseId(testPlanVO.getProjectId(),unSelectCaseIds,unSelectFolderIds));
        }
        if(!CollectionUtils.isEmpty(allSelectFolderIds)){
            caseIds.addAll(testCaseMapper.listCaseIds(testPlanVO.getProjectId(),allSelectFolderIds,null));
        }
    }

}
