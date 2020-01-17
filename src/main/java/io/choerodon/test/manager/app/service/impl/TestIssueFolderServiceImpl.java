package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.choerodon.test.manager.api.vo.event.ProjectEvent;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
public class TestIssueFolderServiceImpl implements TestIssueFolderService {

    public static final String TYPE_CYCLE = "cycle";
    public static final String TYPE_TEMP = "temp";

    private TestCaseService testCaseService;
    private TestIssueFolderMapper testIssueFolderMapper;
    private ModelMapper modelMapper;

    public TestIssueFolderServiceImpl(TestCaseService testCaseService,
                                      TestIssueFolderMapper testIssueFolderMapper,
                                      ModelMapper modelMapper) {
        this.testCaseService = testCaseService;
        this.testIssueFolderMapper = testIssueFolderMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public TestTreeIssueFolderVO queryTreeFolder(Long projectId) {
        List<TestIssueFolderDTO> testIssueFolderDTOList = testIssueFolderMapper.selectListByProjectId(projectId);
        //根目录
        List<Long> rootFolderId = testIssueFolderDTOList.stream().filter(IssueFolder ->
                IssueFolder.getParentId() == 0).map(TestIssueFolderDTO::getFolderId).collect(Collectors.toList());

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
            Boolean hasCase = testIssueFolderDTO.getCaseCount()==0 ? Boolean.FALSE : Boolean.TRUE;
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
        List<TestCaseDTO> testCaseDTOS = testCaseService.listCaseByFolderId(testIssueFolderVO.getParentId());
        if (!CollectionUtils.isEmpty(testCaseDTOS)) {
            throw new CommonException("error.issueFolder.has.case");
        }
        if (testIssueFolderVO.getFolderId() != null) {
            throw new CommonException("error.issue.folder.insert.folderId.should.be.null");
        }
        testIssueFolderVO.setProjectId(projectId);
        TestIssueFolderDTO testIssueFolderDTO = modelMapper.map(testIssueFolderVO, TestIssueFolderDTO.class);
        testIssueFolderDTO.setRank(RankUtil.Operation.INSERT.getRank(null,testIssueFolderMapper.projectLastRank(projectId)));
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
    public String moveFolder(Long projectId, Long targetForderId, TestIssueFolderVO issueFolderVO) {
        List<TestCaseDTO> testCaseDTOS = testCaseService.listCaseByFolderId(targetForderId);
        if (!CollectionUtils.isEmpty(testCaseDTOS)) {
            throw new CommonException("error.issueFolder.has.case");
        }
        TestIssueFolderDTO testIssueFolderDTO = testIssueFolderMapper.selectByPrimaryKey(issueFolderVO.getFolderId());
        testIssueFolderDTO.setParentId(targetForderId);
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
    public List<TestIssueFolderDTO> listFolderByFolderIds(Long projectId,List<Long> folderIds) {
        List<TestIssueFolderDTO> testIssueFolderDTOS = testIssueFolderMapper.selectListByProjectId(projectId);
        Map<Long, TestIssueFolderDTO> allFolderMap = testIssueFolderDTOS.stream()
                .map(v -> {
                    if(ObjectUtils.isEmpty(v.getParentId())){
                        v.setParentId(v.getParentId());
                    }
                    return v;
                }).collect(Collectors.toMap(TestIssueFolderDTO::getFolderId, Function.identity()));
        Map<Long,TestIssueFolderDTO> map = new TreeMap<>();
        folderIds.forEach(v -> bulidFolder(v,map,allFolderMap));
        List<TestIssueFolderDTO> collect = map.values().stream().collect(Collectors.toList());
        return collect;
    }

    @Override
    public void initializationFolderInfo(ProjectEvent projectEvent) {
        TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();
        testIssueFolderVO.setName(projectEvent.getProjectName());
        testIssueFolderVO.setParentId(0L);
        testIssueFolderVO.setVersionId(0L);
        testIssueFolderVO.setType(TYPE_CYCLE);
        create(projectEvent.getProjectId(),testIssueFolderVO);
    }

    @Override
    public List<TestIssueFolderDTO> listByProject(Long projectId) {
        List<TestIssueFolderDTO> testIssueFolderDTOS = testIssueFolderMapper.selectListByProjectId(projectId);
        if (CollectionUtils.isEmpty(testIssueFolderDTOS)) {
            return new ArrayList<>();
        }
        return testIssueFolderDTOS;
    }

    private void bulidFolder(Long folderId, Map<Long, TestIssueFolderDTO> map, Map<Long, TestIssueFolderDTO> allFolderMap) {
        TestIssueFolderDTO testIssueFolderDTO = map.get(folderId);
        if(ObjectUtils.isEmpty(testIssueFolderDTO)){
           TestIssueFolderDTO testIssueFolder = allFolderMap.get(folderId);
           if(!ObjectUtils.isEmpty(testIssueFolder)){
               map.put(folderId,testIssueFolder);
               if(testIssueFolder.getParentId() != 0){
                   bulidFolder(testIssueFolder.getParentId(),map,allFolderMap);
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
