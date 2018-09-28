package io.choerodon.test.manager.domain.service;

import java.util.List;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public interface ITestIssueFolderService {
    List<TestIssueFolderE> query(TestIssueFolderE testIssueFolderE);

    List<TestIssueFolderE> queryByParameterWithPageUnderProject(TestIssueFolderE testIssueFolderE, PageRequest pageRequest);

    TestIssueFolderE insert(TestIssueFolderE testIssueFolderE);

    void delete(TestIssueFolderE testIssueFolderE);

    TestIssueFolderE update(TestIssueFolderE testIssueFolderE);

    TestIssueFolderE queryOne(TestIssueFolderE testIssueFolderE);

    TestIssueFolderE queryByPrimaryKey(TestIssueFolderE testIssueFolderE);

    TestIssueFolderE updateWithNoType(TestIssueFolderE testIssueFolderE);
}
