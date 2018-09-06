package io.choerodon.test.manager.domain.service;

import java.util.List;

import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderRelE;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public interface ITestIssueFolderRelService {
    List<TestIssueFolderRelE> query(TestIssueFolderRelE testIssueFolderRelE);

    TestIssueFolderRelE queryOne(TestIssueFolderRelE testIssueFolderRelE);

    TestIssueFolderRelE insert(TestIssueFolderRelE testIssueFolderRelE);

    void delete(TestIssueFolderRelE testIssueFolderRelE);

    TestIssueFolderRelE update(TestIssueFolderRelE testIssueFolderRelE);

}
