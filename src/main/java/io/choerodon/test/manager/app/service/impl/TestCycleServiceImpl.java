package io.choerodon.test.manager.app.service.impl;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.agile.api.vo.ProductVersionDTO;
import io.choerodon.agile.api.vo.ProductVersionPageDTO;
import io.choerodon.agile.api.vo.UserDO;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestCycleType;
import io.choerodon.test.manager.infra.enums.TestFileLoadHistoryEnums;
import io.choerodon.test.manager.infra.enums.TestStatusType;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.TestDateUtil;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestCycleServiceImpl implements TestCycleService {

    private static final String NODE_CHILDREN = "children";
    private static final String CYCLE_ID = "cycleId";
    private static final String NOTIFYCYCLECODE = "test-cycle-batch-clone";
    private static final String CYCLE_DATE_NULL_ERROR = "error.clone.cycle.date.not.be.null";

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCycleServiceImpl.class);

    @Autowired
    private TestFileLoadHistoryService testFileLoadHistoryService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private TestStatusService testStatusService;

    @Autowired
    private TestCycleMapper cycleMapper;

    @Autowired
    private TestStatusMapper testStatusMapper;

    @Autowired
    private TestIssueFolderRelMapper testIssueFolderRelMapper;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    private TestCaseStepMapper testCaseStepMapper;

    @Autowired
    private TestCycleCaseStepMapper testCycleCaseStepMapper;

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    private TestFileLoadHistoryMapper testFileLoadHistoryMapper;

    @Autowired
    private ModelMapper modelMapper;


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
        return cycleDTO;
    }

    @Override
    public TestCycleVO insertWithoutSyncFolder(Long projectId, TestCycleVO testCycleVO) {
        TestCycleVO cycleE = baseInsert(projectId, testCycleVO);
        cycleE.setProjectId(projectId);
        if (StringUtils.equals(cycleE.getType(), TestCycleType.FOLDER)) {
            syncCycleDate(projectId, cycleE);
        }
        return modelMapper.map(cycleE, TestCycleVO.class);
    }

    /**
     * 同步文件夹下的所有执行
     *
     * @param cycleId
     * @param folderId
     */
    @Override
    public boolean synchroFolder(Long cycleId, Long folderId, Long projectId) {
        //获取folder下所有issue
        TestIssueFolderRelDTO folder = new TestIssueFolderRelDTO();
        folder.setFolderId(folderId);
        List<TestIssueFolderRelDTO> list = Optional.ofNullable(testIssueFolderRelMapper.select(folder)).orElseGet(ArrayList::new);
        Set<Long> folderIssues = list.stream().map(TestIssueFolderRelDTO::getIssueId).collect(Collectors.toSet());
        //获取cycle下所有issue执行
        TestCycleCaseDTO cycleCaseE = new TestCycleCaseDTO();
        cycleCaseE.setCycleId(cycleId);
        List<TestCycleCaseDTO> caseList = Optional.ofNullable(testCycleCaseMapper.select(cycleCaseE)).orElseGet(ArrayList::new);
        Map<Long, Long> caseIssues = caseList.stream().collect(Collectors.toMap(TestCycleCaseDTO::getCaseId, TestCycleCaseDTO::getExecuteId));
        //对比执行和folder中的issue添加未添加的执行
        TestCycleCaseVO dto = new TestCycleCaseVO();
        dto.setCycleId(cycleId);
        folderIssues.forEach(v -> {
            if (!caseIssues.containsKey(v)) {
                dto.setIssueId(v);
                testCycleCaseService.create(dto, projectId);
            } else {
                //对比issue是否更新step
                syncCycleCaseStep(caseIssues.get(v), v);
            }
        });
        return true;
    }

    /**
     * 同步执行步骤
     *
     * @param executeId
     * @param issueId
     */
    private void syncCycleCaseStep(Long executeId, Long issueId) {
        //获取issue下所有步骤
        TestCaseStepDTO caseStepE = new TestCaseStepDTO();
        caseStepE.setIssueId(issueId);
        List<TestCaseStepDTO> caseSteps = testCaseStepMapper.query(caseStepE);
        if (ObjectUtils.isEmpty(caseSteps)) {
            return;
        }
        //获取执行下的所有步骤
        TestCycleCaseStepDTO stepE = new TestCycleCaseStepDTO();

        stepE.setExecuteId(executeId);
        List<TestCycleCaseStepDTO> cycleSteps = Optional.ofNullable(testCycleCaseStepMapper.select(stepE)).orElseGet(ArrayList::new);
        //当issue下步骤有修改时启动新的步骤

        if (caseSteps.size() != cycleSteps.size()) {
            Long status = testStatusMapper.getDefaultStatus(TestStatusType.STATUS_TYPE_CASE_STEP);
            Set<Long> newStepSet = compareStep(caseSteps, cycleSteps);
            newStepSet.forEach(v -> {
                TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
                testCycleCaseStepDTO.setExecuteId(executeId);
                testCycleCaseStepDTO.setStepId(v);
                testCycleCaseStepDTO.setStepStatus(status);
                Assert.notNull(testCycleCaseStepDTO.getExecuteId(), "error.cant.run.step.because.executeId.is.null");
                Assert.notNull(testCycleCaseStepDTO.getStepId(), "error.cant.run.step.because.stepId.is.null");
                Assert.notNull(testCycleCaseStepDTO.getStepStatus(), "error.cant.run.step.because.stepStatus.is.null");
                testCycleCaseStepMapper.insert(testCycleCaseStepDTO);
            });
        }
    }

    /**
     * 对比issue下步骤和case步骤找出不同的需要启动的步骤
     *
     * @param caseSteps
     * @param cycleSteps
     * @return
     */
    private Set<Long> compareStep(List<TestCaseStepDTO> caseSteps, List<TestCycleCaseStepDTO> cycleSteps) {
        Set<Long> newStep = new HashSet<>();
        Set caseStepSet = caseSteps.stream().map(TestCaseStepDTO::getStepId).collect(Collectors.toSet());
        Set cycleStepSet = cycleSteps.stream().map(TestCycleCaseStepDTO::getStepId).collect(Collectors.toSet());
        Iterator<Long> iterator = caseStepSet.iterator();
        while (iterator.hasNext()) {
            Long stepId = iterator.next();
            if (!cycleStepSet.contains(stepId)) {
                newStep.add(stepId);
            }
        }
        return newStep;
    }


    @Override
    public boolean synchroFolderInCycle(Long cycleId, Long projectId) {
        //查询cycle下所有关联folder
        TestCycleDTO cycleE = new TestCycleDTO();
        cycleE.setParentCycleId(cycleId);
        List<TestCycleDTO> list = cycleMapper.select(cycleE);
        if (!ObjectUtils.isEmpty(list)) {
            list.forEach(v -> synchroFolder(v.getCycleId(), v.getFolderId(), projectId));
        }
        return true;
    }

    @Override
    public boolean synchroFolderInVersion(Long versionId, Long projectId) {
        TestCycleDTO cycleE = new TestCycleDTO();
        cycleE.setVersionId(versionId);
        List<TestCycleDTO> list = cycleMapper.select(cycleE);
        if (!ObjectUtils.isEmpty(list)) {
            list.forEach(v -> synchroFolderInCycle(v.getCycleId(), projectId));
        }
        return true;
    }

    @Override
    public void delete(Long cycleId, Long projectId) {
        List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseService.listByCycleIds(Arrays.asList(cycleId));
        List<Long> executeIds = testCycleCaseDTOS.stream().map(TestCycleCaseDTO::getExecuteId).collect(Collectors.toList());
        testCycleCaseService.batchDeleteByExecuteIds(executeIds);
        cycleMapper.deleteByPrimaryKey(cycleId);
    }

    @Override
    public TestCycleVO update(Long projectId, TestCycleVO testCycleVO) {

        TestCycleDTO map = modelMapper.map(testCycleVO, TestCycleDTO.class);
        updateSelf(map);
        return modelMapper.map(cycleMapper.selectByPrimaryKey(map.getCycleId()), TestCycleVO.class);
    }

    /**
     * 修改cycle时间后同步子folder的时间跨度
     *
     * @param projectId
     * @param cycleVO
     */
    private void syncFolderDate(Long projectId, TestCycleVO cycleVO) {
        TestCycleDTO select = new TestCycleDTO();
        select.setParentCycleId(cycleVO.getCycleId());
        select.setType(TestCycleType.FOLDER);
        List<TestCycleDTO> folders = cycleMapper.select(select);
        folders.stream().filter(u -> ifSyncNeed(u, cycleVO.getFromDate(), cycleVO.getToDate())).forEach(v -> baseUpdate(projectId, v));
    }

    private void syncCycleDate(Long projectId, TestCycleVO cycleE) {
        Long parentCycleId = cycleE.getParentCycleId();
        TestCycleDTO parentCycle = new TestCycleDTO();
        parentCycle.setCycleId(parentCycleId);
        parentCycle = cycleMapper.selectOne(parentCycle);
        if (ifSyncNeed(parentCycle, cycleE.getFromDate(), cycleE.getToDate())) {
            baseUpdate(projectId, parentCycle);
        }
    }

    /**
     * 判断folder是否在限定时间段外 或者cycle是否在限定时间内如果是则为true
     *
     * @param type
     * @param from
     * @param to
     * @return
     */
    private boolean ifSyncNeed(TestCycleDTO type, Date from, Date to) {
        boolean flag = false;

        if (StringUtils.equals(type.getType(), TestCycleType.FOLDER)) {
            long folderPeriod = getDuration(type.getFromDate(), type.getToDate());
            long cyclePeriod = getDuration(from, to);

            if (type.getFromDate() == null || type.getFromDate().compareTo(from) < 0) {
                type.setFromDate(from);
                flag = true;
            }
            if (type.getToDate() == null || type.getToDate().compareTo(to) > 0) {
                type.setToDate(to);
                flag = true;
            }
            adaption(type, folderPeriod, cyclePeriod, from, to);

        } else {
            if (type.getFromDate() == null || type.getFromDate().compareTo(from) > 0) {
                type.setFromDate(from);
                flag = true;
            }
            if (type.getToDate() == null || type.getToDate().compareTo(to) < 0) {
                type.setToDate(to);
                flag = true;
            }
        }
        return flag;
    }

    public long getDuration(Date from, Date to) {
        if (to != null && from != null) {
            return ChronoUnit.SECONDS.between(from.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    to.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        return 0;
    }

    private void adaption(TestCycleDTO folder, long folderDuration, long cycleDuration, Date from, Date to) {
        if (folder.getToDate().compareTo(folder.getFromDate()) < 0) {
            if (folderDuration < cycleDuration) {
                if (folder.getToDate().compareTo(from) < 0) {
                    folder.setFromDate(from);
                    folder.setToDate(Date.from(from.toInstant().plus(folderDuration, ChronoUnit.SECONDS)));
                } else if (folder.getFromDate().compareTo(to) > 0) {
                    folder.setToDate(to);
                    folder.setFromDate(Date.from(to.toInstant().minus(folderDuration, ChronoUnit.SECONDS)));
                }

            } else {
                folder.setFromDate(from);
                folder.setToDate(to);
            }
        }
    }

    public TestCycleVO getOneCycle(Long cycleId) {
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        testCycleDTO.setCycleId(cycleId);
        return modelMapper.map(cycleMapper.selectOne(testCycleDTO), TestCycleVO.class);
    }

    @Override
    public JSONObject getTestCycle(Long projectId, Long assignedTo) {
        List<ProductVersionDTO> versions = testCaseService.getVersionInfo(projectId).values()
                .stream().sorted(Comparator.comparing(ProductVersionDTO::getStatusCode).reversed().thenComparing(ProductVersionDTO::getSequence)).collect(Collectors.toList());
        JSONObject root = new JSONObject();

        if (versions.isEmpty()) {
            root.put("versions", new ArrayList<>());
            return root;
        }

        JSONArray versionStatus = new JSONArray();
        root.put("versions", versionStatus);

        List<TestCycleVO> cycles = testCycleDTOToTestCycleVO(countStatus(cycleMapper.query(projectId, versions.stream()
                .map(ProductVersionDTO::getVersionId).toArray(Long[]::new), assignedTo)));
        populateUsers(cycles);
        initVersionTree(projectId, versionStatus, versions, cycles);

        return root;
    }

    private List<TestCycleVO> testCycleDTOToTestCycleVO(List<TestCycleProDTO> testCycleProDTOS) {
        List<TestCycleVO> tempTestCycleVOS = modelMapper.map((testCycleProDTOS), new TypeToken<List<TestCycleVO>>() {
        }.getType());
        List<TestCycleVO> resTestCycleVOS = new ArrayList<>();
        for (int a = 0; a < tempTestCycleVOS.size(); a++) {
            TestCycleProDTO testCycleDTO = testCycleProDTOS.get(a);
            TestCycleVO testCycleVO = tempTestCycleVOS.get(a);
            List<Object> list = new ArrayList<>();
            if (testCycleDTO.getCycleCaseList() != null) {
                testCycleDTO.getCycleCaseList().forEach((k, v) -> list.add(v));
            }
            testCycleVO.setCycleCaseWithBarList(list);
            resTestCycleVOS.add(testCycleVO);
        }
        return resTestCycleVOS;
    }

    @Override
    public JSONArray getTestCycleCaseCountInVersion(Long versionId, Long projectId, Long cycleId) {
        TestCycleProDTO testCycleProDTO = new TestCycleProDTO();
        testCycleProDTO.setCycleCaseList(new ArrayList<>());
        List<TestCycleProDTO> list;
        List<TestCycleProDTO> allCycle = new ArrayList<>();

        Optional optionalCycleId = Optional.ofNullable(cycleId);
        if (optionalCycleId.isPresent()) {
            list = countStatus(cycleMapper.queryOneCycleBar(cycleId));
            list.forEach(v -> {
                if (!ObjectUtils.isEmpty(v.getCycleCaseList())) {
                    allCycle.add(v);
                }
            });
        } else {
            list = modelMapper.map(cycleMapper.query(projectId, new Long[]{versionId}, null),
                    new TypeToken<List<TestCycleProDTO>>() {
                    }.getType());
            list.forEach(v -> {
                if (v.getType().equals(TestCycleType.CYCLE) && !ObjectUtils.isEmpty(v.getCycleCaseList())) {
                    allCycle.add(v);
                }
            });
        }
        testCycleProDTO.countChildStatus(allCycle);
        JSONArray root = new JSONArray();
        if (!ObjectUtils.isEmpty(testCycleProDTO.getCycleCaseList())) {
            createCountColorJson(testCycleProDTO.getCycleCaseList(), root);
        }
        return root;
    }

    private void createCountColorJson(Map<Long, Object> cycle, JSONArray root) {
        cycle.forEach((k, v) -> {
            JSONObject object = new JSONObject();
            TestCycleProDTO.ProcessBarSection processBarSection = (TestCycleProDTO.ProcessBarSection) v;
            object.put("counts", processBarSection.getCounts());
            object.put("name", processBarSection.getStatusName());
            object.put("color", processBarSection.getColor());
            root.add(object);
        });
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
    public void initVersionTree(Long projectId, JSONArray versionStatus, List<ProductVersionDTO> versionDTOList, List<TestCycleVO> cycleDTOList) {
        Map<String, JSONObject> versionsMap = new HashMap<>(versionDTOList.size());
        Map<Long, String> versions = versionDTOList.stream().collect(Collectors.toMap(ProductVersionDTO::getVersionId, ProductVersionDTO::getName));
        Map<Long, List<TestCycleVO>> cycleVersionGroup = cycleDTOList.stream().filter(cycleDTO -> StringUtils.equals(cycleDTO.getType(), TestCycleType.CYCLE)).collect(Collectors.groupingBy(TestCycleVO::getVersionId));
        Map<Long, List<TestCycleVO>> tempVersionGroup = cycleDTOList.stream().filter(cycleDTO -> StringUtils.equals(cycleDTO.getType(), TestCycleType.TEMP)).collect(Collectors.groupingBy(TestCycleVO::getVersionId));
        Map<Long, List<TestCycleVO>> parentGroup = cycleDTOList.stream().filter(x -> x.getParentCycleId() != null).collect(Collectors.groupingBy(TestCycleVO::getParentCycleId));
        TestIssueFolderDTO foldE = new TestIssueFolderDTO();
        foldE.setProjectId(projectId);
        List<TestIssueFolderVO> testIssueFolderVOS = modelMapper.map(testIssueFolderMapper.select(foldE), new TypeToken<List<TestIssueFolderVO>>() {
        }.getType());
        Map<Long, TestIssueFolderVO> folderMap = testIssueFolderVOS.stream().collect(Collectors.toMap(TestIssueFolderVO::getFolderId, Function.identity()));
        for (ProductVersionDTO versionDTO : versionDTOList) {
            JSONObject version = versionsMap.get(versionDTO.getStatusName());
            if (version == null) {
                version = createVersionNode(versionDTO.getStatusName(), "0-" + versionsMap.size(), null);
                versionStatus.add(version);
                versionsMap.put(versionDTO.getStatusName(), version);
            }

            JSONArray versionNames = version.getJSONArray(NODE_CHILDREN);

            String nowStatusHeight = version.get("key").toString();
            String nowNamesHeight = String.valueOf(versionNames.size());
            JSONObject versionName = createVersionNode(versionDTO.getName(), nowStatusHeight + "-" + nowNamesHeight, versionDTO.getVersionId());
            versionNames.add(versionName);

            Long versionId = versionDTO.getVersionId();
            initCycleTree(versionName.getJSONArray(NODE_CHILDREN), versionName.get("key").toString(), versions, cycleVersionGroup.get(versionId), tempVersionGroup.get(versionId), parentGroup, folderMap);
        }
    }


    @Override
    public List<TestCycleVO> getCyclesInVersion(Long versionId) {
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        testCycleDTO.setVersionId(versionId);
        testCycleDTO.setType(TestCycleType.CYCLE);
        return modelMapper.map(cycleMapper.select(testCycleDTO), new TypeToken<List<TestCycleVO>>() {
        }.getType());
    }

    @Override
    public void batchChangeAssignedInOneCycle(Long projectId, Long userId, Long cycleId) {
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        testCycleDTO.setParentCycleId(cycleId);
        List<TestCycleDTO> cycleES = cycleMapper.select(testCycleDTO);
        if (ObjectUtils.isEmpty(cycleES)) {
            batchChangeCase(projectId, userId, cycleId);
        } else {
            for (TestCycleDTO cycle : cycleES) {
                batchChangeCase(projectId, userId, cycle.getCycleId());
            }
        }
    }

    @Override
    public void batchCloneCycles(Long projectId, Long versionId, List<BatchCloneCycleVO> list) {
        Boolean hasSameNameCycle = checkSameNameCycleForBatchClone(versionId, list);

        if (!hasSameNameCycle) {
            batchCloneCycleAndFolders(projectId, versionId, list, DetailsHelper.getUserDetails().getUserId());
        }
    }

    @Override
    public JSONObject getTestCycleInVersionForBatchClone(Long versionId, Long projectId) {
        JSONObject root = new JSONObject();
        Long[] versionIds = {versionId};

        List<TestCycleVO> cycles = modelMapper.map(cycleMapper.query(projectId, versionIds, null), new TypeToken<List<TestCycleVO>>() {
        }.getType());
        populateUsers(cycles);

        Map<Long, List<TestCycleVO>> cycleVersionGroup = cycles.stream()
                .filter(cycleDTO -> StringUtils.equals(cycleDTO.getType(), TestCycleType.CYCLE))
                .collect(Collectors.groupingBy(TestCycleVO::getVersionId));

        Map<Long, List<TestCycleVO>> parentGroup = cycles.stream()
                .filter(x -> x.getParentCycleId() != null)
                .collect(Collectors.groupingBy(TestCycleVO::getParentCycleId));
        if (ObjectUtils.isEmpty(cycleVersionGroup.get(versionId))) {
            root.put("cycle", new ArrayList<>());
        } else {
            List<JSONObject> cycleList = new ArrayList<>();

            for (TestCycleVO testCycleVO : cycleVersionGroup.get(versionId)) {
                JSONObject cycle = new JSONObject();

                cycle.put(CYCLE_ID, testCycleVO.getCycleId());
                cycle.put("type", testCycleVO.getType());
                cycle.put("cycleName", testCycleVO.getCycleName());
                cycle.put("rank", testCycleVO.getRank());

                List<JSONObject> childrenList = new ArrayList<>();

                if (ObjectUtils.isEmpty(parentGroup.get(testCycleVO.getCycleId()))) {
                    cycle.put(NODE_CHILDREN, new ArrayList<>());
                } else {
                    for (TestCycleVO folderCycleDTO : parentGroup.get(testCycleVO.getCycleId())) {
                        JSONObject children = new JSONObject();

                        children.put(CYCLE_ID, folderCycleDTO.getCycleId());
                        children.put("type", folderCycleDTO.getType());
                        children.put("cycleName", folderCycleDTO.getCycleName());
                        children.put("rank", folderCycleDTO.getRank());
                        children.put("parentCycleId", folderCycleDTO.getParentCycleId());

                        childrenList.add(children);
                    }
                    cycle.put(NODE_CHILDREN, childrenList);
                }
                cycleList.add(cycle);
            }
            root.put("cycle", cycleList);
        }
        return root;
    }

    @Override
    public TestFileLoadHistoryVO queryLatestBatchCloneHistory(Long projectId) {
        TestFileLoadHistoryDTO testFileLoadHistoryDTO = new TestFileLoadHistoryDTO();
        testFileLoadHistoryDTO.setCreatedBy(DetailsHelper.getUserDetails().getUserId());
        testFileLoadHistoryDTO.setActionType(TestFileLoadHistoryEnums.Action.CLONE_CYCLES.getTypeValue());
        testFileLoadHistoryDTO = testFileLoadHistoryService.queryLatestHistory(testFileLoadHistoryDTO);

        if (testFileLoadHistoryDTO == null) {
            return null;
        }

        return modelMapper.map(testFileLoadHistoryDTO, TestFileLoadHistoryVO.class);
    }

    private void batchChangeCase(Long projectId, Long userId, Long cycleId) {
        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setCycleId(cycleId);
        List<TestCycleCaseVO> caseDTOS = modelMapper.map(testCycleCaseMapper.select(testCycleCaseDTO), new TypeToken<List<TestCycleCaseVO>>() {
        }.getType());

        caseDTOS.forEach(v -> v.setAssignedTo(userId));
        testCycleCaseService.batchChangeCase(projectId, caseDTOS);
    }


    private JSONObject createVersionNode(String title, String height, Long versionId) {
        JSONObject version = new JSONObject();
        version.put("title", title);
        version.put("key", height);
        Optional.ofNullable(versionId).ifPresent(v -> version.put("versionId", v));
        JSONArray versionNames = new JSONArray();
        version.put(NODE_CHILDREN, versionNames);
        return version;
    }

    private JSONObject createCycle(TestCycleVO testCycleVO, String height, Map<Long, String> versions, Map<Long, TestIssueFolderVO> folderMap) {
        JSONObject version = new JSONObject();
        version.put("title", testCycleVO.getCycleName());
        version.put("environment", testCycleVO.getEnvironment());
        version.put("description", testCycleVO.getDescription());
        version.put("build", testCycleVO.getBuild());
        version.put("type", testCycleVO.getType());
        version.put("parentCycleId", testCycleVO.getParentCycleId());
        version.put("versionId", testCycleVO.getVersionId());
        version.put(CYCLE_ID, testCycleVO.getCycleId());
        Optional.ofNullable(testCycleVO.getCreatedUser()).ifPresent(v ->
                version.put("createdUser", JSONObject.toJSON(v))
        );
        version.put("folderId", testCycleVO.getFolderId());
        Optional.ofNullable(testCycleVO.getFolderId()).map(folderMap::get)
                .ifPresent(v -> {
                    version.put("folderName", v.getName());
                    version.put("folderVersionID", v.getVersionId());
                    version.put("folderVersionName", versions.get(v.getVersionId()));
                });
        version.put("rank", testCycleVO.getRank());
        version.put("lastRank", testCycleVO.getLastRank());
        version.put("nextRank", testCycleVO.getNextRank());
        version.put("toDate", testCycleVO.getToDate());
        version.put("fromDate", testCycleVO.getFromDate());
        version.put("cycleCaseList", testCycleVO.getCycleCaseWithBarList());
        version.put("objectVersionNumber", testCycleVO.getObjectVersionNumber());
        version.put("key", height);
        version.put("versionName", versions.get(testCycleVO.getVersionId()));
        JSONArray versionNames = new JSONArray();
        version.put(NODE_CHILDREN, versionNames);
        return version;
    }


    private void initCycleTree(JSONArray cycles, String height, Map<Long, String> versions, List<TestCycleVO> cycleList, List<TestCycleVO> tempList, Map<Long, List<TestCycleVO>> parentMap, Map<Long, TestIssueFolderVO> folderMap) {
        if (cycleList != null) {
            cycleList.stream().forEach(v -> {
                JSONObject cycle = createCycle(v, height + "-" + cycles.size(), versions, folderMap);
                cycles.add(cycle);
                initCycleFolderTree(cycle.getJSONArray(NODE_CHILDREN), cycle.get("key").toString(), parentMap.get(v.getCycleId()), versions, folderMap);
            });
        }

        if (tempList != null) {
            tempList.stream().forEach(v -> {
                JSONObject cycle = createCycle(v, height + "-" + cycles.size(), versions, folderMap);
                cycles.add(cycle);
            });
        }
    }

    private void initCycleFolderTree(JSONArray folders, String height, List<TestCycleVO> cycleDTOList, Map<Long, String> versions, Map<Long, TestIssueFolderVO> folderMap) {
        if (cycleDTOList != null) {
            cycleDTOList.stream().forEach(u ->
                    folders.add(createCycle(u, height + "-" + folders.size(), versions, folderMap))
            );
        }
    }

    @Override
    public ResponseEntity<PageInfo<ProductVersionPageDTO>> getTestCycleVersion(Long projectId, Map<String, Object> searchParamMap) {
        return testCaseService.getTestCycleVersionInfo(projectId, searchParamMap);
    }

    /**
     * 将一个cycle克隆到指定version下
     *
     * @param cycleId   指定被克隆的cycle
     * @param versionId 克隆到指定的versionID下
     * @param cycleName 克隆的新cycle的name
     * @param projectId projectId
     * @return
     */
    @Override
    public TestCycleVO cloneCycle(Long cycleId, Long versionId, String cycleName, Long projectId) {
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        testCycleDTO.setCycleId(cycleId);
        TestCycleDTO protoTestCycleDTO = cycleMapper.selectOne(testCycleDTO);

        Assert.notNull(protoTestCycleDTO, "error.clone.cycle.protoCycle.not.be.null");
        TestCycleVO newTestCycleE = new TestCycleVO();
        newTestCycleE.setCycleName(cycleName);
        newTestCycleE.setVersionId(versionId);
        newTestCycleE.setType(TestCycleType.CYCLE);
        List<TestCycleDTO> checkCycleExistList = cycleMapper.select(modelMapper.map(newTestCycleE, TestCycleDTO.class));
        if (checkCycleExistList != null && !checkCycleExistList.isEmpty()) {
            throw new CommonException("error.testCycle.exist");
        }
        return modelMapper.map(baseCloneCycle(protoTestCycleDTO, newTestCycleE, projectId), TestCycleVO.class);
    }

    @Override
    public TestCycleVO cloneFolder(Long cycleId, TestCycleVO testCycleVO, Long projectId) {
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        testCycleDTO.setCycleId(cycleId);
        TestCycleDTO protoTestCycleDTO = cycleMapper.selectOne(testCycleDTO);
        Assert.notNull(protoTestCycleDTO, "error.clone.folder.protoFolder.not.be.null");
        testCycleVO.setType(TestCycleType.FOLDER);

        return modelMapper.map(baseCloneFolder(protoTestCycleDTO, testCycleVO, projectId), TestCycleVO.class);
    }


    @Override
    public List<TestCycleVO> getFolderByCycleId(Long cycleId) {
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        testCycleDTO.setParentCycleId(cycleId);
        testCycleDTO.setType(TestCycleType.FOLDER);
        List<TestCycleDTO> select = cycleMapper.select(testCycleDTO);
        return modelMapper.map(cycleMapper.select(testCycleDTO), new TypeToken<List<TestCycleVO>>() {
        }.getType());
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
        testCycleVO.setRank(RankUtil.Operation.INSERT.getRank(getLastedRank(testCycleVO), null));
        cycleMapper.insert(testCycleDTO);

        return modelMapper.map(testCycleDTO, TestCycleVO.class);
    }

    public void checkRank(TestCycleVO testCycleVO) {
        if (getCount(testCycleVO) != 0 && StringUtils.isEmpty(getLastedRank(testCycleVO))) {
            fixRank(testCycleVO);
        }
    }

    @Override
    public Boolean checkName(Long projectId, String type, String cycleName, Long versionId, Long parentCycleId) {
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        testCycleDTO.setProjectId(projectId);
        testCycleDTO.setVersionId(versionId);
        testCycleDTO.setCycleName(cycleName);
        testCycleDTO.setType(type);
        if (Objects.equals(type, TestCycleType.FOLDER) && !Objects.isNull(parentCycleId)) {
            //如果是一个FOLDER类型，在一个父cycle下不能重名
            testCycleDTO.setParentCycleId(parentCycleId);
        }
        List<TestCycleDTO> testCycleDTOS = cycleMapper.select(testCycleDTO);
        return testCycleDTOS != null && !testCycleDTOS.isEmpty();
    }

    @Override
    public List<TestCycleDTO> batchInsertByFoldersAndPlan(TestPlanDTO testPlanDTO, List<TestIssueFolderDTO> testIssueFolderDTOS) {
        if (CollectionUtils.isEmpty(testIssueFolderDTOS)) {
            return new ArrayList<>();
        }
        Map<Long, List<TestIssueFolderDTO>> parentMap = testIssueFolderDTOS.stream().collect(Collectors.groupingBy(TestIssueFolderDTO::getParentId));
        Map<Long, Long> cycleMap = new HashMap<>();
        List<TestCycleDTO> endCycle = new ArrayList<>();
        parentMap.keySet().forEach(key -> {
            List<TestCycleDTO> testCycleDTOS = new ArrayList<>();
            List<TestIssueFolderDTO> testIssueFolder = parentMap.get(key);
            testIssueFolder.forEach(v -> {
                TestCycleDTO testCycleDTO = new TestCycleDTO();
                testCycleDTO.setFromDate(testPlanDTO.getStartDate());
                if (v.getParentId() != 0) {
                    testCycleDTO.setParentCycleId(cycleMap.get(v.getParentId()));
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
            Map<Long, List<TestCycleDTO>> listMap = testCycleDTOS.stream().collect(Collectors.groupingBy(TestCycleDTO::getPlanId));
            List<TestCycleDTO> cycleDTOList = doRank(listMap);
            cycleMapper.batchInsert(cycleDTOList);
            Map<Long, Long> returnCycleId = cycleDTOList.stream().collect(Collectors.toMap(TestCycleDTO::getFolderId, TestCycleDTO::getCycleId));
            cycleMap.putAll(returnCycleId);
            endCycle.addAll(cycleDTOList);
        });
        return endCycle;
    }

    @Override
    public List<TestCycleDTO> listByPlanIds(List<Long> planIds) {
        return cycleMapper.listByPlanIds(null, planIds);
    }

    @Override
    public void batchDelete(List<Long> needDeleteCycleIds) {
        if (CollectionUtils.isEmpty(needDeleteCycleIds)) {
            return;
        }
        List<TestCycleCaseDTO> needDeleteCycleCase = testCycleCaseService.listByCycleIds(needDeleteCycleIds);
        List<Long> executeIds = needDeleteCycleCase.stream().map(TestCycleCaseDTO::getExecuteId).collect(Collectors.toList());
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
        testIssueFolderVO.setParentId(testCycleDTO.getParentCycleId());
        return testIssueFolderVO;
    }

    @Override
    public void baseUpdate(TestCycleDTO testCycleDTO) {
        if (cycleMapper.updateByPrimaryKeySelective(testCycleDTO) != 1) {
            throw new CommonException("error.update.cycle");
        }
    }

    @Override
    public String moveCycle(Long projectId, Long targetCycleId, Long cycleId, String lastRank, String nextRank) {
        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setCycleId(targetCycleId);
        List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseMapper.select(testCycleCaseDTO);
        if (!CollectionUtils.isEmpty(testCycleCaseDTOS)) {
            throw new CommonException("error.issueFolder.has.case");
        }
        TestCycleDTO testCycleDTO = cycleMapper.selectByPrimaryKey(cycleId);
        testCycleDTO.setParentCycleId(targetCycleId);
        if (ObjectUtils.isEmpty(lastRank) && ObjectUtils.isEmpty(nextRank)) {
            testCycleDTO.setRank(RankUtil.Operation.INSERT.getRank(lastRank, nextRank));
        } else {
            testCycleDTO.setRank(RankUtil.Operation.UPDATE.getRank(lastRank, nextRank));
        }
        baseUpdate(testCycleDTO);
        return testCycleDTO.getRank();
    }

    @Override
    public void syncByCaseFolder(Long folderId, Long cycleId) {
        TestIssueFolderDTO testIssueFolderDTO = testIssueFolderMapper.selectByPrimaryKey(folderId);
        TestCycleDTO testCycleDTO = cycleMapper.selectByPrimaryKey(cycleId);
        testCycleDTO.setCycleName(testIssueFolderDTO.getName());
        updateSelf(testCycleDTO);
    }

    @Override
    public void cloneCycleByPlanId(Long copyPlanId, Long newPlanId) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        List<TestCycleDTO> testCycleDTOS = listByPlanIds(Arrays.asList(copyPlanId));
        if (CollectionUtils.isEmpty(testCycleDTOS)) {
            return;
        }
        testCycleDTOS = testCycleDTOS.stream().map(v -> {
            if (ObjectUtils.isEmpty(v.getParentCycleId())) {
                v.setParentCycleId(0L);
            }
            return v;
        }).sorted(Comparator.comparing(v -> v.getParentCycleId())).collect(Collectors.toList());
        Map<Long, List<TestCycleDTO>> olderCycleMap = testCycleDTOS.stream().collect(Collectors.groupingBy(TestCycleDTO::getParentCycleId));

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
            testCycle.forEach(v -> {
                newMapping.put(v.getOldCycleId(), v.getCycleId());
            });
        });
        // 复制执行
        testCycleCaseService.cloneCycleCase(newMapping, cycIds);
    }

    @Override
    public TestTreeIssueFolderVO queryTreeByPlanId(Long planId) {
        List<TestCycleDTO> testCycleDTOS = cycleMapper.listByPlanIds(null, Arrays.asList(planId));
        List<TestCycleDTO> collect = testCycleDTOS.stream().map(v -> {
            if (ObjectUtils.isEmpty(v.getParentCycleId())) {
                v.setParentCycleId(0L);
            }
            return v;
        }).collect(Collectors.toList());
        Map<Long, TestCycleDTO> allMap = collect.stream().collect(Collectors.toMap(TestCycleDTO::getCycleId, Function.identity()));
        Map<Long, List<Long>> parentMap = collect.stream().collect(Collectors.groupingBy(TestCycleDTO::getParentCycleId, Collectors.mapping(TestCycleDTO::getCycleId, Collectors.toList())));
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
        return cycleMapper.getPlanLastedRank(testCycleVO.getParentCycleId());
    }

    private void insertCaseToFolder(Long issueFolderId, Long cycleId) {
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
        testIssueFolderRelDTO.setFolderId(issueFolderId);
        List<TestIssueFolderRelDTO> list = testIssueFolderRelMapper.select(testIssueFolderRelDTO);
        TestCycleCaseVO dto = new TestCycleCaseVO();
        dto.setCycleId(cycleId);
        list.forEach(v -> {
            dto.setIssueId(v.getIssueId());
            testCycleCaseService.create(dto, v.getProjectId());
        });
    }

    private TestCycleDTO baseUpdate(Long projectId, TestCycleDTO testCycleE) {
        if (testCycleE.getFolderId() != null) {
            TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
            testCycleCaseDTO.setCycleId(testCycleE.getCycleId());
            testCycleCaseDTO.setProjectId(projectId);
            testCycleCaseMapper.select(testCycleCaseDTO).forEach(v -> testCycleCaseService.delete(v.getExecuteId(), 0L));
            insertCaseToFolder(testCycleE.getFolderId(), testCycleE.getCycleId());
        }
        testCycleE.setProjectId(projectId);
        return updateSelf(testCycleE);
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

    private void deleteCycleWithCase(TestCycleDTO testCycleE, Long projectId) {
        TestCycleCaseDTO testCycleCaseE = new TestCycleCaseDTO();

        testCycleCaseE.setCycleId(testCycleE.getCycleId());
        testCycleCaseE.setProjectId(projectId);
        testCycleCaseMapper.select(testCycleCaseE).forEach(v -> testCycleCaseService.delete(v.getExecuteId(), projectId));
        cycleMapper.delete(testCycleE);
    }

    private List<String> queryUpdateRank(TestCycleVO testCycleE) {
        long lastCycleId = Long.parseLong(testCycleE.getRank());
        List<TestCycleDTO> testCycleDTOS;
        List<String> res = new ArrayList<>();

        if (testCycleE.getType().equals(TestCycleType.CYCLE)) {
            TestCycleDTO convert = modelMapper.map(testCycleE, TestCycleDTO.class);
            testCycleDTOS = cycleMapper.queryCycleInVersion(convert);
        } else {
            TestCycleDTO testCycleETemp = new TestCycleDTO();
            testCycleETemp.setCycleId(testCycleE.getParentCycleId());
            TestCycleDTO convert = modelMapper.map(testCycleETemp, TestCycleDTO.class);
            testCycleDTOS = cycleMapper.queryChildCycle(convert);
        }

        if (lastCycleId != -1L) {
            int lastIndex = -1;

            for (int a = 0; a < testCycleDTOS.size(); a++) {
                if (testCycleDTOS.get(a).getCycleId().equals(lastCycleId)) {
                    lastIndex = a;
                }
            }

            if (lastIndex >= 0) {
                TestCycleDTO testCycleETemp = new TestCycleDTO();
                testCycleETemp.setCycleId(testCycleDTOS.get(lastIndex).getCycleId());
                List<TestCycleDTO> list = cycleMapper.select(testCycleETemp);
                res.add(list.get(0).getRank());
            } else {
                res.add(null);
            }

            if (lastIndex < testCycleDTOS.size() - 1) {
                TestCycleDTO testCycleETemp = new TestCycleDTO();
                testCycleETemp.setCycleId(testCycleDTOS.get(lastIndex + 1).getCycleId());
                List<TestCycleDTO> list = cycleMapper.select(testCycleETemp);
                res.add(list.get(0).getRank());
            } else {
                res.add(null);
            }
        } else {
            res.add(null);
            TestCycleDTO testCycleETemp = new TestCycleDTO();
            testCycleETemp.setCycleId(testCycleDTOS.get(0).getCycleId());
            List<TestCycleDTO> list = cycleMapper.select(testCycleETemp);
            res.add(list.get(0).getRank());
        }
        return res;
    }

    private List<TestCycleProDTO> countStatus(List<TestCycleDTO> testCycleES) {
        List<TestCycleProDTO> testCycleProDTOS = modelMapper.map(testCycleES, new TypeToken<List<TestCycleProDTO>>() {
        }.getType());

        Map<Long, List<TestCycleProDTO>> parentGroup = testCycleProDTOS.stream().filter(x -> x.getParentCycleId() != null
                && TestCycleType.FOLDER.equals(x.getType())).collect(Collectors.groupingBy(TestCycleProDTO::getParentCycleId));

        testCycleProDTOS.parallelStream().filter(v -> StringUtils.equals(v.getType(), TestCycleType.CYCLE))
                .forEach(u -> u.countChildStatus(parentGroup.get(u.getCycleId())));
        return testCycleProDTOS;
    }

    private Boolean checkSameNameCycleForBatchClone(Long versionId, List<BatchCloneCycleVO> list) {
        list.forEach(v -> {
            TestCycleDTO oldTestCycleDTO = new TestCycleDTO();
            oldTestCycleDTO.setCycleId(v.getCycleId());
            TestCycleDTO protoTestCycleDTO = cycleMapper.selectOne(oldTestCycleDTO);
            protoTestCycleDTO.setVersionId(versionId);

            validateCycle(protoTestCycleDTO);
        });
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchCloneCycleAndFolders(Long projectId, Long versionId, List<BatchCloneCycleVO> list, Long userId) {
        TestFileLoadHistoryWithRateVO testFileLoadHistoryVO = initBatchCloneFileLoadHistory(projectId, versionId);

        int sum = 0;
        int offset = 0;

        for (BatchCloneCycleVO batchCloneCycleVO : list) {
            sum = sum + batchCloneCycleVO.getFolderIds().length;
        }

        try {
            for (BatchCloneCycleVO batchCloneCycleVO : list) {
                offset = cloneCycleWithSomeFolder(projectId, versionId, batchCloneCycleVO,
                        testFileLoadHistoryVO, sum, offset, userId);
            }

            testFileLoadHistoryVO.setLastUpdateDate(new Date());
            testFileLoadHistoryVO.setSuccessfulCount(Integer.toUnsignedLong(sum));
            testFileLoadHistoryVO.setStatus(TestFileLoadHistoryEnums.Status.SUCCESS.getTypeValue());
        } catch (Exception e) {
            LOGGER.error(e.toString());

            testFileLoadHistoryVO.setLastUpdateDate(new Date());
            testFileLoadHistoryVO.setFailedCount(Integer.toUnsignedLong(sum));
            testFileLoadHistoryVO.setStatus(TestFileLoadHistoryEnums.Status.FAILURE.getTypeValue());

            notifyService.postWebSocket(NOTIFYCYCLECODE, String.valueOf(userId),
                    JSON.toJSONString(testFileLoadHistoryVO));
            throw new CommonException(CYCLE_DATE_NULL_ERROR);
        }

        testFileLoadHistoryMapper.updateByPrimaryKey(modelMapper.map(testFileLoadHistoryVO, TestFileLoadHistoryDTO.class));
    }

    private int cloneCycleWithSomeFolder(Long projectId, Long versionId, BatchCloneCycleVO batchCloneCycleVO,
                                         TestFileLoadHistoryWithRateVO testFileLoadHistoryVO, int sum, int offset, Long userId) {
        TestCycleDTO oldTestCycleE = new TestCycleDTO();
        oldTestCycleE.setCycleId(batchCloneCycleVO.getCycleId());
        TestCycleDTO protoTestCycleDTO = cycleMapper.selectOne(oldTestCycleE);

        TestCycleVO newTestCycleE = new TestCycleVO();
        newTestCycleE.setCycleName(protoTestCycleDTO.getCycleName());
        newTestCycleE.setVersionId(versionId);
        newTestCycleE.setType(TestCycleType.CYCLE);

        TestCycleDTO parentCycle = baseCloneFolder(protoTestCycleDTO, newTestCycleE, projectId);

        if (sum == 0) {
            testFileLoadHistoryVO.setRate(1.0);
            notifyService.postWebSocket(NOTIFYCYCLECODE, String.valueOf(userId),
                    JSON.toJSONString(testFileLoadHistoryVO));

            return 0;
        }

        for (Long folderId : batchCloneCycleVO.getFolderIds()) {
            TestCycleDTO oldFolderTestCycleE = new TestCycleDTO();
            oldFolderTestCycleE.setCycleId(folderId);
            TestCycleDTO protoFolderTestCycleE = cycleMapper.selectOne(oldFolderTestCycleE);

            TestCycleVO newFolderTestCycleE = new TestCycleVO();
            newFolderTestCycleE.setParentCycleId(parentCycle.getCycleId());
            newFolderTestCycleE.setVersionId(versionId);
            newFolderTestCycleE.setType(TestCycleType.FOLDER);

            baseCloneFolder(protoFolderTestCycleE, newFolderTestCycleE, projectId);

            offset++;
            testFileLoadHistoryVO.setRate(offset * 1.0 / sum);
            notifyService.postWebSocket(NOTIFYCYCLECODE, String.valueOf(userId),
                    JSON.toJSONString(testFileLoadHistoryVO));
        }
        return offset;
    }

    private TestFileLoadHistoryWithRateVO initBatchCloneFileLoadHistory(Long projectId, Long versionId) {
        TestFileLoadHistoryDTO testFileLoadHistoryDTO = new TestFileLoadHistoryDTO();
        testFileLoadHistoryDTO.setProjectId(projectId);
        testFileLoadHistoryDTO.setActionType(TestFileLoadHistoryEnums.Action.CLONE_CYCLES.getTypeValue());
        testFileLoadHistoryDTO.setSourceType(TestFileLoadHistoryEnums.Source.VERSION.getTypeValue());
        testFileLoadHistoryDTO.setLinkedId(versionId);
        testFileLoadHistoryDTO.setStatus(TestFileLoadHistoryEnums.Status.SUSPENDING.getTypeValue());
        testFileLoadHistoryMapper.insert(testFileLoadHistoryDTO);
        return modelMapper.map(testFileLoadHistoryMapper.selectByPrimaryKey(testFileLoadHistoryDTO), TestFileLoadHistoryWithRateVO.class);
    }

    private TestCycleDTO baseCloneFolder(TestCycleDTO protoTestCycleDTO, TestCycleVO newTestCycleE, Long projectId) {
        protoTestCycleDTO.setProjectId(projectId);
        checkRank(newTestCycleE);
        TestCycleDTO parentCycleE = new TestCycleDTO();

        parentCycleE.setCycleId(newTestCycleE.getParentCycleId());
        if (!protoTestCycleDTO.getType().equals(TestCycleType.CYCLE)) {
            List<TestCycleDTO> parentCycleES = cycleMapper.select(parentCycleE);

            Date parentFromDate = parentCycleES.get(0).getFromDate();
            Date parentToDate = parentCycleES.get(0).getToDate();

            Date oldFolderFromDate = protoTestCycleDTO.getFromDate();
            Date oldFolderToDate = protoTestCycleDTO.getToDate();

            Assert.notNull(parentFromDate, CYCLE_DATE_NULL_ERROR);
            Assert.notNull(parentToDate, CYCLE_DATE_NULL_ERROR);
            Assert.notNull(oldFolderFromDate, CYCLE_DATE_NULL_ERROR);
            Assert.notNull(oldFolderToDate, CYCLE_DATE_NULL_ERROR);

            int differentDaysOldFolder = TestDateUtil.differentDaysByMillisecond(oldFolderFromDate, oldFolderToDate);
            int differentDaysParent = TestDateUtil.differentDaysByMillisecond(parentFromDate, parentToDate);

            protoTestCycleDTO.setFromDate(parentFromDate);

            if (differentDaysOldFolder > differentDaysParent) {
                protoTestCycleDTO.setToDate(parentToDate);
            } else {
                protoTestCycleDTO.setToDate(TestDateUtil.increaseDaysOnDate(parentFromDate, differentDaysOldFolder));
            }
        }
        newTestCycleE.setRank(RankUtil.Operation.INSERT.getRank(getLastedRank(newTestCycleE), null));
        TestCycleDTO newCycleE = cloneCycle(protoTestCycleDTO, newTestCycleE);
        cloneSubCycleCase(protoTestCycleDTO.getCycleId(), newCycleE.getCycleId(), projectId);

        return newCycleE;
    }

    private TestCycleDTO baseCloneCycle(TestCycleDTO protoTestCycleDTO, TestCycleVO newTestCycleE, Long projectId) {
        TestCycleDTO parentCycle = baseCloneFolder(protoTestCycleDTO, newTestCycleE, projectId);
        cycleMapper.queryChildFolderByRank(protoTestCycleDTO.getCycleId()).forEach(v -> {
            TestCycleVO testCycleDTO = new TestCycleVO();
            testCycleDTO.setParentCycleId(parentCycle.getCycleId());
            testCycleDTO.setVersionId(parentCycle.getVersionId());
            testCycleDTO.setType(TestCycleType.FOLDER);
            baseCloneFolder(v, testCycleDTO, projectId);
        });
        return parentCycle;
    }

    private void cloneSubCycleCase(Long protoTestCycleId, Long newCycleId, Long projectId) {
        Assert.notNull(protoTestCycleId, "error.clone.cycle.protoCycleId.not.be.null");
        Assert.notNull(newCycleId, "error.clone.cycle.newCycleId.not.be.null");

        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setCycleId(protoTestCycleId);
        Long defaultStatus = testStatusService.getDefaultStatusId(TestStatusType.STATUS_TYPE_CASE);
        final String[] lastRank = new String[1];
        lastRank[0] = testCycleCaseMapper.getLastedRank(protoTestCycleId);

        //查询出cycle下所有case将其创建到新的cycle下并执行
        testCycleCaseMapper.select(testCycleCaseDTO).forEach(v ->
                lastRank[0] = cloneCycleCase(getCloneCase(RankUtil.Operation.INSERT
                        .getRank(lastRank[0], null), newCycleId, defaultStatus, v), projectId).getRank()
        );
    }

    private TestCycleDTO cloneCycle(TestCycleDTO proto, TestCycleVO newTestCycleVO) {
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        testCycleDTO.setParentCycleId(Optional.ofNullable(newTestCycleVO.getParentCycleId()).orElse(proto.getParentCycleId()));
        testCycleDTO.setCycleName(Optional.ofNullable(newTestCycleVO.getCycleName()).orElse(proto.getCycleName()));
        testCycleDTO.setVersionId(Optional.ofNullable(newTestCycleVO.getVersionId()).orElse(proto.getVersionId()));
        testCycleDTO.setDescription(Optional.ofNullable(newTestCycleVO.getDescription()).orElse(proto.getDescription()));
        testCycleDTO.setBuild(Optional.ofNullable(newTestCycleVO.getBuild()).orElse(proto.getBuild()));
        testCycleDTO.setEnvironment(Optional.ofNullable(newTestCycleVO.getEnvironment()).orElse(proto.getEnvironment()));
        testCycleDTO.setFromDate(Optional.ofNullable(newTestCycleVO.getFromDate()).orElse(proto.getFromDate()));
        testCycleDTO.setToDate(Optional.ofNullable(newTestCycleVO.getToDate()).orElse(proto.getToDate()));
        testCycleDTO.setType(Optional.ofNullable(newTestCycleVO.getType()).orElse(proto.getType()));
        testCycleDTO.setFolderId(Optional.ofNullable(newTestCycleVO.getFolderId()).orElse(proto.getFolderId()));
        testCycleDTO.setRank(Optional.ofNullable(newTestCycleVO.getRank()).orElse(proto.getRank()));
        testCycleDTO.setProjectId(Optional.ofNullable(newTestCycleVO.getProjectId()).orElse(proto.getProjectId()));
        cycleMapper.insert(testCycleDTO);
        return testCycleDTO;
    }

    private TestCycleCaseDTO cloneCycleCase(TestCycleCaseDTO testCycleCaseE, Long projectId) {
        testCycleCaseE.setProjectId(projectId);
        if (testCycleCaseMapper.validateCycleCaseInCycle(testCycleCaseE).longValue() > 0) {
            throw new CommonException("error.cycle.case.insert.have.one.case.in.cycle");
        }
        testCycleCaseMapper.insert(testCycleCaseE);

        testCycleCaseService.createTestCycleCaseStep(testCycleCaseE);
        return testCycleCaseE;
    }

    private TestCycleCaseDTO getCloneCase(String rank, Long newCycleId, Long defaultStatus, TestCycleCaseDTO testCycleCaseDTO) {
        testCycleCaseDTO.setExecuteId(null);
        testCycleCaseDTO.setRank(rank);
        testCycleCaseDTO.setCycleId(newCycleId);
        testCycleCaseDTO.setExecutionStatus(defaultStatus);
        testCycleCaseDTO.setObjectVersionNumber(null);
        return testCycleCaseDTO;
    }

    private TestCycleDTO baseInsert(TestCycleDTO testCycleDTO) {
        if (ObjectUtils.isEmpty(testCycleDTO)) {
            throw new CommonException("error.insert.test.cycle.is.not.null");
        }
        DBValidateUtil.executeAndvalidateUpdateNum(cycleMapper::insertSelective, testCycleDTO, 1, "error.insert.test.cycle");
        return testCycleDTO;
    }

    private List<TestCycleDTO> doRank(Map<Long, List<TestCycleDTO>> listMap) {
        List<TestCycleDTO> testCycleDTOS = new ArrayList<>();
        for (Map.Entry<Long, List<TestCycleDTO>> map : listMap.entrySet()
        ) {
            String prevRank = RankUtil.Operation.INSERT.getRank(cycleMapper.getPlanLastedRank(map.getKey()), null);
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