package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceLogE;

import java.util.List;

public interface ITestAppInstanceLogService {
    List<TestAppInstanceLogE> query(TestAppInstanceLogE logE);

    TestAppInstanceLogE update(TestAppInstanceLogE logE);

    void delete(TestAppInstanceLogE logE);

    TestAppInstanceLogE insert(TestAppInstanceLogE logE);

    String queryLog(Long logId);
}
