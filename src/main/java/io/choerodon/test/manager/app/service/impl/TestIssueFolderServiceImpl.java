package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.choerodon.agile.api.dto.ProductVersionDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.api.dto.*;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.app.service.TestIssueFolderRelService;
import io.choerodon.test.manager.app.service.TestIssueFolderService;
import io.choerodon.test.manager.domain.service.ITestIssueFolderService;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<TestIssueFolderDTO> queryByParameter(Long projectId, Long versionId) {
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO(null, null, versionId, projectId, null, null);
        return ConvertHelper.convertList(iTestIssueFolderService.query(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
    }

    @Override
    public List<TestIssueFolderWithVersionNameDTO> queryByParameterWithVersionName(Long projectId, Long versionId) {
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO(null, null, versionId, projectId, null, null);
        List<TestIssueFolderDTO> resultTemp = ConvertHelper.convertList(iTestIssueFolderService.query(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
        List<TestIssueFolderWithVersionNameDTO> result = new ArrayList<>();
        String versionName = testCaseService.getVersionInfo(projectId).get(versionId).getName();

        resultTemp.forEach(v -> {
            TestIssueFolderWithVersionNameDTO t = new TestIssueFolderWithVersionNameDTO();
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderDTO insert(TestIssueFolderDTO testIssueFolderDTO) {
        return ConvertHelper.convert(iTestIssueFolderService.insert(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long projectId, Long folderId) {
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        testIssueFolderDTO.setFolderId(folderId);

        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
        testIssueFolderRelDTO.setFolderId(folderId);

        TestCycleDTO testCycleDTO = new TestCycleDTO();
        testCycleDTO.setFolderId(folderId);

        List<Long> issuesId = testIssueFolderRelService.queryByFolder(testIssueFolderRelDTO).stream()
                .map(TestIssueFolderRelDTO::getIssueId).collect(Collectors.toList());
        testCaseService.batchDeleteIssues(projectId, issuesId);
        iTestIssueFolderService.delete(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class));
        testCycleService.delete(testCycleDTO, projectId);
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
        List<ProductVersionDTO> versions = testCaseService.getVersionInfo(projectId).values()
                .stream().sorted(Comparator.comparing(ProductVersionDTO::getStatusCode).reversed().thenComparing(ProductVersionDTO::getSequence)).collect(Collectors.toList());
        if (versions.isEmpty()) {
            return new JSONObject();
        }
        JSONObject root = new JSONObject();
        JSONArray versionStatus = new JSONArray();
        root.put("versions", versionStatus);
        List<TestIssueFolderDTO> testIssueFolderDTOS = ConvertHelper.convertList(iTestIssueFolderService.query(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
        List<TestCycleDTO> cycles = testIssueFolderDTOS.stream().map(TestIssueFolderDTO::transferToCycle).collect(Collectors.toList());
        testCycleService.initVersionTree(projectId, versionStatus, versions, cycles);
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

    /**
     * @param projectId
     * @param versionId 要复制到的目标version
     * @param folderIds 要被复制的源folder
     * @return 被复制成功的目标folder
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void copyFolder(Long projectId, Long versionId, Long[] folderIds) {
        for (Long folderId : folderIds) {
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
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void moveFolder(Long projectId, List<TestIssueFolderDTO> testIssueFolderDTOS) {
        for (TestIssueFolderDTO testIssueFolderDTO : testIssueFolderDTOS) {
            //查找folder下的issues
            TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
            testIssueFolderRelDTO.setFolderId(testIssueFolderDTO.getFolderId());
            List<TestIssueFolderRelDTO> resTestIssueFolderRelDTOS = testIssueFolderRelService.queryByFolder(testIssueFolderRelDTO);
            //批量改变issue的version并修改对应关联中的version
            List<Long> issuesId = resTestIssueFolderRelDTOS.stream().map(TestIssueFolderRelDTO::getIssueId).collect(Collectors.toList());
            TestIssueFolderRelDTO changeTestIssueFolderRelDTO = new TestIssueFolderRelDTO(testIssueFolderDTO.getFolderId(), testIssueFolderDTO.getVersionId(), projectId, null, null);
            testIssueFolderRelService.updateVersionByFolderWithoutLockAndChangeIssueVersion(changeTestIssueFolderRelDTO, issuesId);
            //更新folder信息
            iTestIssueFolderService.updateWithNoType(ConvertHelper.convert(testIssueFolderDTO, TestIssueFolderE.class));
        }
    }
}
