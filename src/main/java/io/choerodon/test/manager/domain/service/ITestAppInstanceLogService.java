package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceLogE;

import java.util.List;

public interface ITestAppInstanceLogService {
    List<TestAppInstanceLogE> query(TestAppInstanceLogE testAppInstanceE);

    TestAppInstanceLogE update(TestAppInstanceLogE testAppInstanceE);

    void delete(TestAppInstanceLogE testAppInstanceE);

    TestAppInstanceLogE insert(TestAppInstanceLogE testAppInstanceE);
}
