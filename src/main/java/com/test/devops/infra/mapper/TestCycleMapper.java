package com.test.devops.infra.mapper;

import com.test.devops.infra.dataobject.TestCycleDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCycleMapper extends BaseMapper<TestCycleDO> {

	List<TestCycleDO> query(@Param("versionId") Long versionId);

//	List<Map<String,Long>> queryAboutBar(@Param("cycleId") Long cycleId);
}
