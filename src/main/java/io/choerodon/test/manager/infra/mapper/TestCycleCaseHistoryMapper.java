package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseHistoryDO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseHistoryMapper extends Mapper<TestCycleCaseHistoryDO> {
    List<TestCycleCaseHistoryDO> query(@Param("dto") TestCycleCaseHistoryDO dto);

    List<TestCycleCaseHistoryDO> queryByPrimaryKey(Long id);

    void updateAuditFields(@Param("executeIds") Long[] executeId, @Param("userId") Long userId, @Param("date") Date date);
}
