package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestCycleCaseHistoryDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseHistoryMapper extends BaseMapper<TestCycleCaseHistoryDTO> {
    List<TestCycleCaseHistoryDTO> query(@Param("dto") TestCycleCaseHistoryDTO dto);

    List<TestCycleCaseHistoryDTO> queryByPrimaryKey(Long id);

    void updateAuditFields(@Param("executeIds") Long[] executeId, @Param("userId") Long userId, @Param("date") Date date);

    void batchDeleteByExecutIds(@Param("list") List<Long> executeIds);
}
