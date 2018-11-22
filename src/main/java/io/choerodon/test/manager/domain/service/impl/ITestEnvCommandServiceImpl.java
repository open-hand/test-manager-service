package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.test.manager.domain.service.ITestEnvCommandService;
import io.choerodon.test.manager.domain.test.manager.entity.TestEnvCommand;
import io.choerodon.test.manager.infra.mapper.TestEnvCommandMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by zongw.lee@gmail.com on 20/11/2018
 */
public class ITestEnvCommandServiceImpl implements ITestEnvCommandService {

    @Autowired
    TestEnvCommandMapper envCommandMapper;

    @Override
    public List<TestEnvCommand> queryEnvCommand(TestEnvCommand envCommand) {
        return envCommandMapper.select(envCommand);
    }

    @Override
    public TestEnvCommand insertOne(TestEnvCommand envCommand) {
        envCommandMapper.insert(envCommand);
        return envCommandMapper.selectOne(envCommand);
    }
}
