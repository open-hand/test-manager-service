package io.choerodon.test.manager.app.service.impl;

import static java.util.stream.Collectors.*;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.api.vo.agile.ProductVersionDTO;
import io.choerodon.test.manager.api.vo.agile.UserDO;
import io.choerodon.test.manager.app.assembler.TestCycleAssembler;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestCycleType;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.RankUtil;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestCycleServiceImpl implements TestCycleService {

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    private TestCycleMapper cycleMapper;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestCycleAssembler testCycleAssembler;

    /**
     * 新建cycle，folder 并同步folder下的执行
     *
     * @param testCycleVO
     * @return
     */
    @Override
    public TestCycleVO insert(Long projectId, TestCycleVO testCycleVO) {
        testCycleVO.setType("folder");
        TestCycleVO cycleDTO = baseInsert(projectId, testCycleVO);
        testCycleAssembler.updateTime(projectId,modelMapper.map(cycleDTO,TestCycleDTO.class));
        return cycleDTO;
    }

    @Override
    public void delete(Long cycleId, Long projectId) {
        TestCycleDTO testCycleDTO = cycleMapper.selectByPrimaryKey(cycleId);
        List<TestCycleDTO> testCycleDTOS = cycleMapper.listByPlanIds(null, Arrays.asList(testCycleDTO.getPlanId()), projectId);
        List<Long> cycleIds = new ArrayList<>();
        cycleIds.add(cycleId);
        if (!CollectionUtils.isEmpty(testCycleDTOS)) {
            Map<Long, List<TestCycleDTO>> cycleMap = testCycleDTOS.stream().collect(groupingBy(TestCycleDTO::getParentCycleId));
            findCycleChildren(cycleId, cycleIds, cycleMap);
            List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseService.listByCycleIds(cycleIds);
            List<Long> executeIds = testCycleCaseDTOS.stream().map(TestCycleCaseDTO::getExecuteId).collect(toList());
            testCycleCaseService.batchDeleteByExecuteIds(executeIds);
        }
        cycleMapper.batchDelete(cycleIds);
    }

    private void findCycleChildren(Long cycleId, List<Long> cycleIds, Map<Long, List<TestCycleDTO>> cycleMap) {
        List<TestCycleDTO> testCycleDTOS = cycleMap.get(cycleId);
        if(!CollectionUtils.isEmpty(testCycleDTOS)){
            List<Long> child = testCycleDTOS.stream().map(TestCycleDTO::getCycleId).collect(toList());
            cycleIds.addAll(child);
            child.forEach(v -> findCycleChildren(v,cycleIds,cycleMap));
        }
    }

    @Override
    public TestCycleVO update(Long projectId, TestCycleVO testCycleVO) {

        TestCycleDTO map = modelMapper.map(testCycleVO, TestCycleDTO.class);
        updateSelf(map);
        TestCycleDTO testCycleDTO = cycleMapper.selectByPrimaryKey(map.getCycleId());
        testCycleAssembler.updateTime(projectId, testCycleDTO);
        return modelMapper.map(testCycleDTO, TestCycleVO.class);
    }

    public long getDuration(Date from, Date to) {
        if (to != null && from != null) {
            return ChronoUnit.SECONDS.between(from.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    to.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        return 0;
    }

    public void populateUsers(List<TestCycleVO> dtos) {
        Long[] usersId = dtos.stream().map(TestCycleVO::getCreatedBy).toArray(Long[]::new);
        Map<Long, UserDO> users = userService.query(usersId);
        dtos.forEach(v -> {
            if (v.getCreatedBy() != null && v.getCreatedBy().longValue() != 0) {
                UserDO u = users.get(v.getCreatedBy());
                if (null != u) {
                    v.setCreatedUser(u);
                }
            }
        });
    }

    @Override
    public void populateVersion(TestCycleVO cycle, Long projectId) {
        Map<Long, ProductVersionDTO> map = testCaseService.getVersionInfo(projectId);
        cycle.setVersionName(map.get(cycle.getVersionId()).getName());
        cycle.setVersionStatusName(map.get(cycle.getVersionId()).getStatusName());
    }

    private TestCycleVO baseInsert(Long projectId, TestCycleVO testCycleVO) {
        TestCycleDTO testCycleDTO = modelMapper.map(testCycleVO, TestCycleDTO.class);
        testCycleDTO.setProjectId(projectId);
        validateCycle(testCycleDTO);
        checkRank(testCycleVO);
        testCycleDTO.setRank(RankUtil.Operation.INSERT.getRank(null, getLastedRank(testCycleVO)));
        cycleMapper.insert(testCycleDTO);

        return modelMapper.map(testCycleDTO, TestCycleVO.class);
    }

    public void checkRank(TestCycleVO testCycleVO) {
        if (getCount(testCycleVO) != 0 && StringUtils.isEmpty(getLastedRank(testCycleVO))) {
            fixRank(testCycleVO);
        }
    }

    @Override
    public List<TestCycleDTO> batchInsertByFoldersAndPlan(TestPlanDTO testPlanDTO, List<TestIssueFolderDTO> testIssueFolderDTOS) {
        if (CollectionUtils.isEmpty(testIssueFolderDTOS)) {
            return new ArrayList<>();
        }
        Map<Long, List<TestIssueFolderDTO>> collect = testIssueFolderDTOS.stream().collect(groupingBy(TestIssueFolderDTO::getParentId));
        Map<Long, List<TestIssueFolderDTO>> parentMap = new LinkedHashMap<>();
        findChildren(parentMap,0L,collect);
        Map<Long, Long> cycleMap = new HashMap<>();
        List<TestCycleDTO> endCycle = new ArrayList<>();
        parentMap.keySet().forEach(key -> {
            List<TestCycleDTO> testCycleDTOS = new ArrayList<>();
            List<TestIssueFolderDTO> testIssueFolder = parentMap.get(key);
            testIssueFolder.forEach(v -> {
                TestCycleDTO testCycleDTO = new TestCycleDTO();
                testCycleDTO.setFromDate(testPlanDTO.getStartDate());
                if (v.getParentId() != 0) {
                    if (!ObjectUtils.isEmpty(cycleMap.get(v.getParentId()))) {
                        testCycleDTO.setParentCycleId(cycleMap.get(v.getParentId()));
                    }
                } else {
                    testCycleDTO.setParentCycleId(0L);
                }
                testCycleDTO.setToDate(testPlanDTO.getEndDate());
                testCycleDTO.setProjectId(testPlanDTO.getProjectId());
                testCycleDTO.setVersionId(1L);
                testCycleDTO.setPlanId(testPlanDTO.getPlanId());
                testCycleDTO.setCycleName(v.getName());
                testCycleDTO.setFolderId(v.getFolderId());
                testCycleDTO.setType(TestCycleType.FOLDER);
                testCycleDTO.setCreatedBy(testPlanDTO.getCreatedBy());
                testCycleDTO.setLastUpdatedBy(testPlanDTO.getLastUpdatedBy());
                testCycleDTOS.add(testCycleDTO);
            });
            Map<Long, List<TestCycleDTO>> listMap = testCycleDTOS.stream().collect(groupingBy(TestCycleDTO::getPlanId));
            List<TestCycleDTO> cycleDTOList = doRank(listMap);
            cycleMapper.batchInsert(cycleDTOList);
            Map<Long, Long> returnCycleId = cycleDTOList.stream().collect(toMap(TestCycleDTO::getFolderId, TestCycleDTO::getCycleId));
            cycleMap.putAll(returnCycleId);
            endCycle.addAll(cycleDTOList);
        });
        return endCycle;
    }


    private void findChildren(Map<Long, List<TestIssueFolderDTO>> parentMap, Long folderId, Map<Long, List<TestIssueFolderDTO>> collect) {
        List<TestIssueFolderDTO> testIssueFolderDTOS = collect.get(folderId);
        if(!CollectionUtils.isEmpty(testIssueFolderDTOS)){
            List<TestIssueFolderDTO> list = testIssueFolderDTOS.stream().sorted(Comparator.comparing(TestIssueFolderDTO::getRank)).collect(toList());
            parentMap.put(folderId,list);
            testIssueFolderDTOS.forEach(v -> findChildren(parentMap,v.getFolderId(),collect));
        }
    }

    @Override
    public List<TestCycleDTO> listByPlanIds(List<Long> planIds,Long projectId) {
        return cycleMapper.listByPlanIds(null, planIds,projectId);
    }

    @Override
    public void batchDelete(List<Long> needDeleteCycleIds) {
        if (CollectionUtils.isEmpty(needDeleteCycleIds)) {
            return;
        }
        List<TestCycleCaseDTO> needDeleteCycleCase = testCycleCaseService.listByCycleIds(needDeleteCycleIds);
        List<Long> executeIds = needDeleteCycleCase.stream().map(TestCycleCaseDTO::getExecuteId).collect(toList());
        testCycleCaseService.batchDeleteByExecuteIds(executeIds);
        cycleMapper.batchDelete(needDeleteCycleIds);
    }

    @Override
    public TestIssueFolderVO cycleToIssueFolderVO(TestCycleDTO testCycleDTO) {
        TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();
        testIssueFolderVO.setFolderId(testCycleDTO.getCycleId());
        testIssueFolderVO.setParentId(testCycleDTO.getParentCycleId());
        testIssueFolderVO.setName(testCycleDTO.getCycleName());
        testIssueFolderVO.setObjectVersionNumber(testCycleDTO.getObjectVersionNumber());
        testIssueFolderVO.setType(testCycleDTO.getType());
        testIssueFolderVO.setProjectId(testCycleDTO.getProjectId());
        testIssueFolderVO.setRank(testCycleDTO.getRank());
        testIssueFolderVO.setFromDate(testCycleDTO.getFromDate());
        testIssueFolderVO.setToDate(testCycleDTO.getToDate());
        return testIssueFolderVO;
    }

    @Override
    public void baseUpdate(TestCycleDTO testCycleDTO) {
        if (cycleMapper.updateByPrimaryKeySelective(testCycleDTO) != 1) {
            throw new CommonException("error.update.cycle");
        }
    }

    @Override
    public String moveCycle(Long projectId, Long targetCycleId, TestCycleVO testCycleVO) {
        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setCycleId(targetCycleId);
        List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseMapper.select(testCycleCaseDTO);
        if (!CollectionUtils.isEmpty(testCycleCaseDTOS)) {
            throw new CommonException("error.issueFolder.has.case");
        }
        TestCycleDTO testCycleDTO = cycleMapper.selectByPrimaryKey(testCycleVO.getCycleId());
        testCycleDTO.setParentCycleId(targetCycleId);
        if (ObjectUtils.isEmpty(testCycleVO.getLastRank()) && ObjectUtils.isEmpty(testCycleVO.getNextRank())) {
            testCycleDTO.setRank(RankUtil.Operation.INSERT.getRank(testCycleVO.getLastRank(), testCycleVO.getNextRank()));
        } else {
            testCycleDTO.setRank(RankUtil.Operation.UPDATE.getRank(testCycleVO.getLastRank(), testCycleVO.getNextRank()));
        }
        baseUpdate(testCycleDTO);
        return testCycleDTO.getRank();
    }

    @Override
    public void cloneCycleByPlanId(Long copyPlanId, Long newPlanId,Long projectId) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        List<TestCycleDTO> testCycleDTOS = listByPlanIds(Arrays.asList(copyPlanId),projectId);
        if (CollectionUtils.isEmpty(testCycleDTOS)) {
            return;
        }
        testCycleDTOS = testCycleDTOS.stream().map(v -> {
            if (ObjectUtils.isEmpty(v.getParentCycleId())) {
                v.setParentCycleId(0L);
            }
            return v;
        }).collect(toList());

        Map<Long, List<TestCycleDTO>> olderCycleMap = new TreeMap<>();
        testCycleDTOS.forEach(v -> {
            List<TestCycleDTO> testCycleDTOList = olderCycleMap.get(v.getParentCycleId());
            if (ObjectUtils.isEmpty(testCycleDTOList)) {
                olderCycleMap.put(v.getParentCycleId(), Arrays.asList(v));
            } else {
                List<TestCycleDTO> newTestCycleList = new ArrayList<>();
                newTestCycleList.addAll(testCycleDTOList);
                newTestCycleList.add(v);
                olderCycleMap.put(v.getParentCycleId(), newTestCycleList);
            }
        });

        Map<Long, Long> newMapping = new HashMap<>();
        List<Long> cycIds = new ArrayList<>();
        olderCycleMap.keySet().forEach(key -> {
            List<TestCycleDTO> testCycle = new ArrayList<>();
            List<TestCycleDTO> testCycleDTOS1 = olderCycleMap.get(key);
            testCycleDTOS1.forEach(testCycleDTO -> {
                Long olderCycle = testCycleDTO.getCycleId();
                cycIds.add(olderCycle);
                if (testCycleDTO.getParentCycleId() != 0) {
                    Long cycleId = newMapping.get(testCycleDTO.getParentCycleId());
                    testCycleDTO.setParentCycleId(cycleId);
                }
                testCycleDTO.setCycleId(null);
                testCycleDTO.setCreatedBy(userDetails.getUserId());
                testCycleDTO.setLastUpdatedBy(userDetails.getUserId());
                testCycleDTO.setOldCycleId(olderCycle);
                testCycleDTO.setPlanId(newPlanId);
                testCycle.add(testCycleDTO);
            });
            cycleMapper.batchInsert(testCycle);
            testCycle.forEach(v -> newMapping.put(v.getOldCycleId(), v.getCycleId()));
        });
        // 复制执行
        testCycleCaseService.cloneCycleCase(newMapping, cycIds);
    }

    @Override
    public TestTreeIssueFolderVO queryTreeByPlanId(Long planId,Long projectId) {
        List<TestCycleDTO> testCycleDTOS = cycleMapper.listByPlanIds(null, Arrays.asList(planId),projectId);
        List<TestCycleDTO> collect = testCycleDTOS.stream().map(v -> {
            if (ObjectUtils.isEmpty(v.getParentCycleId())) {
                v.setParentCycleId(0L);
            }
            return v;
        }).collect(toList());
        Map<Long, List<Long>> parentMap = collect.stream().collect(groupingBy(TestCycleDTO::getParentCycleId, mapping(TestCycleDTO::getCycleId, toList())));
        List<Long> root = new ArrayList<>();
        List<TestTreeFolderVO> treeFolder = new ArrayList<>();
        collect.stream().forEach(cycle -> bulidTree(cycle, planId, root, parentMap, treeFolder));
        TestTreeIssueFolderVO testTreeIssueFolderVO = new TestTreeIssueFolderVO();
        testTreeIssueFolderVO.setTreeFolder(treeFolder);
        testTreeIssueFolderVO.setRootIds(root);
        return testTreeIssueFolderVO;
    }

    private void bulidTree(TestCycleDTO cycle, Long planId, List<Long> root, Map<Long, List<Long>> parentMap, List<TestTreeFolderVO> treeFolder) {
        TestTreeFolderVO testTreeFolderVO = new TestTreeFolderVO();
        testTreeFolderVO.setPlanId(planId);
        testTreeFolderVO.setId(cycle.getCycleId());
        testTreeFolderVO.setExpanded(false);
        TestIssueFolderVO issueFolderVO = new TestIssueFolderVO(cycle.getCycleId(), cycle.getCycleName(), null, cycle.getProjectId(), null, cycle.getObjectVersionNumber());
        issueFolderVO.setParentId(cycle.getParentCycleId());
        issueFolderVO.setRank(cycle.getRank());
        testTreeFolderVO.setIssueFolderVO(issueFolderVO);
        testTreeFolderVO.setChildren(parentMap.get(cycle.getCycleId()));
        if (cycle.getParentCycleId() == 0) {
            root.add(cycle.getCycleId());
            testTreeFolderVO.setTopLevel(true);
        } else {
            testTreeFolderVO.setTopLevel(false);
        }
        treeFolder.add(testTreeFolderVO);
    }

    private Long getCount(TestCycleVO testCycleVO) {
        if (testCycleVO.getType().equals(TestCycleType.CYCLE)) {
            return cycleMapper.getCycleCountInVersion(testCycleVO.getVersionId());
        } else {
            return cycleMapper.getFolderCountInCycle(testCycleVO.getParentCycleId());
        }
    }

    private void fixRank(TestCycleVO testCycleVO) {
        List<TestCycleDTO> cycleES;
        if (testCycleVO.getType().equals(TestCycleType.CYCLE)) {
            cycleES = cycleMapper.queryCycleInVersion(modelMapper.map(testCycleVO, TestCycleDTO.class));
        } else {
            TestCycleDTO testCycleDTO = new TestCycleDTO();
            testCycleDTO.setCycleId(testCycleVO.getParentCycleId());
            cycleES = cycleMapper.queryChildCycle(testCycleDTO);
        }
        for (int a = 0; a < cycleES.size(); a++) {
            TestCycleDTO testCycleETemp = cycleES.get(a);
            List<TestCycleDTO> list = cycleMapper.select(testCycleETemp);
            TestCycleDTO testCycleETemp1 = list.get(0);
            if (a == 0) {
                testCycleETemp1.setRank(RankUtil.Operation.INSERT.getRank(null, null));
            } else {
                testCycleETemp1.setRank(RankUtil.Operation.INSERT.getRank(cycleES.get(a - 1).getRank(), null));
            }
            if (cycleMapper.updateByPrimaryKeySelective(testCycleETemp1) != 1) {
                throw new CommonException("error.testCycle.update");
            }
            testCycleETemp1 = cycleMapper.selectByPrimaryKey(testCycleETemp1.getCycleId());
            cycleES.set(a, testCycleETemp1);
        }
    }

    private String getLastedRank(TestCycleVO testCycleVO) {
        return cycleMapper.getPlanLastedRank(testCycleVO.getPlanId());
    }

    private TestCycleDTO updateSelf(TestCycleDTO testCycleE) {
        Assert.notNull(testCycleE, "error.cycle.update.not.be.null");
        if (cycleMapper.updateByPrimaryKeySelective(testCycleE) != 1) {
            throw new CommonException("error.testCycle.update");
        }
        return cycleMapper.selectByPrimaryKey(testCycleE.getCycleId());
    }

    private void validateCycle(TestCycleDTO testCycleE) {
        Assert.notNull(testCycleE.getProjectId(), "error.cycle.projectId.not.be.null");
        Assert.notNull(testCycleE.getCycleName(), "error.cycle.name.not.be.null");
        if (!cycleMapper.validateCycle(testCycleE).equals(0L)) {
            throw new CommonException("error.cycle.in.version.has.existed");
        }
    }

    private List<TestCycleDTO> doRank(Map<Long, List<TestCycleDTO>> listMap) {
        List<TestCycleDTO> testCycleDTOS = new ArrayList<>();
        for (Map.Entry<Long, List<TestCycleDTO>> map : listMap.entrySet()
        ) {
            String prevRank = null;
            if (!CollectionUtils.isEmpty(map.getValue())) {
                for (TestCycleDTO testCycleDTO : map.getValue()) {
                    testCycleDTO.setRank(RankUtil.Operation.INSERT.getRank(prevRank, null));
                    prevRank = testCycleDTO.getRank();
                    testCycleDTOS.add(testCycleDTO);
                }
            }
        }
        return testCycleDTOS;
    }
}