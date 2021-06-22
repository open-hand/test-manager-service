package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleMapper extends BaseMapper<TestCycleDTO> {

    List<TestCycleDTO> query(@Param("projectId") Long projectId, @Param("versionIds") Long[] versionId, @Param("assignedTo") Long assignedTo);

    List<TestCycleDTO> queryOneCycleBar(@Param("cycleId") Long cycleId);

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
     * @param testCycleDTO
     * @return
     */
    Long validateCycle(TestCycleDTO testCycleDTO);

    List<TestCycleDTO> queryChildCycle(@Param("dto") TestCycleDTO testCycleDTO);

    List<TestCycleDTO> queryCycleInVersion(@Param("dto") TestCycleDTO testCycleDTO);

    List<TestCycleDTO> queryByIds(@Param("cycleIds") List<Long> cycleIds);

    void updateAuditFields(@Param("cycleIds") Long[] cycleId, @Param("userId") Long userId, @Param("date") Date date);

    String getCycleLastedRank(@Param("versionId") Long versionId);

    String getPlanLastedRank(@Param("planId") Long planId);

    Long getCycleCountInVersion(@Param("versionId") Long versionId);

    Long getFolderCountInCycle(@Param("cycleId") Long cycleId);

    List<TestCycleDTO> queryChildFolderByRank(@Param("cycleId") Long cycleId);

    List<TestCycleDTO> listByPlanIds(@Param("type") String type,@Param("planIds") List<Long> planIds,Long projectId);

    List<Long> selectVersionId();

    void fixPlanId(@Param("versionId") Long versionId, @Param("planId") Long planId);

    void batchInsert(@Param("list") List<TestCycleDTO> list);

    void batchDelete( @Param("list") List<Long> needDeleteCycleIds);

    void fixRank(@Param("list") List<TestCycleDTO> list);

    List<TestCycleDTO> listByPlanIdAndProjectId(@Param("projectId") Long projectId,@Param("planId") Long plandId);

    List<TestCycleDTO> selectAndOrderByIds(@Param("projectId")Long projectId, @Param("cycleIds") List<Long> cycleIds);

    List<TestCycleDTO> listRankIsNullCycle(@Param("projectId") Long projectId, @Param("parentCycleId") Long parentCycleId);
}
