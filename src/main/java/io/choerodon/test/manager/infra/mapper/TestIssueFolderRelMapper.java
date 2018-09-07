package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dataobject.TestIssueFolderRelDO;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public interface TestIssueFolderRelMapper extends BaseMapper<TestIssueFolderRelDO> {
    int updateFolderByIssue(TestIssueFolderRelDO testIssueFolderRelDO);

    int updateVersionByFolderWithNoLock(TestIssueFolderRelDO testIssueFolderRelDO);
}