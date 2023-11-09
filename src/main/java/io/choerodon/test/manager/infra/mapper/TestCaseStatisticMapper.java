package io.choerodon.test.manager.infra.mapper;

import io.choerodon.test.manager.api.vo.DailyStatisticVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author superlee
 * @since 2022-06-01
 */
public interface TestCaseStatisticMapper {


    List<DailyStatisticVO> selectTestCaseAddedByDate(@Param("projectIds") List<Long> projectIds,
                                                     @Param("dailyStartDate") Date dailyStartDate,
                                                     @Param("dailyEndDate") Date dailyEndDate);

    List<DailyStatisticVO> selectTestCaseExecByDate(@Param("projectIds") List<Long> projectIds,
                                                    @Param("dailyStartDate") Date dailyStartDate,
                                                    @Param("dailyEndDate") Date dailyEndDate);

    List<DailyStatisticVO> selectTestCaseStepExecByDate(@Param("projectIds") List<Long> projectIds,
                                                        @Param("dailyStartDate") Date dailyStartDate,
                                                        @Param("dailyEndDate") Date dailyEndDate);
}
