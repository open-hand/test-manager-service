package io.choerodon.test.manager.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.test.manager.app.service.TestProjectInfoService;
import io.choerodon.test.manager.infra.dto.TestProjectInfoDTO;
import io.choerodon.test.manager.infra.mapper.TestProjectInfoMapper;

/**
 * @author: 25499
 * @date: 2019/11/22 9:33
 * @description:
 */
@Service
public class TestProjectInfoServiceImpl implements TestProjectInfoService {
    @Autowired
    private TestProjectInfoMapper testProjectInfoMapper;
    @Override
    public void batchCreate(List<TestProjectInfoDTO> testProjectInfoDTOList) {
        testProjectInfoMapper.batchInsert(testProjectInfoDTOList);
    }
}
