package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestCycleCaseStepDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseStepMapper extends Mapper<TestCycleCaseStepDTO> {

    List<TestCycleCaseStepDTO> queryWithTestCaseStep(@Param("dto") TestCycleCaseStepDTO testCycleCaseStepDTO, @Param("page") Integer page, @Param("pageSize") Integer pageSize);

    List<TestCycleCaseStepDTO> queryWithTestCaseStep_oracle(@Param("dto") TestCycleCaseStepDTO testCycleCaseStepDTO, @Param("page") int page, @Param("pageSize") int pageSize);

    Long queryWithTestCaseStep_count(@Param("executeId") Long executeId);

    List<TestCycleCaseStepDTO> queryCycleCaseForReporter(@Param("ids") Long[] ids);

    int batchInsertTestCycleCaseSteps(List<TestCycleCaseStepDTO> testCycleCaseStepDTOS);

//    int batchUpdateTestCycleCaseSteps(List<TestCycleCaseStepDTO> testCycleCaseStepDTOS);

    void updateAuditFields(@Param("executeIds") Long[] executeId, @Param("userId") Long userId, @Param("date") Date date);

    List<TestCycleCaseStepDTO> querListByexecuteId(@Param("executeId") Long executeId);

    void fixCycleCaseStep();

    void batchDeleteByExecutIds(@Param("list") List<Long> executeIds);

}
