package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.ProductVersionDTO;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.domain.service.ITestIssueFolderService;
import io.choerodon.test.manager.domain.service.ITestStatusService;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.domain.test.manager.factory.*;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleServiceImpl implements TestCycleService {
    @Autowired
    ITestCycleService iTestCycleService;

    @Autowired
    ProductionVersionClient productionVersionClient;

    @Autowired
    TestCaseService testCaseService;

    @Autowired
    UserService userService;

    @Autowired
    TestCycleCaseService testCycleCaseService;

    @Autowired
    ITestStatusService iTestStatusService;

    @Autowired
    FixDataService fixDataService;

    private static final String NODE_CHILDREN = "children";

    @Autowired
    private SagaClient sagaClient;

    @Autowired
    ITestIssueFolderService folderService;

    @Autowired
    TestIssueFolderMapper testIssueFolderMapper;

    /**
     * 新建cycle，folder 并同步folder下的执行
     *
     * @param testCycleDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleDTO insert(TestCycleDTO testCycleDTO) {
        TestCycleDTO cycleDTO = ConvertHelper.convert(iTestCycleService.insert(ConvertHelper.convert(testCycleDTO, TestCycleE.class)), TestCycleDTO.class);
        if (testCycleDTO.getFolderId() != null) {
            TestIssueFolderRelE folder = TestIssueFolderRelEFactory.create();
            folder.setFolderId(testCycleDTO.getFolderId());
            List<TestIssueFolderRelE> list = folder.queryAllUnderProject();
            TestCycleCaseDTO dto = new TestCycleCaseDTO();
            dto.setCycleId(cycleDTO.getCycleId());
            list.forEach(v -> {
                dto.setIssueId(v.getIssueId());
                testCycleCaseService.create(dto, v.getProjectId());
            });
        }
        return cycleDTO;
    }

    /**
     * 同步文件夹下的所有执行
     *
     * @param cycleId
     * @param folderId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean synchroFolder(Long cycleId, Long folderId, Long projectId) {
        //获取folder下所有issue
        TestIssueFolderRelE folder = TestIssueFolderRelEFactory.create();
        folder.setFolderId(folderId);
        List<TestIssueFolderRelE> list = Optional.ofNullable(folder.queryAllUnderProject()).orElseGet(ArrayList::new);
        Set<Long> folderIssues = list.stream().map(TestIssueFolderRelE::getIssueId).collect(Collectors.toSet());
        //获取cycle下所有issue执行
        TestCycleCaseE cycleCaseE = TestCycleCaseEFactory.create();
        cycleCaseE.setCycleId(cycleId);
        List<TestCycleCaseE> caseList = Optional.ofNullable(cycleCaseE.querySelf()).orElseGet(ArrayList::new);
        Map<Long, Long> caseIssues = caseList.stream().collect(Collectors.toMap(TestCycleCaseE::getIssueId, TestCycleCaseE::getExecuteId));
        //对比执行和folder中的issue添加未添加的执行
        TestCycleCaseDTO dto = new TestCycleCaseDTO();
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
        TestCaseStepE caseStepE = TestCaseStepEFactory.create();
        caseStepE.setIssueId(issueId);
        List<TestCaseStepE> caseSteps = caseStepE.querySelf();
        if (ObjectUtils.isEmpty(caseSteps)) {
            return;
        }
        //获取执行下的所有步骤
        TestCycleCaseStepE stepE = TestCycleCaseStepEFactory.create();
        stepE.setExecuteId(executeId);
        List<TestCycleCaseStepE> cycleSteps = Optional.ofNullable(stepE.querySelf()).orElseGet(ArrayList::new);
        //当issue下步骤有修改时启动新的步骤

        if (caseSteps.size() != cycleSteps.size()) {
            TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
            Long status = iTestStatusService.getDefaultStatusId(TestStatusE.STATUS_TYPE_CASE_STEP);

            Set<Long> newStepSet = compareStep(caseSteps, cycleSteps);
            newStepSet.forEach(v -> testCycleCaseStepE.runOneStep(executeId, v, status));
        }
    }

    /**
     * 对比issue下步骤和case步骤找出不同的需要启动的步骤
     *
     * @param caseSteps
     * @param cycleSteps
     * @return
     */
    private Set<Long> compareStep(List<TestCaseStepE> caseSteps, List<TestCycleCaseStepE> cycleSteps) {
        Set<Long> newStep = new HashSet<>();
        Set caseStepSet = caseSteps.stream().map(TestCaseStepE::getStepId).collect(Collectors.toSet());
        Set cycleStepSet = cycleSteps.stream().map(TestCycleCaseStepE::getStepId).collect(Collectors.toSet());
        Iterator<Long> iterator = caseStepSet.iterator();
        while (iterator.hasNext()) {
            Long stepId = iterator.next();
            if (!cycleStepSet.contains(stepId)) {
                newStep.add(stepId);
            }
        }
        return newStep;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean synchroFolderInCycle(Long cycleId, Long projectId) {
        //查询cycle下所有关联folder
        TestCycleE cycleE = TestCycleEFactory.create();
        cycleE.setParentCycleId(cycleId);
        List<TestCycleE> list = cycleE.querySelf();
        if (!ObjectUtils.isEmpty(list)) {
            list.forEach(v -> synchroFolder(v.getCycleId(), v.getFolderId(), projectId));
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean synchroFolderInVersion(Long versionId, Long projectId) {
        TestCycleE cycleE = TestCycleEFactory.create();
        cycleE.setVersionId(versionId);
        List<TestCycleE> list = cycleE.querySelf();
        if (!ObjectUtils.isEmpty(list)) {
            list.forEach(v -> synchroFolderInCycle(v.getCycleId(), projectId));
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(TestCycleDTO testCycleDTO, Long projectId) {
        iTestCycleService.delete(ConvertHelper.convert(testCycleDTO, TestCycleE.class), projectId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleDTO update(TestCycleDTO testCycleDTO) {
        TestCycleE temp = TestCycleEFactory.create();
        temp.setCycleId(testCycleDTO.getCycleId());
        TestCycleE temp1 = temp.queryOne();
        if (temp1.getType().equals(TestCycleE.FOLDER)) {
            Optional.ofNullable(testCycleDTO.getCycleName()).ifPresent(temp1::setCycleName);
            Optional.ofNullable(testCycleDTO.getFromDate()).ifPresent(temp1::setFromDate);
            Optional.ofNullable(testCycleDTO.getToDate()).ifPresent(temp1::setToDate);
            Optional.ofNullable(testCycleDTO.getDescription()).ifPresent(temp1::setDescription);
            temp1.setObjectVersionNumber(testCycleDTO.getObjectVersionNumber());
        } else if (temp1.getType().equals(TestCycleE.CYCLE)) {
            Optional.ofNullable(testCycleDTO.getBuild()).ifPresent(temp1::setBuild);
            Optional.ofNullable(testCycleDTO.getCycleName()).ifPresent(temp1::setCycleName);
            Optional.ofNullable(testCycleDTO.getDescription()).ifPresent(temp1::setDescription);
            Optional.ofNullable(testCycleDTO.getEnvironment()).ifPresent(temp1::setEnvironment);
            Optional.ofNullable(testCycleDTO.getFromDate()).ifPresent(temp1::setFromDate);
            Optional.ofNullable(testCycleDTO.getToDate()).ifPresent(temp1::setToDate);
            Optional.ofNullable(testCycleDTO.getObjectVersionNumber()).ifPresent(temp1::setObjectVersionNumber);
        }
        return ConvertHelper.convert(iTestCycleService.update(temp1), TestCycleDTO.class);

    }

    public TestCycleDTO getOneCycle(Long cycleId) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        testCycleE.setCycleId(cycleId);
        testCycleE.querySelf();
        return ConvertHelper.convert(testCycleE.queryOne(), TestCycleDTO.class);
    }

    @Override
    public JSONObject getTestCycle(Long projectId, Long assignedTo) {
        List<ProductVersionDTO> versions = new ArrayList<>(testCaseService.getVersionInfo(projectId).values());
        if (versions.isEmpty()) {
            return new JSONObject();
        }
        JSONObject root = new JSONObject();
        JSONArray versionStatus = new JSONArray();
        root.put("versions", versionStatus);

        List<TestCycleDTO> cycles = ConvertHelper.convertList(iTestCycleService.queryCycleWithBar(versions.stream().map(ProductVersionDTO::getVersionId).toArray(Long[]::new), assignedTo), TestCycleDTO.class);

        initVersionTree(versionStatus, versions, cycles);

        return root;
    }

    @Override
    public JSONArray getTestCycleCaseCountInVersion(Long versionId, Long projectId) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        testCycleE.setCycleCaseList(new ArrayList<>());
        List<TestCycleE> list = iTestCycleService.queryCycleWithBar(new Long[]{versionId}, null);
        List<TestCycleE> allCycle = new ArrayList<>();
        list.forEach(v -> {
            if (v.getType().equals(TestCycleE.CYCLE) && !ObjectUtils.isEmpty(v.getCycleCaseList())) {
                allCycle.add(v);
            }
        });
        testCycleE.countChildStatus(allCycle);
        JSONArray root = new JSONArray();
        if (!ObjectUtils.isEmpty(testCycleE.getCycleCaseList())) {
            createCountColorJson(testCycleE.getCycleCaseList(), root, projectId);
        }
        return root;
    }

    private void createCountColorJson(Map<String, Object> cycle, JSONArray root, Long projectId) {
        TestStatusE statusE = TestStatusEFactory.create();
        statusE.setProjectId(projectId);
        statusE.setStatusType(TestStatusE.STATUS_TYPE_CASE);
        Map<String, String> colorMap = statusE.queryAllUnderProject().stream().collect(Collectors.toMap(TestStatusE::getStatusColor, TestStatusE::getStatusName));
        cycle.forEach((k, v) -> {
            JSONObject object = new JSONObject();
            object.put("value", v);
            object.put("name", colorMap.get(k));
            object.put("color", k);
            root.add(object);
        });
    }

    public void populateUsers(List<TestCycleDTO> dtos) {
        Long[] usersId = dtos.stream().map(TestCycleDTO::getCreatedBy).toArray(Long[]::new);
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
    public void initVersionTree(JSONArray versionStatus, List<ProductVersionDTO> versionDTOList, List<TestCycleDTO> cycleDTOList) {
        Map<String, JSONObject> versionsMap = new HashMap<>();
        Map<Long, String> versions = versionDTOList.stream().collect(Collectors.toMap(ProductVersionDTO::getVersionId, ProductVersionDTO::getName));

        for (ProductVersionDTO versionDTO : versionDTOList) {
            JSONObject version;
            if (!versionsMap.containsKey(versionDTO.getStatusName())) {
                version = createVersionNode(versionDTO.getStatusName(), "0-" + versionsMap.size(), null);
                versionStatus.add(version);
                versionsMap.put(versionDTO.getStatusName(), version);
            } else {
                version = versionsMap.get(versionDTO.getStatusName());
            }

            JSONArray versionNames = version.getJSONArray(NODE_CHILDREN);

            String nowStatusHeight = version.get("key").toString();
            String nowNamesHeight = String.valueOf(versionNames.size());
            JSONObject versionName = createVersionNode(versionDTO.getName(), nowStatusHeight + "-" + nowNamesHeight, versionDTO.getVersionId());
            versionNames.add(versionName);

            initCycleTree(versionName.getJSONArray(NODE_CHILDREN), versionName.get("key").toString(), versionDTO.getVersionId(), cycleDTOList, versions);
        }
    }


    @Override
    public List<TestCycleDTO> getCyclesInVersion(Long versionId) {
        TestCycleE cycleE = TestCycleEFactory.create();
        cycleE.setVersionId(versionId);
        cycleE.setType(TestCycleE.CYCLE);
        return ConvertHelper.convertList(cycleE.querySelf(), TestCycleDTO.class);
    }

    @Override
    @Saga(code = "test-fix-cycle-data", description = "修复数据", inputSchemaClass = TestIssueFolderDTO.class)
    public void fixCycleData(Long projectId) {
        sagaClient.startSaga("test-fix-cycle-data", new StartInstanceDTO(JSON.toJSONString(
                new TestIssueFolderDTO(null, null, null, projectId, null, null)),
                "", ""));
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

    private JSONObject createCycle(TestCycleDTO testCycleDTO, String height, Map<Long, String> versions) {
        JSONObject version = new JSONObject();
        version.put("title", testCycleDTO.getCycleName());
        version.put("environment", testCycleDTO.getEnvironment());
        version.put("description", testCycleDTO.getDescription());
        version.put("build", testCycleDTO.getBuild());
        version.put("type", testCycleDTO.getType());
        version.put("parentCycleId", testCycleDTO.getParentCycleId());
        version.put("versionId", testCycleDTO.getVersionId());
        version.put("cycleId", testCycleDTO.getCycleId());
        Optional.ofNullable(testCycleDTO.getCreatedUser()).ifPresent(v ->
                version.put("createdUser", JSONObject.toJSON(v))
        );
        version.put("folderId", testCycleDTO.getFolderId());
        TestIssueFolderE folderE = TestIssueFolderEFactory.create();
        Optional.ofNullable(testCycleDTO.getFolderId()).map(v -> {
            folderE.setFolderId(v);
            TestIssueFolderE res = folderE.queryByPrimaryKey();
            res.setName(res == null ? "文件夹存在错误，文件夹不存在" : res.getName());
            return res;
        }).ifPresent(v -> version.put("folderName", v.getName()));
        version.put("toDate", testCycleDTO.getToDate());
        version.put("fromDate", testCycleDTO.getFromDate());
        version.put("cycleCaseList", testCycleDTO.getCycleCaseList());
        version.put("objectVersionNumber", testCycleDTO.getObjectVersionNumber());
        version.put("key", height);
        version.put("versionName", versions.get(testCycleDTO.getVersionId()));
        JSONArray versionNames = new JSONArray();
        version.put(NODE_CHILDREN, versionNames);
        return version;
    }


    private void initCycleTree(JSONArray cycles, String height, Long versionId, List<TestCycleDTO> cycleDTOList, Map<Long, String> versions) {

        cycleDTOList.stream().filter(cycleDTO -> cycleDTO.getVersionId().equals(versionId) && StringUtils.equals(cycleDTO.getType(), TestCycleE.CYCLE))
                .forEach(v -> {
                    JSONObject cycle = createCycle(v, height + "-" + cycles.size(), versions);
                    cycles.add(cycle);
                    initCycleFolderTree(cycle.getJSONArray(NODE_CHILDREN), cycle.get("key").toString(), v.getCycleId(), cycleDTOList, versions);
                });

        cycleDTOList.stream().filter(cycleDTO -> cycleDTO.getVersionId().equals(versionId) && StringUtils.equals(cycleDTO.getType(), TestCycleE.TEMP))
                .forEach(v -> {
                    JSONObject cycle = createCycle(v, height + "-" + cycles.size(), versions);
                    cycles.add(cycle);
                });
    }


    private void initCycleFolderTree(JSONArray folders, String height, Long parentId, List<TestCycleDTO> cycleDTOList, Map<Long, String> versions) {
        cycleDTOList.stream().filter(v -> parentId.equals(v.getParentCycleId())).forEach(u ->
                folders.add(createCycle(u, height + "-" + folders.size(), versions))
        );
    }


    @Override
    public ResponseEntity<Page<ProductVersionPageDTO>> getTestCycleVersion(Long projectId, Map<String, Object> searchParamMap) {
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
    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleDTO cloneCycle(Long cycleId, Long versionId, String cycleName, Long projectId) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        testCycleE.setCycleId(cycleId);
        TestCycleE protoTestCycleE = testCycleE.queryOne();

        Assert.notNull(protoTestCycleE, "error.clone.cycle.protoCycle.not.be.null");
        TestCycleE newTestCycleE = TestCycleEFactory.create();
        newTestCycleE.setCycleName(cycleName);
        newTestCycleE.setVersionId(versionId);
        newTestCycleE.setType(TestCycleE.CYCLE);
        return ConvertHelper.convert(iTestCycleService.cloneCycle(protoTestCycleE, newTestCycleE, projectId), TestCycleDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleDTO cloneFolder(Long cycleId, TestCycleDTO testCycleDTO, Long projectId) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        testCycleE.setCycleId(cycleId);
        TestCycleE protoTestCycleE = testCycleE.queryOne();
        Assert.notNull(protoTestCycleE, "error.clone.folder.protoFolder.not.be.null");

        return ConvertHelper.convert(iTestCycleService.cloneFolder(protoTestCycleE, ConvertHelper.convert(testCycleDTO, TestCycleE.class), projectId), TestCycleDTO.class);
    }


    @Override
    public List<TestCycleDTO> getFolderByCycleId(Long cycleId) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        testCycleE.setCycleId(cycleId);
        return ConvertHelper.convertList(testCycleE.getChildFolder(), TestCycleDTO.class);
    }

    @Override
    public void populateVersion(TestCycleDTO cycle, Long projectId) {
        Map<Long, ProductVersionDTO> map = testCaseService.getVersionInfo(projectId);
        cycle.setVersionName(map.get(cycle.getVersionId()).getName());
        cycle.setVersionStatusName(map.get(cycle.getVersionId()).getStatusName());
    }
}
