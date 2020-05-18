package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestCycleCaseStepDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseStepMapper extends BaseMapper<TestCycleCaseStepDTO> {

    List<TestCycleCaseStepDTO> queryWithTestCaseStep(@Param("dto") TestCycleCaseStepDTO testCycleCaseStepDTO, @Param("page") Integer page, @Param("pageSize") Integer pageSize);

    List<TestCycleCaseStepDTO> queryCycleCaseForReporter(@Param("ids") Long[] ids);

    int batchInsertTestCycleCaseSteps(List<TestCycleCaseStepDTO> testCycleCaseStepDTOS);

    void updateAuditFields(@Param("executeIds") Long[] executeId, @Param("userId") Long userId, @Param("date") Date date);

    List<TestCycleCaseStepDTO> querListByexecuteId(@Param("executeId") Long executeId);

    void batchDeleteTestCycleCaseSteps(@Param("executeStepIds") List<Long> executeStepIds);

    String getLastedRank(@Param("executeId") Long executeId);

    void fixCycleCaseStep();

    void fixCycleCaseStepRank();

    void batchDeleteByExecutIds(@Param("list") List<Long> executeIds);

    List<TestCycleCaseStepDTO>  listByexecuteIds(@Param("ids") List<Long> olderExecuteId);

    int countByExecuteIds(@Param("list") List<Long> olderExecuteIds);

    List<TestCycleCaseStepDTO> queryStepByExecuteId(@Param("executeId") Long executeId);

    int updateCycleCaseStepStatus(@Param("executeId") Long executeId);
}
