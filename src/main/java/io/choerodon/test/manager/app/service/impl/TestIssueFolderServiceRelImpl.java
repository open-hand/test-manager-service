package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.api.dto.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.domain.service.ITestIssueFolderRelService;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderRelE;
import io.choerodon.test.manager.domain.test.manager.factory.TestIssueFolderEFactory;
import io.choerodon.test.manager.infra.common.utils.PageUtil;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
@Component
public class TestIssueFolderServiceRelImpl implements TestIssueFolderRelService {

    @Autowired
    ITestIssueFolderRelService iTestIssueFolderRelService;

    @Autowired
    TestCaseService testCaseService;

    @Autowired
    TestIssueFolderService testIssueFolderService;

    @Autowired
    ReporterFormService reporterFormService;

    @Autowired
    TestCycleCaseService testCycleCaseService;

    @Autowired
    TestCaseStepService testCaseStepService;

    @Override
    public PageInfo<IssueComponentDetailFolderRelDTO> queryIssuesById(Long projectId, Long versionId, Long folderId, Long[] issueIds, Long organizationId) {
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO(folderId, null, projectId, null, null);
        List<TestIssueFolderRelDTO> resultRelDTOS = new ArrayList<>();
        for (Long issueId : issueIds) {
            testIssueFolderRelDTO.setIssueId(issueId);
            resultRelDTOS.add(ConvertHelper.convert(iTestIssueFolderRelService.queryOne(ConvertHelper
                    .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class)), TestIssueFolderRelDTO.class));
        }
        if (ObjectUtils.isEmpty(resultRelDTOS)) {
            return new PageInfo<>(new ArrayList<>());
        }
        List<IssueComponentDetailFolderRelDTO> issueComponentDetailFolderRelDTOS = new ArrayList<>();
        Map<Long, IssueInfosDTO> map = testCaseService.getIssueInfoMap(projectId, issueIds, true, organizationId);
        if (ObjectUtils.isEmpty(map)) {
            return new PageInfo<>(new ArrayList<>());
        }

        TestIssueFolderE testIssueFolderE = TestIssueFolderEFactory.create();

        for (TestIssueFolderRelDTO resultRelDTO : resultRelDTOS) {
            if (resultRelDTO != null && map.containsKey(resultRelDTO.getIssueId())) {
                IssueComponentDetailFolderRelDTO issueComponentDetailFolderRelDTO = new IssueComponentDetailFolderRelDTO(map.get(resultRelDTO.getIssueId()));
                issueComponentDetailFolderRelDTO.setObjectVersionNumber(resultRelDTO.getObjectVersionNumber());
                testIssueFolderE.setFolderId(resultRelDTO.getFolderId());
                TestIssueFolderE resE = testIssueFolderE.queryByPrimaryKey();
                issueComponentDetailFolderRelDTO.setFolderId(resultRelDTO.getFolderId());
                issueComponentDetailFolderRelDTO.setFolderName(resE.getName());
                issueComponentDetailFolderRelDTOS.add(issueComponentDetailFolderRelDTO);
            }
        }
        return new PageInfo<>(issueComponentDetailFolderRelDTOS);
    }

    private void loadResultRelDTOS(Long projectId, Long versionId, Long folderId, Long issueId, List<TestIssueFolderRelDTO> resultRelDTOS) {
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO(folderId, versionId, projectId, issueId, null);
        List res = ConvertHelper.convertList(iTestIssueFolderRelService.query(ConvertHelper
                .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class)), TestIssueFolderRelDTO.class);
        if (!res.isEmpty()) {
            resultRelDTOS.addAll(res);
        }
    }

    @Override
    public PageInfo<IssueComponentDetailFolderRelDTO> query(Long projectId, Long folderId, TestFolderRelQueryDTO testFolderRelQueryDTO, PageRequest pageRequest, Long organizationId) {
        SearchDTO searchDTO = Optional.ofNullable(testFolderRelQueryDTO.getSearchDTO()).orElseGet(SearchDTO::new);
        //查询出所属的issue
        List<TestIssueFolderRelDTO> resultRelDTOS = new ArrayList<>();

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
            if (ObjectUtils.isEmpty(testFolderRelQueryDTO.getVersionIds())) {
                loadResultRelDTOS(projectId, null, folderId, null, resultRelDTOS);
            } else {
                for (Long versionId : testFolderRelQueryDTO.getVersionIds()) {
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
        int pageNum = pageRequest.getPage() - 1;
        int pageSize = pageRequest.getSize();
        int highPage = (pageNum + 1) * pageSize;
        int lowPage = pageNum * pageSize;
        //创建一个Long数组，将对应分页的issuesId传给它
        int size = highPage >= allIssuesArray.length ? allIssuesArray.length - lowPage : pageSize;
        Long[] pagedIssues = new Long[size];
        System.arraycopy(allIssuesArray, lowPage, pagedIssues, 0, size);
        return new CustomPage<>(queryIssuesById(projectId, null, folderId, pagedIssues, organizationId).getList(), allIssuesArray);
    }

    private Long[] getFilterIssues(Long projectId, SearchDTO searchDTO, List<TestIssueFolderRelDTO> resultRelDTOS) {
        //返回过滤后的issueIds
        List<Long> filteredIssues = testCaseService.queryIssueIdsByOptions(searchDTO, projectId);

        List<Long> allFilteredIssues = new ArrayList<>();
        resultRelDTOS.stream().map(TestIssueFolderRelDTO::getIssueId).forEach(v -> {
            if (filteredIssues.contains(v)) {
                allFilteredIssues.add(v);
            }
        });
        Collections.reverse(allFilteredIssues);
        return allFilteredIssues.toArray(new Long[allFilteredIssues.size()]);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderRelDTO insertTestAndRelationship(IssueCreateDTO issueCreateDTO, Long projectId, Long folderId, Long versionId, String applyType) {
        Long newFolderId = getDefaultFolderId(projectId, folderId, versionId);
        IssueDTO issueDTO = testCaseService.createTest(issueCreateDTO, projectId, applyType);
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO(newFolderId, versionId, projectId, issueDTO.getIssueId(), null);
        return ConvertHelper.convert(iTestIssueFolderRelService.insert(ConvertHelper
                .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class)), TestIssueFolderRelDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<TestIssueFolderRelDTO> insertBatchRelationship(Long projectId, List<TestIssueFolderRelDTO> testIssueFolderRelDTOS) {
        List<TestIssueFolderRelDTO> resultTestIssueFolderRelDTOS = new ArrayList<>();
        Long newFolderId = null;
        if (!ObjectUtils.isEmpty(testIssueFolderRelDTOS)) {
            newFolderId = getDefaultFolderId(projectId, testIssueFolderRelDTOS.get(0).getFolderId(), testIssueFolderRelDTOS.get(0).getVersionId());
        }
        for (TestIssueFolderRelDTO testIssueFolderRelDTO : testIssueFolderRelDTOS) {
            testIssueFolderRelDTO.setFolderId(newFolderId);
            testIssueFolderRelDTO.setProjectId(projectId);
            TestIssueFolderRelDTO resultTestIssueFolderRelDTO = ConvertHelper.convert(iTestIssueFolderRelService.insert(ConvertHelper
                    .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class)), TestIssueFolderRelDTO.class);
            resultTestIssueFolderRelDTOS.add(resultTestIssueFolderRelDTO);
        }
        return resultTestIssueFolderRelDTOS;
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
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
        for (Long issueId : issuesId) {
            testIssueFolderRelDTO.setIssueId(issueId);
            iTestIssueFolderRelService.delete(ConvertHelper
                    .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderRelDTO updateVersionByFolderWithoutLockAndChangeIssueVersion(TestIssueFolderRelDTO testIssueFolderRelDTO, List<Long> issues) {
        TestIssueFolderRelDTO resTestIssueFolderRelDTO = ConvertHelper.convert(iTestIssueFolderRelService.updateVersionByFolderWithNoLock(ConvertHelper
                .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class)), TestIssueFolderRelDTO.class);
        testCaseService.batchIssueToVersionTest(testIssueFolderRelDTO.getProjectId(), testIssueFolderRelDTO.getVersionId(), issues);
        return resTestIssueFolderRelDTO;
    }

    @Override
    public List<TestIssueFolderRelDTO> queryByFolder(TestIssueFolderRelDTO testIssueFolderRelDTO) {
        return ConvertHelper.convertList(iTestIssueFolderRelService.query(ConvertHelper
                .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class)), TestIssueFolderRelDTO.class);
    }

    @Override
    public TestIssueFolderRelDTO cloneOneIssue(Long projectId, Long issueId) {
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
        testIssueFolderRelDTO.setIssueId(issueId);

        TestIssueFolderRelDTO resTestIssueFolderRelDTO = ConvertHelper.convert(iTestIssueFolderRelService.queryOne(
                ConvertHelper.convert(testIssueFolderRelDTO, TestIssueFolderRelE.class)), TestIssueFolderRelDTO.class);

        Long[] issueIds = {issueId};
        List<Long> issuesId = testCaseService.batchCloneIssue(projectId, resTestIssueFolderRelDTO.getVersionId(), issueIds);
        for (Long id : issuesId) {
            //克隆issue步骤
            TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();
            testCaseStepDTO.setIssueId(issueId);
            testCaseStepService.batchClone(testCaseStepDTO, id, projectId);
            //插入issue与folder的关联
            resTestIssueFolderRelDTO.setId(null);
            resTestIssueFolderRelDTO.setIssueId(id);
            iTestIssueFolderRelService.insert(ConvertHelper
                    .convert(resTestIssueFolderRelDTO, TestIssueFolderRelE.class));
        }
        return resTestIssueFolderRelDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void moveFolderIssue(Long projectId, Long versionId, Long folderId, List<IssueInfosDTO> issueInfosDTOS) {
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO(folderId, versionId, projectId, null, null);
        for (IssueInfosDTO issueInfosDTO : issueInfosDTOS) {
            testIssueFolderRelDTO.setObjectVersionNumber(issueInfosDTO.getObjectVersionNumber());
            testIssueFolderRelDTO.setIssueId(issueInfosDTO.getIssueId());
            iTestIssueFolderRelService.updateFolderByIssue(ConvertHelper
                    .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class));
        }
        testCaseService.batchIssueToVersionTest(projectId, versionId, issueInfosDTOS.stream().map(IssueInfosDTO::getIssueId).collect(Collectors.toList()));
    }

    /*
     * @param projectId
     * @param versionId 目标version
     * @param folderId 目标folder
     * @param issueInfosDTOS 插入成功的issue信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void copyIssue(Long projectId, Long versionId, Long folderId, List<IssueInfosDTO> issueInfosDTOS) {
        TestIssueFolderRelDTO testIssueFolderRelDTO;
        testIssueFolderRelDTO = new TestIssueFolderRelDTO(folderId, versionId, projectId, null, null);
        //远程服务复制issue，得到远程issue的ids
        if (!ObjectUtils.isEmpty(issueInfosDTOS)) {
            //克隆接口，传给它的顺序是怎么样的返回的就是怎么样的
            List<Long> issuesId = testCaseService.batchCloneIssue(projectId, versionId,
                    issueInfosDTOS.stream().map(IssueInfosDTO::getIssueId).toArray(Long[]::new));
            int i = 0;
            for (Long id : issuesId) {
                //克隆issue步骤
                TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();
                testCaseStepDTO.setIssueId(issueInfosDTOS.get(i++).getIssueId());
                testCaseStepService.batchClone(testCaseStepDTO, id, projectId);
                //插入issue与folder的关联
                testIssueFolderRelDTO.setIssueId(id);
                iTestIssueFolderRelService.insert(ConvertHelper
                        .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class));
            }
        }
    }

}
