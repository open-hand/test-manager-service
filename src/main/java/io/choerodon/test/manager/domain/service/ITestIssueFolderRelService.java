package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderRelE;

import java.util.List;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public interface ITestIssueFolderRelService {
    List<TestIssueFolderRelE> query(TestIssueFolderRelE testIssueFolderRelE);

    TestIssueFolderRelE queryOne(TestIssueFolderRelE testIssueFolderRelE);

    TestIssueFolderRelE insert(TestIssueFolderRelE testIssueFolderRelE);

    void delete(TestIssueFolderRelE testIssueFolderRelE);

    TestIssueFolderRelE updateFolderByIssue(TestIssueFolderRelE testIssueFolderRelE);

    TestIssueFolderRelE updateVersionByFolderWithNoLock(TestIssueFolderRelE testIssueFolderRelE);
}
