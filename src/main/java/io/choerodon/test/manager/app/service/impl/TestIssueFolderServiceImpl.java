package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.ProductVersionDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderRelDTO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.app.service.TestIssueFolderRelService;
import io.choerodon.test.manager.app.service.TestIssueFolderService;
import io.choerodon.test.manager.domain.service.ITestIssueFolderService;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import org.codehaus.jackson.map.type.CollectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
@Component
public class TestIssueFolderServiceImpl implements TestIssueFolderService {

    @Autowired
    TestCycleService testCycleService;

    @Autowired
    ITestIssueFolderService iTestIssueFolderService;

    @Autowired
    TestIssueFolderRelService testIssueFolderRelService;

    @Autowired
    TestCaseService testCaseService;


    @Override
    public List<TestIssueFolderDTO> query(TestIssueFolderDTO testIssueFolderDTO) {
        return ConvertHelper.convertList(iTestIssueFolderService.query(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
    }

    @Override
    public List<TestIssueFolderDTO> queryByVersion(Long projectId,Long versionId){
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO(null,null,versionId,projectId,null,null);
        return ConvertHelper.convertList(iTestIssueFolderService.query(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderDTO insert(TestIssueFolderDTO testIssueFolderDTO) {
        return ConvertHelper.convert(iTestIssueFolderService.insert(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long projectId,Long folderId) {
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        testIssueFolderDTO.setFolderId(folderId);
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
        testIssueFolderRelDTO.setFolderId(folderId);
        List<Long> issuesId = testIssueFolderRelService.queryByFolder(testIssueFolderRelDTO).stream()
                .map(TestIssueFolderRelDTO::getIssueId).collect(Collectors.toList());
        testIssueFolderRelService.delete(projectId,issuesId);
        iTestIssueFolderService.delete(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderDTO update(TestIssueFolderDTO testIssueFolderDTO) {
        return ConvertHelper.convert(iTestIssueFolderService.update(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
    }

    @Override
    public JSONObject getTestIssueFolder(Long projectId) {
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        testIssueFolderDTO.setProjectId(projectId);
        List<ProductVersionDTO> versions = testCaseService.getVersionInfo(projectId).values().stream().collect(Collectors.toList());
        if (versions.isEmpty()) {
            return new JSONObject();
        }
        JSONObject root = new JSONObject();
        JSONArray versionStatus = new JSONArray();
        root.put("versions", versionStatus);
        List<TestIssueFolderDTO> testIssueFolderDTOS = ConvertHelper.convertList(iTestIssueFolderService.query(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
        if (testIssueFolderDTOS.isEmpty()) {
            return new JSONObject();
        }
        List<TestCycleDTO> cycles = testIssueFolderDTOS.stream().map(TestIssueFolderDTO::transferToCycle).collect(Collectors.toList());
        testCycleService.initVersionTree(versionStatus, versions, cycles);
        return root;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long getDefaultFolderId(Long projectId, Long versionId) {
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO(null, null, versionId, projectId, "temp", null);
        TestIssueFolderDTO resultTestIssueFolderDTO = ConvertHelper.convert(iTestIssueFolderService.queryOne(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
        testIssueFolderDTO.setName("临时");
        if (resultTestIssueFolderDTO == null) {
            return ConvertHelper.convert(iTestIssueFolderService.insert(ConvertHelper
                    .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class).getFolderId();
        } else {
            return resultTestIssueFolderDTO.getFolderId();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderDTO copyFolder(Long projectId, Long versionId, Long folderId) {
        //通过folder查找
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        testIssueFolderDTO.setFolderId(folderId);
        TestIssueFolderDTO resTestIssueFolderDTO = ConvertHelper.convert(iTestIssueFolderService.queryByPrimaryKey(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
        //创建文件夹
        resTestIssueFolderDTO.setFolderId(null);
        resTestIssueFolderDTO.setVersionId(versionId);
        TestIssueFolderDTO returnTestIssueFolderDTO = ConvertHelper.convert(iTestIssueFolderService.insert(ConvertHelper
                .convert(resTestIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
        //复制issue到目的文件夹
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO(folderId, null, null, null, null);
        List<IssueInfosDTO> issueInfosDTOS = new ArrayList<>();
        List<TestIssueFolderRelDTO> resTestIssueFolderRelDTOS = testIssueFolderRelService.queryByFolder(testIssueFolderRelDTO);
        for (TestIssueFolderRelDTO resTestIssueFolderRelDTO : resTestIssueFolderRelDTOS) {
            IssueInfosDTO issueInfosDTO = new IssueInfosDTO();
            issueInfosDTO.setIssueId(resTestIssueFolderRelDTO.getIssueId());
            issueInfosDTOS.add(issueInfosDTO);
        }
        testIssueFolderRelService.copyIssue(projectId, versionId, returnTestIssueFolderDTO.getFolderId(), issueInfosDTOS);
        return returnTestIssueFolderDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderDTO moveFolder(Long projectId, TestIssueFolderDTO testIssueFolderDTO) {
        //查找folder下的issues
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
        testIssueFolderRelDTO.setFolderId(testIssueFolderDTO.getFolderId());
        List<TestIssueFolderRelDTO> resTestIssueFolderRelDTOS = testIssueFolderRelService.queryByFolder(testIssueFolderRelDTO);
        List<IssueInfosDTO> issueInfosDTOS = new ArrayList<>();
        for (TestIssueFolderRelDTO relTestIssueFolderRelDTO : resTestIssueFolderRelDTOS) {
            IssueInfosDTO issueInfosDTO = new IssueInfosDTO();
            issueInfosDTO.setIssueId(relTestIssueFolderRelDTO.getIssueId());
            issueInfosDTOS.add(issueInfosDTO);
        }
        //批量改变issue的version并修改对应关联中的version
        List<Long> issuesId = resTestIssueFolderRelDTOS.stream().map(TestIssueFolderRelDTO::getIssueId).collect(Collectors.toList());
        TestIssueFolderRelDTO changeTestIssueFolderRelDTO = new TestIssueFolderRelDTO(testIssueFolderDTO.getFolderId(), testIssueFolderDTO.getVersionId(), projectId, null, null);
        testIssueFolderRelService.updateVersionByFolderWithoutLockAndChangeIssueVersion(changeTestIssueFolderRelDTO, issuesId);
        //更新folder信息
        return ConvertHelper.convert(iTestIssueFolderService.updateWithNoType(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
    }
}
