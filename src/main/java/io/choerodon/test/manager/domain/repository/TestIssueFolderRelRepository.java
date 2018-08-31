package io.choerodon.test.manager.domain.repository;

import java.util.List;

import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderRelE;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public interface TestIssueFolderRelRepository {
	TestIssueFolderRelE insert(TestIssueFolderRelE testIssueFolderRelE);

    void delete(TestIssueFolderRelE testIssueFolderRelE);

	TestIssueFolderRelE update(TestIssueFolderRelE testIssueFolderRelE);

	List<TestIssueFolderRelE> queryAllUnderProject(TestIssueFolderRelE testIssueFolderRelE);
}
