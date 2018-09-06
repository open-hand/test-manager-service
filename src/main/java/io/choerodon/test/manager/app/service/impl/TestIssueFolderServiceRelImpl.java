package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import io.choerodon.agile.api.dto.*;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.dto.CustomPage;
import io.choerodon.test.manager.api.dto.IssueComponentDetailFolderRelDTO;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderRelDTO;
import io.choerodon.test.manager.app.service.ReporterFormService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestIssueFolderRelService;
import io.choerodon.test.manager.app.service.TestIssueFolderService;
import io.choerodon.test.manager.domain.service.ITestIssueFolderRelService;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderRelE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
@Component
public class TestIssueFolderServiceRelImpl implements TestIssueFolderRelService {

    private static final String transfer_ERROR = "error.issue.folder.rel.query.transfer";

    @Autowired
    ITestIssueFolderRelService iTestIssueFolderRelService;

    @Autowired
    TestCaseService testCaseService;

    @Autowired
    TestIssueFolderService testIssueFolderService;

    @Autowired
    ReporterFormService reporterFormService;

    @Override
    public Page<IssueComponentDetailFolderRelDTO> queryIssuesById(Long projectId, Long versionId, Long folderId, Long[] issueIds) {
        Assert.notEmpty(issueIds, "error.query.issue.folder.issueId.not.empty");
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
        testIssueFolderRelDTO.setProjectId(projectId);
        testIssueFolderRelDTO.setFolderId(folderId);
        testIssueFolderRelDTO.setVersionId(versionId);
        List<TestIssueFolderRelDTO> resultRelDTOS = new ArrayList<>();
        for (Long issueId : issueIds) {
            testIssueFolderRelDTO.setIssueId(issueId);
            resultRelDTOS.add(ConvertHelper.convert(iTestIssueFolderRelService.queryOne(ConvertHelper
                    .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class)), TestIssueFolderRelDTO.class));
        }
        //构造一些必要的数据传给listIssueWithoutSubDetail方法
        //构造SearchDTO
        SearchDTO needSearchDTO = new SearchDTO();
        Map needArgs = new HashMap();
        needArgs.put("issueIds", issueIds);
        needSearchDTO.setOtherArgs(needArgs);
        //构造分页参数
        PageRequest needPageRequest = new PageRequest();
        needPageRequest.setPage(0);
        needPageRequest.setSize(9999999);
        needPageRequest.setSort(new Sort(Sort.Direction.ASC, "issueId"));
        List<IssueComponentDetailFolderRelDTO> issueComponentDetailFolderRelDTOS = new ArrayList<>();
        List<IssueComponentDetailDTO> issueComponentDetailDTOS = testCaseService.listIssueWithoutSubDetail(projectId, needSearchDTO, needPageRequest).getBody().stream().collect(Collectors.toList());
        for (IssueComponentDetailDTO issueComponentDetailDTO : issueComponentDetailDTOS) {
            for (TestIssueFolderRelDTO resultRelDTO : resultRelDTOS) {
                if (resultRelDTO.getIssueId().equals(issueComponentDetailDTO.getIssueId())) {
                    try {
                        issueComponentDetailFolderRelDTOS.add(new IssueComponentDetailFolderRelDTO(resultRelDTO.getObjectVersionNumber(), issueComponentDetailDTO));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Page page = new Page();
        page.setContent(issueComponentDetailFolderRelDTOS);
        return page;
    }

    @Override
    public Page<IssueComponentDetailFolderRelDTO> query(Long projectId, Long folderId, Long versionId, SearchDTO searchDTO, PageRequest pageRequest) {
        //查询出所有属于该folder的issue
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
        testIssueFolderRelDTO.setProjectId(projectId);
        testIssueFolderRelDTO.setFolderId(folderId);
        testIssueFolderRelDTO.setVersionId(versionId);
        List<TestIssueFolderRelDTO> resultRelDTOS = ConvertHelper.convertList(iTestIssueFolderRelService.query(ConvertHelper
                .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class)), TestIssueFolderRelDTO.class);
        if (ObjectUtils.isEmpty(resultRelDTOS)) {
            return new Page<>();
        }
        //先去过滤issues
        //将list中issuedId转换为Long数组
        Long[] issueIds = resultRelDTOS.stream().map(TestIssueFolderRelDTO::getIssueId).toArray(Long[]::new);
        //将issueId放入searchDTO中
        Map args = Optional.ofNullable(searchDTO.getOtherArgs()).orElseGet(HashMap::new);
        args.put("issueIds", issueIds);
        if (searchDTO.getOtherArgs() == null) {
            searchDTO.setOtherArgs(args);
        }
        //返回过滤后的issueIds，进行分页
        Long[] allFilteredIssues = testCaseService.queryIssueIdsByOptions(searchDTO, projectId).stream().toArray(Long[]::new);
        if (ObjectUtils.isEmpty(allFilteredIssues)) {
            return new Page<>();
        }
        int pageNum = pageRequest.getPage();
        int pageSize = pageRequest.getSize();
        int highPage = (pageNum + 1) * pageSize;
        int lowPage = pageNum * pageSize;
        //创建一个Long数组，将对应分页的issuesId传给它
        int size = highPage >= allFilteredIssues.length ? allFilteredIssues.length - lowPage : pageSize;
        Long[] pagedIssues = new Long[size];
        System.arraycopy(allFilteredIssues, lowPage, pagedIssues, 0, size);
        return new CustomPage(queryIssuesById(projectId, versionId, folderId, pagedIssues).stream().collect(Collectors.toList()), allFilteredIssues);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderRelDTO insertTestAndRelationship(IssueCreateDTO issueCreateDTO, Long projectId, Long folderId, Long versionId) {
        Long newFolderId = getDefaultFolderId(projectId, folderId, versionId);
        IssueDTO issueDTO = testCaseService.createTest(issueCreateDTO, projectId);
        TestIssueFolderRelDTO testIssueFolderRelDTO = loadTestIssueFolderRelDTOInfo(projectId, newFolderId, versionId, issueDTO.getIssueId());
        return ConvertHelper.convert(iTestIssueFolderRelService.insert(ConvertHelper
                .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class)), TestIssueFolderRelDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<TestIssueFolderRelDTO> insertRelationship(Long projectId, List<TestIssueFolderRelDTO> testIssueFolderRelDTOS) {
        List<TestIssueFolderRelDTO> resultTestIssueFolderRelDTOS = new ArrayList<>();
        Long newFolderId = getDefaultFolderId(projectId, testIssueFolderRelDTOS.get(0).getFolderId(), testIssueFolderRelDTOS.get(0).getVersionId());
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


    private TestIssueFolderRelDTO loadTestIssueFolderRelDTOInfo(Long projectId, Long folderId, Long versionId, Long issueId) {
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
        testIssueFolderRelDTO.setVersionId(versionId);
        testIssueFolderRelDTO.setFolderId(folderId);
        testIssueFolderRelDTO.setProjectId(projectId);
        testIssueFolderRelDTO.setIssueId(issueId);
        return testIssueFolderRelDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(TestIssueFolderRelDTO testIssueFolderRelDTO) {
        iTestIssueFolderRelService.delete(ConvertHelper
                .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderRelDTO update(TestIssueFolderRelDTO testIssueFolderRelDTO) {
        return ConvertHelper.convert(iTestIssueFolderRelService.update(ConvertHelper
                .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class)), TestIssueFolderRelDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changeIssue(Long projectId, Long versionId, Long folderId, String type, List<IssueInfosDTO> issueInfosDTOS) {
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
        testIssueFolderRelDTO.setProjectId(projectId);
        testIssueFolderRelDTO.setFolderId(folderId);
        testIssueFolderRelDTO.setVersionId(versionId);
        CopyConditionDTO copyConditionDTO = new CopyConditionDTO();
        if (type.equals("copy")) {
            for (IssueInfosDTO issueInfosDTO : issueInfosDTOS) {
                copyConditionDTO.setSummary(issueInfosDTO.getSummary());
                //远程服务复制issue，得到远程issue的id
                IssueDTO afterCloneIssueDTO = testCaseService.cloneIssueByIssueId(projectId,issueInfosDTO.getIssueId(),copyConditionDTO);
                //插入issue与folder的关联
                testIssueFolderRelDTO.setIssueId(afterCloneIssueDTO.getIssueId());
                iTestIssueFolderRelService.insert(ConvertHelper
                        .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class));
            }
        } else if (type.equals("move")) {
            for (IssueInfosDTO issueInfosDTO : issueInfosDTOS) {
                testIssueFolderRelDTO.setObjectVersionNumber(issueInfosDTO.getObjectVersionNumber());
                testIssueFolderRelDTO.setIssueId(issueInfosDTO.getIssueId());
                update(testIssueFolderRelDTO);
            }
            testCaseService.batchIssueToVersion(projectId,versionId,issueInfosDTOS.stream().map(IssueInfosDTO::getIssueId).collect(Collectors.toList()));
        }
    }
}
