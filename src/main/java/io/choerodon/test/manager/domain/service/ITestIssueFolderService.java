package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;

import java.util.List;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public interface ITestIssueFolderService {
    List<TestIssueFolderE> query(TestIssueFolderE testIssueFolderE);

    TestIssueFolderE insert(TestIssueFolderE testIssueFolderE);

    void delete(TestIssueFolderE testIssueFolderE);

    TestIssueFolderE update(TestIssueFolderE testIssueFolderE);

    TestIssueFolderE queryOne(TestIssueFolderE testIssueFolderE);

    TestIssueFolderE queryByPrimaryKey(TestIssueFolderE testIssueFolderE);

    TestIssueFolderE updateWithNoType(TestIssueFolderE testIssueFolderE);
}
