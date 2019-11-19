package io.choerodon.test.manager.app.eventhandler;

import io.choerodon.test.manager.app.service.TestDataLogService;
import io.choerodon.test.manager.infra.dto.TestDataLogDTO;
import io.choerodon.test.manager.infra.mapper.TestDataLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhaotianxin
 * @since 2019/11/18
 */
@Component
public class TestDataLogServiceImpl implements TestDataLogService {
    @Autowired
    private TestDataLogMapper testDataLogMapper;
    @Override
    public void create(TestDataLogDTO testDataLogDTO) {
        testDataLogMapper.insertSelective(testDataLogDTO);
    }

    @Override
    public void delete(TestDataLogDTO dataLogDTO) {
        testDataLogMapper.delete(dataLogDTO);
    }
}
