package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.agile.api.vo.ProductVersionDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.app.service.TestIssueFolderService;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.infra.exception.IssueFolderException;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
@Component
public class TestIssueFolderServiceImpl implements TestIssueFolderService {

    public static final String TYPE_CYCLE = "cycle";
    public static final String TYPE_TEMP = "temp";

    private Logger logger = LoggerFactory.getLogger(TestIssueFolderServiceImpl.class);
    @Autowired
    private TestCycleService testCycleService;
//    @Autowired
//    private TestIssueFolderRelService testIssueFolderRelService;
    @Autowired
    private TestCaseService testCaseService;
    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductionVersionClient productionVersionClient;
    @Override
    public TestIssueFolderDTO baseInsert(TestIssueFolderDTO insert) {
        if (testIssueFolderMapper.insert(insert) != 1) {
            throw new CommonException("error.issueFolder.insert");
        }

        return testIssueFolderMapper.selectByPrimaryKey(insert.getFolderId());
    }

    @Override
    public List<TestIssueFolderVO> queryByParameter(Long projectId, Long versionId) {
        TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO(null, null, versionId, projectId, null, null);
        return modelMapper.map(testIssueFolderMapper.select(modelMapper.map(testIssueFolderVO, TestIssueFolderDTO.class)), new TypeToken<List<TestIssueFolderVO>>() {
        }.getType());
    }

    @Override
    public List<TestIssueFolderWithVersionNameVO> queryByParameterWithVersionName(Long projectId, Long versionId) {
        TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO(null, null, versionId, projectId, null, null);
        List<TestIssueFolderVO> resultTemp = modelMapper.map(testIssueFolderMapper.select(modelMapper
                .map(testIssueFolderVO, TestIssueFolderDTO.class)), new TypeToken<List<TestIssueFolderVO>>() {
        }.getType());
        List<TestIssueFolderWithVersionNameVO> result = new ArrayList<>();
        String versionName = testCaseService.getVersionInfo(projectId).getOrDefault(versionId,new ProductVersionDTO()).getName();

        resultTemp.forEach(v -> {
            TestIssueFolderWithVersionNameVO t = new TestIssueFolderWithVersionNameVO();
            t.setFolderId(v.getFolderId());
            t.setName(v.getName());
            t.setVersionId(v.getVersionId());
            t.setVersionName(versionName);
            t.setProjectId(v.getProjectId());
            t.setType(v.getType());
            t.setObjectVersionNumber(v.getObjectVersionNumber());
            result.add(t);
        });

        return result;
    }

    @Override
    public TestTreeIssueFolderVO queryTreeFolder(Long projectId) {
        TestIssueFolderVO testIssueFolder = new TestIssueFolderVO(null, null, null, projectId, null, null);
        List<TestIssueFolderVO> testIssueFolderVOS = modelMapper.map(testIssueFolderMapper.select(modelMapper
                .map(testIssueFolder, TestIssueFolderDTO.class)), new TypeToken<List<TestIssueFolderVO>>() {
        }.getType());

        //根目录
        List<Long> rootFolderId = testIssueFolderVOS.stream().filter(IssueFolder -> IssueFolder.getParentId() == 0).map(TestIssueFolderVO::getFolderId).collect(Collectors.toList());

        List<TestTreeFolderVO> list = new ArrayList<>();
        testIssueFolderVOS.forEach(testIssueFolderVO -> {
            TestTreeFolderVO folderVO = new TestTreeFolderVO();
            List<TestIssueFolderDTO> testIssueFolderDTOS = testIssueFolderMapper.selectChildrenByParentId(testIssueFolderVO.getFolderId());
//            List<TestIssueFolderVO> collect = testIssueFolderVOS.stream().filter(issueFolderVO -> issueFolderVO.getParentId() ==folderId ).collect(Collectors.toList());
            List<Long> ids = new ArrayList<>();
            if(testIssueFolderDTOS!=null){
                 ids = testIssueFolderDTOS.stream().map(TestIssueFolderDTO::getFolderId).collect(Collectors.toList());
            }

            folderVO.setId(testIssueFolderVO.getFolderId());
            folderVO.setIssueFolderVO(testIssueFolderVO);
            folderVO.setExpanded(false);
            folderVO.setChildrenLoading(false);
            if (CollectionUtils.isEmpty(ids)) {
                folderVO.setHasChildren(false);
                folderVO.setChildren(ids);
            } else {
                folderVO.setChildren(ids);
                folderVO.setHasChildren(true);
            }
            list.add(folderVO);
        });
        List<TestTreeFolderVO> treeFolderVOS = list.stream().sorted(Comparator.comparing(TestTreeFolderVO::getId)).collect(Collectors.toList());
        return new TestTreeIssueFolderVO(rootFolderId, treeFolderVOS);
    }

    @Override
    @Transactional
    @Async
    public void fixVersionFolder() {
        TestIssueFolderVO testIssueFolder = new TestIssueFolderVO();
        List<TestIssueFolderVO> testIssueFolderVOS = modelMapper.map(testIssueFolderMapper.select(modelMapper
                .map(testIssueFolder, TestIssueFolderDTO.class)), new TypeToken<List<TestIssueFolderVO>>() {
        }.getType());
        Set<Long> projectFolderIds = testIssueFolderVOS.stream().map(TestIssueFolderVO::getProjectId).collect(Collectors.toSet());
        projectFolderIds.forEach(projectFolderId -> {
            List<ProductVersionDTO> productVersionDTOList = productionVersionClient.listByProjectId(projectFolderId).getBody();
            Map<Long, String> versionNameMap = productVersionDTOList.stream().filter(e->e.getName()!=null).collect(Collectors.toMap(ProductVersionDTO::getVersionId, ProductVersionDTO::getName));
//            List<TestIssueFolderVO> testIssueProjectFolderVOs = testIssueFolderVOS.stream().filter(testIssueFolderVO -> testIssueFolderVO.getProjectId() == projectFolderId).collect(Collectors.toList());
            TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
            testIssueFolderDTO.setProjectId(projectFolderId);
            List<TestIssueFolderVO> testIssueProjectFolderVOs = modelMapper.map(testIssueFolderMapper.select(modelMapper
                    .map(testIssueFolderDTO, TestIssueFolderDTO.class)), new TypeToken<List<TestIssueFolderVO>>() {
            }.getType());
            //以version区分
            Map<Long, List<TestIssueFolderVO>> projectVersionFolderVOs = testIssueProjectFolderVOs.stream().filter(e -> e.getVersionId() != null).collect(Collectors.groupingBy(TestIssueFolderVO::getVersionId));
            for (Map.Entry<Long, List<TestIssueFolderVO>> entry : projectVersionFolderVOs.entrySet()) {
                //1.创建版本文件目录
                String folderName = versionNameMap.get(entry.getKey());
                if(!StringUtils.isEmpty(folderName)){
                    TestIssueFolderVO newFolderVO = new TestIssueFolderVO();
                    newFolderVO.setName(folderName);
                    newFolderVO.setParentId(0L);
                    newFolderVO.setProjectId(projectFolderId);
                    newFolderVO.setType("cycle");
                    TestIssueFolderVO testIssueFolderVO = create(projectFolderId, newFolderVO);
                    //2.更新二级目录
                    if (!CollectionUtils.isEmpty(entry.getValue())) {
                        entry.getValue().stream().forEach(folderVO -> {
                            folderVO.setParentId(testIssueFolderVO.getFolderId());
                            update(folderVO);
                        });
                    }
                }
            }
            logger.info("=============================>project:{} copy successed",projectFolderId);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderVO create(Long projectId,TestIssueFolderVO testIssueFolderVO) {
        validateType(testIssueFolderVO);
        List<TestCaseDTO> testCaseDTOS = testCaseService.listCaseByFolderId(testIssueFolderVO.getParentId());
        if(!CollectionUtils.isEmpty(testCaseDTOS)){
            throw new CommonException("error.issueFolder.has.case");
        }
        if (testIssueFolderVO.getFolderId() != null) {
            throw new CommonException("error.issue.folder.insert.folderId.should.be.null");
        }
        return modelMapper.map(this.baseInsert(modelMapper
                .map(testIssueFolderVO, TestIssueFolderDTO.class)), TestIssueFolderVO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long projectId, Long folderId) {
        List<TestIssueFolderDTO> folderList = new ArrayList<>();
        List<TestCaseRepVO> testCaseVOs = testCaseService.listAllCaseByFolderId(projectId, folderId, null, null).getList();
        Set<TestIssueFolderDTO> folderList1 = new HashSet<>();
        Set<TestIssueFolderDTO> testIssueFolderDTOS = findchildFolder(folderId, folderList1);
        //删除文件夹下用例
        if (!CollectionUtils.isEmpty(testCaseVOs)) {
            testCaseVOs.forEach(e -> {
                testCaseService.deleteCase(projectId, e.getCaseId());
            });
        }
        //删除文件夹
        testIssueFolderDTOS.forEach(e -> {
            testIssueFolderMapper.delete(modelMapper.map(e, TestIssueFolderDTO.class));
        });
    }

    @Transactional(rollbackFor = Exception.class)
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
    public JSONObject getTestIssueFolder(Long projectId) {
        TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();
        testIssueFolderVO.setProjectId(projectId);
        List<ProductVersionDTO> versions = testCaseService.getVersionInfo(projectId).values()
                .stream().sorted(Comparator.comparing(ProductVersionDTO::getStatusCode).reversed().thenComparing(ProductVersionDTO::getSequence)).collect(Collectors.toList());

        JSONObject root = new JSONObject();
        if (versions.isEmpty()) {
            root.put("versions", new ArrayList<>());
            return root;
        }

        JSONArray versionStatus = new JSONArray();
        root.put("versions", versionStatus);
        List<TestIssueFolderVO> testIssueFolderVOS = modelMapper.map(testIssueFolderMapper.select(modelMapper
                .map(testIssueFolderVO, TestIssueFolderDTO.class)), new TypeToken<List<TestIssueFolderVO>>() {
        }.getType());
        List<TestCycleVO> cycles = testIssueFolderVOS.stream().map(TestIssueFolderVO::transferToCycle).collect(Collectors.toList());
        testCycleService.initVersionTree(projectId, versionStatus, versions, cycles);
        return root;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long getDefaultFolderId(Long projectId, Long versionId) {
        TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO(null, null, versionId, projectId, "temp", null);
        TestIssueFolderVO resultTestIssueFolderVO = modelMapper.map(testIssueFolderMapper.selectOne(modelMapper
                .map(testIssueFolderVO, TestIssueFolderDTO.class)), TestIssueFolderVO.class);
        testIssueFolderVO.setName("临时");
        if (resultTestIssueFolderVO == null) {
            return create(projectId,testIssueFolderVO).getFolderId();
        } else {
            return resultTestIssueFolderVO.getFolderId();
        }
    }

    /**
     * @param projectId
     * @param targetForderId 要复制到的目标folder
     * @param folderIds 要被复制的源folder
     * @return 被复制成功的目标folder
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void copyFolder(Long projectId, Long targetForderId, Long[] folderIds) {

        TestIssueFolderVO tergetInssueFolderVO = modelMapper.map(testIssueFolderMapper.selectByPrimaryKey(targetForderId), TestIssueFolderVO.class);
        for (Long folderId : folderIds) {
            //通过folder查找
            TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();
            testIssueFolderVO.setFolderId(folderId);
            TestIssueFolderVO resTestIssueFolderVO = modelMapper.map(testIssueFolderMapper.selectByPrimaryKey(folderId), TestIssueFolderVO.class);
            //创建文件夹
            resTestIssueFolderVO.setFolderId(null);
            resTestIssueFolderVO.setVersionId(targetForderId);
            resTestIssueFolderVO.setParentId(tergetInssueFolderVO.getFolderId());
            TestIssueFolderVO returnTestIssueFolderVO = create(projectId,resTestIssueFolderVO);
            //复制issue到目的文件夹
            //todo 复制文件夹下的case到新文件夹
//            List<TestCaseRepVO> testCaseRepVOS = testCaseService.listAllCaseByFolderId(projectId, folderId);
//            TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO(folderId, null, null, null, null);
//            List<IssueInfosVO> issueInfosVOS = new ArrayList<>();
//            List<TestIssueFolderRelVO> resTestIssueFolderRelVOS = testIssueFolderRelService.queryByFolder(testIssueFolderRelVO);
//            for (TestIssueFolderRelVO resTestIssueFolderRelVO : resTestIssueFolderRelVOS) {
//                IssueInfosVO issueInfosVO = new IssueInfosVO();
//                issueInfosVO.setIssueId(resTestIssueFolderRelVO.getIssueId());
//                issueInfosVOS.add(issueInfosVO);
//            }
//            testIssueFolderRelService.copyIssue(projectId, versionId, returnTestIssueFolderVO.getFolderId(), issueInfosVOS);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void moveFolder(Long projectId,  Long targetForderId,List<Long> folderIds) {
        folderIds.forEach(folderId->{
            TestIssueFolderDTO testIssueFolderDTO = testIssueFolderMapper.selectByPrimaryKey(folderId);
            testIssueFolderDTO.setParentId(targetForderId);
            if (testIssueFolderMapper.updateByPrimaryKeySelective(testIssueFolderDTO) != 1) {
                throw new IssueFolderException(IssueFolderException.ERROR_UPDATE, testIssueFolderDTO.toString());
            }
        });
    }

    private void validateType(TestIssueFolderVO testIssueFolderVO) {
        if (!(StringUtils.equals(testIssueFolderVO.getType(), TYPE_CYCLE) || StringUtils.equals(testIssueFolderVO.getType(), TYPE_TEMP))) {
            throw new IssueFolderException(IssueFolderException.ERROR_FOLDER_TYPE);
        }
    }

    @Override
    public List<TestIssueFolderDTO> queryChildFolder(Long folderId) {
        List<TestIssueFolderDTO> folders = new ArrayList<>();
        return recurisionQuery(folderId,folders);
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
            folders.add(testIssueFolderMapper.selectByPrimaryKey(parentId));
            return folders;
        } else {
            for (TestIssueFolderDTO tmp : tmpList) {
                folders.add(testIssueFolderMapper.selectByPrimaryKey(parentId));
                folders.addAll(tmpList);
                findchildFolder(tmp.getFolderId(), folders);
            }
        }
        return folders;
    }
}
