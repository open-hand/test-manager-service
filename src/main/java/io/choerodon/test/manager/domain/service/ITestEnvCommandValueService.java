package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestEnvCommandValue;

public interface ITestEnvCommandValueService {

    TestEnvCommandValue query(Long id);

    TestEnvCommandValue update(TestEnvCommandValue testEnvCommandValue);

    void delete(TestEnvCommandValue testEnvCommandValue);

    TestEnvCommandValue insert(TestEnvCommandValue testEnvCommandValue);
}
