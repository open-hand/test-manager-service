package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public interface TestIssueFolderMapper extends Mapper<TestIssueFolderDTO> {
    void updateAuditFields(@Param("folderIds") Long[] folderId, @Param("userId") Long userId, @Param("date") Date date);

    List<TestIssueFolderDTO> selectChildrenByParentId(@Param("parentId") Long parentId);


}