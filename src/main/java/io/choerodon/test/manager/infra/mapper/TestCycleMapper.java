package io.choerodon.test.manager.infra.mapper;

import io.choerodon.test.manager.infra.dataobject.TestCycleDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleMapper extends BaseMapper<TestCycleDO> {

	List<TestCycleDO> query(@Param("versionId") Long versionId);

//	List<Map<String,Long>> queryAboutBar(@Param("cycleId") Long cycleId);
}
