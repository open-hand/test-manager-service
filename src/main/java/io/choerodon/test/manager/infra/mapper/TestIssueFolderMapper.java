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

    List<Long> selectProjectIdList();

    List<Long> selectVersionIdList(@Param("projectId") Long projectId);

    List<TestIssueFolderDTO> selectListByProjectId(@Param("projectId") Long projectId);

    void updateByVersionId(@Param("projectId") Long projectId,@Param("versionId") Long versionId,@Param("parentId") Long parentId);

    List<TestIssueFolderDTO> listFolderByFolderIds(@Param("folderIds") List<Long> folderIds);

    void fixRank(@Param("testIssueFolderDTOS") List<TestIssueFolderDTO> testIssueFolderDTOS);

    String projectLastRank(@Param("projectId") Long projectId);
}