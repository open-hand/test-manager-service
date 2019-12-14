package io.choerodon.test.manager.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.test.manager.api.vo.agile.IssueCreateDTO;
import io.choerodon.test.manager.api.vo.agile.IssueDTO;
import io.choerodon.test.manager.api.vo.agile.SearchDTO;
import org.springframework.data.domain.Pageable;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCaseStepService;
import io.choerodon.test.manager.app.service.TestIssueFolderRelService;
import io.choerodon.test.manager.app.service.TestIssueFolderService;
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.infra.dto.TestIssueFolderRelDTO;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderRelMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
@Service
public class TestIssueFolderServiceRelImpl implements TestIssueFolderRelService {

    @Autowired
    private TestCaseService testCaseService;
    @Autowired
    private TestIssueFolderService testIssueFolderService;
    @Autowired
    private TestCaseStepService testCaseStepService;
    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;
    @Autowired
    private TestIssueFolderRelMapper testIssueFolderRelMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TestIssueFolderRelDTO baseInsert(TestIssueFolderRelDTO insert) {
        if (testIssueFolderRelMapper.insert(insert) != 1) {
            throw new CommonException("error.issueFolderRel.insert");
        }
        return testIssueFolderRelMapper.selectByPrimaryKey(insert.getId());
    }

    @Override
    public PageInfo<IssueComponentDetailFolderRelVO> queryIssuesById(Long projectId, Long versionId, Long folderId, Long[] issueIds, Long organizationId) {
        TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO(folderId, null, projectId, null, null);
        List<TestIssueFolderRelVO> resultRelDTOS = new ArrayList<>();
        for (Long issueId : issueIds) {
            testIssueFolderRelVO.setIssueId(issueId);
            resultRelDTOS.add(modelMapper.map(testIssueFolderRelMapper.selectOne(modelMapper
                    .map(testIssueFolderRelVO, TestIssueFolderRelDTO.class)), TestIssueFolderRelVO.class));
        }
        if (ObjectUtils.isEmpty(resultRelDTOS)) {
            return new PageInfo<>(new ArrayList<>());
        }
        List<IssueComponentDetailFolderRelVO> issueComponentDetailFolderRelVOS = new ArrayList<>();
        Map<Long, IssueInfosVO> map = testCaseService.getIssueInfoMap(projectId, issueIds, true, organizationId);
        if (ObjectUtils.isEmpty(map)) {
            return new PageInfo<>(new ArrayList<>());
        }

        for (TestIssueFolderRelVO resultRelDTO : resultRelDTOS) {
            if (resultRelDTO != null && map.containsKey(resultRelDTO.getIssueId())) {
                IssueComponentDetailFolderRelVO issueComponentDetailFolderRelVO = new IssueComponentDetailFolderRelVO(map.get(resultRelDTO.getIssueId()));
                issueComponentDetailFolderRelVO.setObjectVersionNumber(resultRelDTO.getObjectVersionNumber());
                TestIssueFolderDTO testIssueFolderDTO = testIssueFolderMapper.selectByPrimaryKey(resultRelDTO.getFolderId());
                issueComponentDetailFolderRelVO.setFolderId(resultRelDTO.getFolderId());
                issueComponentDetailFolderRelVO.setFolderName(testIssueFolderDTO.getName());
                issueComponentDetailFolderRelVOS.add(issueComponentDetailFolderRelVO);
            }
        }
        return new PageInfo<>(issueComponentDetailFolderRelVOS);
    }

    private void loadResultRelDTOS(Long projectId, Long versionId, Long folderId, Long issueId, List<TestIssueFolderRelVO> resultRelDTOS) {
        TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO(folderId, versionId, projectId, issueId, null);
        List<TestIssueFolderRelVO> res = modelMapper.map(testIssueFolderRelMapper.select(modelMapper.map(testIssueFolderRelVO, TestIssueFolderRelDTO.class)),
                new TypeToken<List<TestIssueFolderRelVO>>() {
                }.getType());
        if (!res.isEmpty()) {
            resultRelDTOS.addAll(res);
        }
    }

    @Override
    public PageInfo<IssueComponentDetailFolderRelVO> query(Long projectId, Long folderId, TestFolderRelQueryVO testFolderRelQueryVO, Pageable pageable, Long organizationId) {
        SearchDTO searchDTO = Optional.ofNullable(testFolderRelQueryVO.getSearchDTO()).orElseGet(SearchDTO::new);
        //查询出所属的issue
        List<TestIssueFolderRelVO> resultRelDTOS = new ArrayList<>();

        String sIssueIds = "issueIds";

        //如果传入的参数包含issueIds,就只去查找这些issueIds
        if (searchDTO.getOtherArgs() != null && searchDTO.getOtherArgs().containsKey(sIssueIds)) {
            List<Integer> issueIds = (ArrayList<Integer>) searchDTO.getOtherArgs().get(sIssueIds);
            for (Integer id : issueIds) {
                Long issueId = id.longValue();
                loadResultRelDTOS(projectId, null, folderId, issueId, resultRelDTOS);
            }
        } else {
            //如果传入了version就去筛选这些version下的rel
            if (ObjectUtils.isEmpty(testFolderRelQueryVO.getVersionIds())) {
                loadResultRelDTOS(projectId, null, folderId, null, resultRelDTOS);
            } else {
                for (Long versionId : testFolderRelQueryVO.getVersionIds()) {
                    loadResultRelDTOS(projectId, versionId, folderId, null, resultRelDTOS);
                }
            }
        }

        if (ObjectUtils.isEmpty(resultRelDTOS)) {
            return new PageInfo<>(new ArrayList<>());
        }

        Long[] allIssuesArray = getFilterIssues(projectId, searchDTO, resultRelDTOS);

        if (ObjectUtils.isEmpty(allIssuesArray)) {
            return new PageInfo<>(new ArrayList<>());
        }

        //进行分页
        int pageNum = pageable.getPageNumber()- 1;
        int pageSize = pageable.getPageSize();
        int highPage = (pageNum + 1) * pageSize;
        int lowPage = pageNum * pageSize;
        //创建一个Long数组，将对应分页的issuesId传给它
        int size = highPage >= allIssuesArray.length ? allIssuesArray.length - lowPage : pageSize;
        Long[] pagedIssues = new Long[size];
        System.arraycopy(allIssuesArray, lowPage, pagedIssues, 0, size);
        return new CustomPage<>(queryIssuesById(projectId, null, folderId, pagedIssues, organizationId).getList(), allIssuesArray);
    }

    private Long[] getFilterIssues(Long projectId, SearchDTO searchDTO, List<TestIssueFolderRelVO> resultRelDTOS) {
        //返回过滤后的issueIds
        List<Long> filteredIssues = testCaseService.queryIssueIdsByOptions(searchDTO, projectId);

        List<Long> allFilteredIssues = new ArrayList<>();
        resultRelDTOS.stream().map(TestIssueFolderRelVO::getIssueId).forEach(v -> {
            if (filteredIssues.contains(v)) {
                allFilteredIssues.add(v);
            }
        });
        Collections.reverse(allFilteredIssues);
        return allFilteredIssues.toArray(new Long[allFilteredIssues.size()]);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderRelVO insertTestAndRelationship(IssueCreateDTO issueCreateDTO, Long projectId, Long folderId, Long versionId, String applyType) {
        Long newFolderId = getDefaultFolderId(projectId, folderId, versionId);
        IssueDTO issueDTO = testCaseService.createTest(issueCreateDTO, projectId, applyType);
        TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO(newFolderId, versionId, projectId, issueDTO.getIssueId(), null);
        return modelMapper.map(this.baseInsert(modelMapper.map(testIssueFolderRelVO, TestIssueFolderRelDTO.class)), TestIssueFolderRelVO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<TestIssueFolderRelVO> insertBatchRelationship(Long projectId, List<TestIssueFolderRelVO> testIssueFolderRelVOS) {
        List<TestIssueFolderRelVO> resultTestIssueFolderRelVOS = new ArrayList<>();
        Long newFolderId = null;
        if (!ObjectUtils.isEmpty(testIssueFolderRelVOS)) {
            newFolderId = getDefaultFolderId(projectId, testIssueFolderRelVOS.get(0).getFolderId(), testIssueFolderRelVOS.get(0).getVersionId());
        }
        for (TestIssueFolderRelVO testIssueFolderRelVO : testIssueFolderRelVOS) {
            testIssueFolderRelVO.setFolderId(newFolderId);
            testIssueFolderRelVO.setProjectId(projectId);
            TestIssueFolderRelVO resultTestIssueFolderRelVO = modelMapper.map(this.baseInsert(modelMapper.map(testIssueFolderRelVO, TestIssueFolderRelDTO.class)), TestIssueFolderRelVO.class);
            resultTestIssueFolderRelVOS.add(resultTestIssueFolderRelVO);
        }
        return resultTestIssueFolderRelVOS;
    }

    private Long getDefaultFolderId(Long projectId, Long folderId, Long versionId) {
        if (folderId == null) {
            return testIssueFolderService.getDefaultFolderId(projectId, versionId);
        } else {
            return folderId;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long projectId, List<Long> issuesId) {
        TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO();
        for (Long issueId : issuesId) {
            testIssueFolderRelVO.setIssueId(issueId);
            testIssueFolderRelMapper.delete(modelMapper.map(testIssueFolderRelVO, TestIssueFolderRelDTO.class));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateVersionByFolderWithoutLockAndChangeIssueVersion(TestIssueFolderRelVO testIssueFolderRelVO, List<Long> issues) {
        TestIssueFolderRelDTO testIssueFolderRelDTO = modelMapper.map(testIssueFolderRelVO, TestIssueFolderRelDTO.class);
        testIssueFolderRelMapper.updateVersionByFolderWithNoLock(testIssueFolderRelDTO);
//        TestIssueFolderRelVO resTestIssueFolderRelVO = modelMapper.map(testIssueFolderRelMapper
//                .selectByPrimaryKey(testIssueFolderRelDTO.getId()), TestIssueFolderRelVO.class);
        testCaseService.batchIssueToVersionTest(testIssueFolderRelVO.getProjectId(), testIssueFolderRelVO.getVersionId(), issues);
    }

    @Override
    public List<TestIssueFolderRelVO> queryByFolder(TestIssueFolderRelVO testIssueFolderRelVO) {
        return modelMapper.map(testIssueFolderRelMapper.select(modelMapper
                        .map(testIssueFolderRelVO, TestIssueFolderRelDTO.class)),
                new TypeToken<List<TestIssueFolderRelVO>>() {
                }.getType());
    }

    @Override
    public TestIssueFolderRelVO cloneOneIssue(Long projectId, Long issueId) {
        TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO();
        testIssueFolderRelVO.setIssueId(issueId);

        TestIssueFolderRelVO resTestIssueFolderRelVO = modelMapper.map(testIssueFolderRelMapper
                .selectOne(modelMapper.map(testIssueFolderRelVO, TestIssueFolderRelDTO.class)), TestIssueFolderRelVO.class);

        Long[] issueIds = {issueId};
        List<Long> issuesId = testCaseService.batchCloneIssue(projectId, resTestIssueFolderRelVO.getVersionId(), issueIds);
        for (Long id : issuesId) {
            //克隆issue步骤
            TestCaseStepVO testCaseStepVO = new TestCaseStepVO();
            testCaseStepVO.setIssueId(issueId);
            testCaseStepService.batchClone(testCaseStepVO, id, projectId);
            //插入issue与folder的关联
            resTestIssueFolderRelVO.setId(null);
            resTestIssueFolderRelVO.setIssueId(id);
            this.baseInsert(modelMapper.map(resTestIssueFolderRelVO, TestIssueFolderRelDTO.class));
        }
        return resTestIssueFolderRelVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void moveFolderIssue(Long projectId, Long versionId, Long folderId, List<IssueInfosVO> issueInfosVOS) {
        TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO(folderId, versionId, projectId, null, null);
        for (IssueInfosVO issueInfosVO : issueInfosVOS) {
            testIssueFolderRelVO.setObjectVersionNumber(issueInfosVO.getObjectVersionNumber());
            testIssueFolderRelVO.setIssueId(issueInfosVO.getIssueId());
            TestIssueFolderRelDTO testIssueFolderRelDTO = modelMapper.map(testIssueFolderRelVO, TestIssueFolderRelDTO.class);
            testIssueFolderRelMapper.updateFolderByIssue(testIssueFolderRelDTO);
        }
        testCaseService.batchIssueToVersionTest(projectId, versionId, issueInfosVOS.stream().map(IssueInfosVO::getIssueId).collect(Collectors.toList()));
    }

    /*
     * @param projectId
     * @param versionId 目标version
     * @param folderId 目标folder
     * @param issueInfosVOS 插入成功的issue信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void copyIssue(Long projectId, Long versionId, Long folderId, List<IssueInfosVO> issueInfosVOS) {
        TestIssueFolderRelVO testIssueFolderRelVO;
        testIssueFolderRelVO = new TestIssueFolderRelVO(folderId, versionId, projectId, null, null);
        //远程服务复制issue，得到远程issue的ids
        if (!ObjectUtils.isEmpty(issueInfosVOS)) {
            //克隆接口，传给它的顺序是怎么样的返回的就是怎么样的
            List<Long> issuesId = testCaseService.batchCloneIssue(projectId, versionId,
                    issueInfosVOS.stream().map(IssueInfosVO::getIssueId).toArray(Long[]::new));
            int i = 0;
            for (Long id : issuesId) {
                //克隆issue步骤
                TestCaseStepVO testCaseStepVO = new TestCaseStepVO();
                testCaseStepVO.setIssueId(issueInfosVOS.get(i++).getIssueId());
                testCaseStepService.batchClone(testCaseStepVO, id, projectId);
                //插入issue与folder的关联
                testIssueFolderRelVO.setIssueId(id);
                this.baseInsert(modelMapper.map(testIssueFolderRelVO, TestIssueFolderRelDTO.class));
            }
        }
    }

}
