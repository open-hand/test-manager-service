package io.choerodon.test.manager.infra.mapper;

import io.choerodon.test.manager.infra.dataobject.TestCycleDO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleMapper extends BaseMapper<TestCycleDO> {

	List<TestCycleDO> query(@Param("versionIds") Long[] versionId,@Param("assignedTo") Long assignedTo);

	@Deprecated
	List<TestCycleDO> filter(Map maps);

	@Deprecated
	List<TestCycleDO> getCyclesByVersionId(@Param("versionId") Long versionId);

	/**获取version下的所有循环Id
	 * @param versionIds
	 * @return
	 */
	List<Long> selectCyclesInVersions(@Param("versionIds") Long[] versionIds);

	/** 验证version下是否有重名cycle
	 * @param testCycleDO
	 * @return
	 */
	Long validateCycle(TestCycleDO testCycleDO);
}
