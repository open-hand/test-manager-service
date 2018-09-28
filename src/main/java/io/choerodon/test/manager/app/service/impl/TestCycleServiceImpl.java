package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.dto.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.domain.service.ITestStatusService;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.domain.test.manager.factory.*;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
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
    TestIssueFolderRelService testIssueFolderRelService;

    @Autowired
    TestIssueFolderService testIssueFolderService;

    @Autowired
    TestCycleCaseService testCycleCaseService;

    @Autowired
    ITestStatusService iTestStatusService;

    @Autowired
    TestCycleCaseStepService testCycleCaseStepService;

    @Autowired
    TestCaseStepService testCaseStepService;

    private static final String NODE_CHILDREN = "children";
    private static final String CYCLE = "cycle";

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
            newStepSet.forEach(v -> {
                testCycleCaseStepE.runOneStep(executeId, v, status);
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
            temp1.setCycleName(testCycleDTO.getCycleName());
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

        populateUsers(cycles);
        initVersionTree(versionStatus, versions, cycles);

        return root;
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

            initCycleTree(versionName.getJSONArray(NODE_CHILDREN), versionName.get("key").toString(), versionDTO.getVersionId(), cycleDTOList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void fixCycleData(Long projectId) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();

        //从敏捷查询所有type是issue_test的issue
        List<IssueProjectDTO> issueProjectDTOS = testCaseService.queryIssueTestGroupByProject(projectId);
        //旧数据放到当前项目（min version）最小版本的一个叫做旧数据的文件夹下
        Long[] versionIdsUnderProject = testCaseService.getVersionIds(projectId);
        TestIssueFolderDTO needFolder = null;
        if(!ObjectUtils.isEmpty(versionIdsUnderProject)) {
            TestIssueFolderDTO needTestIssueFolderDTO = new TestIssueFolderDTO(null, "旧数据", versionIdsUnderProject[0], projectId, "cycle", null);
            needFolder = testIssueFolderService.insert(needTestIssueFolderDTO);
        }
        //根据projectId查找version
        for (IssueProjectDTO issueProjectDTO : issueProjectDTOS) {
            Long[] versionIds = testCaseService.getVersionIds(issueProjectDTO.getProjectId());
            if (ObjectUtils.isEmpty(versionIds) || ObjectUtils.isEmpty(versionIdsUnderProject)) {
                //无version的全删除掉
                testCaseService.batchDeleteIssues(issueProjectDTO.getProjectId(), issueProjectDTO.getIssueIdList());
            } else {
                //有version的将他们放到目标文件夹
                List<TestIssueFolderRelDTO> testIssueFolderRelDTOS = new ArrayList<>();
                for (Long issueId : issueProjectDTO.getIssueIdList()) {
                    if(needFolder!=null) {
                        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO(needFolder.getFolderId(), needFolder.getVersionId(), projectId, issueId, null);
                        testIssueFolderRelDTOS.add(testIssueFolderRelDTO);
                    }
                }
                testIssueFolderRelService.insertBatchRelationship(projectId,testIssueFolderRelDTOS);
            }
        }


        List<TestCycleE> testCycleES = testCycleE.queryAll();
        //用于设置IssueFolder
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        //修正所有的cycle数据
        for (TestCycleE resTestCycleE : testCycleES) {
            Long tempCycleId = resTestCycleE.getCycleId();
            //设置修正数据
            TestCycleE needTestCycleE;
            if (resTestCycleE.getType().equals("folder")) {
                //TestCycleE的type为folder的情况
                needTestCycleE = resTestCycleE;
                testIssueFolderDTO.setType(CYCLE);
            } else {
                if (resTestCycleE.getType().equals("temp")) {
                    resTestCycleE.setType("cycle");
                    resTestCycleE.updateSelf();
                }
                resTestCycleE.setType("folder");
                resTestCycleE.setParentCycleId(resTestCycleE.getCycleId());
                resTestCycleE.setCycleId(null);
                //如果是cycle或者temp类型就新增一个cycle为其子folder
                needTestCycleE = resTestCycleE.addSelf();
                needTestCycleE.setObjectVersionNumber(1L);
                testIssueFolderDTO.setType(CYCLE);
            }

            Long needProjectId = testCaseService.queryProjectIdByVersionId(needTestCycleE.getVersionId());
            testIssueFolderDTO.setProjectId(needProjectId);
            testIssueFolderDTO.setVersionId(needTestCycleE.getVersionId());

            //如果有父节点的话，将folder的名字设置为如：父名称_子名称
            if (needTestCycleE.getParentCycleId() != null) {
                testCycleE.setCycleId(needTestCycleE.getParentCycleId());
                TestCycleE fatherCycleE = testCycleE.queryOne();
                //如果父名字和子名字是相同的就说明这个名字是唯一的只需要给folder设置此名即可，不需要加 _
                if (!fatherCycleE.getCycleName().equals(needTestCycleE.getCycleName())) {
                    testIssueFolderDTO.setName(fatherCycleE.getCycleName() + "_" + needTestCycleE.getCycleName());
                } else {
                    testIssueFolderDTO.setName(needTestCycleE.getCycleName());
                }
                needTestCycleE.setVersionId(fatherCycleE.getVersionId());
                testIssueFolderDTO.setVersionId(fatherCycleE.getVersionId());
            }

            //插入folder表，更新cycle表
            Long folderId = testIssueFolderService.insert(testIssueFolderDTO).getFolderId();
            needTestCycleE.setFolderId(folderId);
            needTestCycleE.updateSelf();

            //查询原来的cycle在cycleCase表中的数据
            testCycleCaseE.setCycleId(tempCycleId);
            List<TestCycleCaseE> testCycleCaseES = testCycleCaseE.querySelf();

            //用于设置folderRel数据
            List<TestIssueFolderRelDTO> testIssueFolderRelDTOS = new ArrayList<>();
            //没有case存在的话就不进行cyclecase，各个step和folderRel表的操作
            if (ObjectUtils.isEmpty(testCycleCaseES)) {
                continue;
            }
            List<Long> issueIds = testCaseService.batchCloneIssue(needProjectId, needTestCycleE.getVersionId(), testCycleCaseES.stream().map(TestCycleCaseE::getIssueId).toArray(Long[]::new));

            //将原来的case关联的issue改成新克隆出来的issue，并修改各个step关系
            int i = 0;
            for (TestCycleCaseE cycleCaseE : testCycleCaseES) {
                //根据以前的issueId找到case_step
                TestCaseStepE testCaseStepE = TestCaseStepEFactory.create();
                testCaseStepE.setIssueId(cycleCaseE.getIssueId());
                List<TestCaseStepE> oldCaseSteps = testCaseStepE.queryByParameter();

                cycleCaseE.setIssueId(issueIds.get(i));
                cycleCaseE.updateSelf();

                //根据issueId克隆caseStep
                TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();
                testCaseStepDTO.setIssueId(cycleCaseE.getIssueId());
                List<TestCaseStepDTO> clonedCaseStepDTO = testCaseStepService.batchClone(testCaseStepDTO, issueIds.get(i++), needProjectId);

                List<TestCycleCaseStepE> cycleCaseStepES = new ArrayList<>();
                TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
                int j = 0;
                for (TestCaseStepE v : oldCaseSteps) {
                    //根据以前的case_step去将cycle_case_step更新为前面克隆得到的step
                    //查找以前stepId对应的CycleCaseStep
                    testCycleCaseStepE.setStepId(v.getStepId());
                    List<TestCycleCaseStepE> testCycleCaseStepES = testCycleCaseStepE.querySelf();
                    //将CycleCaseStep对应的stepId修改为新克隆出来的stepId
                    for (TestCycleCaseStepE cs : testCycleCaseStepES) {
                        if (!ObjectUtils.isEmpty(clonedCaseStepDTO)) {
                            cs.setStepId(clonedCaseStepDTO.get(j).getStepId());
                            cycleCaseStepES.add(cs);
                        }
                    }
                    j++;
                }
                testCycleCaseStepService.update(ConvertHelper.convertList(cycleCaseStepES, TestCycleCaseStepDTO.class));
            }

            issueIds.forEach(v -> {
                TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
                testIssueFolderRelDTO.setFolderId(folderId);
                testIssueFolderRelDTO.setProjectId(needProjectId);
                testIssueFolderRelDTO.setVersionId(needTestCycleE.getVersionId());
                testIssueFolderRelDTO.setIssueId(v);
                testIssueFolderRelDTOS.add(testIssueFolderRelDTO);
            });
            testIssueFolderRelService.insertBatchRelationship(needProjectId, testIssueFolderRelDTOS);
        }
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

    private JSONObject createCycle(TestCycleDTO testCycleDTO, String height) {
        JSONObject version = new JSONObject();
        version.put("title", testCycleDTO.getCycleName());
        version.put("environment", testCycleDTO.getEnvironment());
        version.put("description", testCycleDTO.getDescription());
        version.put("build", testCycleDTO.getBuild());
        version.put("type", testCycleDTO.getType());
        version.put("versionId", testCycleDTO.getVersionId());
        version.put("cycleId", testCycleDTO.getCycleId());
        Optional.ofNullable(testCycleDTO.getCreatedUser()).ifPresent(v ->
                version.put("createdUser", JSONObject.toJSON(v))
        );
        version.put("folderId", testCycleDTO.getFolderId());
        version.put("toDate", testCycleDTO.getToDate());
        version.put("fromDate", testCycleDTO.getFromDate());
        version.put("cycleCaseList", testCycleDTO.getCycleCaseList());
        version.put("objectVersionNumber", testCycleDTO.getObjectVersionNumber());
        version.put("key", height);
        JSONArray versionNames = new JSONArray();
        version.put(NODE_CHILDREN, versionNames);
        return version;
    }


    private void initCycleTree(JSONArray cycles, String height, Long versionId, List<TestCycleDTO> cycleDTOList) {

        cycleDTOList.stream().filter(cycleDTO -> cycleDTO.getVersionId().equals(versionId) && StringUtils.equals(cycleDTO.getType(), TestCycleE.CYCLE))
                .forEach(v -> {
                    JSONObject cycle = createCycle(v, height + "-" + cycles.size());
                    cycles.add(cycle);
                    initCycleFolderTree(cycle.getJSONArray(NODE_CHILDREN), cycle.get("key").toString(), v.getCycleId(), cycleDTOList);
                });

        cycleDTOList.stream().filter(cycleDTO -> cycleDTO.getVersionId().equals(versionId) && StringUtils.equals(cycleDTO.getType(), TestCycleE.TEMP))
                .forEach(v -> {
                    JSONObject cycle = createCycle(v, height + "-" + cycles.size());
                    cycles.add(cycle);
                });
    }


    private void initCycleFolderTree(JSONArray folders, String height, Long parentId, List<TestCycleDTO> cycleDTOList) {
        cycleDTOList.stream().filter(v -> parentId.equals(v.getParentCycleId())).forEach(u ->
                folders.add(createCycle(u, height + "-" + folders.size()))
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
    public void initOldData(Long projectId) {
        ResponseEntity<List<Long>> entityVersionIds = productionVersionClient.listAllVersionId(projectId);
        List<Long> versionIds = entityVersionIds.getBody();
        for (Long versionId : versionIds) {
            TestCycleDTO testCycleDTO = new TestCycleDTO();
            testCycleDTO.setVersionId(versionId);
            testCycleDTO.setType(TestCycleE.TEMP);
            testCycleDTO.setCycleName("临时");
            TestCycleE cycleE = ConvertHelper.convert(testCycleDTO, TestCycleE.class);
            if (cycleE.queryOne() == null) {
                cycleE.setCycleName(TestCycleE.TEMP_CYCLE_NAME);
                cycleE.addSelf();
            }
        }
    }

    @Override
    public void populateVersion(TestCycleDTO cycle, Long projectId) {
        Map<Long, ProductVersionDTO> map = testCaseService.getVersionInfo(projectId);
        cycle.setVersionName(map.get(cycle.getVersionId()).getName());
        cycle.setVersionStatusName(map.get(cycle.getVersionId()).getStatusName());
    }
}
