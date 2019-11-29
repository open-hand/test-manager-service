package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.constant.SagaTopicCodeConstants;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;
import io.choerodon.test.manager.infra.dto.TestPlanDTO;
import io.choerodon.test.manager.infra.enums.TestPlanStatus;
import io.choerodon.test.manager.infra.mapper.TestPlanMapper;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import org.checkerframework.checker.units.qual.A;
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

    @Override
    public TestPlanVO update(Long projectId, TestPlanVO testPlanVO) {
        TestPlanDTO testPlanDTO = modelMapper.map(testPlanVO, TestPlanDTO.class);
        if (testPlanMapper.updateByPrimaryKeySelective(testPlanDTO) != 1) {
            throw new CommonException("error.update.plan");
        }
        return modelMapper.map(testPlanMapper.selectByPrimaryKey(testPlanDTO.getPlanId()), TestPlanVO.class);
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long projectId, Long planId) {
        //todo
    }

    @Override
    @Saga(code = SagaTopicCodeConstants.TEST_MANAGER_CREATE_PLAN,
            description = "test-manager创建测试计划", inputSchema = "{}")
    public TestPlanDTO create(Long projectId, TestPlanVO testPlanVO) {
        // 创建计划
        TestPlanDTO testPlan = modelMapper.map(testPlanVO, TestPlanDTO.class);
        testPlan.setProjectId(projectId);
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
        List<TestPlanTreeVO> testPlanTreeVOS = new ArrayList<>();
        testPlanTreeVOS = modelMapper.map(testPlanDTOS, new TypeToken<List<TestPlanTreeVO>>() {
        }.getType());

        // 获取planIds,查询出所有底层文件夹Id
        List<Long> planIds = testPlanTreeVOS.stream().map(TestPlanTreeVO::getPlanId).collect(Collectors.toList());
        List<TestCycleDTO> testCycleDTOS = testCycleService.listByPlanIds(planIds);
        Map<Long, List<TestCycleDTO>> testCycleMap = testCycleDTOS.stream().collect(Collectors.groupingBy(TestCycleDTO::getPlanId));
        // 获取项目下所有的文件夹
        List<TestIssueFolderVO> testIssueFolderVOS = testIssueFolderService.queryListByProjectId(projectId);
        Map<Long, TestIssueFolderVO> allFolderMap = testIssueFolderVOS.stream().collect(Collectors.toMap(TestIssueFolderVO::getFolderId, Function.identity()));
        Map<Long, List<TestIssueFolderVO>> parentMap = testIssueFolderVOS.stream().collect(Collectors.groupingBy(TestIssueFolderVO::getParentId));
        // 实例化返回的树
        TestTreeIssueFolderVO testTreeIssueFolderVO = new TestTreeIssueFolderVO();
        // 用于接收TestTreeFolderVO,便于判断和构建树
        Map<Long, TestTreeFolderVO> map = new HashMap<>();

        // 接收位于树顶层的测试计划
        List<TestTreeFolderVO> planTreeList = new ArrayList<>();
        List<Long> root = new ArrayList<>();
        testPlanTreeVOS.forEach(v -> {
            // 将计划Id设置为root
            root.add(v.getPlanId());
            List<Long> folderRoot = new ArrayList<>();
            List<TestCycleDTO> testCycles = testCycleMap.get(v.getPlanId());
            if (!CollectionUtils.isEmpty(testCycles)) {
                // 构建顶层
                TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();
                testIssueFolderVO.setProjectId(v.getProjectId());
                testIssueFolderVO.setName(v.getName());
                testIssueFolderVO.setFolderId(v.getPlanId());
                TestTreeFolderVO plantreeVO = new TestTreeFolderVO();
                plantreeVO.setId(v.getPlanId());
                plantreeVO.setIssueFolderVO(testIssueFolderVO);
                plantreeVO.setChildrenLoading(false);
                plantreeVO.setExpanded(false);
                plantreeVO.setTopLevel(true);
                // 构建文件夹树
                testCycles.forEach(testCycleDTO -> buildTree(folderRoot, testCycleDTO.getFolderId(), allFolderMap, map, parentMap));
                if (!CollectionUtils.isEmpty(folderRoot)) {
                    plantreeVO.setHasChildren(true);
                    plantreeVO.setChildren(folderRoot);
                } else {
                    plantreeVO.setHasChildren(false);
                }
                planTreeList.add(plantreeVO);
            }
        });
        // 合并、构建树
        List<TestTreeFolderVO> testTreeFolderVOS = map.values().stream().collect(Collectors.toList());
        planTreeList.addAll(testTreeFolderVOS);
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
            List<Long> selectCase = cycleCaseMap.get(v.getCycleId());
            if (CollectionUtils.isEmpty(folderAllCaseIds) || CollectionUtils.isEmpty(folderAllCaseIds)) {
                caseSelectMap.put(v.getFolderId(), caseSelectVO);
                return;
            }
            // 文件夹中选中的用例包含文件夹下所有的用例,没有自选过
            if (selectCase.contains(folderAllCaseIds)) {
                caseSelectVO.setCustom(false);
                caseSelectMap.put(v.getFolderId(), caseSelectVO);
            } else {
                caseSelectVO.setCustom(false);
                // 如果所有用例数的一半还大于选中的用例数,则直接赋值给selected属性，相反就求差集，返回未选中的
                if (folderAllCaseIds.size() / 2 > selectCase.size()) {
                    caseSelectVO.setSelected(selectCase);
                } else {
                    folderAllCaseIds.removeAll(selectCase);
                    caseSelectVO.setSelected(folderAllCaseIds);
                }
                caseSelectMap.put(v.getFolderId(), caseSelectVO);
            }
        });
        testPlanVO.setCaseSelected(caseSelectMap);
        return testPlanVO;
    }


    private TestPlanDTO baseCreate(TestPlanDTO testPlanDTO) {
        if (ObjectUtils.isEmpty(testPlanDTO)) {
            throw new CommonException("error.test.plan.is.not.null");
        }
        DBValidateUtil.executeAndvalidateUpdateNum(testPlanMapper::insertSelective, testPlanDTO, 1, "error.insert.test.plan");
        return testPlanDTO;
    }

    private void buildTree(List<Long> root, Long folderId, Map<Long, TestIssueFolderVO> allFolderMap, Map<Long, TestTreeFolderVO> map, Map<Long, List<TestIssueFolderVO>> parentMap) {
        TestIssueFolderVO testIssueFolderVO = allFolderMap.get(folderId);
        TestTreeFolderVO testTreeFolderVO = null;
        // 读取map中是否存在当前文件夹信息
        if (!ObjectUtils.isEmpty(map.get(folderId))) {
            testTreeFolderVO = map.get(folderId);
        }
        // 不存在就新建
        if (ObjectUtils.isEmpty(testTreeFolderVO)) {
            testTreeFolderVO = new TestTreeFolderVO();
            testTreeFolderVO.setId(testIssueFolderVO.getFolderId());
            if (CollectionUtils.isEmpty(parentMap.get(testIssueFolderVO.getFolderId()))) {
                testTreeFolderVO.setHasChildren(false);
            } else {
                testTreeFolderVO.setHasChildren(true);
            }
            testTreeFolderVO.setIssueFolderVO(testIssueFolderVO);
            testTreeFolderVO.setExpanded(false);
            testTreeFolderVO.setChildrenLoading(false);
            map.put(folderId, testTreeFolderVO);
        }

        // 判断是不是顶层文件夹,是顶层文件夹就结束递归
        if (testIssueFolderVO.getParentId() == 0) {
            if (!root.contains(testIssueFolderVO.getFolderId())) {
                root.add(testIssueFolderVO.getFolderId());
            }
            return;
        } else {
            // 查看当前文件夹的父文件夹是否存在
            TestTreeFolderVO parentTreeFolderVO = null;
            if (!ObjectUtils.isEmpty(map.get(testIssueFolderVO.getParentId()))) {
                parentTreeFolderVO = map.get(testIssueFolderVO.getParentId());
            }
            if (ObjectUtils.isEmpty(parentTreeFolderVO)) {
                // 不存在就创建父级文件夹,并加入map中
                parentTreeFolderVO = new TestTreeFolderVO();
                if (ObjectUtils.isEmpty(allFolderMap.get(testIssueFolderVO.getParentId()))) {
                    return;
                }
                TestIssueFolderVO parentFolderVO = allFolderMap.get(testIssueFolderVO.getParentId());
                if (CollectionUtils.isEmpty(parentMap.get(parentFolderVO.getFolderId()))) {
                    testTreeFolderVO.setHasChildren(false);
                } else {
                    testTreeFolderVO.setHasChildren(true);
                }
                parentTreeFolderVO.setId(parentFolderVO.getFolderId());
                parentTreeFolderVO.setIssueFolderVO(parentFolderVO);
                parentTreeFolderVO.setExpanded(false);
                parentTreeFolderVO.setChildrenLoading(false);
                parentTreeFolderVO.setChildren(Arrays.asList(testIssueFolderVO.getFolderId()));
                map.put(testIssueFolderVO.getParentId(), parentTreeFolderVO);
            } else {
                //存在就更新父文件夹的Children值
                List<Long> children = new ArrayList<>();
                if (!ObjectUtils.isEmpty(parentTreeFolderVO.getChildren())) {
                    children.addAll(parentTreeFolderVO.getChildren());
                }
                if (!children.contains(testIssueFolderVO.getFolderId())) {
                    children.add(testIssueFolderVO.getFolderId());
                }
                parentTreeFolderVO.setChildren(children);
                map.put(testIssueFolderVO.getParentId(), parentTreeFolderVO);
            }
            //使用父文件夹递归
            buildTree(root, testIssueFolderVO.getParentId(), allFolderMap, map, parentMap);
        }
    }

}
