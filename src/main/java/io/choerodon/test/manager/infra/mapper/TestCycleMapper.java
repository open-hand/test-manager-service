package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dataobject.TestCycleDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleMapper extends BaseMapper<TestCycleDO> {

    List<TestCycleDO> query(@Param("versionIds") Long[] versionId, @Param("assignedTo") Long assignedTo);

    List<TestCycleDO> queryOneCycleBar(@Param("cycleId") Long cycleId);

    /**
     * 获取version下的所有循环Id
     *
     * @param versionIds
     * @return
     */
    List<Long> selectCyclesInVersions(@Param("versionIds") Long[] versionIds);

    /**
     * 验证version下是否有重名cycle
     *
     * @param testCycleDO
     * @return
     */
    Long validateCycle(TestCycleDO testCycleDO);

    List<TestCycleDO> queryChildCycle(@Param("dto") TestCycleDO testCycleDO);

    List<TestCycleDO> queryByIds(@Param("cycleIds") List<Long> cycleIds);

    void updateAuditFields(@Param("cycleIds") Long[] cycleId, @Param("userId") Long userId, @Param("date") Date date);
}
