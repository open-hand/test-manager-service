package io.choerodon.test.manager.infra.mapper;

import io.choerodon.test.manager.infra.dataobject.TestStatusDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
public interface TestStatusMapper extends BaseMapper<TestStatusDO> {

	List<TestStatusDO> queryAllUnderProject(@Param("dto") TestStatusDO testStatusDO);
}
