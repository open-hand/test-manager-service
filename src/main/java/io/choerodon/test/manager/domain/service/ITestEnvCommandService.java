package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestEnvCommand;

import java.util.List;

/**
 * Created by zongw.lee@gmail.com on 11/20/2018
 */
public interface ITestEnvCommandService {
    List<TestEnvCommand> queryEnvCommand(TestEnvCommand envCommand);

    TestEnvCommand insertOne(TestEnvCommand envCommand);
}
