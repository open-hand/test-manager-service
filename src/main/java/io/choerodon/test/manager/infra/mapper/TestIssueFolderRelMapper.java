package io.choerodon.test.manager.infra.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestIssueFolderRelDTO;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public interface TestIssueFolderRelMapper extends Mapper<TestIssueFolderRelDTO> {
    int updateFolderByIssue(TestIssueFolderRelDTO testIssueFolderRelDTO);

    int updateVersionByFolderWithNoLock(TestIssueFolderRelDTO testIssueFolderRelDTO);

    void updateAuditFields(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("date") Date date);
}