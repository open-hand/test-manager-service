package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.choerodon.agile.api.vo.UserDO;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.constant.SagaTopicCodeConstants;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestPlanStatus;
import io.choerodon.test.manager.infra.mapper.TestPlanMapper;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import jodd.util.ArraysUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long projectId, Long planId) {
        List<TestCycleDTO> testCycleDTOS = testCycleService.listByPlanIds(Arrays.asList(planId));
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
        testPlan.setInitStatus(TestPlanStatus.DOING.getStatus());
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
    public void batchInsert(List<TestPlanDTO> testPlanDTOS) {

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
        List<TestCycleDTO> testCycleDTOS = testCycleService.listByPlanIds(planIds);
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
        testPlanDTOS.forEach(v -> {
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
                testCycles.forEach(testCycleDTO -> buildTree(folderRoot, testCycleDTO.getCycleId(), allCycleMap, map, parentCycleMap, v.getPlanId()));
            }
            if (!CollectionUtils.isEmpty(folderRoot)) {
                planTreeVO.setHasChildren(true);
                planTreeVO.setChildren(folderRoot);
            } else {
                planTreeVO.setHasChildren(false);
            }
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
        List<TestCaseDTO> testCaseDTOS = new ArrayList<>();
        List<TestCaseDTO> allTestCase = testCaseService.listCaseByProjectId(testPlanVO.getProjectId());
        // 是否自选
        if (!testPlanVO.getCustom()) {
            testCaseDTOS.addAll(allTestCase);
            List<Long> folderIds = testCaseDTOS.stream().map(TestCaseDTO::getFolderId).collect(Collectors.toList());
            testIssueFolderDTOS.addAll(testIssueFolderService.listFolderByFolderIds(folderIds));
        } else {
            createPlanCustomCase(testPlanVO, testIssueFolderDTOS, allTestCase, testCaseDTOS);
        }
        TestPlanDTO testPlanDTO = testPlanMapper.selectByPrimaryKey(testPlanVO.getPlanId());
        // 创建测试循环
        List<TestCycleDTO> testCycleDTOS = testCycleService.batchInsertByFoldersAndPlan(testPlanDTO, testIssueFolderDTOS);
        // 创建测试循环用例
        Map<Long, TestCycleDTO> testCycleMap = testCycleDTOS.stream().collect(Collectors.toMap(TestCycleDTO::getFolderId, Function.identity()));
        testCycleCaseService.batchInsertByTestCase(testCycleMap, testCaseDTOS);
        TestPlanDTO testPlan = new TestPlanDTO();
        testPlan.setPlanId(testPlanVO.getPlanId());
        testPlan.setInitStatus(TestPlanStatus.DONE.getStatus());
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
            Map<Long, UserDO> query = userService.query(ArraysUtil.array(managerId));
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
    public TestPlanVO queryPlanInfo(Long projectId, Long planId) {
        // 查询计划的信息
        TestPlanDTO testPlanDTO = testPlanMapper.selectByPrimaryKey(planId);
        if (ObjectUtils.isEmpty(testPlanDTO)) {
            return new TestPlanVO();
        }
        TestPlanVO testPlanVO = modelMapper.map(testPlanDTO, TestPlanVO.class);
        // 查询cycle的信息
        List<TestCycleDTO> testCycleDTOS = testCycleService.listByPlanIds(Arrays.asList(planId));
        if (CollectionUtils.isEmpty(testCycleDTOS)) {
            return testPlanVO;
        }
        // 查询cycle_case 的信息
        List<Long> cycleIds = testCycleDTOS.stream().map(TestCycleDTO::getCycleId).collect(Collectors.toList());
        List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseService.listByCycleIds(cycleIds);
        Map<Long, List<Long>> cycleCaseMap = testCycleCaseDTOS.stream().collect(Collectors.groupingBy(TestCycleCaseDTO::getCycleId, Collectors.mapping(TestCycleCaseDTO::getCaseId, Collectors.toList())));

        List<TestCaseDTO> testCaseDTOS = testCaseService.listCaseByProjectId(projectId);
        Map<Long, List<Long>> caseMap = testCaseDTOS.stream().collect(Collectors.groupingBy(TestCaseDTO::getFolderId, Collectors.mapping(TestCaseDTO::getCaseId, Collectors.toList())));
        // 如果用例数与选中的计划执行数相同,是全部用例类型
        if (testCaseDTOS.size() == testCycleCaseDTOS.size()) {
            testPlanVO.setCustom(false);
            return testPlanVO;
        }
        testPlanVO.setCustom(true);
        // 筛选自选的数据
        Map<Long, CaseSelectVO> caseSelectMap = new HashMap<>();
        testCycleDTOS.forEach(v -> {
            CaseSelectVO caseSelectVO = new CaseSelectVO();
            List<Long> folderAllCaseIds = caseMap.get(v.getFolderId());
            if (CollectionUtils.isEmpty(folderAllCaseIds)) {
                caseSelectMap.put(v.getFolderId(), caseSelectVO);
                return;
            }
            List<Long> selectCase = cycleCaseMap.get(v.getCycleId());
            // 文件夹中选中的用例包含文件夹下所有的用例,没有自选过
            if (selectCase.contains(folderAllCaseIds)) {
                caseSelectVO.setCustom(false);
                caseSelectMap.put(v.getFolderId(), caseSelectVO);
            } else {
                caseSelectVO.setCustom(true);
                // 如果所有用例数的一半还大于选中的用例数,则直接赋值给selected属性，相反就求差集，返回未选中的
                if (folderAllCaseIds.size() / 2 > selectCase.size() && !CollectionUtils.isEmpty(selectCase)) {
                    caseSelectVO.setSelected(selectCase);
                } else {
                    folderAllCaseIds.removeAll(selectCase);
                    if (!CollectionUtils.isEmpty(folderAllCaseIds)) {
                        caseSelectVO.setUnSelected(folderAllCaseIds);
                    }
                }
                caseSelectMap.put(v.getFolderId(), caseSelectVO);
            }
        });
        testPlanVO.setCaseSelected(caseSelectMap);
        return testPlanVO;
    }

    private void baseDelete(Long planId) {
        if (testPlanMapper.deleteByPrimaryKey(planId) != 1) {
            throw new CommonException("error.delete.test.plan");
        }
    }

    private TestPlanDTO baseCreate(TestPlanDTO testPlanDTO) {
        if (ObjectUtils.isEmpty(testPlanDTO)) {
            throw new CommonException("error.test.plan.is.not.null");
        }
        DBValidateUtil.executeAndvalidateUpdateNum(testPlanMapper::insertSelective, testPlanDTO, 1, "error.insert.test.plan");
        return testPlanDTO;
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
            testTreeFolderVO.setId(cycleId);
            if (!ObjectUtils.isEmpty(testCycleDTO.getParentCycleId()) && CollectionUtils.isEmpty(parentMap.get(cycleId))) {
                testTreeFolderVO.setHasChildren(false);
            } else {
                testTreeFolderVO.setHasChildren(true);
            }
            testTreeFolderVO.setIssueFolderVO(testCycleService.cycleToIssueFolderVO(testCycleDTO));
            testTreeFolderVO.setExpanded(false);
            testTreeFolderVO.setChildrenLoading(false);
            testTreeFolderVO.setPlanId(planId);
            testTreeFolderVO.setTopLevel(false);
            map.put(cycleId, testTreeFolderVO);
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
            TestIssueFolderVO parentFolderVO = testCycleService.cycleToIssueFolderVO(allFolderMap.get(testCycleDTO.getParentCycleId()));
            if (!ObjectUtils.isEmpty(parentFolderVO.getParentId()) && CollectionUtils.isEmpty(parentMap.get(parentFolderVO.getFolderId()))) {
                parentTreeFolderVO.setHasChildren(false);
            } else {
                parentTreeFolderVO.setHasChildren(true);
            }
            parentTreeFolderVO.setId(parentFolderVO.getFolderId());
            parentTreeFolderVO.setIssueFolderVO(parentFolderVO);
            parentTreeFolderVO.setExpanded(false);
            parentTreeFolderVO.setChildrenLoading(false);
            parentTreeFolderVO.setChildren(Arrays.asList(testCycleDTO.getCycleId()));
            parentTreeFolderVO.setPlanId(planId);
            map.put(testCycleDTO.getParentCycleId(), parentTreeFolderVO);
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
     * @param allTestCase
     * @param testCaseDTOS
     */
    private void createPlanCustomCase(TestPlanVO testPlanVO, List<TestIssueFolderDTO> testIssueFolderDTOS, List<TestCaseDTO> allTestCase, List<TestCaseDTO> testCaseDTOS) {
        Map<Long, CaseSelectVO> maps = testPlanVO.getCaseSelected();
        List<Long> folderIds = maps.keySet().stream().collect(Collectors.toList());
        testIssueFolderDTOS.addAll(testIssueFolderService.listFolderByFolderIds(folderIds));
        Map<Long, List<TestCaseDTO>> caseMap = allTestCase.stream().collect(Collectors.groupingBy(TestCaseDTO::getFolderId));
        List<Long> caseIds = new ArrayList<>();
        for (Long key : maps.keySet()) {
            CaseSelectVO caseSelectVO = maps.get(key);
            // 判断是否是自选
            if (!caseSelectVO.getCustom()) {
                if (!CollectionUtils.isEmpty(caseMap.get(key))) {
                    testCaseDTOS.addAll(caseMap.get(key));
                }
            } else {
                // 判断是反选还是正向选择
                if (CollectionUtils.isEmpty(caseSelectVO.getSelected())) {
                    // 反选就
                    List<Long> unSelected = caseSelectVO.getUnSelected();
                    // 获取文件夹所有的测试用例
                    List<Long> allList = caseMap.get(key).stream().filter(v -> !ObjectUtils.isEmpty(v)).map(TestCaseDTO::getCaseId).collect(Collectors.toList());
                    allList.removeAll(unSelected);
                    caseIds.addAll(allList);
                } else {
                    caseIds.addAll(caseSelectVO.getSelected());
                }
            }
        }

        if (!CollectionUtils.isEmpty(caseIds)) {
            testCaseDTOS.addAll(testCaseService.listByCaseIds(testPlanVO.getProjectId(), caseIds));
        }

    }

}
