package io.choerodon.test.manager.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.test.manager.app.service.TestPlanServcie;
import io.choerodon.test.manager.infra.dto.TestPlanDTO;
import io.choerodon.test.manager.infra.mapper.TestPlanMapper;

/**
 * @author: 25499
 * @date: 2019/11/26 14:17
 * @description:
 */
@Service
public class TestPlanServiceImpl implements TestPlanServcie {
    @Autowired
    private TestPlanMapper testPlanMapper;
    @Override
    public void batchInsert(List<TestPlanDTO> testPlanDTOList) {
        testPlanMapper.batchInsert(testPlanDTOList);
    }
}
