package io.choerodon.test.manager.app.service.impl;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.vo.TestCaseRepVO;
import io.choerodon.test.manager.api.vo.event.ProjectEvent;
import io.choerodon.test.manager.infra.constant.SagaTaskCodeConstants;
import io.choerodon.test.manager.infra.constant.SagaTopicCodeConstants;
import io.choerodon.test.manager.infra.enums.TestPlanInitStatus;
import io.choerodon.test.manager.infra.mapper.TestCaseMapper;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.MessageClient;
import org.hzero.core.base.AopProxy;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import io.choerodon.test.manager.infra.util.RankUtil;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
import io.choerodon.test.manager.api.vo.TestTreeFolderVO;
import io.choerodon.test.manager.api.vo.TestTreeIssueFolderVO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestIssueFolderService;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.infra.exception.IssueFolderException;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper;


@Service
@Transactional(rollbackFor = Exception.class)
public class TestIssueFolderServiceImpl implements TestIssueFolderService, AopProxy<TestIssueFolderService> {

    public static final Logger log = LoggerFactory.getLogger(TestIssueFolderServiceImpl.class);

    public static final String TYPE_CYCLE = "cycle";
    public static final String TYPE_TEMP = "temp";
    private static final String API_TYPE = "api";


    private TestCaseService testCaseService;
    private TestIssueFolderMapper testIssueFolderMapper;
    private ModelMapper modelMapper;
    private TestCaseMapper testCaseMapper;
    private MessageClient messageClient;
    private TransactionalProducer producer;
    private ObjectMapper objectMapper;

    public TestIssueFolderServiceImpl(TestCaseService testCaseService,
                                      TestIssueFolderMapper testIssueFolderMapper,
                                      ModelMapper modelMapper, TestCaseMapper testCaseMapper,
                                      MessageClient messageClient, TransactionalProducer producer,
                                      ObjectMapper objectMapper) {
        this.testCaseService = testCaseService;
        this.testIssueFolderMapper = testIssueFolderMapper;
        this.modelMapper = modelMapper;
        this.testCaseMapper = testCaseMapper;
        this.messageClient = messageClient;
        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    @Override
    public TestTreeIssueFolderVO queryTreeFolder(Long projectId) {
        List<TestIssueFolderDTO> testIssueFolderDTOList = testIssueFolderMapper.selectListByProjectId(projectId);
        //根目录
        List<Long> rootFolderId = testIssueFolderDTOList.stream().filter(issueFolder ->
                issueFolder.getParentId() == 0).map(TestIssueFolderDTO::getFolderId).collect(Collectors.toList());

        List<TestTreeFolderVO> list = new ArrayList<>();
        testIssueFolderDTOList.forEach(testIssueFolderDTO -> {
            TestTreeFolderVO folderVO = new TestTreeFolderVO();
            List<Long> childrenIds = testIssueFolderDTOList.stream().filter(e -> e.getParentId().equals(testIssueFolderDTO.getFolderId()))
                    .map(TestIssueFolderDTO::getFolderId).collect(Collectors.toList());
            folderVO.setId(testIssueFolderDTO.getFolderId());
            folderVO.setIssueFolderVO(modelMapper.map(testIssueFolderDTO, TestIssueFolderVO.class));
            folderVO.setExpanded(false);
            folderVO.setChildrenLoading(false);
            folderVO.setCaseCount(testIssueFolderDTO.getCaseCount());
            // 判断是否有case
            Boolean hasCase = testIssueFolderDTO.getCaseCount() == 0 ? Boolean.FALSE : Boolean.TRUE;
            folderVO.setHasCase(hasCase);
            if (CollectionUtils.isEmpty(childrenIds)) {
                folderVO.setHasChildren(false);
                folderVO.setChildren(childrenIds);
            } else {
                folderVO.setChildren(childrenIds);
                folderVO.setHasChildren(true);
            }
            list.add(folderVO);
        });
        return new TestTreeIssueFolderVO(rootFolderId, list);
    }

    @Override
    public TestIssueFolderVO create(Long projectId, TestIssueFolderVO testIssueFolderVO) {
        validateType(testIssueFolderVO);
        Boolean isRootNode = testIssueFolderVO.getRootNode();
        if (Boolean.TRUE.equals(isRootNode)) {
            testIssueFolderVO.setParentId(0L);
        }
        List<TestCaseDTO> testCaseDTOS = testCaseService.listCaseByFolderId(testIssueFolderVO.getParentId());
        if (!CollectionUtils.isEmpty(testCaseDTOS)) {
            throw new CommonException("error.issueFolder.has.case");
        }
        if (testIssueFolderVO.getFolderId() != null) {
            throw new CommonException("error.issue.folder.insert.folderId.should.be.null");
        }
        testIssueFolderVO.setProjectId(projectId);
        TestIssueFolderDTO testIssueFolderDTO = modelMapper.map(testIssueFolderVO, TestIssueFolderDTO.class);
        testIssueFolderDTO.setRank(RankUtil.Operation.INSERT.getRank(null, testIssueFolderMapper.projectLastRank(projectId)));
        if (testIssueFolderMapper.insert(testIssueFolderDTO) != 1) {
            throw new CommonException("error.issueFolder.insert");
        }
        return modelMapper.map(testIssueFolderMapper.selectByPrimaryKey(testIssueFolderDTO.getFolderId()), TestIssueFolderVO.class);
    }

    @Override
    @Async
    public void delete(Long projectId, Long folderId) {
        List<Long> caseIdList = testCaseService.listAllCaseByFolderId(projectId, folderId);
        Set<TestIssueFolderDTO> folderDTOSet = new HashSet<>();
        folderDTOSet.add(testIssueFolderMapper.selectByPrimaryKey(folderId));
        Set<TestIssueFolderDTO> testIssueFolderDTOS = findchildFolder(folderId, folderDTOSet);
        //删除文件夹下用例
        if (!CollectionUtils.isEmpty(caseIdList)) {
            caseIdList.forEach(caseId -> testCaseService.deleteCase(projectId, caseId));
        }
        //删除文件夹
        testIssueFolderDTOS.forEach(e -> testIssueFolderMapper.delete(modelMapper.map(e, TestIssueFolderDTO.class)));
    }

    @Override
    public TestIssueFolderVO update(TestIssueFolderVO testIssueFolderVO) {
        validateType(testIssueFolderVO);
        TestIssueFolderDTO testIssueFolderDTO = modelMapper.map(testIssueFolderVO, TestIssueFolderDTO.class);
        if (testIssueFolderMapper.updateByPrimaryKeySelective(testIssueFolderDTO) != 1) {
            throw new IssueFolderException(IssueFolderException.ERROR_UPDATE, testIssueFolderDTO.toString());
        }
        return modelMapper.map(testIssueFolderMapper.selectByPrimaryKey(testIssueFolderDTO.getFolderId()), TestIssueFolderVO.class);
    }


    @Override
    public String moveFolder(Long projectId, Long targetFolderId, TestIssueFolderVO issueFolderVO) {
        //移动到根目录下，targetFolderId为空，后端设置为0
        if (ObjectUtils.isEmpty(targetFolderId)) {
            targetFolderId = 0L;
        }
        List<TestCaseDTO> testCaseDTOS = testCaseService.listCaseByFolderId(targetFolderId);
        if (!CollectionUtils.isEmpty(testCaseDTOS)) {
            throw new CommonException("error.issueFolder.has.case");
        }
        TestIssueFolderDTO testIssueFolderDTO = testIssueFolderMapper.selectByPrimaryKey(issueFolderVO.getFolderId());
        testIssueFolderDTO.setParentId(targetFolderId);
        if (ObjectUtils.isEmpty(issueFolderVO.getLastRank()) && ObjectUtils.isEmpty(issueFolderVO.getNextRank())) {
            testIssueFolderDTO.setRank(RankUtil.Operation.INSERT.getRank(issueFolderVO.getLastRank(), issueFolderVO.getNextRank()));
        } else {
            testIssueFolderDTO.setRank(RankUtil.Operation.UPDATE.getRank(issueFolderVO.getLastRank(), issueFolderVO.getNextRank()));
        }
        if (testIssueFolderMapper.updateByPrimaryKeySelective(testIssueFolderDTO) != 1) {
            throw new IssueFolderException(IssueFolderException.ERROR_UPDATE, testIssueFolderDTO.toString());
        }
        return testIssueFolderDTO.getRank();
    }

    private void validateType(TestIssueFolderVO testIssueFolderVO) {
        if (!(StringUtils.equals(testIssueFolderVO.getType(), TYPE_CYCLE) || StringUtils.equals(testIssueFolderVO.getType(), TYPE_TEMP))) {
            throw new IssueFolderException(IssueFolderException.ERROR_FOLDER_TYPE);
        }
    }

    @Override
    public List<TestIssueFolderDTO> queryChildFolder(Long folderId) {
        List<TestIssueFolderDTO> folders = new ArrayList<>();
        return recurisionQuery(folderId, folders);
    }

    @Override
    public List<Long> queryProjectIdList() {
        return testIssueFolderMapper.selectProjectIdList();
    }

    @Override
    public List<TestIssueFolderDTO> listFolderByFolderIds(Long projectId, List<Long> folderIds) {
        List<TestIssueFolderDTO> testIssueFolderDTOS = testIssueFolderMapper.selectListByProjectId(projectId);
        Map<Long, TestIssueFolderDTO> allFolderMap = testIssueFolderDTOS.stream()
                .map(v -> {
                    if (ObjectUtils.isEmpty(v.getParentId())) {
                        v.setParentId(v.getParentId());
                    }
                    return v;
                }).collect(Collectors.toMap(TestIssueFolderDTO::getFolderId, Function.identity()));
        Map<Long, TestIssueFolderDTO> map = new TreeMap<>();
        folderIds.forEach(v -> bulidFolder(v, map, allFolderMap));
        return new ArrayList<>(map.values());
    }

    @Override
    public void initializationFolderInfo(ProjectEvent projectEvent) {
        TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();
        testIssueFolderVO.setName(projectEvent.getProjectName());
        testIssueFolderVO.setParentId(0L);
        testIssueFolderVO.setVersionId(0L);
        testIssueFolderVO.setType(TYPE_CYCLE);
        create(projectEvent.getProjectId(), testIssueFolderVO);
    }

    @Override
    public List<TestIssueFolderDTO> listByProject(Long projectId) {
        List<TestIssueFolderDTO> testIssueFolderDTOS = testIssueFolderMapper.selectListByProjectId(projectId);
        if (CollectionUtils.isEmpty(testIssueFolderDTOS)) {
            return new ArrayList<>();
        }
        return testIssueFolderDTOS;
    }

    @Override
    @Saga(code = SagaTopicCodeConstants.TEST_MANAGER_CLONE_TEST_ISSUE_FOLDER,
            description = "test-manager 复制用例文件夹", inputSchema = "{}")
    public TestIssueFolderDTO cloneFolder(Long projectId, Long folderId) {
        TestIssueFolderDTO newFolder = this.self().cloneCurrentFolder(projectId, folderId);
        producer.apply(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("")
                        .withSagaCode(SagaTopicCodeConstants.TEST_MANAGER_CLONE_TEST_ISSUE_FOLDER)
                        .withPayloadAndSerialize(newFolder)
                        .withRefId("")
                        .withSourceId(projectId),
                builder -> {
                });
        return newFolder;
    }

    @Override
    public TestIssueFolderDTO cloneCurrentFolder(Long projectId, Long folderId) {
        // 检查当前文件夹是否存在
        TestIssueFolderDTO folder = new TestIssueFolderDTO();
        folder.setProjectId(projectId);
        folder.setFolderId(folderId);
        TestIssueFolderDTO folderDTO = testIssueFolderMapper.selectOne(folder);
        if (Objects.isNull(folderDTO)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        // 复制当前文件夹
        TestIssueFolderDTO newFolder = new TestIssueFolderDTO(folderDTO, folderDTO.getParentId(), 0L);
        newFolder.setName(newFolder.getName() + "-副本");
        newFolder.setRank(RankUtil.genNext(newFolder.getRank()));
        newFolder.setInitStatus(TestPlanInitStatus.CREATING);
        testIssueFolderMapper.insertSelective(newFolder);
        newFolder.setOldFolderId(folderId);
        return newFolder;
    }

    @Override
    @SagaTask(code = SagaTaskCodeConstants.TEST_MANAGER_CLONE_TEST_ISSUE_FOLDER_TASK, description = "复制用例文件夹",
            sagaCode = SagaTopicCodeConstants.TEST_MANAGER_CLONE_TEST_ISSUE_FOLDER, seq = 1)
    public void wrapCloneFolder(String payload){
        // 读取payload
        TestIssueFolderDTO newFolder = null;
        Long userId = DetailsHelper.getUserDetails().getUserId();
        try {
            newFolder = objectMapper.readValue(payload, TestIssueFolderDTO.class);
            Assert.notNull(newFolder, BaseConstants.ErrorCode.DATA_INVALID);
        } catch (IOException e) {
            log.error("[{}] payload convert failed, e.message: [{}], trace: [{}]",
                    SagaTaskCodeConstants.TEST_MANAGER_CLONE_TEST_ISSUE_FOLDER_TASK, e.getMessage(), e.getStackTrace());
        }
        // 复制子文件夹
        try {
            this.cloneChildrenFolderAndCase(newFolder.getProjectId(), newFolder);
            newFolder.setInitStatus(TestPlanInitStatus.SUCCESS);
            testIssueFolderMapper.updateOptional(newFolder, "initStatus");
            messageClient.sendByUserId(userId, TestIssueFolderDTO.MESSAGE_COPY_TEST_FOLDER, BaseConstants.FIELD_SUCCESS);
        }catch (Exception e){
            newFolder.setInitStatus(TestPlanInitStatus.FAIL);
            testIssueFolderMapper.updateOptional(newFolder, "initStatus");
            log.error("case folder clone field, e.message: [{}], trace: [{}]", e.getMessage(), e.getStackTrace());
            messageClient.sendByUserId(userId, TestIssueFolderDTO.MESSAGE_COPY_TEST_FOLDER, BaseConstants.FIELD_FAILED);
        }
    }

    @Override
    public void cloneChildrenFolderAndCase(Long projectId, TestIssueFolderDTO newFolder) {
        // 查询文件夹下所有的目录
        Set<Long> folderIdSet = testCaseService.selectFolderIds(projectId, newFolder.getOldFolderId());
        folderIdSet.remove(newFolder.getOldFolderId());
        // 复制文件夹
        Map<Long, Long> oldNewMap = cloneChildrenFolder(newFolder, folderIdSet);
        // 复制用例
        cloneChildrenCase(projectId, newFolder, folderIdSet, oldNewMap);

    }

    private void cloneChildrenCase(Long projectId, TestIssueFolderDTO newFolder, Set<Long> folderIdSet, Map<Long, Long> oldNewMap) {
        List<TestCaseDTO> testCaseList = testCaseMapper.selectByCondition(Condition.builder(TestCaseDTO.class)
                .andWhere(Sqls.custom().andIn(TestCaseDTO.FIELD_FOLDER_ID,
                        CollectionUtils.isEmpty(folderIdSet) ? Collections.singleton(newFolder.getOldFolderId()) : folderIdSet)).build());
        if (CollectionUtils.isEmpty(testCaseList)) {
            return;
        }
        Map<Long, List<TestCaseDTO>> folderMap =
                testCaseList.stream().collect(Collectors.groupingBy(TestCaseDTO::getFolderId));
        for (Map.Entry<Long, List<TestCaseDTO>> entry : folderMap.entrySet()) {
            if (Objects.isNull(oldNewMap.get(entry.getKey()))) {
                continue;
            }
            testCaseService.batchCopy(projectId, oldNewMap.get(entry.getKey()), entry.getValue().stream().map(caseDTO -> {
                TestCaseRepVO rep = new TestCaseRepVO();
                rep.setCaseId(caseDTO.getCaseId());
                return rep;
            }).collect(Collectors.toList()));
        }
    }

    private Map<Long, Long> cloneChildrenFolder(TestIssueFolderDTO newFolder, Set<Long> folderIdSet) {
        Map<Long, Long> oldNewMap = new HashMap<>();
        oldNewMap.put(newFolder.getOldFolderId(), newFolder.getFolderId());
        if (CollectionUtils.isNotEmpty(folderIdSet)) {
            List<TestIssueFolderDTO> folderList;
            folderList = testIssueFolderMapper.selectByIds(StringUtils.join(folderIdSet, BaseConstants.Symbol.COMMA));
            Map<Long, List<TestIssueFolderDTO>> parentMap =
                    folderList.stream().collect(Collectors.groupingBy(TestIssueFolderDTO::getParentId));
            boolean breakFlag = false;
            Iterator<Map.Entry<Long, List<TestIssueFolderDTO>>> iterator;
            while (MapUtils.isNotEmpty(parentMap)) {
                if (breakFlag) {
                    break;
                }
                breakFlag = true;
                iterator = parentMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Long, List<TestIssueFolderDTO>> temp = iterator.next();
                    if (Objects.isNull(oldNewMap.get(temp.getKey()))) {
                        continue;
                    }
                    breakFlag = false;
                    temp.getValue().stream().map(folder ->
                            new TestIssueFolderDTO(folder, oldNewMap.get(temp.getKey()), 0L))
                            .forEach(folderDTO -> {
                                testIssueFolderMapper.insertSelective(folderDTO);
                                oldNewMap.put(folderDTO.getOldFolderId(), folderDTO.getFolderId());
                            });
                    iterator.remove();
                }
            }
        }
        return oldNewMap;
    }

    private void bulidFolder(Long folderId, Map<Long, TestIssueFolderDTO> map, Map<Long, TestIssueFolderDTO> allFolderMap) {
        TestIssueFolderDTO testIssueFolderDTO = map.get(folderId);
        if (ObjectUtils.isEmpty(testIssueFolderDTO)) {
            TestIssueFolderDTO testIssueFolder = allFolderMap.get(folderId);
            if (!ObjectUtils.isEmpty(testIssueFolder)) {
                map.put(folderId, testIssueFolder);
                if (testIssueFolder.getParentId() != 0) {
                    bulidFolder(testIssueFolder.getParentId(), map, allFolderMap);
                }
            }
        }
    }

    // 递归查询最底层文件夹
    private List<TestIssueFolderDTO> recurisionQuery(Long parentId, List<TestIssueFolderDTO> folders) {
        List<TestIssueFolderDTO> tmpList = testIssueFolderMapper.selectChildrenByParentId(parentId);
        if (CollectionUtils.isEmpty(tmpList)) {
            folders.add(testIssueFolderMapper.selectByPrimaryKey(parentId));
            return folders;
        } else {
            for (TestIssueFolderDTO tmp : tmpList) {
                recurisionQuery(tmp.getFolderId(), folders);
            }
        }
        return folders;
    }

    // 递归查询子文件夹
    private Set<TestIssueFolderDTO> findchildFolder(Long parentId, Set<TestIssueFolderDTO> folders) {
        List<TestIssueFolderDTO> tmpList = testIssueFolderMapper.selectChildrenByParentId(parentId);
        if (CollectionUtils.isEmpty(tmpList)) {
            return folders;
        } else {
            folders.addAll(tmpList);
            for (TestIssueFolderDTO tmp : tmpList) {
                findchildFolder(tmp.getFolderId(), folders);
            }
        }
        return folders;
    }
}
