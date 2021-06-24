package io.choerodon.test.manager.infra.mapper;

import java.util.Date;
import java.util.List;

import io.choerodon.test.manager.api.vo.TestMyExecutionCaseStatusVO;
import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestStatusDTO;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
public interface TestStatusMapper extends BaseMapper<TestStatusDTO> {

    List<TestStatusDTO> queryAllUnderProject(@Param("dto") TestStatusDTO testStatusDTO);

    Long ifDeleteCycleCaseAllow(@Param("statusId") Long statusId);

    Long ifDeleteCaseStepAllow(@Param("statusId") Long statusId);

    Long getDefaultStatus(@Param("statusType") String statusType);

    TestStatusDTO queryDefaultStatus(@Param("statusType") String statusType,@Param("statusName")String statusName);

    void updateAuditFields(@Param("statusId") Long statusId, @Param("userId") Long userId, @Param("date") Date date);

    List<TestMyExecutionCaseStatusVO> queryMyExecutionalCaseStatus(@Param("projectIds") List<Long> projectIds);
}
