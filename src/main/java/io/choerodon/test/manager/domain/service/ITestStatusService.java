package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/25/18.
 */
public interface ITestStatusService {
    List<TestStatusE> query(TestStatusE testStatusE);

    TestStatusE insert(TestStatusE testStatusE);

    void delete(TestStatusE testStatusE);

    TestStatusE update(TestStatusE testStatusE);
}
