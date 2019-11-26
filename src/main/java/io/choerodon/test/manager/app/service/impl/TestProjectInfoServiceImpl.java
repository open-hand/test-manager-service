package io.choerodon.test.manager.app.service.impl;

import java.util.List;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.event.ProjectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.test.manager.app.service.TestProjectInfoService;
import io.choerodon.test.manager.infra.dto.TestProjectInfoDTO;
import io.choerodon.test.manager.infra.mapper.TestProjectInfoMapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: 25499
 * @date: 2019/11/22 9:33
 * @description:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestProjectInfoServiceImpl implements TestProjectInfoService {

    @Autowired
    private TestProjectInfoMapper testProjectInfoMapper;

    @Override
    public void batchCreate(List<TestProjectInfoDTO> testProjectInfoDTOList) {
        testProjectInfoMapper.batchInsert(testProjectInfoDTOList);
    }

    @Override
    public void initializationProjectInfo(ProjectEvent projectEvent) {
        TestProjectInfoDTO projectInfoDTO = new TestProjectInfoDTO();
        projectInfoDTO.setCaseMaxNum(0L);
        projectInfoDTO.setProjectCode(projectEvent.getProjectCode());
        projectInfoDTO.setProjectId(projectEvent.getProjectId());
        int result = testProjectInfoMapper.insert(projectInfoDTO);
        if (result != 1) {
            throw new CommonException("error.projectInfo.initializationProjectInfo");
        }
    }
}
