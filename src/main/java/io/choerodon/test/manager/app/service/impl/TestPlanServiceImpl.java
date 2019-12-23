package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        return modelMapper.map(testPlanMapper.selectByPrimaryKey(testPlanDTO.getPlanId()), TestPlanVO.class);
    }

    @Override
    @Async
    public void delete(Long projectId, Long planId) {
        List<TestCycleDTO> testCycleDTOS = testCycleService.listByPlanIds(Arrays.asList(planId),projectId);
        List<Long> collect = testCycleDTOS.stream().map(TestCycleDTO::getCycleId).collect(Collectors.toList());
        testCycleService.batchDelete(collect);
        baseDelete(planId);
    }

    @Override
    @Saga(code = SagaTopicCodeConstants.TEST_MANAGER_CREATE_PLAN,
            description = "test-manager创建测试计划", inputSchema = "{}")
    public TestPlanDTO create(Long projectId, TestPlanVO testPlanVO) {
        // 创建计划
        testPlanVO.setProjectId(projectId);
        TestPlanDTO testPlan = modelMapper.map(testPlanVO, TestPlanDTO.class);
        testPlan.setStatusCode(TestPlanStatus.TODO.getStatus());
        testPlan.setInitStatus("creating");
        TestPlanDTO testPlanDTO = baseCreate(testPlan);
        testPlanVO.setPlanId(testPlan.getPlanId());
        testPlanVO.setObjectVersionNumber(testPlan.getObjectVersionNumber());
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
        testTreeFolderVO.setHasCase(testCycleDTO.getCaseCount()==0?false:true);
        testTreeFolderVO.setIssueFolderVO(testCycleService.cycleToIssueFolderVO(testCycleDTO));
        testTreeFolderVO.setExpanded(false);
        testTreeFolderVO.setChildrenLoading(false);
        testTreeFolderVO.setPlanId(testCycleDTO.getPlanId());
        testTreeFolderVO.setTopLevel(false);
        return testTreeFolderVO;
    }

    @Override
    public TestTreeIssueFolderVO ListPlanAndFolderTree(Long projectId, String statusCode) {
        TestPlanDTO testPlanDTO = new TestPlanDTO();
        testPlanDTO.setProjectId(projectId);
        testPlanDTO.setStatusCode(statusCode);
        List<TestPlanDTO> testPlanDTOS = testPlanMapper.select(testPlanDTO);
        if (CollectionUtils.isEmpty(testPlanDTOS)) {
            return new TestTreeIssueFolderVO();
        }
        // 获取planIds,查询出所有底层文件夹Id
        List<Long> planIds = testPlanDTOS.stream().map(TestPlanDTO::getPlanId).collect(Collectors.toList());
        List<TestCycleDTO> testCycleDTOS = testCycleService.listByPlanIds(planIds,projectId);
        Map<Long, List<TestCycleDTO>> testCycleMap = testCycleDTOS.stream().collect(Collectors.groupingBy(TestCycleDTO::getPlanId));
        // 获取项目下所有的文件夹
        Map<Long, TestCycleDTO> allCycleMap = testCycleDTOS.stream().collect(Collectors.toMap(TestCycleDTO::getCycleId, Function.identity()));
        Map<Long, List<TestCycleDTO>> parentCycleMap = testCycleDTOS.stream().filter(v -> !ObjectUtils.isEmpty(v.getParentCycleId())).collect(Collectors.groupingBy(TestCycleDTO::getParentCycleId));
        // 实例化返回的树
        TestTreeIssueFolderVO testTreeIssueFolderVO = new TestTreeIssueFolderVO();

        // 接收位于树顶层的测试计划
        List<TestTreeFolderVO> planTreeList = new ArrayList<>();
        List<Long> root = new ArrayList<>();
        List<TestTreeFolderVO> testTreeFolderVOS = new ArrayList<>();
        testPlanDTOS.stream().sorted(Comparator.comparing(TestPlanDTO::getPlanId).reversed()).forEach(v -> {
            // 用于接收TestTreeFolderVO,便于判断和构建树
            Map<Long, TestTreeFolderVO> map = new HashMap<>();
            // 将计划Id设置为root
            root.add(v.getPlanId());
            List<Long> folderRoot = new ArrayList<>();
            List<TestCycleDTO> testCycles = testCycleMap.get(v.getPlanId());
            // 构建顶层
            TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();
            testIssueFolderVO.setProjectId(v.getProjectId());
            testIssueFolderVO.setName(v.getName());
            testIssueFolderVO.setFolderId(v.getPlanId());
            testIssueFolderVO.setInitStatus(v.getInitStatus());
            testIssueFolderVO.setObjectVersionNumber(v.getObjectVersionNumber());
            TestTreeFolderVO planTreeVO = new TestTreeFolderVO();
            planTreeVO.setId(v.getPlanId());
            planTreeVO.setIssueFolderVO(testIssueFolderVO);
            planTreeVO.setChildrenLoading(false);
            planTreeVO.setExpanded(false);
            planTreeVO.setTopLevel(true);
            if (!CollectionUtils.isEmpty(testCycles)) {
                // 构建文件夹树
                testCycles.forEach(testCycleDTO ->
                        buildTree(folderRoot, testCycleDTO.getCycleId(), allCycleMap, map, parentCycleMap, v.getPlanId()));
            }
            if (!CollectionUtils.isEmpty(folderRoot)) {
                planTreeVO.setHasChildren(true);
                planTreeVO.setChildren(folderRoot);
            } else {
                planTreeVO.setHasChildren(false);
            }
            planTreeVO.setHasCase(false);
            List<TestTreeFolderVO> collect = map.values().stream().collect(Collectors.toList());
            planTreeList.add(planTreeVO);
            if (!CollectionUtils.isEmpty(collect)) {
                planTreeList.addAll(collect);
            }

        });
        // 合并、构建树
        testTreeIssueFolderVO.setRootIds(root);
        testTreeIssueFolderVO.setTreeFolder(planTreeList);
        return testTreeIssueFolderVO;
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
        if (!testPlanVO.getCustom()) {
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
        testCycleCaseService.batchInsertByTestCase(testCycleMap,caseIds,testPlanVO.getProjectId());
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
    public void sagaClonePlan(Map<String, Integer> map) {
        Long copyPlanId = map.get("older").longValue();
        Long newPlanId = map.get("new").longValue();
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
    public void SetPlanInitStatusFail(TestPlanVO testPlanVO) {
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
        DBValidateUtil.executeAndvalidateUpdateNum(testPlanMapper::insertSelective, testPlanDTO, 1, "error.insert.test.plan");
        return testPlanDTO;
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
            return;
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
        testTreeFolderVO.setHasCase(testCycleDTO.getCaseCount()==0?false:true);
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
        for (Long key : maps.keySet()) {
            CaseSelectVO caseSelectVO = maps.get(key);
            // 判断是否是自选
            if (!caseSelectVO.getCustom()) {
                allSelectFolderIds.add(key);
            } else {
                // 判断是反选还是正向选择
                if (CollectionUtils.isEmpty(caseSelectVO.getSelected())) {
                    unSelectFolderIds.add(key);
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
