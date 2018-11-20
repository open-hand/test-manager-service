package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestEnvCommandValue;

import java.util.List;

public interface ITestEnvCommandValueService {

    List<TestEnvCommandValue> query(TestEnvCommandValue testEnvCommandValue);

    TestEnvCommandValue update(TestEnvCommandValue testEnvCommandValue);

    void delete(TestEnvCommandValue testEnvCommandValue);

    TestEnvCommandValue insert(TestEnvCommandValue testEnvCommandValue);
}
