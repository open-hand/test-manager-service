package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dataobject.TestIssueFolderRelDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public interface TestIssueFolderRelMapper extends BaseMapper<TestIssueFolderRelDO> {
    int updateFolderByIssue(TestIssueFolderRelDO testIssueFolderRelDO);

    int updateVersionByFolderWithNoLock(TestIssueFolderRelDO testIssueFolderRelDO);

    void updateAuditFields(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("date") Date date);
}