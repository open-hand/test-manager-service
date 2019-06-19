package io.choerodon.test.manager.infra.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dataobject.TestIssueFolderDO;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public interface TestIssueFolderMapper extends Mapper<TestIssueFolderDO> {
    void updateAuditFields(@Param("folderIds") Long[] folderId, @Param("userId") Long userId, @Param("date") Date date);
}