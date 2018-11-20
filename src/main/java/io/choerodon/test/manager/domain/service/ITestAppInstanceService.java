package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceE;

import java.util.List;

public interface ITestAppInstanceService {

    List<TestAppInstanceE> query(TestAppInstanceE testEnvCommandValue);

    TestAppInstanceE update(TestAppInstanceE testEnvCommandValue);

    void delete(TestAppInstanceE testEnvCommandValue);

    TestAppInstanceE insert(TestAppInstanceE testEnvCommandValue);

}
