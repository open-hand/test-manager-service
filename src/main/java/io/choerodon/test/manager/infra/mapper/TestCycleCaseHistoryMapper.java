package io.choerodon.test.manager.infra.mapper;

import io.choerodon.test.manager.infra.dataobject.TestCycleCaseHistoryDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseHistoryMapper extends BaseMapper<TestCycleCaseHistoryDO> {
    List<TestCycleCaseHistoryDO> query(@Param("dto") TestCycleCaseHistoryDO dto);
    List<TestCycleCaseHistoryDO> queryByPrimaryKey(Long id);
}
