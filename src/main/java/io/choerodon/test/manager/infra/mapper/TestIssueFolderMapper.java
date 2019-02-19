package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dataobject.TestIssueFolderDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public interface TestIssueFolderMapper extends BaseMapper<TestIssueFolderDO> {
    void updateAuditFields(@Param("folderIds") Long[] folderId, @Param("userId") Long userId, @Param("date") Date date);
}