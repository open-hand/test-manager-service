package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceE;

import java.util.List;

public interface ITestAppInstanceService {

    String queryValueByEnvIdAndAppId( Long envId, Long appId);

    TestAppInstanceE update(TestAppInstanceE instanceE);

    void delete(TestAppInstanceE instanceE);

    TestAppInstanceE insert(TestAppInstanceE instanceE);

    TestAppInstanceE queryOne(TestAppInstanceE id);
}
