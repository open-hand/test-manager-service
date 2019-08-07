package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.vo.ProductVersionDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.app.service.TestIssueFolderRelService;
import io.choerodon.test.manager.app.service.TestIssueFolderService;
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.infra.exception.IssueFolderException;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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

    public static final String TYPE_CYCLE = "cycle";
    public static final String TYPE_TEMP = "temp";

    @Autowired
    private TestCycleService testCycleService;
    @Autowired
    private TestIssueFolderRelService testIssueFolderRelService;
    @Autowired
    private TestCaseService testCaseService;
    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;
    @Autowired
    private ModelMapper modelMapper;

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
        String versionName = testCaseService.getVersionInfo(projectId).get(versionId).getName();

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderVO insert(TestIssueFolderVO testIssueFolderVO) {
        validateType(testIssueFolderVO);
        if (testIssueFolderVO.getFolderId() != null) {
            throw new CommonException("error.issue.folder.insert.folderId.should.be.null");
        }
        return modelMapper.map(this.baseInsert(modelMapper
                .map(testIssueFolderVO, TestIssueFolderDTO.class)), TestIssueFolderVO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long projectId, Long folderId) {
        TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();
        testIssueFolderVO.setFolderId(folderId);

        TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO();
        testIssueFolderRelVO.setFolderId(folderId);

        TestCycleVO testCycleVO = new TestCycleVO();
        testCycleVO.setFolderId(folderId);

        List<Long> issuesId = testIssueFolderRelService.queryByFolder(testIssueFolderRelVO).stream()
                .map(TestIssueFolderRelVO::getIssueId).collect(Collectors.toList());
        testCaseService.batchDeleteIssues(projectId, issuesId);
        testIssueFolderMapper.delete(modelMapper.map(testIssueFolderVO, TestIssueFolderDTO.class));
        testCycleService.delete(testCycleVO, projectId);
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
            return insert(testIssueFolderVO).getFolderId();
        } else {
            return resultTestIssueFolderVO.getFolderId();
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
            TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();
            testIssueFolderVO.setFolderId(folderId);
            TestIssueFolderVO resTestIssueFolderVO = modelMapper.map(testIssueFolderMapper.selectByPrimaryKey(folderId), TestIssueFolderVO.class);
            //创建文件夹
            resTestIssueFolderVO.setFolderId(null);
            resTestIssueFolderVO.setVersionId(versionId);
            TestIssueFolderVO returnTestIssueFolderVO = insert(resTestIssueFolderVO);
            //复制issue到目的文件夹
            TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO(folderId, null, null, null, null);
            List<IssueInfosVO> issueInfosVOS = new ArrayList<>();
            List<TestIssueFolderRelVO> resTestIssueFolderRelVOS = testIssueFolderRelService.queryByFolder(testIssueFolderRelVO);
            for (TestIssueFolderRelVO resTestIssueFolderRelVO : resTestIssueFolderRelVOS) {
                IssueInfosVO issueInfosVO = new IssueInfosVO();
                issueInfosVO.setIssueId(resTestIssueFolderRelVO.getIssueId());
                issueInfosVOS.add(issueInfosVO);
            }
            testIssueFolderRelService.copyIssue(projectId, versionId, returnTestIssueFolderVO.getFolderId(), issueInfosVOS);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void moveFolder(Long projectId, List<TestIssueFolderVO> testIssueFolderVOS) {
        for (TestIssueFolderVO testIssueFolderVO : testIssueFolderVOS) {
            //查找folder下的issues
            TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO();
            testIssueFolderRelVO.setFolderId(testIssueFolderVO.getFolderId());
            List<TestIssueFolderRelVO> resTestIssueFolderRelVOS = testIssueFolderRelService.queryByFolder(testIssueFolderRelVO);
            //批量改变issue的version并修改对应关联中的version
            List<Long> issuesId = resTestIssueFolderRelVOS.stream().map(TestIssueFolderRelVO::getIssueId).collect(Collectors.toList());
            TestIssueFolderRelVO changeTestIssueFolderRelVO = new TestIssueFolderRelVO(testIssueFolderVO.getFolderId(), testIssueFolderVO.getVersionId(), projectId, null, null);
            testIssueFolderRelService.updateVersionByFolderWithoutLockAndChangeIssueVersion(changeTestIssueFolderRelVO, issuesId);
            //更新folder信息
            if (testIssueFolderMapper.updateByPrimaryKeySelective(modelMapper.map(testIssueFolderVO, TestIssueFolderDTO.class)) != 1) {
                throw new IssueFolderException(IssueFolderException.ERROR_UPDATE, modelMapper.map(testIssueFolderVO, TestIssueFolderDTO.class).toString());
            }
        }
    }

    private void validateType(TestIssueFolderVO testIssueFolderVO) {
        if (!(StringUtils.equals(testIssueFolderVO.getType(), TYPE_CYCLE) || StringUtils.equals(testIssueFolderVO.getType(), TYPE_TEMP))) {
            throw new IssueFolderException(IssueFolderException.ERROR_FOLDER_TYPE);
        }
    }
}
