package io.choerodon.test.manager.app.service.impl;

import java.util.List;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.api.dto.TestIssueFolderRelDTO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestIssueFolderRelService;
import io.choerodon.test.manager.app.service.TestIssueFolderService;
import io.choerodon.test.manager.domain.service.ITestIssueFolderRelService;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderRelE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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


    @Override
    public List<TestIssueFolderRelDTO> query(TestIssueFolderRelDTO testIssueFolderRelDTO) {
        return ConvertHelper.convertList(iTestIssueFolderRelService.query(ConvertHelper
                .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class)), TestIssueFolderRelDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderRelDTO insertTestAndRelationship(IssueCreateDTO issueCreateDTO, Long projectId, Long folderId, Long versionId) {
        Long newFolderId = getDefaultFolderId(projectId,folderId,versionId);
        IssueDTO issueDTO = testCaseService.createTest(issueCreateDTO, projectId);
        TestIssueFolderRelDTO testIssueFolderRelDTO = loadTestIssueFolderRelDTOInfo(projectId,newFolderId,versionId,issueDTO.getIssueId());
        return ConvertHelper.convert(iTestIssueFolderRelService.insert(ConvertHelper
                .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class)), TestIssueFolderRelDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderRelDTO insertRelationship(Long projectId, Long folderId, Long versionId,Long issueId) {
        Long newFolderId = getDefaultFolderId(projectId,folderId,versionId);
        TestIssueFolderRelDTO testIssueFolderRelDTO = loadTestIssueFolderRelDTOInfo(projectId,newFolderId,versionId,issueId);
        return ConvertHelper.convert(iTestIssueFolderRelService.insert(ConvertHelper
                .convert(testIssueFolderRelDTO, TestIssueFolderRelE.class)), TestIssueFolderRelDTO.class);
    }

    private Long getDefaultFolderId(Long projectId, Long folderId, Long versionId){
        if(folderId == null){
            return testIssueFolderService.getDefaultFolderId(projectId,versionId);
        }else{
            return folderId;
        }
    }


    private TestIssueFolderRelDTO loadTestIssueFolderRelDTOInfo(Long projectId, Long folderId, Long versionId,Long issueId){
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

}
