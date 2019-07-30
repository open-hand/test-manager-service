package io.choerodon.test.manager.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.test.manager.app.service.TestAppInstanceLogService;
import io.choerodon.test.manager.infra.mapper.TestAppInstanceLogMapper;

@Component
public class TestAppInstanceLogServiceImpl implements TestAppInstanceLogService {

    @Autowired
    private TestAppInstanceLogMapper mapper;

    @Override
    public String queryLog(Long logId) {
        return mapper.selectByPrimaryKey(logId).getLog();
    }
}
